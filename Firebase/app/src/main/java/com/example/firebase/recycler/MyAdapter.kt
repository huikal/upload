package com.example.firebase.recycler

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebase.ContentActivity
import com.example.firebase.DelActivity
import com.example.firebase.MyApplication
import com.example.firebase.databinding.ItemMainBinding
import com.example.firebase.model.ItemData

class MyViewHolder(val binding: ItemMainBinding) : RecyclerView.ViewHolder(binding.root) {
    val deleteButton = binding.deleteButton
}

class MyAdapter(
    val context: Context,
    val itemList: MutableList<ItemData>,
    /////////// 현재 사용자 이메일 추가 ///////////
    val currentUserEmail: String
    /////////// 현재 사용자 이메일 추가 ///////////
) : RecyclerView.Adapter<MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MyViewHolder(ItemMainBinding.inflate(layoutInflater))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = itemList[position]

        holder.binding.run {
            itemEmailView.text = data.email
            itemDateView.text = data.date
            itemContentView.text = data.content
            itemArticleView.text = data.article
            itemActivityView.text = data.activityName

            itemArticleView.visibility = View.GONE

        }

        holder.binding.itemData.setOnClickListener {
            val intent = Intent(context, ContentActivity::class.java)
                .apply {putExtra("docId",data.docId)  }
            context.startActivity(intent)
        }

        // 스토리지 이미지 다운로드
        val imgRef = MyApplication.storage.reference.child("images/${data.docId}.jpg")
        imgRef.getDownloadUrl().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Glide.with(context)
                    .load(task.result)
                    .into(holder.binding.itemImageView)
            }
        }

        /////////// 현재 사용자가 작성자인지 확인하여 삭제 버튼 가시성 설정 ///////////
//        if (data.email == currentUserEmail || currentUserEmail == "kyh0106855@gmail.com") {
//            임시로 어드민권한 주는 방식
        if (data.email == currentUserEmail || currentUserEmail == "kyh0106855@gmail.com") {
            holder.deleteButton.visibility = View.VISIBLE
        } else {
            holder.deleteButton.visibility = View.GONE
        }
        /////////// 현재 사용자가 작성자인지 확인하여 삭제 버튼 가시성 설정 ///////////

        holder.deleteButton.setOnClickListener {
            val intent = Intent(context, DelActivity::class.java).apply {
                putExtra("docId", data.docId)
            }
            context.startActivity(intent)
        }

    }
}
