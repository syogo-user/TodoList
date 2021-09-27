package com.example.todolist.ui.list

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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

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
//            val intent = Intent(this@ListActivity, InputActivity::class.java)
//            intent.putExtra(EXTRA_TASK_ID, mTaskAdapter.getMaxId())
//            startActivity(intent)
        }
        // TODO ログインの確認後
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





//        tasks.get().addOnCompleteListener(OnCompleteListener { it ->
//            if (it.isSuccessful()) {
//                it.result?.let {
//                    val taskList = it.toObjects(Task::class.java)
//                    mTaskAdapter.taskList = taskList
//                    // ListViewのアダプターに設定する
//                    listView.adapter = mTaskAdapter
//                    // アダプターにデータの変更を通知する
//                    mTaskAdapter.notifyDataSetChanged()
//                }
//            }
//        })
    }












//    private lateinit var homeViewModel: HomeViewModel
//    private var _binding: FragmentHomeBinding? = null
//
//    // This property is only valid between onCreateView and
//    // onDestroyView.
//    private val binding get() = _binding!!
//
//    override fun onCreateView(
//            inflater: LayoutInflater,
//            container: ViewGroup?,
//            savedInstanceState: Bundle?
//    ): View? {
//        homeViewModel =
//                ViewModelProvider(this).get(HomeViewModel::class.java)
//
//        _binding = FragmentHomeBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//
//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
//        return root
//    }

    override fun onDestroyView() {
        super.onDestroyView()
//        _binding = null
    }
}