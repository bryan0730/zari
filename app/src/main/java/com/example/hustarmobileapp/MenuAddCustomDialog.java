package com.example.hustarmobileapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MenuAddCustomDialog extends Dialog{
    private static String IP_ADDRESS="192.168.0.35";
    private final String TAG = "CustomDialog";

    private EditText menuCateEditText, menuNameEditText, menuPriceEditText;
    private Button menuAddButton, addMenuCancelButton;
    private int storeSeq;
    private Context context;

    //public CustomDialog(Context context, CustomDialogListener customDialogListener)
    public MenuAddCustomDialog(Context context, int storeSeq)
    {
        super(context);
        this.context = context;
        this.storeSeq = storeSeq;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.menu_add_dialog);

        menuCateEditText = findViewById(R.id.menuCateEditText);
        menuNameEditText = findViewById(R.id.menuNameEditText);
        menuPriceEditText = findViewById(R.id.menuPriceEditText);
        menuAddButton = findViewById(R.id.storeSalesOkbutton);
        menuAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "STORE SEQ : "+storeSeq);
                String storeSeq1 = Integer.toString(storeSeq);
                String menuName = menuNameEditText.getText().toString();
                String menuPrice = menuPriceEditText.getText().toString();
                String menuCategory = menuCateEditText.getText().toString();

                InsertData task = new InsertData();
                task.execute("http://"+IP_ADDRESS+"/menuinsert.php", storeSeq1, menuName, menuPrice,menuCategory);
                dismiss();
            }
        });
        addMenuCancelButton = findViewById(R.id.addMenuCancelButton);
        addMenuCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(context, "입력중 . . ", null, true, true);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            Log.d(TAG, "POST response - "+result);
        }

        @Override
        protected String doInBackground(String... params) {
            String storeSeq = params[1];
            String menuName = params[2];
            String menuPrice = params[3];
            String menuCateGory = params[4];
            String serverURL = params[0];

            String postParameter = "storeSeq="+storeSeq+"&menuName="+menuName+"&menuPrice="+menuPrice+"&menuCategory="+menuCateGory;

            try{
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();;

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameter.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response : "+responseStatusCode);

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
                Log.d(TAG, "InsertData : Error ", e);
                return new String("Error: " + e.getMessage());
            }
        }
    }
}

