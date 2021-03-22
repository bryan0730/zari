package com.example.hustarmobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddressWebViewActivity extends AppCompatActivity {
    private static String IP_ADDRESS = "192.168.0.35";

    private WebView webView;
    private TextView result;
    private Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        result = findViewById(R.id.result);
        init();
        handler = new Handler();
    }

    public void init(){
        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.addJavascriptInterface(new AndroidBridge(), "Zari");
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("http://"+IP_ADDRESS+"/addressweb.php");
    }
    public void onClickaddAd(View v){
        String addressResult = result.getText().toString();
        Intent intent = new Intent();
        intent.putExtra("address", addressResult);
        setResult(RESULT_OK, intent);
        finish();
    }

    private class AndroidBridge {
        @JavascriptInterface
        public void setAddress(final String arg1, final String arg2, final String arg3) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    result.setText(String.format("(%s) %s %s", arg1, arg2, arg3));
                    init();
                }
            });
        }
    }
}
