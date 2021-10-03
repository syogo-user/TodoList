package com.example.todolist.ui.account

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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.MainActivity
import com.example.todolist.R
import com.example.todolist.ui.login.EXTRA_EMAIL
import com.example.todolist.ui.login.EXTRA_PASSWORD
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class CreateAccountActivity: AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mCreateAccountListener: OnCompleteListener<AuthResult>
    private lateinit var mLoginListener: OnCompleteListener<AuthResult>
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        val createAccountButton = findViewById<Button>(R.id.createAccountButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)
        val email = findViewById<EditText>(R.id.editTextTextAccountEmail)
        val password1 = findViewById<EditText>(R.id.editTextTextAccountPassword1)
        val password2 = findViewById<EditText>(R.id.editTextTextAccountPassword2)
        val userName = findViewById<EditText>(R.id.editTextTextAccountUserName)
        progressBar = findViewById<ProgressBar>(R.id.progressBar2)
        // プログレスバーを非表示
        progressBar.visibility = View.GONE
        // FirebaseAuthのオブジェクトを取得する
        mAuth = FirebaseAuth.getInstance()

        // ログイン画面からの値を受け取る
        val intent = intent
        val receiveEmail = intent.getStringExtra(EXTRA_EMAIL)
        val receivePassword = intent.getStringExtra(EXTRA_PASSWORD)
        // テキストに反映する
        receiveEmail?.let {
            email.setText(it)
        }
        receivePassword?.let {
            password1.setText(it)
        }

        // アカウント作成リスナー
        mCreateAccountListener = OnCompleteListener {
            if (it.isSuccessful) {
                // 成功
                login(email.text.toString(), password1.text.toString())
            } else {
                // 失敗
                val view = findViewById<View>(android.R.id.content)
                Snackbar.make(view, "アカウント作成に失敗しました", Snackbar.LENGTH_LONG).show()
            }
            // プログレスバーを非表示
            progressBar.visibility = View.GONE
        }

        // ログインリスナー
        mLoginListener = OnCompleteListener { task ->
            if (task.isSuccessful) {
                // 成功
                Log.d("TAG","ログイン成功")
                // MainActivityに遷移してその上にスタックしているActivityを削除する
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            } else {
                // 失敗
                val view = findViewById<View>(android.R.id.content)
                Snackbar.make(view,"ログインに失敗しました", Snackbar.LENGTH_LONG).show()
            }
            // プログレスバー非表示
            progressBar.visibility = View.GONE
        }

        createAccountButton.setOnClickListener{
            dismissKeyboard(it)
            // 入力チェック
            when {
                email.text.toString().isEmpty() -> Snackbar.make(it, "メールアドレスを入力してください", Snackbar.LENGTH_LONG).show()
                password1.text.toString().isEmpty() -> Snackbar.make(it, "パスワードを入力してください", Snackbar.LENGTH_LONG).show()
                password2.text.toString().isEmpty() -> Snackbar.make(it, "パスワード（確認用）を入力してください", Snackbar.LENGTH_LONG).show()
                userName.text.toString().isEmpty() -> Snackbar.make(it, "名前を入力してください", Snackbar.LENGTH_LONG).show()
                emailFormatCheck(email.text.toString()) -> Snackbar.make(it, "正しいメールアドレスを入力してください", Snackbar.LENGTH_LONG).show()
                lengthCheck(password1.text.toString(), 6) -> Snackbar.make(it, "パスワードは6桁以上で入力してください", Snackbar.LENGTH_LONG).show()
                passwordEqualCheck(password1.text.toString(), password2.text.toString() ) -> Snackbar.make(it, "パスワードは２つとも同じものを入力してください", Snackbar.LENGTH_LONG).show()
                else -> createAccount(email.text.toString(), password1.text.toString())
            }
        }

        cancelButton.setOnClickListener { v ->
            dismissKeyboard(v)
            finish()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // 背景タップ時にキーボードを閉じる
        val inputMethodManager : InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val focusView = currentFocus ?: return false
        inputMethodManager.hideSoftInputFromWindow(focusView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        return false
    }

    /* アカウント作成処理 */
    private fun createAccount(email: String, password: String) {
        // プログレスバーを表示
        progressBar.visibility = View.VISIBLE
        // アカウント作成
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(mCreateAccountListener)
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

    /* パスワード一致チェック 一致しない場合：true */
    private fun passwordEqualCheck(password1: String, password2: String): Boolean {
        return password1 != password2
    }

    /* キーボードを閉じる */
    private fun dismissKeyboard (view: View) {
        val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        im.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }
}