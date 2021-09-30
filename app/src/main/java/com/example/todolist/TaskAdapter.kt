package com.example.todolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.todolist.ui.list.ListFragment
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(fragment: ListFragment) : BaseAdapter() {
    private val mLayoutInflater: LayoutInflater
    var taskList = mutableListOf<Task>()

    init {
        this.mLayoutInflater = LayoutInflater.from(fragment.context)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: mLayoutInflater.inflate(android.R.layout.simple_list_item_2, null)
        val textView1 = view.findViewById<TextView>(android.R.id.text1)
        val textView2 = view.findViewById<TextView>(android.R.id.text2)
        textView1.text = taskList[position].title

        var simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.JAPANESE)
        val date = taskList[position].date
        textView2.text = simpleDateFormat.format(date)
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