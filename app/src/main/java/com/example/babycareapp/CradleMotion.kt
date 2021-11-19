package com.example.babycareapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_cradle_motion.*
import java.io.DataOutputStream
import java.net.Socket
import kotlin.concurrent.timer

//요람 전동 설정
class CradleMotion : AppCompatActivity() {

    var step:Int? = null
    var timer:Int? = null
    var active:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cradle_motion)

        //시간 spinner
        val timerList = arrayOf("10초", "20초")
        timerSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, timerList)

        //'시작하기' 클릭 시 전동 데이터 전송
        vibStart.setOnClickListener{
            step = vibStep.progress
            NetworkThread(stepTrans(step)+timer.toString()).start()
        }

        //'종료하기' 클릭 시 종료 데이터 전송
        vibStop.setOnClickListener{
            NetworkThread("q").start()
        }



        //spinner 선택 결과 변수에 저장
        timerSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                println(timerList[position])
                timer = position + 1
            }
        }

        //seek bar
        val listener = SeekListener()
        vibStep.setOnSeekBarChangeListener(listener)

        //홈으로 돌아가기
        goHome.setOnClickListener{
            finish()
        }
    }

    //seek bar
    inner class SeekListener:SeekBar.OnSeekBarChangeListener{
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            //TODO("Not yet implemented")
            step = progress
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            //TODO("Not yet implemented")
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            //TODO("Not yet implemented")
        }
    }

    //seek bar 선택 데이터 변환
    private fun stepTrans(x: Int?) : String{
        return when(x){
            0 -> "A"
            1 -> "B"
            2 -> "C"
            else -> "n"
        }
    }

    //데이터 전송
    inner class NetworkThread(var msg: String) :Thread(){
        override fun run(){
            try {
                var socket = Socket("172.20.10.2", 8080)

                val output = socket.getOutputStream()
                val dos = DataOutputStream(output)

                dos.writeUTF(msg)

                socket.close()

                //Log.d("LOG", msg)

            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
}