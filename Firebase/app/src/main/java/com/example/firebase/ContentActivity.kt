package com.example.firebase

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.firebase.databinding.ActivityContentBinding
import com.google.firebase.firestore.FirebaseFirestore

class ContentActivity : AppCompatActivity() {
    lateinit var binding: ActivityContentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val docId = intent.getStringExtra("docId")
        val currentUserEmail = MyApplication.auth.currentUser?.email // 현재 사용자 이메일 가져오기

        if (docId != null) {
            val imgRef = MyApplication.storage.reference.child("images/$docId.jpg")

            imgRef.getDownloadUrl().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Glide.with(this)
                        .load(task.result)
                        .into(binding.storeImage)
                } else {
                    Toast.makeText(this, "이미지를 로드하는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("news").document(docId)

            docRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    binding.blogContentName.text = document.getString("content")
                    binding.blogContent.text = document.getString("article")
                    binding.storeEmail.text = document.getString("email")
                    binding.storeRegion.text = document.getString("region")
                    binding.storeCategory.text = document.getString("category")
                    val price = document.getString("price")
                    if (price.isNullOrEmpty()) {
                        binding.storePrice.visibility = View.GONE
                        binding.buyButton.visibility = View.GONE
                    } else {
                        binding.storePrice.text = "$price 원"
                        binding.storePrice.visibility = View.VISIBLE
                        binding.buyButton.visibility = View.VISIBLE
                    }

                    binding.storeMeet.text = document.getString("meet")
                    binding.storeDelivery.text = document.getString("delivery")

                    // 현재 사용자가 작성자인지 확인하여 삭제 버튼 가시성 설정
                    val itemOwnerEmail = document.getString("email")
                    if (currentUserEmail == itemOwnerEmail || currentUserEmail == "kyh0106855@gmail.com") {
                        binding.deleteButton.visibility = View.VISIBLE
                    } else {
                        binding.deleteButton.visibility = View.GONE
                    }

                    // 삭제 버튼 클릭 시 DelActivity로 이동
                    binding.deleteButton.setOnClickListener {
                        val intent = Intent(this, DelActivity::class.java).apply {
                            putExtra("docId", docId)
                        }
                        startActivity(intent)
                    }

                    val activityName = intent.getStringExtra("activityName") // Activity 이름 가져오기
                    // 'SecondActivity'이면 특정 View를 GONE 처리
                    if (activityName == "SecondActivity") {
                        binding.buyButton.visibility = View.GONE
                        binding.storePrice.visibility = View.GONE
                        binding.moreButton.visibility = View.GONE
                    }

                    // activityName에 따라 more_button의 텍스트 설정
                    binding.moreButton.text = when (activityName) {
                        "MainActivity" -> "판매자와 연락하기"
                        "SecondActivity" -> "더 보기"
                        else -> "기사 연결"
                    }

                    // 'more_button' 색상 변경 로직 및 링크 로직
                    val meetLink = binding.storeMeet.text.toString()
                    if (meetLink.isNotEmpty()) {
                        binding.moreButton.setBackgroundColor(
                            ContextCompat.getColor(
                                this,
                                R.color.colorAccent
                            )
                        ) // 색상 변경
                        binding.moreButton.setOnClickListener {
                            if (activityName == "MainActivity") {
                                // MainActivity에서만 인증을 요구
                                if (MyApplication.checkAuth()) {
                                    openLink(meetLink)
                                } else {
                                    Toast.makeText(this, "인증을 먼저 진행해 주세요", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                // 다른 Activity에서는 인증 없이 링크를 열기
                                openLink(meetLink)
                            }
                        }
                    } else {
                        binding.moreButton.setBackgroundColor(
                            ContextCompat.getColor(
                                this,
                                R.color.colorDisabled
                            )
                        ) // 기본 색상으로 변경
                        binding.moreButton.setOnClickListener {
                            Toast.makeText(this, "별도의 연결 링크가 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    // 'buyButton' 클릭 시 PayActivity로 데이터 전달하기
                    binding.buyButton.setOnClickListener {
                        if (MyApplication.checkAuth()) {
                            val intent = Intent(this, PayActivity::class.java).apply {
                                putExtra("productName", document.getString("content"))  // 제목을 productName으로 전달
                                putExtra("productPrice", price)  // 가격을 productPrice로 전달
                            }
                            startActivity(intent)
                            Toast.makeText(this, "가급적 판매자와 연락 후 구입을 해 주시길 바랍니다", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "인증을 먼저 진행해 주세요", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "데이터를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "데이터를 로드하는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "유효하지 않은 데이터입니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 링크 열기 함수
    private fun openLink(link: String) {
        try {
            val uri = Uri.parse(link)
            if (uri.scheme != null && uri.host != null) {
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            } else {
                Toast.makeText(this, "잘못된 링크입니다. 확인 후 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "링크를 열 수 없습니다. 링크를 확인해 주세요.", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}
