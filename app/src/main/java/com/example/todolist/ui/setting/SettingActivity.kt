package com.example.todolist.ui.setting

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.R
import com.google.firebase.auth.FirebaseAuth

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            val alert = AlertDialog.Builder(this@SettingActivity)
            alert.setTitle("ログアウト")
            alert.setMessage("ログアウトしてもよろしいですか？")
            alert.setPositiveButton("OK") { _, _ ->
                logout()
            }
            alert.setNegativeButton("CANCEL", null)
            val dialog = alert.create()
            dialog.show()
        }
    }

    /* ログアウト */
    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        finish()
    }
}