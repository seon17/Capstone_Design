package com.example.babycareapp

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log

class MediaService: Service(){
    var mediaPlayer: MediaPlayer? = null

    override fun onCreate(){
        Log.d("Media", "onCreate()")
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val sharedPreference = getSharedPreferences("setting", 0)
        val alarm = sharedPreference?.getInt("Alarm", 0)

        if(alarm !=5){
            mediaPlayer = MediaPlayer.create(this, selectMp3(alarm!!))
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        }
        else{

        }
        Log.d("Media", "onStartCommand()")
        return Service.START_STICKY
    }

    override fun onDestroy() {
        mediaPlayer?.stop()
        Log.d("Media", "onDestroy()")
    }

    private fun selectMp3(alarm: Int): Int {
        return when(alarm){
            0 -> R.raw.alarm1
            1 -> R.raw.alarm2
            2 -> R.raw.alarm3
            3 -> R.raw.alarm4
            4 -> R.raw.alarm5
            else -> R.raw.alarm1
        }
    }
}