package com.example.firebase

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebase.databinding.ActivityMainBinding
import com.example.firebase.model.ItemData
import com.example.firebase.recycler.MyAdapter
import com.example.firebase.util.myCheckPermission
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    /////////// 현재 사용자 이메일 저장 변수 추가 ///////////
    private lateinit var currentUserEmail: String
    /////////// 현재 사용자 이메일 저장 변수 추가 ///////////

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        myCheckPermission(this)

        binding.addFab.setOnClickListener {
            if (MyApplication.checkAuth()) {
                val intent = Intent(this, AddActivity::class.java)
                intent.putExtra("activityName", "MainActivity")
                startActivity(intent)
            } else {
                Toast.makeText(this, "인증을 먼저 진행해 주세요", Toast.LENGTH_SHORT).show()
            }
        }



        /////////// 현재 사용자 이메일 가져오기 추가 ///////////
        currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
        /////////// 현재 사용자 이메일 가져오기 추가 ///////////

        // BottomNavigationView 설정
        val bottomNavigationView: BottomNavigationView = binding.bottomNavigation
        bottomNavigationView.selectedItemId = R.id.nav_home

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    true
                }
                R.id.nav_second -> {
                    startActivity(Intent(this, SecondActivity::class.java))
                    finish() // 현재 액티비티 종료
                    true
                }
                R.id.nav_third -> {
                    startActivity(Intent(this, ThirdActivity::class.java))
                    finish() // 현재 액티비티 종료
                    true
                }
                else -> false
            }
        }
    }

//    override fun onStart() {
//        super.onStart()
//        if (!MyApplication.checkAuth()) {
//            binding.logoutTextView.visibility = View.VISIBLE
//            binding.mainRecyclerView.visibility = View.GONE
//        } else {
//            binding.logoutTextView.visibility = View.GONE
//            binding.mainRecyclerView.visibility = View.VISIBLE
//            makeRecyclerView()
//        }
//    }

        override fun onStart() {
        super.onStart()
        if (!MyApplication.checkAuth()) {
            binding.logoutTextView.visibility = View.GONE
            binding.mainRecyclerView.visibility = View.VISIBLE
            makeRecyclerView()
        } else {
            binding.logoutTextView.visibility = View.GONE
            binding.mainRecyclerView.visibility = View.VISIBLE
            makeRecyclerView()
        }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        startActivity(Intent(this, AuthActivity::class.java))
        return super.onOptionsItemSelected(item)
    }

    private fun makeRecyclerView() {
        MyApplication.db.collection("news")
            .orderBy("date", Query.Direction.DESCENDING) // 날짜, 내림차순
            .whereEqualTo("activityName", "MainActivity") // 필터링 추가
            .get()
            .addOnSuccessListener { result ->
                val itemList = mutableListOf<ItemData>()
                for (document in result) {
                    val item = document.toObject(ItemData::class.java)
                    item.docId = document.id
                    itemList.add(item)
                }
                binding.mainRecyclerView.layoutManager = LinearLayoutManager(this)

                /////////// 어댑터에 현재 사용자 이메일 전달 추가 ///////////
                binding.mainRecyclerView.adapter = MyAdapter(this, itemList, currentUserEmail)
                /////////// 어댑터에 현재 사용자 이메일 전달 추가 ///////////

            }
            .addOnFailureListener { exception ->
                Log.d("kkang", "Error getting documents: ", exception)
                Toast.makeText(
                    this, "서버로부터 데이터 획득에 실패했습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
