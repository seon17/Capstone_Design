package com.example.babycareapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_diary_main.*
import java.util.*

class DiaryMain : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_main)

        if(intent.hasExtra("year")){
            text_date.text = intent.getIntExtra("year", 0).toString() + " / " + intent.getIntExtra("month", 0).toString() +" / " + intent.getIntExtra("day", 0).toString()
            text_title.text = intent.getStringExtra("title")
            text_contents.text = intent.getStringExtra("contents")
        }
        else{
        }

        btn_back.setOnClickListener{
            startActivity(Intent(this, DiaryList::class.java))
            finish()
        }
    }
}