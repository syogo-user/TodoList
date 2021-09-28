package com.example.todolist.ui.input

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.todolist.R
import com.example.todolist.Task
import com.example.todolist.ui.list.EXTRA_TASK
import com.example.todolist.ui.list.EXTRA_TASK_ID
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*


class InputFragment : Fragment() {
    private var taskId = 0
    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private var mTask: Task? = null
    private lateinit var titleEditText :EditText
    private lateinit var contentEditText :EditText
    private lateinit var dateTextview: TextView

    private val mOnDateClickListener = View.OnClickListener {
        val datePickerDialog = DatePickerDialog( this.context as Context,
            { _, year, month, dayOfMonth ->
                mYear = year
                mMonth = month
                mDay = dayOfMonth
                val dateString: String = "$mYear/" + String.format(
                    "%02d",
                    mMonth + 1
                ) + "/" + String.format("%02d", mDay)
                dateTextview.text = dateString
            }, mYear, mMonth, mDay)

        datePickerDialog.show()
    }

    private val mOnDoneClickListener = View.OnClickListener {
        if (addTask(it)) {
//            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.fragment_input, container, false)
        titleEditText = view.findViewById<EditText>(R.id.title_edit_text)
        contentEditText = view.findViewById<EditText>(R.id.content_edit_text)
        dateTextview = view.findViewById<TextView>(R.id.dateTextView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 保存ボタン
        val doneButton = view.findViewById<Button>(R.id.done_button)
        doneButton.setOnClickListener(mOnDoneClickListener)

        // 日付ボタン
        val dateButton = view.findViewById<ImageButton>(R.id.dateButton)
        dateButton.setOnClickListener(mOnDateClickListener)

        // 遷移元のフラグメントから値を受け取る
        val taskBundle = arguments
        if (taskBundle != null) {
            mTask = taskBundle.getSerializable (EXTRA_TASK) as? Task
        }

        // 値の設定
        if (mTask === null ){
            // 新規
            taskId = taskBundle!!.getInt(EXTRA_TASK_ID, -1)
            val calendar = Calendar.getInstance()
            mYear = calendar.get(Calendar.YEAR)
            mMonth = calendar.get(Calendar.MONTH)
            mDay = calendar.get(Calendar.DAY_OF_MONTH)
        } else {
            // 更新
            taskId = mTask?.id ?: 0
            titleEditText.setText(mTask?.title)
            contentEditText.setText(mTask?.content)
            val calendar = Calendar.getInstance()
            calendar.time = mTask!!.date
            mYear = calendar.get(Calendar.YEAR)
            mMonth = calendar.get(Calendar.MONTH)
            mDay = calendar.get(Calendar.DAY_OF_MONTH)
        }
        val dateString: String = "$mYear/" + String.format(
            "%02d",
            mMonth + 1
        ) + "/" + String.format("%02d", mDay)
        dateTextview.text = dateString
    }

    private fun addTask(view: View): Boolean {
        if (emptyCheck(titleEditText.text.toString(), contentEditText.text.toString())) {
//            Snackbar.make(view, "タイトルとコンテンツは必須入力です", Snackbar.LENGTH_LONG).show()
//            Snackbar.make(view, "", Snackbar.LENGTH_LONG).show()
            return false
        }
        val calendar = GregorianCalendar(mYear, mMonth, mDay)
        var date = calendar.time
        val db = FirebaseFirestore.getInstance()
        val task = Task(taskId, titleEditText.text.toString(), contentEditText.text.toString(), date)

        db.collection("tasks")
            .document("uid" + taskId.toString()) // TODO uidをログインUIDとする
            .set(task)
            .addOnSuccessListener{
                Log.d("TAG","success")
            }
            .addOnFailureListener{ e ->
                Log.d("TAG",e.toString())
            }
        return true
    }

    // 入力チェック
    private fun emptyCheck(vararg str: String): Boolean {
        str.forEach {
            if (it.isEmpty()) {
                return true
            }
        }
        return false
    }
}