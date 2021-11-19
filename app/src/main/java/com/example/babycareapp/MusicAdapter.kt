package com.example.babycareapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MusicAdapter(val musicItemClick: (Music) -> Unit, val context: Context, val music: List<Music>)
    : RecyclerView.Adapter<MusicAdapter.Holder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.music_item, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return music.size
    }

    fun getItem(): List<Music> {
        return music
    }

    override fun onBindViewHolder(holder: MusicAdapter.Holder, position: Int) {
        holder.bind(music[position])
    }

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!){

        init{
        }

        private val title = itemView?.findViewById<TextView>(R.id.text_title)
        private val time = itemView?.findViewById<TextView>(R.id.text_time)

        fun bind(music: Music){
            title?.text = music.title
            time?.text = music.time

            itemView.setOnClickListener{
                musicItemClick(music)
            }
        }
    }
}