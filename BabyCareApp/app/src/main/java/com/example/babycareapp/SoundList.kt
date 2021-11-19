package com.example.babycareapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_diary_list.*
import kotlinx.android.synthetic.main.activity_sound_list.*

class SoundList : AppCompatActivity() {
    private var musicDB: MusicDatabase? = null
    private var musicList = listOf<Music>()
    lateinit var mAdapter: MusicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound_list)

        musicDB = MusicDatabase.getInstance(this)

        val r = Runnable{
            try{
                musicRecyclerView.setHasFixedSize(true)
                musicRecyclerView.layoutManager = GridLayoutManager(this, 2)

                musicList = musicDB?.musicDao()?.getAll()!!
                mAdapter = MusicAdapter({ music ->
                    val intent = Intent(this, SoundMain::class.java)
                    intent.putExtra("id", music.id-1)
                    startActivity(intent)
                },this, musicList)
                mAdapter.notifyDataSetChanged()

                musicRecyclerView.adapter = mAdapter


            }catch(e:Exception){
                // Log.d("tag", "Error - $e")
            }
        }

        val thread = Thread(r)
        thread.start()

        goHome.setOnClickListener{
            finish()
        }
    }

    override fun onDestroy() {
        musicDB?.destroyInstance()
        musicDB = null
        super.onDestroy()
    }

}