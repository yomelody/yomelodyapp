package com.yomelody;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import static com.yomelody.utils.Const.ServiceType.HOST_URL;


public class PrivacyPolicy extends AppCompatActivity {
    WebView wv;
    String url = HOST_URL + "api/company_policy/privacy_policy.php";
    ImageView backIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        wv = (WebView) findViewById(R.id.privacy);
        backIv = (ImageView) findViewById(R.id.backIv);
//        WebSettings webSettings = wv.getSettings();
//        wv.getSettings().setLoadWithOverviewMode(true);
//        wv.getSettings().setUseWideViewPort(true);

        wv.setWebViewClient(new myWebClient());
        wv.loadUrl(url);


        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            try {

                view.loadUrl(url);
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return true;

        }
    }
}
