package com.example.babycareapp

import android.content.Context
import android.content.Intent
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//Diary recyclerview 사용을 위한 Adapter
class DiaryAdapter(val diaryItemClick: (Diary) -> Unit, val context: Context, val diary: List<Diary>)
    : RecyclerView.Adapter<DiaryAdapter.Holder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_main, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return diary.size
    }

    fun getItem(): List<Diary> {
        return diary
    }

    override fun onBindViewHolder(holder: DiaryAdapter.Holder, position: Int) {
        holder?.bind(diary[position])
    }

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!){

        init{ }

        private val date = itemView?.findViewById<TextView>(R.id.wr_date)
        private val title = itemView?.findViewById<TextView>(R.id.title)
        private val contents = itemView?.findViewById<TextView>(R.id.contents)

        fun bind(diary: Diary){
            title?.text = diary.title
            contents?.text = diary.contents
            date?.text = diary.in_year.toString() + "/" + diary.in_month.toString() + "/" + diary.in_day.toString()

            itemView.setOnClickListener{
                diaryItemClick(diary)
            }
        }
    }
}