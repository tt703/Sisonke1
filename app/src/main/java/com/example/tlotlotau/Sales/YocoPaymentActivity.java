package com.example.tlotlotau.Sales;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tlotlotau.R;

public class YocoPaymentActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yoco_payment);

        webView = findViewById(R.id.webView);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        String url = getIntent().getStringExtra("PAYMENT_URL");
        if (url == null) {
            finish();
            return;
        }

        // --- OPTIMIZATION STARTS HERE ---
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        // 1. Performance: Improve Cache to reduce network load
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        // 2. Performance: Hardware Acceleration (Critical for Emulator)
        // If the emulator still crashes, change LAYER_TYPE_HARDWARE to LAYER_TYPE_SOFTWARE
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        // 3. User Experience: Zoom controls
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        // --- OPTIMIZATION ENDS HERE ---

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (!isFinishing()) progressBar.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (!isFinishing()) progressBar.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String currentUrl = request.getUrl().toString();

                if (currentUrl.contains("demo.app/success")) {
                    finishWithResult(Activity.RESULT_OK);
                    return true;
                }
                if (currentUrl.contains("demo.app/fail") || currentUrl.contains("demo.app/cancel")) {
                    finishWithResult(Activity.RESULT_CANCELED);
                    return true;
                }
                return false;
            }
        });

        if (savedInstanceState == null) {
            webView.loadUrl(url);
        }
    }

    private void finishWithResult(int resultCode) {
        setResult(resultCode);
        finish();
    }

    @Override
    protected void onDestroy() {
        // Prevent memory leaks
        if (webView != null) {
            webView.stopLoading();
            webView.destroy();
        }
        super.onDestroy();
    }
}