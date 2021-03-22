package com.example.hustarmobileapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class UserSearchStoreIntroduceAdapter extends ArrayAdapter<UserSearchStoreData> {

    // Get Data
    private Context context;
    private int resId;
    private ArrayList<UserSearchStoreData> datas;

    // View variable
    ImageView storeImageTestImageView;
    TextView searchStoreNameTestTextView;
    TextView searchStoreEmptyTableTextView;

    public UserSearchStoreIntroduceAdapter(@NonNull Context context, int resId, @NonNull ArrayList<UserSearchStoreData> datas) {
        super(context, resId, datas);
        this.context = context;
        this.resId = resId;
        this.datas = datas;
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

        UserSearchStoreData data = datas.get(position);

        // 변환할 뷰에 들어갈 값들
        storeImageTestImageView = convertView.findViewById(R.id.storeImageTestImageView);
        searchStoreNameTestTextView = convertView.findViewById(R.id.searchStoreNameTestTextView);
        searchStoreEmptyTableTextView = convertView.findViewById(R.id.searchStoreEmptyTableTextView);

        // storeImageTestImageView.setImageBitmap();
        searchStoreNameTestTextView.setText(data.getStoreName());
        searchStoreNameTestTextView.setTypeface(null, Typeface.BOLD);
        searchStoreEmptyTableTextView.setText("테이블 남은 갯수" + '\n' + data.getStoreEmptyTable() + '개');
        searchStoreEmptyTableTextView.setTextColor(Color.RED);

        return convertView;
    }
}
