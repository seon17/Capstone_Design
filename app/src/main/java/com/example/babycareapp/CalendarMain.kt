package com.example.babycareapp

import android.os.Bundle
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.babycareapp.Decorator.SaturdayDecorator
import com.example.babycareapp.Decorator.SundayDecorator
import com.example.babycareapp.Decorator.TodayDecorator
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener
import kotlinx.android.synthetic.main.activity_calendar_main.*

//캘린더 
class CalendarMain : AppCompatActivity() {

    private val calendarView: CalendarView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_main)


        val sundayDecorator = SundayDecorator()
        val saturdayDecorator = SaturdayDecorator()
        val todayDecorator = TodayDecorator(this)

        materialCalendar.addDecorators(sundayDecorator, saturdayDecorator, todayDecorator)
        materialCalendar.setOnMonthChangedListener(object: OnMonthChangedListener {
          override fun onMonthChanged(widget: MaterialCalendarView?, date: CalendarDay?) {
          }
        })

        materialCalendar.setOnDateChangedListener(object: OnDateSelectedListener{
            override fun onDateSelected(widget: MaterialCalendarView, date: CalendarDay, selected: Boolean) {
                TODO("Not yet implemented")
            }
        })

    }
}