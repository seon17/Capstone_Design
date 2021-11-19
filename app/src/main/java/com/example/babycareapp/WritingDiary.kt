package com.example.babycareapp


import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_writing_diary.*
import java.sql.Types.NULL
import java.text.SimpleDateFormat
import java.util.*

class WritingDiary : AppCompatActivity() {
    var insertYear: Int? = null
    var insertMonth: Int? = null
    var insertDay: Int? = null

    private var diaryDB: AppDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_writing_diary)

        diaryDB = AppDatabase.getInstance(this)

        if(intent.hasExtra("id")){
            val id = intent.getLongExtra("id",0)
            val upDiary = Diary()
            upDiary.title = diary_title.text.toString()
            upDiary.contents = diary_contents.text.toString()
            //upDiary.in_year = btn_date.text.toString()
            upDiary.up_time = System.currentTimeMillis()
            diaryDB?.dataDao()?.update(upDiary)
        }
        else{

        }

        btn_date.text = SimpleDateFormat("yyyy/MM/dd", Locale.KOREA).format(System.currentTimeMillis())

        btn_date.setOnClickListener{
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val listener = DatePickerDialog.OnDateSetListener { datePicker, year, month, dayOfMonth ->
                btn_date.text = "${year} / ${month+1} / ${dayOfMonth}"
                insertYear = year
                insertMonth = month+1
                insertDay = dayOfMonth

            }
            val picker = DatePickerDialog(this, listener, year, month, day)
            picker.show()

        }


        val addRunnable = Runnable {
            val newDiary = Diary()
            newDiary.title = diary_title.text.toString()
            newDiary.contents = diary_contents.text.toString()
            newDiary.in_year = this.insertYear?:2021
            newDiary.in_month = this.insertMonth?:9
            newDiary.in_day = this.insertDay?:16
            newDiary.up_time = System.currentTimeMillis()
            diaryDB?.dataDao()?.insert(newDiary)
        }

        btn_complete.setOnClickListener {
            if(diary_title.text == null || diary_contents.text == null){
                Toast.makeText(this, "올바르게 작성해주세요", Toast.LENGTH_SHORT).show()
            }
            else {
                val addThread = Thread(addRunnable)
                addThread.start()

                startActivity(Intent(this, DiaryList::class.java))
                finish()
            }
        }
    }

    override fun onDestroy() {
        diaryDB?.destroyInstance()
        super.onDestroy()
    }
}