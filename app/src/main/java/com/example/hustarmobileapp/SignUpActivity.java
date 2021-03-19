package com.example.hustarmobileapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignUpActivity extends AppCompatActivity {
    private final String MANAGER = "사업자";

    private EditText editTextTextId, editTextTextPassword, editTextTextPassword2, editTextTextPersonName, editTextPhone;
    private TextView birthTextView, checkTextView;
    private RadioGroup genderRadioGroup, serviceRadioGroup;

    private boolean checkPw = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        init();
    }

    private void init(){
        checkTextView = findViewById(R.id.pwCheckTextView);
        birthTextView = findViewById(R.id.birthTextView);
        editTextTextId = findViewById(R.id.editTextId);
        editTextTextPassword = findViewById(R.id.editTextPassword);
        editTextTextPassword2 = findViewById(R.id.editTextTextPassword2);
        serviceRadioGroup = findViewById(R.id.serviceRadioGroup);
        editTextTextPassword2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(editTextTextPassword.getText().toString().equals(editTextTextPassword2.getText().toString())){
                    checkTextView.setText("일치");
                    checkTextView.setTextColor(Color.GREEN);
                    checkPw = true;
                }else{
                    checkTextView.setText("불일치");
                    checkTextView.setTextColor(Color.RED);
                    checkPw = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        editTextTextPersonName = findViewById(R.id.editTextTextPersonName);
        editTextPhone = findViewById(R.id.editTextPhone);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            switch (requestCode){
                case 3000 :
                    String year = data.getExtras().getString("yy");
                    String month = data.getExtras().getString("mm");
                    String day = data.getExtras().getString("dd");
                    birthTextView.setText(year + "/" + month + "/" + day);
            }
        }
    }

    public void goBirthActivity(View v){
        Intent intent = new Intent(SignUpActivity.this, BirthActivity.class);
        startActivityForResult(intent, 3000);
    }

    public void onClickSignUp(View v){
        /*
        if (!checkPw || editTextTextPassword.getText().length()<0 || editTextPhone.getText().length()<0 ||
                editTextTextId.getText().length()<0 || birthTextView.getText() == "" || editTextTextPersonName.getText().length()<0 || !genderRadioGroup.isClickable()){
            Toast.makeText(SignUpActivity.this, "다시 확인해 주세요.", Toast.LENGTH_LONG).show();
            return;
        }

         */

        SecurityUtil securityUtil = new SecurityUtil();
        String name = editTextTextPersonName.getText().toString();
        String id = editTextTextId.getText().toString();
        String pw = editTextTextPassword.getText().toString();
        String enPw = securityUtil.encryptSHA256(pw);
        String phone = editTextPhone.getText().toString();
        String birth = birthTextView.getText().toString();
        String gender = ((RadioButton)findViewById(genderRadioGroup.getCheckedRadioButtonId())).getText().toString();

        String userAuth = ((RadioButton)findViewById(serviceRadioGroup.getCheckedRadioButtonId())).getText().toString();
        Log.i("SignUpActivity : ",userAuth);
        if(userAuth.equals(MANAGER)){
            userAuth = "1";
        } else{
            userAuth = "0";
        }

        InsertData task = new InsertData();
        task.execute("http://192.168.0.35/mtest.php", name, id, enPw, phone, birth, gender, userAuth);
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(SignUpActivity.this, "확인중..", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //resultTextView.setText(result);
            Log.d("SignUpActivity", "POST response - " + result);
        }

        @Override
        protected String doInBackground(String... params) {
            String name = (String) params[1];
            String id = (String) params[2];
            String pw = (String) params[3];
            String phone = (String) params[4];
            String birth = (String) params[5];
            String gender = (String) params[6];
            String authority = (String) params[7];
            String serverURL = (String) params[0];

            String postParameters = "name="+ name + "&id="+ id + "&pw="+pw +"&phone="+phone+"&birth="+birth+"&gender="+gender+"&authority="+authority;

            try {
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

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d("JoinActivity", "POST response cod - "+responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK){
                    inputStream = httpURLConnection.getInputStream();
                }else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();

                return  sb.toString();
            }catch (Exception e){
                Log.d("SignUpActivity", "InsertData : Error ", e);
                return new String("Error: " + e.getMessage());
            }
        }
    }

}
