package com.example.hustarmobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

// 등록된 모든 가게를 보여주는 화면에서 각 가게를 클릭 시 그 가게의 상세 정보를 보여주는 화면
public class UserStoreDetailPageActivity extends AppCompatActivity {

    // Member Variable
    private String storeDetailName;
    private String storeDetailAddress;
    private String storeDetailIntroduce;
    private String storeDetailPhoneNumber;

    // View Variable
    private TextView storeDetailNameTextView;
    private TextView storeDetailAddressTextView;
    private TextView storeDetailIntroduceTextView;
    private TextView storeDetailPhoneNumTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_store_detail_page); // user_main_layout
        init();
        getStoreDetailData();
        setStoreDetailData();
    }

    public void init() {
        storeDetailNameTextView         = findViewById(R.id.storeDetailNameTextView);
        storeDetailAddressTextView      = findViewById(R.id.storeDetailAddressTextView);
        storeDetailIntroduceTextView    = findViewById(R.id.storeDetailIntroduceTextView);
        storeDetailPhoneNumTextView     = findViewById(R.id.storeDetailPhoneNumTextView);
    }

    // 전체 가게를 나타내는 리스트 뷰에서 각 가게 버튼 클릭 시 전달되는 가게 상세 데이터를 받아옴
    public void getStoreDetailData() {
        Intent getIntent        = getIntent();
        storeDetailName         = getIntent.getStringExtra("storeDetailName");
        storeDetailAddress      = getIntent.getStringExtra("storeDetailAddress");
        storeDetailIntroduce    = getIntent.getStringExtra("storeDetailIntroduce");
        storeDetailPhoneNumber  = getIntent.getStringExtra("storeDetailPhoneNumber");
    }

    // 텍스트 뷰에 가게 상세 데이터를 표시
    public void setStoreDetailData() {
        storeDetailNameTextView.setText(storeDetailName);
        storeDetailAddressTextView.setText(storeDetailAddress);
        storeDetailIntroduceTextView.setText(storeDetailIntroduce);
        storeDetailPhoneNumTextView.setText(storeDetailPhoneNumber);
    }

    // 하단의 "메뉴보기" 버튼 클릭 시 해당 가게 내 저장된 메뉴 표시
    public void showMenuButton(View v) {
        Intent intent = new Intent(this, UserShowMenuPageActivity.class);
        intent.putExtra("storeDetailName", storeDetailName);
        v.getContext().startActivity(intent);
    }
}
