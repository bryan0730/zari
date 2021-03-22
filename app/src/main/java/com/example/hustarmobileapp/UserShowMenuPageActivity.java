package com.example.hustarmobileapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.Nullable;
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

public class UserShowMenuPageActivity extends AppCompatActivity {

    // Debug Variable
    private static final boolean            D      = true;
    private static final String             TAG     = "UserShowMenuPage";

    // Member Variable
    private String                          storeName;
    private ArrayList<UserStoreMenuData>    datas;
    private JSONArray                       menuList;
    private String                          myJSON;
    private Intent                          intent;

    // View Variable
    private ListView                        searchAllMenuListView;
    private UserStoreMenuAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_menu_page);
        init();
        getMenuListData();
    }

    public void init() {
        intent                  = getIntent();
        storeName               = intent.getStringExtra("storeDetailName");
        searchAllMenuListView   = findViewById(R.id.searchStoreListView);
        datas                   = new ArrayList<>();
        if (D) Log.i(TAG, "init()");
    }

    // 통신을 통해 데이터 가져오기
    public void getMenuListData() {
        GetData task = new GetData();
        task.execute("http://192.168.0.35/getMenu.php", storeName);

        if (D) Log.i(TAG, "getMenuListData()");
    }

    // 메뉴 출력을 위한 어댑터 적용
    public void setMenuAdapter() {
        adapter = new UserStoreMenuAdapter(this, R.layout.user_store_menu, datas);
        searchAllMenuListView.setAdapter(adapter);
        if(D) Log.i(TAG, "setMenuAdapter()");
    }

    // DB를 통해 받아온 데이터를 바탕으로 남은 테이블 수 출력
    public void showList() throws JSONException {
        JSONObject jsonObj = new JSONObject(myJSON);
        menuList = jsonObj.getJSONArray(StringTagName.TAG_RESULT); // jsonobject가 배열 형태로 있음(모음)
        try {
            for(int i=0; i<menuList.length(); i++) {
                JSONObject c        = menuList.getJSONObject(i);
                String menuName     = c.getString("menu_name");
                String menuPrice    = c.getString("menu_price");
                String menuImage    = c.getString("menu_image");
                String menuCategory = c.getString("menu_category");

                UserStoreMenuData data  = new UserStoreMenuData(menuName, menuPrice, menuCategory);
                datas.add(data);
            }
            setMenuAdapter();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(D) Log.i(TAG, "showList()");
    }

    // 클릭한 가게 내의 남은 테이블 수 가져옴
    class GetData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String result) {
            //super.onPostExecute(result);
            myJSON = result;
            try {
                showList();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //setSearchStoreList();
            Log.d(TAG, "POST response  - " + result);
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = (String)params[0];
            String selectedStoreName = (String)params[1];

            String postParameters = "storeName=" + selectedStoreName;

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
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

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