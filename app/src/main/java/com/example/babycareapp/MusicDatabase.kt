package com.example.babycareapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


@Database(entities = [Music::class], version = 1, exportSchema = false)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun musicDao() : MusicDAO

    companion object {
        private var instance: MusicDatabase? = null

        fun getInstance(context: Context): MusicDatabase? {
            if(instance == null){
                synchronized(MusicDatabase::class) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            MusicDatabase::class.java,
                            "tb_music")
                            .createFromAsset("database/tb_music.db")
                            .allowMainThreadQueries()
                            .build()
                }
            }
            return instance
        }
    }

    fun destroyInstance(){
        instance = null
    }
}
