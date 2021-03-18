package com.example.hustarmobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BirthActivity extends AppCompatActivity {
    private DatePicker datePicker;
    public static String yy,mm,dd;
    public static boolean check=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.birth);

        datePicker = findViewById(R.id.datePicker);

        datePicker.init(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                yy=Integer.toString(i);
                mm=Integer.toString(i1);
                dd=Integer.toString(i2);
                check = true;
            }
        });
    }

    public void onClickBirth(View v){
        Intent intent = new Intent();
        intent.putExtra("yy",yy);
        intent.putExtra("mm",mm);
        intent.putExtra("dd",dd);

        Log.i("yy", yy);
        Log.i("mm", mm);
        Log.i("dd", dd);

        setResult(RESULT_OK, intent);
        finish();
    }
}
