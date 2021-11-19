package com.example.babycareapp

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main2.*

class MainActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        val sharedPreference = getSharedPreferences("setting", 0)
        val edit = sharedPreference.edit()
        val alarmOnOff = sharedPreference.getBoolean("On", false)

        if(alarmOnOff){
            startActivity(Intent(this, Home::class.java))
            finish()
        }

        btn_start.setOnClickListener{
            if(edit_ip.text.toString().length in 10..15){
                edit.putString("IP", edit_ip.text.toString())
                edit.apply()
                val builder = AlertDialog.Builder(this)
                builder.setMessage("구토 서비스를 시작합니다.")

                val listener = DialogInterface.OnClickListener{_, p1->
                    if(p1 == DialogInterface.BUTTON_POSITIVE){
                        startService(Intent(this, Service::class.java))
                        startActivity(Intent(this, Home::class.java))
                        finish()
                    }
                }
                builder.setPositiveButton("예", listener)
                builder.show()
            }
            else{
                Toast.makeText(this, "IP주소가 올바르지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }

    }
}