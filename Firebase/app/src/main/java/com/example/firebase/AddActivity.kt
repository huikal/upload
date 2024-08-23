package com.example.firebase

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.firebase.databinding.ActivityAddBinding
import com.example.firebase.util.dateToString
import com.google.firebase.storage.StorageReference
import java.io.File
import java.util.Date

class AddActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddBinding
    lateinit var filePath: String
    lateinit var activityName: String  // 활동 이름을 저장할 필드
    var selectedRegion: String? = null // 선택된 지역을 저장할 필드
    var selectedCategory: String? = null // 선택된 카테고리를 저장할 필드
    var selectedDelivery: String? = null // 선택된 배송 방법을 저장할 필드


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Intent로부터 activityName 값을 가져옴
        activityName = intent.getStringExtra("activityName") ?: "UnknownActivity"
        // activityName에 따라 EditText의 hint 설정
        setupEditTextHints()

        // MainActivity에서 넘어온 경우 가격입력부를 보이게 함
        if (activityName == "MainActivity") {
            binding.categorySpinner.visibility = View.VISIBLE
            setupCategorySpinner()
            binding.regionSpinner.visibility = View.VISIBLE
            setupRegionSpinner()
            binding.goodsPrice.visibility = View.VISIBLE
            binding.radioGroup.visibility = View.VISIBLE
            binding.addMeetView.visibility = View.VISIBLE

            // RadioGroup의 선택된 값을 설정하는 리스너 추가
            binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
                val radioButton = findViewById<RadioButton>(checkedId)
                selectedDelivery = radioButton.text.toString()}

        }

        // ThirdActivity에서 넘어온 경우 지역 선택 스피너, 페이지 추가(필수 아님) 를 보이게 함
        if (activityName == "ThirdActivity") {
            binding.regionSpinner.visibility = View.VISIBLE
            setupRegionSpinner()
            binding.addMeetView.visibility = View.VISIBLE
        }
    }

    private fun setupEditTextHints() {
        when (activityName) {
            "MainActivity" -> {
                binding.addEditView.hint = "상품명을 입력하세요. (18자 이내)"
                binding.addArticleView.hint = "상세 내용을 입력하세요."
                binding.goodsPrice.hint = "판매하고자 하는 가격을 입력하세요"
                binding.addMeetView.hint = "연락 받으실 오픈카톡의 주소를 입력해주세요"
            }
            "ThirdActivity" -> {
                binding.addEditView.hint = "제목을 입력하세요. (18자 이내)"
                binding.addArticleView.hint = "상세 내용을 입력하세요."
                binding.goodsPrice.hint = "필수 입력 사항이 아닙니다."
                binding.addMeetView.hint = "추가정보 웹페이지를 입력해주세요. (선택)"
            }
            else -> {
                binding.addEditView.hint = "제목을 입력하세요. (18자 이내)"
                binding.addArticleView.hint = "상세 내용을 입력하세요."
                binding.goodsPrice.hint = "필수 입력 사항이 아닙니다."
                binding.addMeetView.hint = "추가정보 웹페이지를 입력해주세요. (선택)"
            }
        }
    }


    private fun setupCategorySpinner() {
        val category = arrayOf("자재", "굿즈")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, category)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.categorySpinner.adapter = adapter

        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategory = category[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedCategory = null
            }
        }
    }

    private fun setupRegionSpinner() {
        val regions = arrayOf("서울", "부산", "울산", "경남", "제주")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, regions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.regionSpinner.adapter = adapter

        binding.regionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedRegion = regions[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedRegion = null
            }
        }
    }

    val requestLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode === android.app.Activity.RESULT_OK) {
            Glide.with(applicationContext)
                .load(it.data?.data)
                .apply(RequestOptions().override(250, 200))
                .centerCrop()
                .into(binding.addImageView)

            val cursor = contentResolver.query(it.data?.data as Uri,
                arrayOf(MediaStore.Images.Media.DATA), null, null, null)
            cursor?.moveToFirst().let {
                filePath = cursor?.getString(0) as String
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId === R.id.menu_add_gallery) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            requestLauncher.launch(intent)
        } else if (item.itemId === R.id.menu_add_save) {
            if (binding.addImageView.drawable !== null &&
                binding.addEditView.text.isNotEmpty() &&
                binding.addArticleView.text.isNotEmpty() &&
                (activityName != "ThirdActivity" || selectedRegion != null)&&
                (activityName != "MainActivity" || selectedCategory != null)&&
                (activityName != "MainActivity" || selectedDelivery != null)&&
                (activityName != "MainActivity" || binding.goodsPrice.text.isNotEmpty())&&
                (activityName != "MainActivity" || binding.addMeetView.text.isNotEmpty())


                    ) {
                saveStore()
            } else {
                Toast.makeText(this, "데이터가 모두 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveStore() {
        val data = mapOf(
            "email" to MyApplication.email, // AUTO
            "content" to binding.addEditView.text.toString(), //All
            "article" to binding.addArticleView.text.toString(), //All
            "date" to dateToString(Date()), // AUTO
            "activityName" to activityName, // AUTO
            "region" to selectedRegion, // 선택된 지역을 데이터에 포함 MAIN, THIRD
            "category" to selectedCategory, // 선택된 카테고리를 데이터에 포함
            "delivery" to selectedDelivery,// 선택된 배송 방법을 데이터에 포함
            "price" to binding.goodsPrice.text.toString(), // 가격을 데이터에 포함
            "meet" to binding.addMeetView.text.toString() // MAIN, THIRD

        )
        MyApplication.db.collection("news")
            .add(data)
            .addOnSuccessListener {
                uploadImage(it.id)
            }
            .addOnFailureListener {
                Log.w("AddActivity", "data save error", it)
            }
    }

    private fun uploadImage(docId: String) {
        val storage = MyApplication.storage
        val storageRef: StorageReference = storage.reference
        val imgRef: StorageReference = storageRef.child("images/${docId}.jpg")
        val file = Uri.fromFile(File(filePath))
        imgRef.putFile(file)
            .addOnFailureListener {
                Log.d("AddActivity", "failure: $it")
            }.addOnSuccessListener {
                Toast.makeText(this, "데이터가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
    }
}
