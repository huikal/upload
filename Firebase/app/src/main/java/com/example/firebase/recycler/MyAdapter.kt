package com.example.firebase.recycler

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebase.DelActivity
import com.example.firebase.MyApplication
import com.example.firebase.databinding.ItemMainBinding
import com.example.firebase.model.ItemData


class MyViewHolder(val binding: ItemMainBinding) : RecyclerView.ViewHolder(binding.root){
    val deleteButton = binding.deleteButton
}

class MyAdapter(val context: Context, val itemList: MutableList<ItemData>): RecyclerView.Adapter<MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MyViewHolder(ItemMainBinding.inflate(layoutInflater))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = itemList.get(position)

        holder.binding.run {
            itemEmailView.text = data.email
            itemDateView.text = data.date
            itemContentView.text = data.content
        }

        //스토리지 이미지 다운로드........................
        val imgRef = MyApplication.storage.reference.child("images/${data.docId}.jpg")
        imgRef.getDownloadUrl().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Glide.with(context)
                    .load(task.result)
                    .into(holder.binding.itemImageView)
            }
        }

        holder.deleteButton.setOnClickListener {
            val intent = Intent(context, DelActivity::class.java).apply {
                putExtra("docId", data.docId)
            }
            context.startActivity(intent)
        }
    }
}

