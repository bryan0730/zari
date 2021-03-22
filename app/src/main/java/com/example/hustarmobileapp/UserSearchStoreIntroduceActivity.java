package com.example.hustarmobileapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

// 메인 페이지에서 가게 명 검색 후 검색 버튼 클릭 시, 가게명과 남은 테이블 수를 나타내는 페이지로 이동
public class UserSearchStoreIntroduceActivity extends AppCompatActivity {
    // Debug Variable
    private static final String             TAG                 = "SearchStoreIntroduce";
    private static final boolean            D                   = true;

    // View Variable
    private ListView                        serachStoreListView;

    // Member Variable
    private String                          searchStoreName;    // 검색했던 가게 이름
    private ArrayList<UserSearchStoreData>  searchStoreList;    // 검색한 가게 목록을 저장한 리스트
    private UserSearchStoreIntroduceAdapter adpater;            // ListView에 출력을 위한 어댑터
    private String                          emptyTableCount;    // 가게 내 남은 테이블 수
    private JSONArray                       emptyTables;
    private String                          allTableCount;    // 가게 내 남은 테이블 수
    private JSONArray                       allTables;
    private Intent                          intent;
    private String                          url;                // 남은 테이블 개수 가져오는 url
    private String                          url2;               // 해당 가게 전체 테이블 개수 가져오는 url

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_search_store_page); // user_main_layout
        if (D) Log.i(TAG, "onCreate()");
        init();
        getSearchStoreListData();
        getAllTableListData();
        // setSearchStoreList();
    }

    // 검색한 가게 이름을 가져옴(가게 이름을 검색했던 페이지로부터)
    public void init() {
        intent = getIntent();
        searchStoreName         = intent.getStringExtra("searchStoreName");
        serachStoreListView     = findViewById(R.id.searchStoreListView);
        searchStoreList         = new ArrayList<>();
        emptyTableCount         = "";
        url = "http://192.168.0.35/getEmptyTable.php";
        url2 = "http://192.168.0.35/getAllTable.php";

        if (D) Log.i(TAG, "init()");
    }

    // 통신을 통해 모든 테이블 수 데이터 가져오기
    public void getAllTableListData() {
        GetAllTableData task = new GetAllTableData();
        task.execute(url2, searchStoreName);

        if (D) Log.i(TAG, "getAllTableListData()");
    }


    // 통신을 통해 남은 테이블 수 데이터 가져오기
    public void getSearchStoreListData() {
        GetData task = new GetData();
        task.execute(url, searchStoreName);

        if (D) Log.i(TAG, "getSearchStoreListData()");
    }

    // 어댑터를 통해 리스트 뷰에 데이터 입력
    public void setSearchStoreList() {
        UserSearchStoreData data    = new UserSearchStoreData("image", searchStoreName, emptyTableCount + '/' + allTableCount);
        searchStoreList.add(data);

        adpater                     = new UserSearchStoreIntroduceAdapter(UserSearchStoreIntroduceActivity.this, R.layout.user_search_store_adapter, searchStoreList);
        serachStoreListView.setAdapter(adpater);

        if (D) Log.i(TAG, "setSearchStoreList()");
    }

    // DB를 통해 받아온 데이터를 바탕으로 모든 테이블 수 출력
    public void showAllTableList(String result) {
        try {
            JSONObject jsonObj  = new JSONObject(result);
            allTables         = jsonObj.getJSONArray(StringTagName.TAG_RESULT); // jsonobject가 배열 형태로 있음(모음)
            allTableCount     = emptyTables.getJSONObject(0).getString("all_table_count");
            //emptyTableCount     = String.valueOf(emptyTables.length());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(D) Log.i(TAG, "showList()");
    }

    // DB를 통해 받아온 데이터를 바탕으로 남은 테이블 수 출력
    public void showList(String result) {
        try {
            JSONObject jsonObj  = new JSONObject(result);
            emptyTables         = jsonObj.getJSONArray(StringTagName.TAG_RESULT); // jsonobject가 배열 형태로 있음(모음)
            emptyTableCount     = emptyTables.getJSONObject(0).getString("empty_table");
            //emptyTableCount     = String.valueOf(emptyTables.length());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(D) Log.i(TAG, "showList()");
    }

    // 클릭한 가게 내의 모든 테이블 개수 파악
    class GetAllTableData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String result) {
            //super.onPostExecute(result);
            showAllTableList(result);
            setSearchStoreList();
            Log.d(TAG, "POST response  - " + result);
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL            = (String)params[0];
            String selectedStoreName    = (String)params[1];

            String postParameters = "name=" + selectedStoreName;

            try {
                // 출력 스트림을 통해 해당 URL로 데이터 전달
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                // 응답을 받아서 해당 응답 별로 처리
                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK)
                    inputStream = httpURLConnection.getInputStream();
                else
                    inputStream = httpURLConnection.getErrorStream();


                // 입력 스트림을 열어 가져온 값을 추가
                InputStreamReader inputStreamReader     = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader           = new BufferedReader(inputStreamReader);

                StringBuilder sb                        = new StringBuilder();
                String line                             = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();

                return sb.toString().trim();
            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                return new String("Error: " + e.getMessage());
            }
        }
    }


    // 클릭한 가게 내의 남은 테이블 개수 파악
    class GetData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String result) {
            //super.onPostExecute(result);
            showList(result);
            Log.d(TAG, "POST response  - " + result);
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL            = (String)params[0];
            String selectedStoreName    = (String)params[1];

            String postParameters = "name=" + selectedStoreName;

            try {
                // 출력 스트림을 통해 해당 URL로 데이터 전달
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                // 응답을 받아서 해당 응답 별로 처리
                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK)
                    inputStream = httpURLConnection.getInputStream();
                else
                    inputStream = httpURLConnection.getErrorStream();


                // 입력 스트림을 열어 가져온 값을 추가
                InputStreamReader inputStreamReader     = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader           = new BufferedReader(inputStreamReader);

                StringBuilder sb                        = new StringBuilder();
                String line                             = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();

                return sb.toString().trim();
            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                return new String("Error: " + e.getMessage());
            }
        }
    }
}