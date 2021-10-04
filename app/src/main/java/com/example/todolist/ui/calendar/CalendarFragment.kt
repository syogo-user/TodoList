package com.example.todolist.ui.calendar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.example.todolist.R
import com.example.todolist.Task
import com.example.todolist.const.Const.Companion.EXTRA_TASK_DATESTR
import com.example.todolist.ui.list.ListFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class CalendarFragment : Fragment() {
    private lateinit var calendarView: CalendarView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calendarView = view.findViewById(R.id.calendarView)
        reloadCalendarView()

        calendarView.setOnDayClickListener { eventDay ->
            val nowCalendar = eventDay.calendar
            val dateStr = nowCalendar.get(Calendar.YEAR).toString() + "/" + (nowCalendar.get(Calendar.MONTH) + 1).toString() + "/" + nowCalendar.get(Calendar.DAY_OF_MONTH).toString()
            // Listを呼ぶ
            val manager = parentFragmentManager
            val transaction = manager.beginTransaction()
            val listFragment = ListFragment()
            val bundle = Bundle()
            bundle.putString(EXTRA_TASK_DATESTR,dateStr)
            listFragment.arguments = bundle
            transaction.replace(R.id.nav_host_fragment_activity_main, listFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    private fun layoutStar(taskList: MutableList<Task>) {
        // カレンダーにタスクのマークを設定
        val events = ArrayList<EventDay>()
        taskList.forEach { task ->
            val event = Calendar.getInstance()
            event.time = task.date
            events.add(EventDay(event, R.drawable.orangestar))
        }
        calendarView.setEvents(events)
    }

    private fun reloadCalendarView() {
        // データを取得
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        uid?.let{
            val tasks = db.collection("tasks").whereEqualTo("uid", it)
            tasks.get()
                .addOnSuccessListener { documents ->
                    val taskList = documents.toObjects(Task::class.java)
                    // 星の表示
                    layoutStar(taskList)
                }
                .addOnFailureListener { exception ->
                    Log.w("TAG", "Error getting documents: ", exception)
                }
        }
    }

}