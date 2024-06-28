// DelActivity.kt
package com.example.firebase

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebase.databinding.ActivityDelBinding

class DelActivity : AppCompatActivity() {

    lateinit var binding: ActivityDelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val docId = intent.getStringExtra("docId") ?: return

        binding.deleteButton.setOnClickListener {
            deleteDocument(docId)
        }
    }

    private fun deleteDocument(docId: String) {
        // Firestore에서 삭제
        MyApplication.db.collection("news").document(docId)
            .delete()
            .addOnSuccessListener {
                // Storage에서 이미지 삭제
                val imgRef = MyApplication.storage.reference.child("images/$docId.jpg")
                imgRef.delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "데이터가 성공적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "이미지 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "데이터 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
    }
}
