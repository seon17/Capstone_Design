package com.example.babycareapp

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.webkit.WebView
import kotlinx.android.synthetic.main.activity_cradle_motion.*
import kotlinx.android.synthetic.main.activity_streaming_main.*
import kotlinx.android.synthetic.main.activity_streaming_main.goHome
import java.io.*
import java.net.Socket
import java.sql.Types.NULL
import java.util.Base64

class StreamingMain : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_streaming_main)

        val sharedPresentException = getSharedPreferences("setting",0)
        val ip = sharedPresentException.getString("IP","")

        webViewSetting()
        val url = "http://$ip:8090/?action=stream"
        webView.loadUrl(url)

        goHome.setOnClickListener{
            finish()
        }

    }
    private fun webViewSetting(){
        val webView = findViewById<WebView>(R.id.webView)
        webView.setPadding(0, 0, 0, 0)

        webView.settings.builtInZoomControls = false
        webView.settings.javaScriptEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
    }
}
