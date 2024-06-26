package com.example.phoneapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment

class FragmentImage1 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_image1, container, false)
        val btnToFragmentA: Button = view.findViewById(R.id.btnToFragmentA)

        // Button click listener example, you can handle navigation or other actions here
        btnToFragmentA.setOnClickListener {
            (activity as MainActivity).replaceFragment(FragmentA())
        }

        val btnTemporary: Button = view.findViewById(R.id.btnTemporary)

        btnTemporary.setOnClickListener {
            // Show a toast message when the temporary button is clicked
            Toast.makeText(activity, "아직 미완성임", Toast.LENGTH_SHORT).show()
        }
        return view
    }


}
