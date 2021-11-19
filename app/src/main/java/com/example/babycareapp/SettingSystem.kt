package com.example.babycareapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_setting_system.*
import java.io.DataOutputStream
import java.net.Socket

class SettingSystem : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_system)

        val sharedPreference = getSharedPreferences("setting", 0)
        val edit = sharedPreference.edit()

        if(sharedPreference.getString("system", "0")=="@"){
            btn_system.setText("중지하기")
        }
        else{
            btn_system.setText("재가동하기")
        }

        btn_system.setOnClickListener{
            if(sharedPreference.getString("system","0") == "@"){
                edit.putString("system", "!").apply()
                NetworkThread("!").start()
                btn_system.setText("재가동하기")
            }
            else{
                edit.putString("system", "@").apply()
                NetworkThread("@").start()
                btn_system.setText("중지하기")
            }
        }

        goHome.setOnClickListener {
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
}