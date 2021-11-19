package com.example.babycareapp

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_cradle_motion.*
import kotlinx.android.synthetic.main.activity_setting_alarm.*

class SettingAlarm : AppCompatActivity() {

    var alarm: Int? = null
    var volume: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_alarm)

        val alarmList = arrayOf("탁상 시계", "맥박", "닭울음", "학교벨", "비상벨")
        alarmSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, alarmList)

        val sharedPreference = getSharedPreferences("setting", 0)
        val alarmOnOff = sharedPreference.getBoolean("On", false)

        alarmSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                println(alarmList[sharedPreference.getInt("Alarm", 4)])

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                println(alarmList[position])
                alarm = position
            }
        }

        btn_setting.setOnClickListener{
            val editor = sharedPreference.edit()
            alarm?.let { it1 -> editor.putInt("Alarm", it1) }
            editor.apply()

            finish()
        }

        goBack.setOnClickListener{
            finish()
        }
    }

}