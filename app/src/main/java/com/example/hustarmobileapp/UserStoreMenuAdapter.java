package com.example.hustarmobileapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class UserStoreMenuAdapter extends ArrayAdapter<UserStoreMenuData> {
    private Context context;
    private int resId;
    private ArrayList<UserStoreMenuData> datas;

    public UserStoreMenuAdapter(@NonNull Context context, int resId, @NonNull ArrayList<UserStoreMenuData> datas) {
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
        LayoutInflater inflater         = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView                     = inflater.inflate(resId, null);

        UserStoreMenuData data          = datas.get(position);

        TextView menuNameTextView       = convertView.findViewById(R.id.menuNameTextView);
        TextView menuCategoryTextView   = convertView.findViewById(R.id.menuCategoryTextView);
        TextView menuPriceTextView      = convertView.findViewById(R.id.menuPriceTextView);

        menuNameTextView.setText(data.getMenuName());
        menuCategoryTextView.setText(data.getMenuCategory());
        menuPriceTextView.setText(data.getMenuPrice());

        return convertView;
    }
}
