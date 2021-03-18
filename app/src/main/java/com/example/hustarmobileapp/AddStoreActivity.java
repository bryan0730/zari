package com.example.hustarmobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
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

public class AddStoreActivity extends AppCompatActivity {
    private final String TAG = "AddStoreActivity";
    private Spinner storeCategorySpinner;
    private TextView cateTextView, addressTextView, storeAddressTextView;
    private EditText storeNameEditText, tableNumberEditText, storeNumberEdittText, storeDescriptEditText;


    private String mJsonString;
    private ArrayList<String> cateArrayList = new ArrayList<>();
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_store);
        Log.i(TAG, "init---------------start");
        init();
        Log.i(TAG, "init---------------end");
    }

    private void init() {
        Log.i(TAG, "init---------------first");
        storeCategorySpinner = findViewById(R.id.storeCategorySpinner);
        cateTextView = findViewById(R.id.cateTextView);
        GetData task = new GetData();
        task.execute("http://192.168.0.35/SelectCate.php", "");

        arrayAdapter = new ArrayAdapter<>(AddStoreActivity.this, android.R.layout.simple_spinner_item, cateArrayList);
        storeCategorySpinner.setAdapter(arrayAdapter);

        storeCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i(TAG,cateArrayList.get(i));
                cateTextView.setText(cateArrayList.get(i));
                Toast.makeText(AddStoreActivity.this, cateArrayList.get(i), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.i(TAG,"스피너 이벤트 리스너");
            }
        });
        Log.i(TAG, "init---------------last");
    }

    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(AddStoreActivity.this, "대기", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            if(result == ""){
                Toast.makeText(AddStoreActivity.this, "결과 없음", Toast.LENGTH_SHORT).show();
            }else{
                mJsonString = result;
                showResult();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = params[0];
            String postParameters = "parameter=" + params[1];

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
                Log.i(TAG, "CHECK RETURN VALUE ::::::::::::::::::::: " + sb.toString().trim());
                return sb.toString().trim();
            } catch (Exception e) {
                Log.d(TAG, "GteData: Error ", e);
                errorString = e.toString();
                return null;
            }
        }

            private void showResult(){
                String TAG_JSON = "zari";
                String TAG_CATE_NAME = "categoryName";
                try {
                    JSONObject jsonObject = new JSONObject(mJsonString);
                    JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
                    for(int i =0; i<jsonArray.length(); i++){
                        JSONObject item = jsonArray.getJSONObject(i);
                        String cateItem = item.getString(TAG_CATE_NAME);
                        cateArrayList.add(cateItem);
                        Log.i(TAG, cateArrayList.get(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }