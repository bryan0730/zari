package com.example.hustarmobileapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class MenuOrderCustomDialog extends Dialog {
    private static String IP_ADDRESS="192.168.0.35";
    private final String TAG = "MenuOrderCustomDialog";

    private Context context;
    private RecyclerView menuRecyclerView, orderRecyclerView;
    private Button cancelButton, menuAddButton3, orderCalculateButton,posTableButton;
    private EditText orderPeopleEditText3;
    private TextView orderSalesTextView;

    private int tableSeq;
    private int storeSeq;
    private int usePeople;
    private String mJsonString;
    private String oJasonString;
    private ArrayList<MenuDomain> menuArray;
    private MenuAdapter menuAdapter;
    private int[] images = {R.drawable.facebook, R.drawable.instagram, R.drawable.kik, R.drawable.messenger, R.drawable.youtube, R.drawable.skype};


    private ArrayList<OrderDomain> orderArray = new ArrayList<>();
    private OrderAdapter orderAdapter;

    private ArrayList<OrderDomain> plusOrderArray = new ArrayList<>(); //이거는 주문버튼 누르면 arrayList 비워줘야함(주문에 메뉴 추가할때 담는 list)

    public MenuOrderCustomDialog(@NonNull Context context, int storeSeq, int tableSeq) {
        super(context);
        this.context = context;
        this.storeSeq = storeSeq;
        this.tableSeq = tableSeq;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.menu_order_dialog2);
        init();
    }


    private void init(){
        orderSalesTextView = findViewById(R.id.orderSalesTextView);
        cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        menuAddButton3 = findViewById(R.id.menuAddButton3);
        menuAddButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usePerson = orderPeopleEditText3.getText().toString();
                if(orderPeopleEditText3.getText().length() == 0){
                    showDialog("사용 인원을 입력해 주세요.");
                    return;
                }

                for(int i=0; i<plusOrderArray.size(); i++){
                    // 배열에 담긴 메뉴들 insert 해주기 (tableSeq, menuSeq, tableUsePerson 값 넣기)
                    String tSeq = Integer.toString(tableSeq);
                    String mSeq = Integer.toString(plusOrderArray.get(i).getMenuSeq());

                    InsertData insertData = new InsertData();
                    insertData.execute("http://"+IP_ADDRESS+"/insertorder.php", tSeq, mSeq, usePerson);

                }
                showDialog("정상적으로 주문되었습니다.");
                /*
                posTableButton = ((MasterPostActivity)context).findViewById(tableSeq);
                posTableButton.setBackgroundColor(Color.CYAN);
                posTableButton.setText(posTableButton.getText()+"\n"+usePerson+"명");
                 */
                UpdateData updateData = new UpdateData();
                updateData.execute("http://"+IP_ADDRESS+"/updatetablestatus.php",Integer.toString(tableSeq));
                ((MasterPostActivity)context).onResume();


            }
        });

        orderCalculateButton = findViewById(R.id.orderCalculateButton);
        orderCalculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderArray.clear();

                //데이터 베이스 delete
                DeleteOrder deleteOrder = new DeleteOrder();
                deleteOrder.execute("http://"+IP_ADDRESS+"/deleteorder.php", Integer.toString(tableSeq));

                UpdateData updateData = new UpdateData();
                updateData.execute("http://"+IP_ADDRESS+"/updatetablenostatus.php", Integer.toString(tableSeq));

                orderAdapter.notifyDataSetChanged();
                ((MasterPostActivity)context).onResume();
            }
        });

        orderPeopleEditText3 = findViewById(R.id.orderPeopleEditText3);

        menuRecyclerView = findViewById(R.id.menuRecyclerView);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        String sSeq = Integer.toString(storeSeq);
        menuArray = new ArrayList<>();
        menuAdapter = new MenuAdapter(context, menuArray, images);
        menuAdapter.setOnItemClickListener(new MenuAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int positon) {
                String mName = menuArray.get(positon).getMenuName();
                String mPrice = menuArray.get(positon).getMenuPrice();
                int mSeq = menuArray.get(positon).getMenuSeq();

                OrderDomain orderDomain = new OrderDomain();
                orderDomain.setTableSeq(tableSeq);
                orderDomain.setMenuSeq(mSeq);
                orderDomain.setMenuName(mName);
                orderDomain.setMenuPrice(mPrice);
                //orderDomain.setOrderPerson(Integer.parseInt(orderPerson));

                orderArray.add(orderDomain); //이거는 보여줄 Recyclerview에 담는거(기존에 db에 있던 주문이랑 함께 쌓임)
                plusOrderArray.add(orderDomain); //이거는 추가로 주문에 담는 메뉴 리스트 (담고 주문버튼 누를 때, 클리어 해줘야함)

                orderRecyclerView.setAdapter(orderAdapter);
                orderAdapter.notifyDataSetChanged();
                orderSalesTextView.setText(Integer.toString(orderAdapter.getTotalPrice()));
            }
        });
        menuRecyclerView.setAdapter(menuAdapter);


        orderRecyclerView = findViewById(R.id.orderRecylcerView);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        String tSeq = Integer.toString(tableSeq);
        //orderArray = new ArrayList<>();
        orderAdapter = new OrderAdapter(context, orderArray);
        orderAdapter.setOnItemClickListener(new OrderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int positon) {
                orderArray.remove(positon);
                orderAdapter.notifyDataSetChanged();
                orderSalesTextView.setText(Integer.toString(orderAdapter.getTotalPrice()));
            }
        });

        orderRecyclerView.setAdapter(orderAdapter);

        GetData task = new GetData();
        task.execute("http://"+IP_ADDRESS+"/menuselect.php",sSeq);

        GetOrderData task2 = new GetOrderData();
        task2.execute("http://"+IP_ADDRESS+"/orderselect.php",tSeq);
    }

    private void showDialog(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("ORDER").setMessage(msg);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }


    private class GetData extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "불러오는 중 . . ", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            if(result == null){
                Toast.makeText(context, errorString, Toast.LENGTH_SHORT).show();
                Log.i(TAG, "response - "+result);
            }else{
                mJsonString = result;
                showResult();
                //menuAdapter.notifyDataSetChanged();
                Log.i(TAG,"SHOW RESULT 끝");

                menuAdapter.notifyDataSetChanged();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = params[0];
            String postParameters = "storeSeq="+params[1] ;

            try{
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
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();
                Log.d(TAG, "DO in BackGround ::: "+sb.toString().trim());
                return sb.toString().trim();
            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }
        }
    }


    private class GetOrderData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "ORDER START");
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "대기 . . ", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i(TAG, "ORDER PostExecute - "+result);
            progressDialog.dismiss();
            if(result == null){
                Toast.makeText(context, errorString, Toast.LENGTH_SHORT).show();
                Log.i(TAG, "ORDER response - "+result);
            }else{
                oJasonString = result;
                showResult2();
                //menuAdapter.notifyDataSetChanged();
                Log.i(TAG,"ORDER SHOW RESULT 끝");
                orderPeopleEditText3.setText(Integer.toString(usePeople));
                orderSalesTextView.setText(Integer.toString(orderAdapter.getTotalPrice()));
                orderAdapter.notifyDataSetChanged();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            Log.i(TAG, "ORDER doInBackground : "+params[1]);
            String serverURL = params[0];
            String postParameters = "tableSeq="+params[1];

            try{
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
                Log.d(TAG, "ORDER response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString().trim();
            }catch (Exception e){
                Log.d(TAG, "ORDER SELECT: Error ", e);
                errorString = e.toString();
                return null;
            }
        }
    }
    private void showResult2(){
        Log.i(TAG, "showResult2 start");
        String TAG_JSON = "zari";
        String TAG_TABLE_SEQ = "tableSeq";
        String TAG_ORDER_SEQ = "orderSeq";
        String TAG_MNENU_NAME= "menuName";
        String TAG_MENU_PRICE = "menuPrice";
        String TAG_TABLE_PERSON = "tableUsePerson";
        int tableUsePerson = 0;

        try {
            JSONObject jsonObject = new JSONObject(oJasonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            Log.i(TAG, "ORDER JSON Array ::: "+jsonArray);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                Log.i(TAG, "ORDER JSON Object ::: "+item);
                String tableSeq = item.getString(TAG_TABLE_SEQ);
                String orderSeq = item.getString(TAG_ORDER_SEQ);
                String menuName = item.getString(TAG_MNENU_NAME);
                String menuPrice = item.getString(TAG_MENU_PRICE);
                tableUsePerson = item.getInt(TAG_TABLE_PERSON);

                OrderDomain orderDomain = new OrderDomain(Integer.parseInt(tableSeq), Integer.parseInt(orderSeq), menuName, menuPrice);
                orderArray.add(orderDomain);
                Log.i(TAG, "ORDER ARRAY에 추가::::::::");
                Log.i(TAG, orderArray.get(i).getMenuName());
                orderAdapter.notifyDataSetChanged();
            }
            usePeople = tableUsePerson;
            Log.d(TAG, "USEPERSON !!!!!!!!!!!!!!!!!!!"+usePeople);
        }catch (JSONException e){
            Log.d(TAG, "showResult : ", e);
        }
    }

    private void showResult(){
        String resultJSON="zari";
        String menuName="menuName";
        String menuPrice="menuPrice";
        String menuSeq = "menuSeq";

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(resultJSON);

            Log.i(TAG, "JSON Array ::: "+jsonArray);
            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);
                Log.i(TAG, "JSON Object ::: "+item);
                int seq = item.getInt(menuSeq);
                String name = item.getString(menuName);
                String price = item.getString(menuPrice);

                MenuDomain menuDomain = new MenuDomain(seq, name, price);

                menuArray.add(menuDomain);
                Log.i(TAG, "ARRAY에 추가::::::::");
                menuAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }

    class InsertData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "INSERT ORDER POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {
            String tSeq = params[1];
            String mSeq = params[2];
            String usePerson = params[3];
            String serverURL = (String)params[0];
            String postParameters = "tableSeq=" + tSeq + "&menuSeq=" + mSeq + "&usePerson=" + usePerson;

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
                Log.d(TAG, "INSERT ORDER POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();
                return sb.toString();
            } catch (Exception e) {
                Log.d(TAG, "Insert ORDER DATA: Error ", e);
                return new String("Error: " + e.getMessage());
            }
        }
    }

    class UpdateData extends AsyncTask<String, Void, String>{
        //사실 이거부터 만들어서 했으면 위에 주문테이블 select하는 쿼리 훨씬 쉬웠을 듯. 나중에 시간나면 바꾸던가
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "입력 중 . . ", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "UpdateData POST response  - " + result);
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = params[0];
            String tableSeq= params[1];

            String postParameters = "tableSeq="+tableSeq;
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
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString();
            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                return new String("Error: " + e.getMessage());
            }
        }
    }

    class DeleteOrder extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "계산 중 . .  ", null, true, true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = params[0];
            String tableSeq= params[1];

            String postParameters = "tableSeq="+tableSeq;
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
                Log.d(TAG, "POST DELETE response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString();
            } catch (Exception e) {
                Log.d(TAG, "Delete DATA: Error ", e);
                return new String("Error: " + e.getMessage());
            }
        }
    }
}
