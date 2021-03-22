package com.example.hustarmobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

public class MasterPostActivity extends AppCompatActivity {
    private static String IP_ADDRESS = "192.168.0.35";
    private final String TAG = "MasterPosActivity";

    private String tableDiv;
    private int memberSeq;
    private int storeSeq;
    private int tableCount;
    private int tableSeq;

    private ArrayList<String> storeArrayList = new ArrayList<>();

    private String sJsonString;
    private String mJsonString;
    private ArrayList<Integer> tableArray = new ArrayList<>();

    private androidx.gridlayout.widget.GridLayout masterPosGridLayout;
    private Spinner storeNameSpinner;
    private EditText menuCateEditText, menuNameEditText, menuPriceEditText;
    private TextView addStoreTextView, salesTextView;

    private ArrayAdapter<String> arrayAdapter;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.master_pos);
        Intent intent = getIntent();
        tableCount = intent.getExtras().getInt("tableCount");
        storeSeq = intent.getExtras().getInt("storeSeq");
        Log.i(TAG, "yyyyyyyyyy : " + storeSeq);
        memberSeq = intent.getExtras().getInt("memberSeq");
        tableSeq = intent.getExtras().getInt("tableSeq");
        Toast.makeText(MasterPostActivity.this, "테이블SEQ : " + tableSeq + ", 테이블 수 : " + tableCount + " , 멤버 : " + memberSeq + " , 매장 : " + storeSeq, Toast.LENGTH_SHORT).show();

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        masterPosGridLayout.removeAllViews();
        //masterPosGridLayout.removeAllViewsInLayout();
        String sSeq = Integer.toString(storeSeq);
        Log.i(TAG, "여기 SEQ 없나?????????????????? "+sSeq);
        GetData task = new GetData();
        task.execute("http://" + IP_ADDRESS + "/selectordertable.php", sSeq);

    }

    private void init() {
        masterPosGridLayout = findViewById(R.id.masterPosGridLayout);
        storeNameSpinner = findViewById(R.id.storeNameSpinner);

        GetStoreData task = new GetStoreData();
        task.execute("http://"+IP_ADDRESS+"/selectstorelist.php", Integer.toString(memberSeq));

        arrayAdapter = new ArrayAdapter<>(MasterPostActivity.this, android.R.layout.simple_spinner_dropdown_item, storeArrayList);
        storeNameSpinner.setAdapter(arrayAdapter);

        addStoreTextView = findViewById(R.id.addStoreTextView);
        addStoreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MasterPostActivity.this);
                builder.setTitle("ADD STORE").setMessage("새로운 매장을 등록하시겠습니까?");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent intent = new Intent(MasterPostActivity.this, AddStoreActivity.class);
                        intent.putExtra("seq", memberSeq);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });
        salesTextView = findViewById(R.id.salesTextView);
        salesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StoreSalesDialog storeSalesDialog = new StoreSalesDialog(MasterPostActivity.this);
                storeSalesDialog.show();
            }
        });

    }

    public void createTable() {
        Log.i(TAG, "주문 테이블 개수 배열 : "+tableArray.size());
        Log.i(TAG, "STORE??? " + storeSeq);
        Log.i(TAG, "TABLE DIV = "+tableDiv);
        for (int i = tableSeq; i < tableSeq + tableCount; i++) {

            //abcd : tableSeq = 6 , tableCount = 6
            //final Button button = new Button(getApplicationContext());
            button = new Button(getApplicationContext());
            int tSeq = i;
            String btnText = (i - (tableSeq - 1)) + "번 테이블";
            button.setText(btnText);
            button.setId(i);
            button.setWidth(300);
            button.setHeight(300);
            button.setTextColor(Color.BLACK);
            button.setBackgroundColor(Color.GRAY);

            if (tableArray.size() != 0) {
                if(!tableDiv.equals("no table")) {
                    for (int j = 0; j < tableArray.size(); j++) {
                        Log.i(TAG, "TABLE ARRAY : " + tableArray.get(j));
                        if (tableArray.get(j) == button.getId()) {
                            button.setBackgroundColor(Color.BLACK);
                            button.setTextColor(Color.WHITE);
                        }
                    }
                }
            }

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //버튼 번호(table number도 같이 보내줘야 하나? 어차피 다이얼로그인데)
                    MenuOrderCustomDialog menuOrderCustomDialog = new MenuOrderCustomDialog(MasterPostActivity.this, storeSeq, tSeq);
                    menuOrderCustomDialog.show();

                    // Intent intent = new Intent(MasterPostActivity.this, TestUserActivity.class);
                    //startActivity(intent);
                }
            });
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            button.setLayoutParams(params);

            masterPosGridLayout.addView(button);
        }
    }

    public void goMenuDialog(View v) {
        MenuAddCustomDialog customDialog = new MenuAddCustomDialog(MasterPostActivity.this, storeSeq);
        customDialog.show();
    }

    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MasterPostActivity.this, "불러오는 중 . .", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);
            if (result == "no table") {
                Toast.makeText(MasterPostActivity.this, "result : " + result, Toast.LENGTH_SHORT).show();
            } else {
                tableDiv = result;
                mJsonString = result;
                showResult();
                Log.i(TAG, "show reuslt 끝나고 어레이 사이즈 : "+Integer.toString(tableArray.size()));
                createTable();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = params[0];
            String postParameters = "storeSeq=" + params[1];

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString().trim();

            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();
                return null;
            }
        }
    }

    private class GetStoreData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MasterPostActivity.this, "불러오는 중 . .", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);
            if (result == null) {
                Toast.makeText(MasterPostActivity.this, "result : " + result, Toast.LENGTH_SHORT).show();
            } else {
                sJsonString = result;

                showStoreResult();
                arrayAdapter.notifyDataSetChanged();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = params[0];
            String postParameters = "memberSeq=" + params[1];

            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString().trim();

            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();
                return null;
            }
        }
    }

    private void showResult() {
        String TAG_JSON = "zari";
        String TAG_TABLE_SEQ = "tableSeq";
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            tableArray.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);

                int tableSeq = item.getInt(TAG_TABLE_SEQ);

                tableArray.add(tableSeq);
            }
            Log.i(TAG, "SHOW RESULT 후 TABLE ARYY 0번지는? : "+tableArray.get(0));
            Log.i(TAG, "showResult 배열 사이즈 : "+tableArray.size());
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }

    private void showStoreResult(){
        String TAG_JSON = "zari";
        String TAG_STORE_SEQ = "storeSeq";
        String TAG_TABLE_CONT = "tableCount";
        String TAG_TABEL_SEQ = "tableSeq";
        String TAG_STORE_NAME = "storeName";

        try{
            JSONObject jsonObject = new JSONObject(sJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            for(int i =0; i<jsonArray.length(); i++){
                JSONObject item = jsonArray.getJSONObject(i);
                String storeItem = item.getString(TAG_STORE_NAME);
                storeArrayList.add(storeItem);
                Log.i(TAG, storeArrayList.get(i));
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}