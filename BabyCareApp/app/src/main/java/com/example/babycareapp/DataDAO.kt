package com.example.babycareapp

import androidx.lifecycle.LiveData
import androidx.room.*

//DB Query
@Dao
interface DataDAO {
    @Query("SELECT * FROM tb_app ORDER BY insertYear DESC, insertMonth DESC, insertDay DESC")
    fun getAllDESC(): LiveData<List<Diary>>

    @Query("SELECT * FROM tb_app ORDER BY insertYear DESC, insertMonth DESC, insertDay ASC")
    fun getAllASC(): LiveData<List<Diary>>

    @Insert
    fun insert(diary: Diary)

    @Delete
    fun delete(diary: Diary)

    @Update
    fun update(diary: Diary)

}