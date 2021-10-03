package com.example.todolist.ui.list

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.todolist.R
import com.example.todolist.Task
import com.example.todolist.TaskAdapter
import com.example.todolist.ui.calendar.EXTRA_TASK_DATESTR
import com.example.todolist.ui.input.InputFragment
import com.example.todolist.ui.login.LoginActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

const val EXTRA_TASK = "com.example.todolist.TASK"
const val EXTRA_TASK_ID = "com.example.todolist.TASKID"

class ListFragment : Fragment() {
    private lateinit var mTaskAdapter: TaskAdapter
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.fragment_list, container, false)
        Log.d("TAG1","onCreateView")
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("TAG1","onViewCreated")
        listView = view.findViewById<ListView>(R.id.listView1)
        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        mTaskAdapter = TaskAdapter( this@ListFragment)
        fab.setOnClickListener {
            val manager = parentFragmentManager
            val transaction = manager.beginTransaction()
            val inputFragment = InputFragment()
            val bundle = Bundle()
            bundle.putInt(EXTRA_TASK_ID, mTaskAdapter.getMaxId())
            inputFragment.arguments = bundle
            transaction.replace(R.id.nav_host_fragment_activity_main, inputFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        listView.setOnItemLongClickListener { parent, view, postion, id ->
            // listViewを長押し
            // タスク削除
            val task = parent.adapter.getItem(postion) as Task
            val alert = AlertDialog.Builder(this@ListFragment.context)
            alert.setTitle("削除")
            alert.setMessage(task.title + "を削除しますか？")
            alert.setPositiveButton("OK") { _, _ ->
                deleteTask(mTaskAdapter.taskList[postion].id)
                reloadListView()
            }
            alert.setNegativeButton("CANCEL", null)
            val dialog = alert.create()
            dialog.show()
            true
        }

        listView.setOnItemClickListener { parent, _, position, id ->
            // listViewをタップ時
            val task = parent.adapter.getItem(position) as Task
            val manager = parentFragmentManager
            val transaction = manager.beginTransaction()
            val inputFragment = InputFragment()
            // タスクを渡ための準備
            val bundle = Bundle()
            bundle.putSerializable(EXTRA_TASK, task)
            // bundleとしてタスクを渡す
            inputFragment.arguments = bundle
            // フラグメントを更新
            transaction.replace(R.id.nav_host_fragment_activity_main, inputFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("TAG1","onStart")
        // リストを描画
        reloadListView()
    }

    private fun reloadListView() {
        // データを取得し、日付順にソート
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        // 遷移元のフラグメントから値を受け取る
        val taskBundle = this.arguments
        var filterDay = ""
        if (taskBundle != null) filterDay  = taskBundle.getString(EXTRA_TASK_DATESTR, "")
        uid?.let{
            // uidがnullではない
            val tasks = db.collection("tasks").whereEqualTo("uid", it)
            tasks.get()
                .addOnSuccessListener { documents ->
                    var taskList = documents.toObjects(Task::class.java)
                    if (filterDay.isNotEmpty()) {
                        // 日付が設定されている場合はカレンダー画面からの遷移のため、タスクを日付で絞る
                        taskList = taskList.filter {
                            val calendar = Calendar.getInstance()
                            calendar.time = it.date
                            val date = calendar.get(Calendar.YEAR).toString() + (calendar.get(Calendar.MONTH) + 1).toString()  + calendar.get(Calendar.DAY_OF_MONTH).toString()
                            date == filterDay
                        }
                    }
                    mTaskAdapter.taskList = taskList
                    // ListViewのアダプターに設定する
                    listView.adapter = mTaskAdapter
                    // アダプターにデータの変更を通知する
                    mTaskAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.w("TAG", "Error getting documents: ", exception)
                }
        }

    }

    private fun deleteTask(id: Int) {
        // タスクの削除
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        uid?.let {
            val ref = db.collection("tasks").document(uid + id.toString())
            ref.delete()
                .addOnSuccessListener {
                    Log.d("TAG","DeleteSuccess")
                }
                .addOnFailureListener{
                    Log.d("TAG","DeleteFailure")
                }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}