package com.yomelody.utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.yomelody.PrivacyPolicy;
import com.yomelody.R;

import static com.yomelody.utils.Const.ServiceType.HOST_URL;

/**
 * Created by ADMIN on 12/6/2017.
 */

public class TermsofServices extends AppCompatActivity{
    WebView wv;
    String url = HOST_URL + "api/company_policy/terms_services.php";
    ImageView backIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termsof_services);
        wv = (WebView) findViewById(R.id.privacy);
        backIv = (ImageView) findViewById(R.id.backIv);
//        WebSettings webSettings = wv.getSettings();
//        wv.getSettings().setLoadWithOverviewMode(true);
//        wv.getSettings().setUseWideViewPort(true);

        wv.setWebViewClient(new TermsofServices.myWebClient());
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
        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
            builder.setMessage("Your security warning message here");
            builder.setPositiveButton("proceed", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.proceed();
                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.cancel();
                }
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
