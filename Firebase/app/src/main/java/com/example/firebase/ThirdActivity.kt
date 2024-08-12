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
import com.example.firebase.databinding.ActivityThirdBinding
import com.example.firebase.model.ItemData
import com.example.firebase.recycler.MyAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ThirdActivity : AppCompatActivity() {
    private lateinit var binding: ActivityThirdBinding
    private lateinit var currentUserEmail: String
    private var selectedRegion: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThirdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.addFab.setOnClickListener {
            if (MyApplication.checkAuth()) {
                val intent = Intent(this, AddActivity::class.java)
                intent.putExtra("activityName", "ThirdActivity")
                startActivity(intent)
            } else {
                Toast.makeText(this, "인증을 먼저 진행해 주세요", Toast.LENGTH_SHORT).show()
            }
        }

        var areButtonsVisible = true

        binding.selectBtn.setOnClickListener {
            if (areButtonsVisible) {
                binding.Seoul.visibility = View.GONE
                binding.Busan.visibility = View.GONE
                binding.Ulsan.visibility = View.GONE
                binding.Gyeongnam.visibility = View.GONE
                binding.Jeju.visibility = View.GONE
            } else {
                binding.Seoul.visibility = View.VISIBLE
                binding.Busan.visibility = View.VISIBLE
                binding.Ulsan.visibility = View.VISIBLE
                binding.Gyeongnam.visibility = View.VISIBLE
                binding.Jeju.visibility = View.VISIBLE
            }
            areButtonsVisible = !areButtonsVisible
        }

        val regionButtons = listOf(
            binding.Seoul to "서울",
            binding.Busan to "부산",
            binding.Ulsan to "울산",
            binding.Gyeongnam to "경남",
            binding.Jeju to "제주"
        )

        for ((button, region) in regionButtons) {
            button.setOnClickListener {
                selectedRegion = region
                setupRecyclerView()
            }
        }


        currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""

        val bottomNavigationView: BottomNavigationView = binding.bottomNavigation
        bottomNavigationView.selectedItemId = R.id.nav_third

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }

                R.id.nav_second -> {
                    startActivity(Intent(this, SecondActivity::class.java))
                    finish()
                    true
                }

                R.id.nav_third -> {
                    true
                }

                else -> false
            }
        }

        setupRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        startActivity(Intent(this, AuthActivity::class.java))
        return super.onOptionsItemSelected(item)
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

    private fun setupRecyclerView() {
        val recyclerView = binding.mainRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        var query: Query = FirebaseFirestore.getInstance().collection("news")
            .orderBy("date",Query.Direction.DESCENDING) //날짜, 내림차순
            .whereEqualTo("activityName", "ThirdActivity") //필터

        selectedRegion?.let {
            query = query.whereEqualTo("region", it) // 지역 단위 필터
        }

        query.get()
            .addOnSuccessListener { result ->
                val itemList = mutableListOf<ItemData>()
                for (document in result) {
                    val item = document.toObject(ItemData::class.java)
                    item.docId = document.id
                    itemList.add(item)
                }
                recyclerView.adapter = MyAdapter(this, itemList, currentUserEmail)
                recyclerView.visibility = View.VISIBLE
                binding.logoutTextView.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                Log.d("ThirdActivity", "Error getting documents: $exception")
                Toast.makeText(this, "서버로부터 데이터 획득에 실패했습니다.", Toast.LENGTH_SHORT).show()
                recyclerView.visibility = View.GONE
                binding.logoutTextView.visibility = View.VISIBLE
            }
    }
    }

