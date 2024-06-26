package com.example.phoneapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class FragmentB : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_b, container, false)

        val btnToMain: Button = view.findViewById(R.id.btnToMain)
        val btnToFragmentA: Button = view.findViewById(R.id.btnToFragmentA)

        btnToMain.setOnClickListener {
            (activity as MainActivity).replaceFragment(FragmentA())
        }

        btnToFragmentA.setOnClickListener {
            (activity as MainActivity).replaceFragment(FragmentA())
        }

        return view
    }
}
