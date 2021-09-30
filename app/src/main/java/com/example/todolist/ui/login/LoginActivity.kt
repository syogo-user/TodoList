package com.example.todolist.ui.login

import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class LoginActivity: AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mLoginListener: OnCompleteListener<AuthResult>
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val loginButton = findViewById<Button>(R.id.loginButton)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.GONE

        // FirebaseAuthのオブジェクトを取得する
        mAuth = FirebaseAuth.getInstance()

        mLoginListener = OnCompleteListener { task ->
            if (task.isSuccessful) {
                // 成功
                Log.d("TAG","ログイン成功")
            } else {
                // 失敗
                val view = findViewById<View>(android.R.id.content)
                Snackbar.make(view,"ログインに失敗しました", Snackbar.LENGTH_LONG).show()
            }
            // プログレスバー非表示
            progressBar.visibility = View.GONE

            // 閉じる
            finish()
        }
        loginButton.setOnClickListener { v ->
            // キーボードを閉じる
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            val email = findViewById<EditText>(R.id.editTextTextEmail).text.toString()
            val password = findViewById<EditText>(R.id.editTextTextPassword).text.toString()

            // 入力チェック
            when {
                email.isEmpty() -> Snackbar.make(v, "メールアドレスを入力してください", Snackbar.LENGTH_LONG).show()
                password.isEmpty() -> Snackbar.make(v, "パスワードを入力してください", Snackbar.LENGTH_LONG).show()
                lengthCheck(password, 6) -> Snackbar.make(v, "パスワードは6桁以上で入力してください", Snackbar.LENGTH_LONG).show()
                else -> login(email, password)
            }
        }
    }

    private fun login(email: String, password: String) {
        progressBar.visibility = View.VISIBLE
        // ログイン
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(mLoginListener)
    }

    /* 桁数チェック minLength桁以下の場合エラー：true */
    private fun lengthCheck(password: String, minLength: Int): Boolean {
        return password.length < minLength
    }

}