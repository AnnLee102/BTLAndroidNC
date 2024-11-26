package com.example.readstoryapp;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class ContentActivity extends AppCompatActivity {

    private WebView webView;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        // Thiết lập WebView
        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Bật JavaScript nếu cần
        // Bật JavaScript nếu cần
        webSettings.setJavaScriptEnabled(true);

        // Phóng to chữ
        webSettings.setTextZoom(300); // 120% kích thước mặc định

        // Tăng độ nét và đảm bảo nội dung hiển thị vừa khít
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        // Đảm bảo WebView không mở trình duyệt bên ngoài

        webView.setWebViewClient(new WebViewClient()); // Đảm bảo điều hướng trong app

        // Nhận URL từ Intent và tải nội dung
        String contentUrl = getIntent().getStringExtra("contentUrl");
        if (contentUrl != null && !contentUrl.isEmpty()) {
            webView.loadUrl(contentUrl); // Tải nội dung từ URL
        }

        // Cài đặt GestureDetector để nhận diện cử chỉ vuốt
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 100; // Độ dài tối thiểu để tính là vuốt
            private static final int SWIPE_VELOCITY_THRESHOLD = 100; // Tốc độ tối thiểu để tính là vuốt

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 == null || e2 == null) return false;

                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();

                if (Math.abs(diffX) > Math.abs(diffY) && Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        // Vuốt từ trái sang phải (Back)
                        if (webView.canGoBack()) {
                            webView.goBack();
                        } else {
                            finish(); // Đóng Activity nếu không có trang trước
                        }
                    } else {
                        // Vuốt từ phải sang trái (Hành động khác, nếu muốn)
                        // Bạn có thể để trống hoặc thêm hành động như tải lại trang.
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
