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
import com.example.firebase.databinding.ActivitySecondBinding
import com.example.firebase.model.ItemData
import com.example.firebase.recycler.MyAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class SecondActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySecondBinding

    /////////// 현재 사용자 이메일 저장 변수 추가 ///////////
    private lateinit var currentUserEmail: String
    /////////// 현재 사용자 이메일 저장 변수 추가 ///////////

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.addFab.setOnClickListener {
            if (MyApplication.checkAuth()) {
                val intent = Intent(this, AddActivity::class.java)
                intent.putExtra("activityName", "SecondActivity")
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

        // 현재 선택된 아이템이 nav_second인 경우 아무 동작도 하지 않음
        if (bottomNavigationView.selectedItemId != R.id.nav_second) {
            bottomNavigationView.selectedItemId = R.id.nav_second
        }

        // BottomNavigationView의 선택된 아이템 설정
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish() // 현재 액티비티 종료
                    true
                }
                R.id.nav_second -> {
                    // 이미 SecondActivity이므로 아무 동작도 하지 않음
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

        // RecyclerView 설정
        setupRecyclerView()
    }
    override fun onStart() {
        super.onStart()
        if (!MyApplication.checkAuth()) {
            binding.logoutTextView.visibility = View.VISIBLE
            binding.mainRecyclerView.visibility = View.GONE
        } else {
            binding.logoutTextView.visibility = View.GONE
            binding.mainRecyclerView.visibility = View.VISIBLE
            setupRecyclerView()
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

    private fun setupRecyclerView() {
        val recyclerView = binding.mainRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Firestore에서 데이터 가져오기
        FirebaseFirestore.getInstance().collection("news")
            .orderBy("date", Query.Direction.DESCENDING)
            .whereEqualTo("activityName", "SecondActivity") // 필터링 추가
            .get()
            .addOnSuccessListener { result ->
                val itemList = mutableListOf<ItemData>()
                for (document in result) {
                    val item = document.toObject(ItemData::class.java)
                    item.docId = document.id
                    itemList.add(item)
                }
                recyclerView.adapter = MyAdapter(this, itemList,currentUserEmail)
                recyclerView.visibility = View.VISIBLE
                binding.logoutTextView.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                Log.d("SecondActivity", "Error getting documents: $exception")
                Toast.makeText(this, "서버로부터 데이터 획득에 실패했습니다.", Toast.LENGTH_SHORT).show()
                recyclerView.visibility = View.GONE
                binding.logoutTextView.visibility = View.VISIBLE
            }
    }
}
