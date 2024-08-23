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
//    val deleteButton = binding.deleteButton
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

            // 가격이 0이거나 null이면 TextView를 숨김
            if (data.price.isNullOrEmpty()) {
                itemPriceView.visibility = View.GONE
            } else {
                // 가격 뒤에 "원" 추가하고 TextView를 표시
                itemPriceView.text = "${data.price} 원"
                itemPriceView.visibility = View.VISIBLE
            }
            itemActivityView.text = data.activityName

            itemArticleView.visibility = View.GONE

            itemRegionView.text = data.region
        }



        holder.binding.itemData.setOnClickListener {
            val intent = Intent(context, ContentActivity::class.java).apply {
                putExtra("docId", data.docId)         // docId 추가
                putExtra("activityName", data.activityName)  // activityName 추가
            }
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

    }
}
