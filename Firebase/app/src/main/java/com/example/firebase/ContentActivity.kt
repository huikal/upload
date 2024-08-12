package com.example.firebase

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.firebase.MyApplication
import com.example.firebase.databinding.ActivityContentBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.example.firebase.recycler.MyAdapter


class ContentActivity : AppCompatActivity() {
    lateinit var binding: ActivityContentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding= ActivityContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val docId = intent.getStringExtra("docId")

        if (docId != null) {
            val imgRef = MyApplication.storage.reference.child("images/$docId.jpg")

            imgRef.getDownloadUrl().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Glide를 사용하여 이미지를 ImageView에 로드
                    Glide.with(this)
                        .load(task.result)
                        .into(binding.storeImage)
                } else {
                    // 이미지 로드 실패 시 처리
                    Toast.makeText(this, "이미지를 로드하는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            // 데이터베이스에서 데이터 로드
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("news").document(docId)

            docRef.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // 데이터가 존재하면 데이터를 뷰에 설정
                    binding.blogContentName.text = document.getString("content")
                    binding.blogContent.text = document.getString("article")
                    binding.storeEmail.text = document.getString("email")





                } else {
                    // 문서가 존재하지 않으면 처리
                    Toast.makeText(this, "데이터를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                // 데이터 로드 실패 시 처리
                Toast.makeText(this, "데이터를 로드하는 데 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            // docId가 null인 경우 처리
            Toast.makeText(this, "유효하지 않은 데이터입니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
