package com.example.todolist.ui.list

import android.app.AlertDialog
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
import com.example.todolist.ui.input.InputFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

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
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView = view.findViewById<ListView>(R.id.listView)
        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        mTaskAdapter = TaskAdapter( this@ListFragment)
        fab.setOnClickListener {
            Log.d("TAG","hello!")
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

        listView.setOnItemClickListener { parent, view, position, id ->
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
        // TODO ログインの確認後にリストを描画
        reloadListView()
    }

    private fun reloadListView() {
        // データを取得し、日付順にソート
        val db = FirebaseFirestore.getInstance()
        val tasks = db.collection("tasks")
        tasks.get()
            .addOnSuccessListener { documents ->
                val taskList = documents.toObjects(Task::class.java)
                mTaskAdapter.taskList = taskList
                // ListViewのアダプターに設定する
                listView.adapter = mTaskAdapter
                // アダプターにデータの変更を通知する
                mTaskAdapter.notifyDataSetChanged()
                for (document in documents) {
                    Log.d("TAG", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
            }
    }

    private fun deleteTask(id: Int) {
        // タスクの削除
        val db = FirebaseFirestore.getInstance()
        // TODO
        val ref = db.collection("tasks").document("uid" + id.toString())
        ref.delete()
            .addOnSuccessListener {
                Log.d("TAG","DeleteSuccess")
            }
            .addOnFailureListener{
                Log.d("TAG","DeleteFailure")
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}