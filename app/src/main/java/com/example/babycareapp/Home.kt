package com.example.babycareapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_home.*

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val sharedPreference = getSharedPreferences("setting", 0)
        val edit = sharedPreference.edit()
        edit.putString("system", "@")
        edit.apply()

        btn_cctv.setOnClickListener{
            startActivity(Intent(this, StreamingMain::class.java))
        }

        btn_cradle.setOnClickListener{
            startActivity(Intent(this, CradleMotion::class.java))
        }

        btn_music.setOnClickListener{
            startActivity(Intent(this, SoundList::class.java))

        }

        btn_diary.setOnClickListener {
            startActivity(Intent(this, DiaryList::class.java))
        }

        btn_setting.setOnClickListener{
            startActivity(Intent(this,SettingMain::class.java))
        }
    }

    override fun onDestroy() {

        val sharedPreference = getSharedPreferences("setting", 0)
        val edit = sharedPreference.edit()
        edit.putBoolean("On", false)
        edit.apply()

        super.onDestroy()
    }
}