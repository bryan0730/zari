package com.example.hustarmobileapp;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

class LoginActivity extends AppCompatActivity {
    private EditText editTextId, editTextPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        init();
    }

    private void init(){
        editTextId = findViewById(R.id.editTextId);
        editTextPw = findViewById(R.id.editTextPassword);
    }
}
