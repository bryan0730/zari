package com.example.hustarmobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

// 사용자 화면 메인 페이지
public class UserMainActivity extends AppCompatActivity {

    // Debug Variable
    private static final String     TAG     = "UserMain";
    private static final boolean    D       = true;

    // View Variable
    private EditText                searchEditText; // 가게 검색 창

    // Member Variable
    private Intent                  intent1;
    private Intent                  intent2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_main_page); // user_main_layout
        if (D) Log.i(TAG, "onCreate()");
        init();
    }

    public void init() {
        searchEditText = findViewById(R.id.searchEditTextTest);
        if (D) Log.i(TAG, "init()");
    }

    // 검색 창에 가게명 입력 후, 검색 버튼 클릭 시
    public void onClickSearchStoreList(View v) {
        String inputStoreName   = searchEditText.getText().toString();
        intent1                 = new Intent(this, com.example.myapplication.UserSearchStoreIntroduceActivity.class);

        intent1.putExtra("searchStoreName", inputStoreName);
        startActivity(intent1);
        if (D) Log.i(TAG, "SearchStoreList()");
    }

    // 하단의 "등록된 가게 현황" 버튼 클릭 시
    public void onClickShowAllStore(View v) {
        intent2 = new Intent(com.example.myapplication.UserMainActivity.this, com.example.myapplication.UserAllStoreIntroduceActivity.class);
        startActivity(intent2);
        if (D) Log.i(TAG, "showAllStore()");
    }
}
