package com.example.babycareapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import java.io.DataOutputStream
import java.lang.UnsupportedOperationException
import java.net.Socket
import java.util.*
import kotlin.concurrent.timer

class Service : Service() {

    private var LOG = "Service"
    private var timerTask : Timer? = null
    var emergency = ""
    var builder: NotificationCompat.Builder? = null
    private val channel_name: String = "CHANNEL_1"
    private val CHANNEL_ID: String = "EMER"
    private val notificationId: Int = 1
    private var notificationManager: NotificationManager? = null
    var socket: Socket? = null
    private var sharedPreference:SharedPreferences? = null

    override fun onCreate(){
        Log.d(LOG,"onCreate()")
        NetworkThread().start()
        sharedPreference = getSharedPreferences("setting",0)
    }

    override fun onBind(p0: Intent?):IBinder{
        throw UnsupportedOperationException("Not yet")
    }

    override fun onStartCommand(intent:Intent?, flags: Int, startId: Int) : Int{
        Log.d(LOG, "onStartCommand()")
        startTimerTask()

        return Service.START_STICKY
    }

    override fun onDestroy() {
        Log.d(LOG, "onDestory()")
        stopTimerTask()
        val edit = sharedPreference?.edit()
        edit?.putBoolean("On", false)
        super.onDestroy()
    }

    private fun startTimerTask(){
        timerTask = timer(period=2000){
            socket?.let { InputThread(it).start() }
            Log.d(LOG, "TimerTask")
        }
    }

    private fun stopTimerTask(){
        timerTask?.cancel()
    }

    inner class NetworkThread:Thread() {
        override fun run() {
            try {
                //socket = Socket("ec2-3-134-85-20.us-east-2.compute.amazonaws.com", 8000)
                socket = Socket("172.20.10.2", 8080)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    inner class InputThread(sock: Socket):Thread(){
        private val socket = sock

        override fun run(){
            try {
                val output = socket.getOutputStream()
                val dos = DataOutputStream(output)
                dos.writeUTF("H")

                val input = socket.getInputStream()
                val available = input.available()

                if(available > 0){
                    val dataArr = ByteArray(available)
                    input.read(dataArr)
                    emergency = String(dataArr, Charsets.UTF_8)
                }
                //IntentThread(emergency).start()
                Log.d("LOG", "Thread:" + emergency)

                if(emergency.contains("yes")){
                    createNotificationChannel(CHANNEL_ID, channel_name, "Emergency")
                    createNotification(emergency)
                    notificationManager?.notify(notificationId, builder?.build())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun createNotification(msg: String){
        val intent = Intent(this, AlarmMain::class.java)
        val now = System.currentTimeMillis()
        intent.putExtra("time", now)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.baby)
                .setContentTitle("Baby Care App")
                .setContentText("구토상황 발생 ! ")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        stopService(Intent(this, MediaService::class.java))
        startService(Intent(this, MediaService::class.java))
    }

    private fun createNotificationChannel(id: String, name: String, channelDescription: String){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(id, name, importance)

            val alarm = sharedPreference?.getInt("Alarm", 0)
            val vib = sharedPreference?.getInt("Vib", 0)

            channel.vibrationPattern = vib?.let { selectVib(it) }

            val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .build()

            if(alarm == 5){
            }
            else{
                val sound = Uri.parse("android.resource://"+ packageName +"/"+selectMp3(alarm!!))
                Log.d(LOG, sound.toString())
                channel.setSound(sound, audioAttributes)
            }


            channel.apply{ description = channelDescription }
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager!!.createNotificationChannel(channel)
        }
    }

    private fun selectMp3(alarm: Int): String {
        return when(alarm){
            0 -> "R.raw.alarm1" //MediaPlayer.create(this, R.raw.alarm1)
            1 -> "R.raw.alarm2" //MediaPlayer.create(this, R.raw.alarm2)
            2 -> "R.raw.alarm3" //MediaPlayer.create(this, R.raw.alarm3)
            3 -> "R.raw.alarm4" //MediaPlayer.create(this, R.raw.alarm4)
            4 -> "R.raw.alarm5" //MediaPlayer.create(this, R.raw.alarm5)
            else -> "R.raw.alarm1"
        }
    }

    private fun selectVib(vib: Int): LongArray {
        return when (vib) {
            0 -> longArrayOf(0, 1000, 1000, 1000, 1000, 1000)
            1 -> longArrayOf(0, 4000, 1000, 4000, 1000, 4000)
            else -> longArrayOf(0)
        }
    }
}