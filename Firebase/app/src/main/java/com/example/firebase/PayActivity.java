package com.example.firebase;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class PayActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private WebView webView;
    private Gson gson;
    MyWebViewClient myWebViewClient;
    private String tidPin;
    private String pgToken;
    private String productName;
    private String productPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        // Get product details from Intent
        Intent intent = getIntent();
        productName = intent.getStringExtra("productName");  // 'productName'으로부터 데이터 받기
        productPrice = intent.getStringExtra("productPrice");  // 'productPrice'으로부터 데이터 받기

        requestQueue = Volley.newRequestQueue(this);
        webView = findViewById(R.id.webView);
        gson = new Gson();

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());
        Log.d("Debug", "productName: " + productName);
        Log.d("Debug", "productPrice: " + productPrice);
        // productName과 productPrice가 null인지 확인
        if (productName == null || productPrice == null) {
            Log.e("Debug", "Product name or price is null. Please check the passed data.");
            return; // 값이 null이면 더 이상 진행하지 않음
        }
        // productName과 productPrice가 null이 아니면 진행
        // 초기화
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        myWebViewClient = new MyWebViewClient();
        webView = findViewById(R.id.webView);
        gson = new Gson();

        // 웹 뷰 설정
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(myWebViewClient);

        // 결제 요청 Http 통신 실행
        requestQueue.add(myWebViewClient.readyRequest);
    }

    public class MyWebViewClient extends WebViewClient {

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Debug", "Error : " + error);
            }
        };

        Response.Listener<String> readyResponse = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Debug", response);
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(response);

                String url = element.getAsJsonObject().get("next_redirect_mobile_url").getAsString();
                String tid = element.getAsJsonObject().get("tid").getAsString();
                Log.e("Debug", "url : " + url);
                Log.e("Debug", "tid : " + tid);

                webView.loadUrl(url);
                tidPin = tid;
            }
        };

        StringRequest readyRequest = new StringRequest(Request.Method.POST, "https://kapi.kakao.com/v1/payment/ready", readyResponse, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.e("Debug", "name : " + productName);
                Log.e("Debug", "price : " + productPrice);

                Map<String, String> params = new HashMap<>();
                params.put("cid", "TC0ONETIME"); // 가맹점 코드
                params.put("partner_order_id", "1001"); // 가맹점 주문 번호
                params.put("partner_user_id", "gorany"); // 가맹점 회원 아이디
                params.put("item_name", productName); // 상품 이름
                params.put("quantity", "1"); // 상품 수량
                params.put("total_amount", productPrice); // 상품 총액
                params.put("tax_free_amount", "0"); // 상품 비과세
                params.put("approval_url", "https://testing-5e038.web.app/success.html"); // 결제 성공시 돌려 받을 URL 주소
                params.put("cancel_url", "https://testing-5e038.web.app/cancle.html"); // 결제 취소시 돌려 받을 URL 주소
                params.put("fail_url", "https://testing-5e038.web.app/fail.html"); // 결제 실패시 돌려 받을 URL 주소
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "KakaoAK " + "96b8798ef3ced9a45e4880e129409071");
                return headers;
            }
        };

        Response.Listener<String> approvalResponse = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Debug", response);
            }
        };

        StringRequest approvalRequest = new StringRequest(Request.Method.POST, "https://kapi.kakao.com/v1/payment/approve", approvalResponse, errorListener) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("cid", "TC0ONETIME");
                params.put("tid", tidPin);
                params.put("partner_order_id", "1001");
                params.put("partner_user_id", "gorany");
                params.put("pg_token", pgToken);
                params.put("total_amount", productPrice);
                return params;

            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "KakaoAK " + "96b8798ef3ced9a45e4880e129409071");
                return headers;
            }
        };

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e("Debug", "url" + url);

            //// 카카오페이
            if (url.startsWith("intent://kakaopay/pg")) {

                Intent intent = null;

                try {

                    intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    Uri uri = Uri.parse(intent.getDataString());

                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    return true;

                } catch (URISyntaxException ex) {
                    return false;

                } catch (ActivityNotFoundException e) {
                    if (intent == null) return false;
                    //// 앱이 설치되어 있지 않으면 구글 플레이스토어로 연결함
                    String package_name = intent.getPackage();    //// intent.getPackage() 대신 해당 패키지명을 추가하면 됨

                    if (package_name != null) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + package_name)));
                        return true;

                    }
                }
            }
            return false;
        }
    }
}
