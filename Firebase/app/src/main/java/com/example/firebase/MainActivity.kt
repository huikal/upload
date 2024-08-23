
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
import com.example.firebase.databinding.ActivityMainBinding
import com.example.firebase.model.ItemData
import com.example.firebase.recycler.MyAdapter
import com.example.firebase.util.SpaceItemDecoration
import com.example.firebase.util.myCheckPermission
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var adapter: MyAdapter
    lateinit var itemList: MutableList<ItemData>
    private lateinit var currentUserEmail: String

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

        currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""

        val bottomNavigationView: BottomNavigationView = binding.bottomNavigation
        bottomNavigationView.selectedItemId = R.id.nav_home

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_second -> {
                    startActivity(Intent(this, SecondActivity::class.java))
                    finish()
                    true
                }

                R.id.nav_third -> {
                    startActivity(Intent(this, ThirdActivity::class.java))
                    finish()
                    true
                }

                else -> false
            }
        }
    }

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
        val searchItem = menu?.findItem(R.id.menu_main_search)
        val searchView = searchItem?.actionView as androidx.appcompat.widget.SearchView
        searchView.queryHint = "검색어를 입력해 주세요"

        searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    makeRecyclerView(query)
                } else {
                    makeRecyclerView()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                makeRecyclerView(newText)
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        startActivity(Intent(this, AuthActivity::class.java))
        return super.onOptionsItemSelected(item)
    }


    private fun makeRecyclerView(searchQuery: String? = null) {

        // 마지막 검색어를 사용 (searchQuery 자체를 사용)
        val searchValue = searchQuery?.trim()  // 공백 제거

        // Firestore에서 모든 관련 문서 가져오기
        val dbQuery = MyApplication.db.collection("news")
            .orderBy("date", Query.Direction.DESCENDING)
            .whereEqualTo("activityName", "MainActivity")

        dbQuery.get()
            .addOnSuccessListener { result ->
                val itemList = mutableListOf<ItemData>()

                for (document in result) {
                    val item = document.toObject(ItemData::class.java)
                    item.docId = document.id

                    // content 필드가 searchValue를 포함하는지 확인
                    val content = item.content ?: ""
                    if (searchValue.isNullOrEmpty() || searchValue in content) {
                        itemList.add(item)
                    }
                }

                // RecyclerView 설정
                binding.mainRecyclerView.layoutManager = LinearLayoutManager(this)
                binding.mainRecyclerView.adapter = MyAdapter(this, itemList, currentUserEmail)

                // ItemDecoration 추가 - 항목 간의 간격을 설정
                val spaceHeight = 4 // 공백 크기 (픽셀 단위)
                binding.mainRecyclerView.addItemDecoration(SpaceItemDecoration(spaceHeight))

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
