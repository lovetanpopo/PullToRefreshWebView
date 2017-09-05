package jp.co.den.android.pulltorefreshwebview;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {
    private boolean mEnableSwipeRefresh;
    private WebView mWebView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwipeRefreshLayout = findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.reload();
            }
        });
        mSwipeRefreshLayout.setOnChildScrollUpCallback(new SwipeRefreshLayout.OnChildScrollUpCallback() {
            @Override
            public boolean canChildScrollUp(SwipeRefreshLayout parent, @Nullable View child) {
                return mWebView.getScrollY() != 0 || !mEnableSwipeRefresh;
            }
        });

        final WebView webView = new WebView(this) {
            @Override
            protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
                super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
                mEnableSwipeRefresh = true;
            }
        };
        ((ViewGroup) findViewById(R.id.container)).addView(webView);
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        mEnableSwipeRefresh = false;
                        break;
                }
                return false;
            }
        });

        final WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAppCacheEnabled(true);

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(
                new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

        webView.loadUrl("http://m.yahoo.co.jp");
        mWebView = webView;
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }
        super.onBackPressed();
    }
}
