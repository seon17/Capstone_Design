package com.example.babycareapp


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_alram_main.*
import java.io.DataOutputStream
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.*

class AlarmMain : AppCompatActivity() {

    var now: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alram_main)

        //구토 발생 시간 받아와서 화면에 출력
        now = intent.getLongExtra("time", 0)
        setTimeNow(now!!)

        stopService.setOnClickListener{
            //확인 시 구토 확인 데이터 전송
            stopService(Intent(this, MediaService::class.java))
            NetworkThread().start()
            startActivity(Intent(this, Home::class.java))
            finish()
        }
    }

    //알람 발생 시간 화면 출력 함수
    private fun setTimeNow(now: Long){
        val now = now
        val date = SimpleDateFormat("yyyy/MM/dd", Locale.KOREA).format(now)
        val time = SimpleDateFormat("HH:mm:ss", Locale.KOREA).format(now)

        dateNow.text = date
        timeNow.text = time
    }

    //구토 발생 확인 데이터 전송
    inner class NetworkThread: Thread(){
        override fun run(){
            try{
                var socket = Socket("172.20.10.2", 8080)

                val output = socket.getOutputStream()
                val dos = DataOutputStream(output)

                dos.writeUTF("OK")

                socket.close()

            }catch(e: Exception){
            }
        }
    }
}