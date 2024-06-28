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
import com.example.firebase.model.ItemData
import com.example.firebase.recycler.MyAdapter
import com.example.firebase.util.myCheckPermission
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        myCheckPermission(this)
        binding.addFab.setOnClickListener {
            if (MyApplication.checkAuth()) {
                startActivity(Intent(this, AddActivity::class.java))
            } else {
                Toast.makeText(this, "인증을 먼저 진행해 주세요",
                    Toast.LENGTH_SHORT).show()
            }
        }

        // BottomNavigationView 설정
        val bottomNavigationView: BottomNavigationView = binding.bottomNavigation
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Home 선택 시 MainActivity로 이동
                    true
                }
                R.id.nav_add -> {
                    // Add 선택 시 SecondActivity로 이동
                    startActivity(Intent(this, SecondActivity::class.java))
                    true
                }
                R.id.nav_del -> {
                    // Del 선택 시 ThirdActivity로 이동
                    startActivity(Intent(this, ThirdActivity::class.java))

                    true
                }
                else -> false
            }
        }
    }



    override fun onStart() {
        super.onStart()
        if(!MyApplication.checkAuth()){
            binding.logoutTextView.visibility= View.VISIBLE
            binding.mainRecyclerView.visibility=View.GONE
        }else {
            binding.logoutTextView.visibility= View.GONE
            binding.mainRecyclerView.visibility=View.VISIBLE
            makeRecyclerView()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // menu_main.xml 파일을 인플레이트하여 메뉴 항목을 액션 바에 추가
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        startActivity(Intent(this, AuthActivity::class.java))
        return super.onOptionsItemSelected(item)
    }

    private fun makeRecyclerView(){
        // 컬렉션을 모두 가져오기
        MyApplication.db.collection("news")
            .get()
            .addOnSuccessListener { result ->
                val itemList = mutableListOf<ItemData>()
                for (document in result) {
                    val item = document.toObject(ItemData::class.java)
                    item.docId=document.id
                    itemList.add(item)
                }
                binding.mainRecyclerView.layoutManager= LinearLayoutManager(this)
                binding.mainRecyclerView.adapter= MyAdapter(this, itemList)
            }
            .addOnFailureListener { exception ->
                Log.d("kkang", "Error getting documents: ", exception)
                Toast.makeText(this, "서버로부터 데이터 획득에 실패했습니다.",
                    Toast.LENGTH_SHORT).show()
            }

    }
}