package com.example.todolist.ui.calendar

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.example.todolist.R
import com.example.todolist.Task
import com.example.todolist.TaskAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class CalendarFragment : Fragment() {
    private lateinit var mTaskAdapter: TaskAdapter
    private lateinit var calendarListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.fragment_calendar, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val calendarView = view.findViewById<CalendarView>(R.id.calendarView)
        calendarListView = view.findViewById<ListView>(R.id.calendarListView)
        mTaskAdapter = TaskAdapter( this@CalendarFragment)
        reloadListView()

        // カレンダーにタスクのマークを設定
        val events = ArrayList<EventDay>();
        val calendar = Calendar.getInstance() //
        events.add(EventDay(calendar , R.drawable.bluestar))
        calendarView.setEvents(events)

        calendarView.setOnDayClickListener { eventDay ->
            val nowCalendar = eventDay.calendar
            val date = nowCalendar.get(Calendar.YEAR).toString() + "/"+ (nowCalendar.get(Calendar.MONTH) + 1).toString() + "/" + nowCalendar.get(Calendar.DAY_OF_MONTH).toString()
            Toast.makeText(this.context, date,Toast.LENGTH_SHORT).show()
        }
    }

    private fun reloadListView() {
        // データを取得し、日付順にソート
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        uid?.let{
            val tasks = db.collection("tasks").whereEqualTo("uid", it)
            tasks.get()
                .addOnSuccessListener { documents ->
                    val taskList = documents.toObjects(Task::class.java)
                    mTaskAdapter.taskList = taskList
                    // ListViewのアダプターに設定する
                    calendarListView.adapter = mTaskAdapter
                    // アダプターにデータの変更を通知する
                    mTaskAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.w("TAG", "Error getting documents: ", exception)
                }
        }
    }

}