package com.example.readstoryapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ContentActivity extends AppCompatActivity {

    private WebView webView;
    private Button btnGoToMarked;
    private GestureDetector gestureDetector;

    private static final String PREFS_NAME = "ReadStoryPrefs";
    private static final String MARKED_POSITION_KEY = "markedPosition";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        // Thiết lập WebView
        webView = findViewById(R.id.webView);
        btnGoToMarked = findViewById(R.id.btnGoToMarked);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setTextZoom(300);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webView.setWebViewClient(new WebViewClient());

        // Nhận URL từ Intent và tải nội dung
        String contentUrl = getIntent().getStringExtra("contentUrl");
        if (contentUrl != null && !contentUrl.isEmpty()) {
            webView.loadUrl(contentUrl);
        }

        // Phục hồi vị trí cuộn sau khi tải xong nội dung
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // Lấy vị trí đánh dấu từ SharedPreferences
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                int markedPosition = prefs.getInt(MARKED_POSITION_KEY, -1);

                // Hiển thị nút nếu có vị trí được đánh dấu
                if (markedPosition > 0) {
                    btnGoToMarked.setVisibility(Button.VISIBLE);
                }
            }
        });

        // Xử lý khi nhấn nút "Đến chỗ đọc tiếp"
        btnGoToMarked.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            int markedPosition = prefs.getInt(MARKED_POSITION_KEY, 0);

            // Cuộn đến vị trí được đánh dấu
            webView.scrollTo(0, markedPosition);
            btnGoToMarked.setVisibility(Button.GONE); // Ẩn nút sau khi cuộn
        });

        // Cài đặt GestureDetector để nhận diện double-tap
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                // Lưu vị trí cuộn hiện tại khi double-tap
                int scrollY = webView.getScrollY();
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(MARKED_POSITION_KEY, scrollY);
                editor.apply();

                // Hiển thị thông báo
                btnGoToMarked.setVisibility(Button.VISIBLE);
                return true;
            }
        });
// Cài đặt GestureDetector để nhận diện cử chỉ vuốt
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 == null || e2 == null) return false;

                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();

                if (Math.abs(diffX) > Math.abs(diffY) && Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        if (webView.canGoBack()) {
                            webView.goBack();
                        } else {
                            finish();
                        }
                    }
                    return true;
                }
                return false;
            }
        });


        // Gán GestureDetector cho WebView
        webView.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }
}
