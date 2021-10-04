package com.example.todolist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.todolist.databinding.ActivityMainBinding
import com.example.todolist.ui.login.LoginActivity
import com.example.todolist.ui.setting.SettingActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG1","MainActivity onCreate")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_list, R.id.navigation_calendar))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onStart() {
        super.onStart()


        Log.d("TAG1","MainActivity onStart")
        // ログイン済みか確認
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            // ログインしていない場合はログイン画面に遷移
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.menuSetting -> {
                val settingIntent = Intent(this@MainActivity, SettingActivity::class.java)
                startActivity(settingIntent)
            }
        }
        return true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // 背景タップ時にキーボードを閉じる
        val inputMethodManager : InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val focusView = currentFocus ?: return false
        inputMethodManager.hideSoftInputFromWindow(focusView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        return false
    }

    fun setActionBarTitle(title: String?) {
        supportActionBar?.title = title
    }
}