package com.example.firebase

import android.annotation.SuppressLint
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
        val searchItem = menu?.findItem(R.id.menu_main_search)
        val searchView = searchItem?.actionView as androidx.appcompat.widget.SearchView
        searchView.queryHint = "검색어를 입력해 주세요"

        searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    setupRecyclerView(query)
                } else {
                    setupRecyclerView()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                setupRecyclerView(newText)
                return true
            }
        })
        return true
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
            setupRecyclerView()
        } else {
            binding.logoutTextView.visibility = View.GONE
            binding.mainRecyclerView.visibility = View.VISIBLE
            setupRecyclerView()
        }
    }

    private fun setupRecyclerView(searchQuery: String? = null) {
        val recyclerView = binding.mainRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 기본 쿼리 설정: "ThirdActivity"에 해당하는 문서들만 선택
        var dbQuery: Query = MyApplication.db.collection("news")
            .whereEqualTo("activityName", "ThirdActivity")

        // 지역 필터 적용
        selectedRegion?.let { region ->
            dbQuery = dbQuery.whereEqualTo("region", region)
        }

        // 검색어 필터 적용
        if (!searchQuery.isNullOrEmpty()) {
            // searchQuery를 공백을 제외하고 문자 배열로 변환
            val filteredQuery = searchQuery.replace(" ", "")  // 공백 제거

            // 검색어 필터 적용 및 내림차순 정렬 추가
            dbQuery = dbQuery
                .orderBy("date", Query.Direction.DESCENDING)
        } else {
            // 검색어가 없는 경우: 날짜 순으로 정렬
            dbQuery = dbQuery.orderBy("date", Query.Direction.DESCENDING)
        }

        // 쿼리 실행
        dbQuery.get()
            .addOnSuccessListener { result ->
                val itemList = mutableListOf<ItemData>()

                for (document in result) {
                    val item = document.toObject(ItemData::class.java)
                    item.docId = document.id
                    val content = item.content ?: ""

                    // content 필드가 searchQuery 전체 문자열을 포함하는지 확인
                    val filteredQuery = searchQuery?.replace(" ", "")  // 공백 제거
                    if (filteredQuery.isNullOrEmpty() || content.contains(
                            filteredQuery,
                            ignoreCase = true
                        )
                    ) {
                        itemList.add(item)
                    }
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
