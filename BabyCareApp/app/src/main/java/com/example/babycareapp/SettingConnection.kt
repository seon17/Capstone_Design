package com.example.babycareapp

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_dialog.*
import kotlinx.android.synthetic.main.activity_setting_connection.*
import java.text.SimpleDateFormat
import java.util.*

class SettingConnection : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_connection)

        val sharedPreferences = getSharedPreferences("setting",0)
        val edit = sharedPreferences.edit()
        text_ip.text = sharedPreferences.getString("IP","")

        btn_update.setOnClickListener{
            var builder = AlertDialog.Builder(this)
            builder.setTitle("IP 주소 변경")
            builder.setView(layoutInflater.inflate(R.layout.activity_dialog,null))

            var listener = DialogInterface.OnClickListener{p0, _ ->
                var dialog = p0 as AlertDialog
                var editIP = dialog.findViewById<EditText>(R.id.edit_ip_dialog)

                edit.putString("IP", "${editIP?.text}")
                edit.apply()
                text_ip.text = "${editIP?.text}"
            }

            builder.setPositiveButton("확인", listener)
            builder.setNegativeButton("취소", null)

            builder.show()

        }

        goHome.setOnClickListener{
            finish()
        }
    }
}