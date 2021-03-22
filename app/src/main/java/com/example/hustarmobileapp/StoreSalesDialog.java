package com.example.hustarmobileapp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class StoreSalesDialog extends Dialog {
    private TextView storeSalesPerson, storeSalesPrice;
    private Button storeSalesOkbutton;

    public StoreSalesDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.store_sales_dialog);

        storeSalesPerson = findViewById(R.id.storeSalesPerson);
        storeSalesPrice = findViewById(R.id.storeSalesPrice);
        storeSalesOkbutton = findViewById(R.id.storeSalesOkbutton);
        storeSalesOkbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        storeSalesPrice.setText(" : 14000원");
        storeSalesPerson.setText(" : 8명");
    }
}
