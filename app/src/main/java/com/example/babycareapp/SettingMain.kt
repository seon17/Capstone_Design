package com.example.babycareapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_setting_main.*

class SettingMain : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_main)

        setConnect.setOnClickListener(){
            startActivity(Intent(this,SettingConnection::class.java))

        }

        setSystem.setOnClickListener{
            startActivity(Intent(this, SettingSystem::class.java))
        }

        setAlarm.setOnClickListener(){
            startActivity(Intent(this,SettingAlarm::class.java))

        }

        goHome.setOnClickListener{
            finish()
        }
    }
}