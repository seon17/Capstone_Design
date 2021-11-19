package com.example.babycareapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startLoading()
    }
    private fun startLoading(){
        var handler = Handler()
        handler.postDelayed({finish()}, 2000)
    }
}