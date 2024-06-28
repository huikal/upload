package com.example.firebase

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.multidex.MultiDexApplication
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebase.databinding.ActivityMainBinding
import com.example.firebase.databinding.ActivitySecondBinding
import com.example.firebase.model.ItemData
import com.example.firebase.recycler.MyAdapter
import com.example.firebase.util.myCheckPermission
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth


class SecondActivity : AppCompatActivity() {
    lateinit var binding: ActivitySecondBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // BottomNavigationView 설정
        val bottomNavigationView: BottomNavigationView = binding.bottomNavigation
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Home 선택 시 현재 액티비티에 대한 처리 (이미 MainActivity에 있음)
                    true
                }
                R.id.nav_add -> {
                    // 이미 SecondActivity에 있으므로 아무 작업 없이 true 반환
                    true
                }
                R.id.nav_del -> {
                    // ThirdActivity로 이동
                    startActivity(Intent(this, ThirdActivity::class.java))
                    true
                }
                else -> false
            }
        }

    }



}