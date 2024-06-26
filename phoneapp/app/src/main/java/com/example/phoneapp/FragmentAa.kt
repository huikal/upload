package com.example.phoneapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast

class FragmentAa : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_aa, container, false)

        val btnToMain: Button = view.findViewById(R.id.btnToMain)

        btnToMain.setOnClickListener {
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
