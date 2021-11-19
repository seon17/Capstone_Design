package com.example.babycareapp

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MusicDAO {
    @Insert
    suspend fun addMusicDb(music : List<Music>)

    @Query("SELECT * FROM tb_music ORDER BY id ASC")
    fun getAll(): List<Music>

    @Query("SELECT title FROM tb_music WHERE id MATCH :query")
    fun getTitle(query: String): String

    @Query("SELECT time FROM tb_music WHERE id MATCH :query")
    fun getTime(query: String): String
}