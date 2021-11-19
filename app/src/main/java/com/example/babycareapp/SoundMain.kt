package com.example.babycareapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_sound_main.*
import java.io.DataOutputStream
import java.net.Socket

class SoundMain : AppCompatActivity() {
    private var musicDB: MusicDatabase? = null
    private var musicList = listOf<Music>()
    var musicID: Int = 0
    var msg: String = "music"
    private val titleList = listOf("아기와 곰", "무지개", "잘자라 우리아가", "작은별", "둥개 둥개 둥개야", "등대지기")
    private val timeList = listOf("1:32", "1:04", "1:59","2:03", "1:38", "2:18")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound_main)

        if(intent.hasExtra("id")){
            musicID = intent.getIntExtra("id", 0)
        }
        else{

        }

        selectM(musicID)
        NetworkThread(msg+(musicID+1).toString()).start()

        push.setOnClickListener {
            if(push.isSelected){
                push.isSelected = false
                NetworkThread("music pause").start()
            }
            else{
                push.isSelected = true
                NetworkThread("music unpause").start()
            }
        }

        volDown.setOnClickListener{
            NetworkThread("music vol-").start()
        }

        volUp.setOnClickListener{
            NetworkThread("music vol+").start()
        }

        prev.setOnClickListener{
            if(musicID<=0){
                //selectMusic(musicID+5)
                this.musicID += 5
                selectM(musicID)
            }
            else{
                //selectMusic(musicID-1)
                this.musicID -= 1
                selectM(musicID-1)
            }
            NetworkThread("music prev").start()
        }

        next.setOnClickListener{
            if(musicID>=5){
                //selectMusic(musicID-5)
                this.musicID -= 5
                selectM(musicID)
            }
            else{
                this.musicID += 1
                selectM(musicID)
            }
            NetworkThread("music next").start()
        }



        goHome.setOnClickListener{
            finish()
        }
    }

    inner class NetworkThread(var msg: String) :Thread(){
        override fun run(){
            try {
                var socket = Socket("172.20.10.2", 8080)

                val output = socket.getOutputStream()
                val dos = DataOutputStream(output)

                dos.writeUTF(msg)

                socket.close()

                Log.d("LOG", msg)

            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    private fun selectM(id: Int){
        music_title.setText(titleList[musicID])
        music_time.setText(timeList[musicID])
    }
}