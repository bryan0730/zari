package com.example.hustarmobileapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class UserStoreDetailAdapter extends ArrayAdapter<UserAllStoreDetailData> {
    Context                                     context;
    int                                         resId;
    private ArrayList<UserAllStoreDetailData>   datas;

    public UserStoreDetailAdapter(@NonNull Context context, int resId, @NonNull ArrayList<UserAllStoreDetailData> datas) {
        super(context, resId, datas);
        this.context    = context;
        this.resId      = resId;
        this.datas      = datas;
    }


    @Override
    public int getCount() {
        return datas.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(resId, null);

        UserAllStoreDetailData data = datas.get(position);

        Button storeAllDetail = convertView.findViewById(R.id.storeAllDetail);
//        storeAllDetail.setTextColor(Color.WHITE);
//        storeAllDetail.setBackgroundColor(Color.BLACK);
        storeAllDetail.setText(data.getStoreName());
        storeAllDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserStoreDetailPageActivity.class);
                intent.putExtra("storeDetailName", data.getStoreName());
                intent.putExtra("storeDetailAddress", data.getStoreAddress());
                intent.putExtra("storeDetailIntroduce", data.getStoreIntroduce());
                intent.putExtra("storeDetailPhoneNumber", data.getStorePhoneNumber());
                v.getContext().startActivity(intent);
            }
        });

        return convertView;
    }
}
