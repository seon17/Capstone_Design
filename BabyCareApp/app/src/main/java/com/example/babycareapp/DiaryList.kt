package com.example.babycareapp

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_cradle_motion.*
import kotlinx.android.synthetic.main.activity_diary_list.*

class DiaryList : AppCompatActivity() {

    private var diaryDB: AppDatabase? = null
    private var diaryList = listOf<Diary>()
    lateinit var mAdapter: DiaryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_list)

        initSwipe()
        diaryDB = AppDatabase.getInstance(this)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        diaryDB!!.dataDao().getAllDESC().observe(this, androidx.lifecycle.Observer {
            diaryList = it
            mAdapter = DiaryAdapter({ diary ->
                val intent = Intent(this, DiaryMain::class.java)
                intent.putExtra("year", diary.in_year)
                intent.putExtra("month", diary.in_month)
                intent.putExtra("day", diary.in_day)
                intent.putExtra("title", diary.title)
                intent.putExtra("contents", diary.contents)
                startActivity(intent)
            }, this, diaryList)
            recyclerView.adapter = mAdapter
        })

        btn_write.setOnClickListener{
            startActivity(Intent(this, WritingDiary::class.java))
            finish()
        }
    }

    override fun onDestroy() {
        diaryDB?.destroyInstance()
        diaryDB = null
        super.onDestroy()
    }

    private fun initSwipe(){
        val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback =
            object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition

                    if(direction == ItemTouchHelper.LEFT){
                        val r = Thread{
                            mAdapter.getItem()[position].let {
                                diaryDB?.dataDao()?.delete(it)
                            }
                        }
                        val builder = AlertDialog.Builder(this@DiaryList)
                        builder.setMessage("일기를 삭제하시겠습니까?")
                        val listener = DialogInterface.OnClickListener{ _, p1->
                            if(p1 == DialogInterface.BUTTON_POSITIVE){
                                r.start()
                            }
                            if(p1 == DialogInterface.BUTTON_NEGATIVE){
                                recyclerView.adapter = mAdapter
                            }
                        }
                        builder.setPositiveButton("예", listener)
                        builder.setNegativeButton("아니요", listener)
                        builder.show()

                    }else{
                    }
                }

            }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}