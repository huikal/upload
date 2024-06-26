package com.example.phoneapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences: SharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val phoneNumber = sharedPreferences.getString("phone_number", null)

        val tvPhoneNumber: TextView = findViewById(R.id.tvPhoneNumber)

        if (phoneNumber.isNullOrEmpty()) {
            val intent = Intent(this, PhoneNumberActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // Set phone number to TextView
            tvPhoneNumber.text = "반갑습니다! $phoneNumber"

            // Fragment A를 기본으로 표시
            replaceFragment(FragmentA())

            val btnFragmentA: Button = findViewById(R.id.btnFragmentA)
            val btnFragmentB: Button = findViewById(R.id.btnFragmentB)

            btnFragmentA.setOnClickListener {
                replaceFragment(FragmentA())
            }

            btnFragmentB.setOnClickListener {
                replaceFragment(FragmentB())
            }
        }
    }

    // Method should be public to be accessible from fragments
    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
