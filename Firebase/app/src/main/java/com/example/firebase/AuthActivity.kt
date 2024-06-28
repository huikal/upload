package com.example.firebase

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.firebase.databinding.ActivityAuthBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider

class AuthActivity : AppCompatActivity() {
    lateinit var binding: ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        if(MyApplication.checkAuth()){
            changeVisibility("login")
        }else {
            changeVisibility("logout")
        }

        val requestLauncher = registerForActivityResult(
            //registerForActivityResult()는 ()안의 Activity 활동기록
            ActivityResultContracts.StartActivityForResult())
        //ActivityResultContracts.StartActivityForResult() 결과를 위한 activity실행 및 대기
        {
            //구글 로그인 결과 처리...........................

            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
// 이 줄은 인텐트 데이터에서 로그인된 구글 계정을 얻는 과정을 시작합니다.

            try {
                val account = task.getResult(ApiException::class.java)!!
                // 이 줄은 task에서 로그인된 계정 정보를 얻으려고 시도합니다.
                // 성공하면 'account' 변수에 계정을 할당합니다.
                // '!!' 연산자는 account가 null이 아님을 보장합니다.

                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                // 이 줄은 로그인된 계정의 ID 토큰을 사용하여 GoogleAuth 자격 증명을 만듭니다.

                MyApplication.auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
                    // 이 줄은 Google 자격 증명을 사용하여 Firebase에 로그인하고, 작업 완료를 처리하기 위한 리스너를 첨부합니다.

                    if (task.isSuccessful) {
                        // 로그인에 성공한 경우:

                        MyApplication.email = account.email
                        // 로그인된 사용자의 이메일을 애플리케이션의 전역 상태에 저장합니다.

                        changeVisibility("login")
                        // 사용자가 로그인했음을 나타내기 위해 UI 요소의 가시성을 변경합니다.

                    } else {
                        // 로그인에 실패한 경우:

                        changeVisibility("logout")
                        // 사용자가 로그아웃했음을 나타내기 위해 UI 요소의 가시성을 변경합니다.
                    }
                }
            } catch (e: ApiException) {
                changeVisibility("logout")
                // ApiException이 발생하면 (예: task가 계정을 얻지 못한 경우),
                // 사용자가 로그아웃했음을 나타내기 위해 UI 요소의 가시성을 변경합니다.
            }
        }

        binding.logoutBtn.setOnClickListener {
            //로그아웃...........
            MyApplication.auth.signOut()
            MyApplication.email = null
            changeVisibility("logout")
        }

        binding.goSignInBtn.setOnClickListener{
            changeVisibility("signin")
        }

        binding.googleLoginBtn.setOnClickListener {
            //구글 로그인....................
            val gso = GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            // 구글의 인증 관리 앱 실행
            val signInIntent = GoogleSignIn.getClient(this, gso).signInIntent
            requestLauncher.launch(signInIntent)
        }


        binding.signBtn.setOnClickListener {
            // 이메일, 비밀번호로 회원가입 시도
            val email: String = binding.authEmailEditView.text.toString()
            // 사용자가 입력한 이메일을 가져와 문자열로 변환합니다.
            val password: String = binding.authPasswordEditView.text.toString()
            // 사용자가 입력한 비밀번호를 가져와 문자열로 변환합니다.
            MyApplication.auth.createUserWithEmailAndPassword(email, password)
                // Firebase 인증을 사용하여 입력된 이메일과 비밀번호로 새로운 사용자를 생성하려고 시도합니다.
                .addOnCompleteListener(this) { task ->
                    // 회원가입 작업이 완료되면 실행되는 리스너를 추가합니다.
                    binding.authEmailEditView.text.clear()
                    // 회원가입 시도 후 이메일 입력 필드를 비웁니다.
                    binding.authPasswordEditView.text.clear()
                    // 회원가입 시도 후 비밀번호 입력 필드를 비웁니다.
                    if (task.isSuccessful) {
                        // 회원가입이 성공한 경우
                        MyApplication.auth.currentUser?.sendEmailVerification()
                            // 현재 사용자의 이메일로 인증 메일을 보냅니다.
                            ?.addOnCompleteListener { sendTask ->
                                // 이메일 인증 메일 보내기 작업이 완료되면 실행되는 리스너를 추가합니다.
                                if (sendTask.isSuccessful) {
                                    // 이메일 인증 메일 보내기가 성공한 경우
                                    Toast.makeText(baseContext,
                                        "회원가입에 성공하였습니다. 전송된 메일을 확인해 주세요.",
                                        Toast.LENGTH_SHORT).show()
                                    // "회원가입에 성공하였습니다. 전송된 메일을 확인해 주세요." 메시지를 표시합니다.
                                    changeVisibility("logout")
                                    // UI를 로그아웃 상태로 변경합니다.
                                } else {
                                    // 이메일 인증 메일 보내기가 실패한 경우
                                    Toast.makeText(baseContext, "메일 전송 실패",
                                        Toast.LENGTH_SHORT).show()
                                    // "메일 전송 실패" 메시지를 표시합니다.
                                    changeVisibility("logout")
                                    // UI를 로그아웃 상태로 변경합니다.
                                }
                            }
                    } else {
                        // 회원가입이 실패한 경우
                        Toast.makeText(baseContext, "회원가입 실패",
                            Toast.LENGTH_SHORT).show()
                        // "회원가입 실패" 메시지를 표시합니다.
                        changeVisibility("logout")
                        // UI를 로그아웃 상태로 변경합니다.
                    }
                }
        }

        binding.loginBtn.setOnClickListener {
            //이메일, 비밀번호 로그인.......................
            //이메일, 비밀번호 로그인.......................
            val email: String = binding.authEmailEditView.text.toString()
            val password: String = binding.authPasswordEditView.text.toString()
            MyApplication.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    binding.authEmailEditView.text.clear()
                    binding.authPasswordEditView.text.clear()
                    if (task.isSuccessful) {
                        if (MyApplication.checkAuth()) {
                            // 로그인 성공
                            MyApplication.email = email
                            changeVisibility("login")
                        } else {
            // 발송된 메일로 인증 확인을 안 한 경우
                            Toast.makeText(baseContext,
                                "전송된 메일로 이메일 인증이 되지 않았습니다.",
                                Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(baseContext, "로그인 실패",
                            Toast.LENGTH_SHORT).show()
                    }
                }

        }
    }

    fun changeVisibility(mode: String){
        if(mode === "login"){
            binding.run {
                authMainTextView.text = "${MyApplication.email} 님 반갑습니다."
                logoutBtn.visibility= View.VISIBLE
                goSignInBtn.visibility= View.GONE
                googleLoginBtn.visibility= View.GONE
                authEmailEditView.visibility= View.GONE
                authPasswordEditView.visibility= View.GONE
                signBtn.visibility= View.GONE
                loginBtn.visibility= View.GONE
            }

        }else if(mode === "logout"){
            binding.run {
                authMainTextView.text = "로그인 하거나 회원가입 해주세요."
                logoutBtn.visibility = View.GONE
                goSignInBtn.visibility = View.VISIBLE
                googleLoginBtn.visibility = View.VISIBLE
                authEmailEditView.visibility = View.VISIBLE
                authPasswordEditView.visibility = View.VISIBLE
                signBtn.visibility = View.GONE
                loginBtn.visibility = View.VISIBLE
            }
        }else if(mode === "signin"){
            binding.run {
                logoutBtn.visibility = View.GONE
                goSignInBtn.visibility = View.GONE
                googleLoginBtn.visibility = View.GONE
                authEmailEditView.visibility = View.VISIBLE
                authPasswordEditView.visibility = View.VISIBLE
                signBtn.visibility = View.VISIBLE
                loginBtn.visibility = View.GONE
            }
        }
    }
}