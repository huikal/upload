package com.example.phoneapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class FragmentA : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_a, container, false)

        val btnToAa: Button = view.findViewById(R.id.btnToAa)
        btnToAa.setOnClickListener {
            (activity as MainActivity).replaceFragment(FragmentAa())
        }

        // 각 이미지뷰에 클릭 리스너를 설정합니다.
        val image1: ImageView = view.findViewById(R.id.image1)
        image1.setOnClickListener {
            (activity as MainActivity).replaceFragment(FragmentImage1())
        }



        return view
    }

}
