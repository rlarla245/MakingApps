package com.test.testbooktest5_internet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public WebView webView;
    public EditText uriEditText;
    public Button moveButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (WebView)findViewById(R.id.mainactivity_webview_webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setBuiltInZoomControls(true);

        uriEditText = (EditText)findViewById(R.id.mainactivity_edittext_inputurl);
        moveButton = (Button)findViewById(R.id.mainactivity_button_gobutton);
        backButton = (Button)findViewById(R.id.mainactivity_button_previouspagebutton);

        moveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl(uriEditText.getText().toString());
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.goBack();
            }
        });


    }
}
