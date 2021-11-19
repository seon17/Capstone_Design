package com.example.babycareapp

//DB (파일 이름 재설정 필요)
import androidx.room.*

@Entity(tableName = "tb_app")
class Diary(
    @PrimaryKey(autoGenerate = true) val id:Long,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "contents") var contents: String,
    @ColumnInfo(name = "insertYear") var in_year: Int,
    @ColumnInfo(name = "insertMonth") var in_month: Int,
    @ColumnInfo(name = "insertDay") var in_day: Int,
    @ColumnInfo(name = "updateTime") var up_time: Long
){
    constructor():this(0,"","",0, 0, 0,0)
}

@Entity(tableName = "tb_music")
class Music(
    @PrimaryKey(autoGenerate = true) var id: Int,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "time") var time: String
)
