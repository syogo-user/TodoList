package com.example.todolist.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.R
import com.example.todolist.ui.account.CreateAccountActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

const val EXTRA_EMAIL = "com.example.todolist.email"
const val EXTRA_PASSWORD = "com.example.todolist.password"

class LoginActivity: AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mLoginListener: OnCompleteListener<AuthResult>
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val createAccountButton = findViewById<Button>(R.id.createAccountButtonTrans)
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
            dismissKeyboard(v)
            val email = findViewById<EditText>(R.id.editTextTextEmail).text.toString()
            val password = findViewById<EditText>(R.id.editTextTextPassword).text.toString()

            // 入力チェック
            when {
                email.isEmpty() -> Snackbar.make(v, "メールアドレスを入力してください", Snackbar.LENGTH_LONG).show()
                password.isEmpty() -> Snackbar.make(v, "パスワードを入力してください", Snackbar.LENGTH_LONG).show()
                emailFormatCheck(email) -> Snackbar.make(v, "正しいメールアドレスを入力してください", Snackbar.LENGTH_LONG).show()
                lengthCheck(password, 6) -> Snackbar.make(v, "パスワードは6桁以上で入力してください", Snackbar.LENGTH_LONG).show()
                else -> login(email, password)
            }
        }

        createAccountButton.setOnClickListener{ v ->
            val email = findViewById<EditText>(R.id.editTextTextEmail).text.toString()
            val password = findViewById<EditText>(R.id.editTextTextPassword).text.toString()
            // アカウント作成画面へ遷移
            val createAccountIntent = Intent(this@LoginActivity, CreateAccountActivity::class.java)
            createAccountIntent.putExtra(EXTRA_EMAIL, email)
            createAccountIntent.putExtra(EXTRA_PASSWORD, password)
            startActivity(createAccountIntent)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // 背景タップ時にキーボードを閉じる
        val inputMethodManager : InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val focusView = currentFocus ?: return false
        inputMethodManager.hideSoftInputFromWindow(focusView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        return false
    }

    /* ログイン処理 */
    private fun login(email: String, password: String) {
        progressBar.visibility = View.VISIBLE
        // ログイン
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(mLoginListener)
    }

    /* メールアドレス形式チェック 不正な場合：true */
    private fun emailFormatCheck(email: String): Boolean {
        return !(Patterns.EMAIL_ADDRESS.matcher(email).matches())
    }

    /* 桁数チェック minLength桁以下の場合エラー：true */
    private fun lengthCheck(password: String, minLength: Int): Boolean {
        return password.length < minLength
    }

    /* キーボードを閉じる */
    private fun dismissKeyboard (view: View) {
        val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

}