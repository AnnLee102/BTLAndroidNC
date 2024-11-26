package com.example.readstoryapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private EditText searchInput;
    private RecyclerView recyclerView;
    private StoryAdapter storyAdapter;
    private ArrayList<Story> storyList;
    private DatabaseReference databaseReference;
    private TextView searchTitle;
    private ImageButton clearButton;
    private TextView noResultsText; // Thông báo không có kết quả


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Tìm các thành phần giao diện
        searchInput = findViewById(R.id.search_input);
        recyclerView = findViewById(R.id.recycler_view);
        ImageButton backButton = findViewById(R.id.back_button);
        searchTitle = findViewById(R.id.search_title);
        clearButton = findViewById(R.id.clear_button);
        noResultsText = findViewById(R.id.no_results_text); // Kết nối với TextView thông báo


        // Xử lý nút quay lại
        backButton.setOnClickListener(v -> finish()); // Kết thúc Activity để quay lại

        // Cấu hình RecyclerView
        storyList = new ArrayList<>();
        storyAdapter = new StoryAdapter(this, storyList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(storyAdapter);

        // Lấy 6 sách đầu tiên từ Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("stories");
        fetchTopStories();

        // Lắng nghe thay đổi văn bản trong EditText
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần xử lý
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    clearButton.setVisibility(View.VISIBLE); // Hiển thị nút x khi có nội dung
                } else {
                    clearButton.setVisibility(View.GONE); // Ẩn nút x khi không có nội dung
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không cần xử lý
            }
        });

        // Xử lý sự kiện khi nhấn nút "x" để xóa nội dung
        clearButton.setOnClickListener(v -> {
            searchInput.setText(""); // Xóa toàn bộ nội dung trong EditText
            clearButton.setVisibility(View.GONE); // Ẩn nút x
        });


        // Xử lý khi nhấn Enter trên bàn phím
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = searchInput.getText().toString().trim();
                if (!query.isEmpty()) {
                    searchStories(query);

                    // Cập nhật nội dung dòng chữ
                    searchTitle.setText(Html.fromHtml("Kết quả tìm kiếm cho: <i><b>" + query + " " + "</b></i>", Html.FROM_HTML_MODE_LEGACY));
                    searchTitle.setVisibility(View.VISIBLE);
                }
                return true;
            }
            return false;
        });


    }

    // Lấy 6 sách đầu tiên từ Firebase
    private void fetchTopStories() {
        searchTitle.setText("Sách được tìm kiếm nhiều nhất");
        searchTitle.setVisibility(View.VISIBLE); // Hiển thị tiêu đề mặc định
        databaseReference.limitToFirst(6).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                storyList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Story story = data.getValue(Story.class);
                    if (story != null) {
                        storyList.add(story);
                    }
                }
                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SearchActivity.this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Tìm kiếm sách theo từ khóa
    private void searchStories(String query) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                storyList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Story story = data.getValue(Story.class);
                    if (story != null && (story.getName().toLowerCase().contains(query.toLowerCase())
                            || story.getAuthor().toLowerCase().contains(query.toLowerCase())
                            || story.getCategory().toLowerCase().contains(query.toLowerCase()))) {
                        storyList.add(story);
                    }
                }
                storyAdapter.notifyDataSetChanged();

                // Kiểm tra nếu không có kết quả
                if (storyList.isEmpty()) {
                    noResultsText.setVisibility(View.VISIBLE); // Hiển thị thông báo nếu không có kết quả
                } else {
                    noResultsText.setVisibility(View.GONE); // Ẩn thông báo nếu có kết quả
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SearchActivity.this, "Lỗi khi tìm kiếm", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

