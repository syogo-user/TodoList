package com.example.todolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(fragment: Fragment) : BaseAdapter() {
    private val mLayoutInflater: LayoutInflater = LayoutInflater.from(fragment.context)
    var taskList = mutableListOf<Task>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: mLayoutInflater.inflate(R.layout.list_item, null)
        val textViewTitle = view.findViewById<TextView>(R.id.textViewTitle)
        val textViewContent = view.findViewById<TextView>(R.id.textViewContent)
        val textViewDate = view.findViewById<TextView>(R.id.textViewDate)
        textViewTitle.text = taskList[position].title
        textViewContent.text = taskList[position].content
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.JAPANESE)
        val date = taskList[position].date
        textViewDate.text = simpleDateFormat.format(date)
        return view
    }

    override fun getItem(position: Int): Any {
        return taskList[position]
    }

    override fun getItemId(position: Int): Long {
        return taskList[position].id.toLong()
    }

    override fun getCount(): Int {
        return taskList.size
    }

    // IDの最大値を取得
    fun getMaxId(): Int {
        return if (taskList.size > 0) {
            taskList.maxOf { it.id }
        } else {
            -1
        }
    }
}