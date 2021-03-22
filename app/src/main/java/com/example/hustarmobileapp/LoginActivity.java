package com.example.hustarmobileapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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

public class LoginActivity extends AppCompatActivity {
    private static String IP_ADDRESS="192.168.0.35";
    private final String TAG = "LoginActivity";
    private EditText editTextId, editTextPw;
    private TextView testTextView, resultTextView;

    private String mJasonString;
    private int seq;
    private String name;
    private int auth;
    private int tableCount;
    private String storeSeq;
    private String tableSeq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        init();
    }

    private void init(){
        editTextId = findViewById(R.id.menuPriceEditText);
        editTextPw = findViewById(R.id.editTextPassword);

        testTextView = findViewById(R.id.testTextView);
        resultTextView = findViewById(R.id.resultTextView);
    }

    public void goSignUp(View v){
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    public void onClickLogin(View v){
        String id = editTextId.getText().toString();
        String pw = editTextPw.getText().toString();

        SecurityUtil securityUtil = new SecurityUtil();
        pw = securityUtil.encryptSHA256(pw);

        GetData task = new GetData();
        task.execute("http://"+IP_ADDRESS+"/select.php", id, pw);
    }

    private class GetData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;
        String errorString = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(LoginActivity.this, "로그인 중 . .", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "result - ::::::::::::::::::"+result);

            if (result == ""){

            }else {
                testTextView.setText("완료");
                mJasonString = result;
                showResult();
                Log.i(TAG, "NAEM :: "+name);
                if(name == null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("LOGIN").setMessage("ID/PW가 일치하지 않습니다.");
                    builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                    editTextId.setText("");
                    editTextPw.setText("");
                    return;
                }
                switch (auth){
                    case 0:
                        Log.i(TAG, "USER");
                        Intent userIntent = new Intent(LoginActivity.this, MainActivity.class);
                        userIntent.putExtra("seq", seq);
                        userIntent.putExtra("name", name);
                        userIntent.putExtra("auth", auth);
                        startActivity(userIntent);
                        finish();
                    case 1:
                        Log.i(TAG, "MASTER");
                        if(storeSeq.equals("null")) {
                            Log.i(TAG, "go to add_store.xml");
                            Intent addStoreIntent = new Intent(LoginActivity.this, AddStoreActivity.class);
                            addStoreIntent.putExtra("seq", seq);
                            addStoreIntent.putExtra("name", name);
                            addStoreIntent.putExtra("auth", auth);
                            startActivity(addStoreIntent);
                            finish();
                        }else{
                            Intent masterIntent = new Intent(LoginActivity.this, MasterPostActivity.class);
                            masterIntent.putExtra("memberSeq", seq);
                            masterIntent.putExtra("name", name);
                            masterIntent.putExtra("auth", auth);
                            masterIntent.putExtra("storeSeq", Integer.parseInt(storeSeq));
                            masterIntent.putExtra("tableCount", tableCount);
                            masterIntent.putExtra("tableSeq", Integer.parseInt(tableSeq));
                            startActivity(masterIntent);
                            finish();
                        }
                        if(this.getStatus() == AsyncTask.Status.RUNNING) {
                            this.cancel(true);
                            Log.i(TAG, "Login status - " + this.getStatus());
                        }
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = params[0];
            String idParameter = params[1];
            String pwParameter = params[2];

            String postParameter = "id="+idParameter+"&pw="+pwParameter;

            try{
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameter.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - "+ responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == httpURLConnection.HTTP_OK){
                    inputStream = httpURLConnection.getInputStream();
                }else{
                    inputStream = httpURLConnection.getErrorStream();
                }
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();
                Log.i(TAG, "CHECK RETURN VALUE ::::::::::::::::::::: " + sb.toString().trim());
                return sb.toString().trim();
            } catch(Exception e){
                Log.d(TAG, "GetDataError : Error ", e);
                return null;
            }
        }

        private void showResult(){
            String TAG_JSON = "zari";
            String TAG_SEQ = "seq";
            String TAG_AUTH = "authority";
            String TAG_NAME = "name";
            String TAG_STORE_SEQ = "have";
            String TAG_TABLE_COUNT = "tableCount";
            String TAG_TABLE_SEQ = "tableSeq";

            try {
                JSONObject jsonObject = new JSONObject(mJasonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
                JSONObject item = jsonArray.getJSONObject(0);

                seq = item.getInt(TAG_SEQ);
                name = item.getString(TAG_NAME);
                auth = item.getInt(TAG_AUTH);
                storeSeq = item.getString(TAG_STORE_SEQ);
                tableCount = item.getInt(TAG_TABLE_COUNT);
                tableSeq = Integer.toString(item.getInt(TAG_TABLE_SEQ));

                Log.i(TAG,name+", "+seq + ", "+auth + ", "+storeSeq);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
