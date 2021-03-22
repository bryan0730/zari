package com.example.hustarmobileapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

// 메인 페이지에서 "등록된 가게 현황" 버튼을 클릭 시, 등록된 가게명을 전부 보여주는 페이지로 이동
public class UserAllStoreIntroduceActivity extends AppCompatActivity {
    // Debug Variable
    private static final String                     TAG         = "UserAllStoreIntroduce";
    private static final boolean                    D           = true;

    // View Variable
    private ListView                                searchAllStoreListView;

    // Member Variable
    private ArrayList<UserAllStoreDetailData>       allStoreListData;
    private UserStoreDetailAdapter adapter;
    //private String                                  myJSON;
    private JSONArray                               emptyTables;
    private String                                  url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_all_store_page); // user_main_layout
        if (D) Log.i(TAG, "onCreate()");
        init();
        getData(url);
    }

    // 모든 가게 명을 가져옴
    public void init() {
        searchAllStoreListView  = findViewById(R.id.searchStoreListView);
        allStoreListData        = new ArrayList<>();
        url                     = "http://192.168.0.38/getStore.php";
        if (D) Log.i(TAG, "init()");
    }

    // 모든 가게명을 보여주는 ListView에 어댑터 적용
    public void setAdapter() {
        adapter = new UserStoreDetailAdapter(this, R.layout.user_all_store_list, allStoreListData);
        searchAllStoreListView.setAdapter(adapter);
        if (D) Log.i(TAG, "setAdapter()");
    }

    // DB를 통해 받아온 데이터를 바탕으로 남은 테이블 수 출력
    public void showList(String result) {
        try {
            Log.i("********************", result);
            JSONObject jsonObj = new JSONObject(result);
            emptyTables = jsonObj.getJSONArray(StringTagName.TAG_RESULT); // jsonobject가 배열 형태로 있음(모음)

            for(int i=0; i<emptyTables.length(); i++) {
                JSONObject c                = emptyTables.getJSONObject(i);
                String storeSeq             = c.getString("store_seq");
                String storeName            = c.getString("store_name");
                String storeAddress         = c.getString("store_address");
                String storeIntroduce       = c.getString("store_introduce");
                String storePhoneNumber     = c.getString("store_phone_number");
                String storeImage           = c.getString("store_image");

                UserAllStoreDetailData data = new UserAllStoreDetailData(storeSeq, storeName, storeAddress, storeIntroduce, storePhoneNumber, storeImage);
                allStoreListData.add(data);
            }
            //Log.i(TAG, "*****" + String.valueOf(emptyTables.length()));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(D) Log.i(TAG, "showList()");
    }

    // 모든 가게명을 가져옴
    public void getData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            // 쓰레드에 의해 처리될 내용을 담은 함수
            // params : execute 함수의 인자로 전달되는 값(...은 배열이라고 생각)
            // execute 함수에 의해 호출
            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                BufferedReader bufferedReader = null;
//
//                if(D)
//                    Log.i(TAG, "doInBackground()");

                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection)url.openConnection(); // 전달받은 url을 통해 연결
                    StringBuilder sb = new StringBuilder();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream())); // inputstream을 통해 php 실행 결과 가져옴(json)
                    String json;
                    while((json = bufferedReader.readLine()) != null) {
                        Log.i(TAG, "가져온 값 => " + json);
                        sb.append(json + "\n");
                    }
                    Log.i("**************", sb.toString().trim());
                    return sb.toString().trim(); // onPostExecute 함수로 넘겨주는 값
                } catch (Exception e) {
                    return null;
                }
            }
            // AsyncTask의 모든 작업이 완료된 후 가장 마지막에 호출(doInBackground 함수의 최종 값을 받기 위해)
            // result : json형태의 값(전체 뭉탱이)
            @Override
            protected void onPostExecute(String result) {
                if(D) {
                    Log.i(TAG, "onPostExecute()");
                }

                showList(result);
                setAdapter();
            }
        }
        if(D) Log.i(TAG, "getData()");
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }
}