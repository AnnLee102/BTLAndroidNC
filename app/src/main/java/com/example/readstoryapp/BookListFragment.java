package com.example.readstoryapp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BookListFragment extends Fragment {
    private static final String ARG_CATEGORY = "category";

    public static BookListFragment newInstance(String category) {
        BookListFragment fragment = new BookListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_book_list, container, false);

        // Lấy category từ arguments
        String category = getArguments() != null ? getArguments().getString(ARG_CATEGORY) : "Unknown";

        // Hiển thị thể loại
        TextView textViewCategory = rootView.findViewById(R.id.textViewCategory);
        textViewCategory.setText("Thể loại: " + category);

        // Xử lý nút "X"
        ImageView closeButton = rootView.findViewById(R.id.closeButton); // ID của nút "X"
        closeButton.setOnClickListener(v -> returnToHomeFragment());

        // Ánh xạ các TextView thể loại
        TextView textViewCategory1 = rootView.findViewById(R.id.textViewCategory1);
        TextView textViewCategory2 = rootView.findViewById(R.id.textViewCategory2);
        TextView textViewCategory3 = rootView.findViewById(R.id.textViewCategory3);
        TextView textViewCategory4 = rootView.findViewById(R.id.textViewCategory4);
        TextView textViewCategory5 = rootView.findViewById(R.id.textViewCategory5);
        TextView textViewCategory6 = rootView.findViewById(R.id.textViewCategory6);

        // Xử lý click cho TextView "Sách nói"
        textViewCategory1.setOnClickListener(v -> openBookListFragment("Sách nói"));

        // Xử lý click cho TextView "Sách điện tử"
        textViewCategory2.setOnClickListener(v -> openBookListFragment("Sách điện tử"));

        // Xử lý click cho TextView "Truyện tranh"
        textViewCategory3.setOnClickListener(v -> openBookListFragment("Truyện tranh"));

        // Xử lý click cho TextView "Hiệu Sồi"
        textViewCategory4.setOnClickListener(v -> openBookListFragment("Hiệu Sồi"));

        // Xử lý click cho TextView "Sách giấy"
        textViewCategory5.setOnClickListener(v -> openBookListFragment("Sách giấy"));

        // Xử lý click cho TextView "Podcast"
        textViewCategory6.setOnClickListener(v -> openBookListFragment("Podcast"));

        // Hiển thị danh sách hình ảnh
        updateImagesByCategory(rootView, category);

        return rootView;
    }



    private void openBookListFragment(String category) {
        // Tạo instance mới của BookListFragment với category tương ứng
        BookListFragment bookListFragment = BookListFragment.newInstance(category);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, bookListFragment) // fragment_container là ID của ViewGroup chứa các Fragment
                .addToBackStack(null) // Thêm vào BackStack để quay lại fragment trước đó
                .commit();
    }

    private void updateImagesByCategory(View rootView, String category) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("stories");

        // Lấy dữ liệu theo thể loại
        databaseReference.orderByChild("category").equalTo(category).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Log.e("Firebase", "Không tìm thấy dữ liệu cho thể loại: " + category);
                } else {
                    List<Story> stories = new ArrayList<>();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Story story = postSnapshot.getValue(Story.class);
                        if (story != null) {
                            Log.d("Firebase", "Tên truyện: " + story.getName() + ", URL hình ảnh: " + story.getImageUrl());
                            stories.add(story);
                        }
                    }

                    if (stories.isEmpty()) {
                        Log.e("Firebase", "Danh sách truyện trống!");
                    } else {
                        displayStories(rootView, stories); // Hiển thị dữ liệu nếu có
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Lỗi khi lấy dữ liệu từ Firebase: " + error.getMessage());
            }
        });
    }


    private void displayStories(View rootView, List<Story> stories) {
        LinearLayout layoutImages = rootView.findViewById(R.id.layoutImages);
        layoutImages.removeAllViews(); // Xóa các hình ảnh cũ nếu có

        // Dùng để tạo row mới (mỗi row chứa 2 ảnh)
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        rowParams.setMargins(0, 5, 0, 5); // Thêm margin giữa các row

        for (int i = 0; i < stories.size(); i += 2) {
            // Tạo một dòng chứa 2 ảnh và tên truyện
            LinearLayout row = new LinearLayout(getContext());
            row.setOrientation(LinearLayout.HORIZONTAL); // Ảnh ngang nhau
            row.setLayoutParams(rowParams);

            // Tạo ảnh và tên truyện đầu tiên
            Story story1 = stories.get(i);
            LinearLayout imageLayout1 = createImageWithTitle(story1);
            row.addView(imageLayout1);

            // Tạo ảnh và tên truyện thứ hai nếu có
            if (i + 1 < stories.size()) {
                Story story2 = stories.get(i + 1);
                LinearLayout imageLayout2 = createImageWithTitle(story2);
                row.addView(imageLayout2);
            }

            // Thêm row vào layout chính
            layoutImages.addView(row);
        }
    }

    private LinearLayout createImageWithTitle(Story story) {
        // Tạo layout cho ảnh và tên truyện
        LinearLayout imageLayout = new LinearLayout(getContext());
        imageLayout.setOrientation(LinearLayout.VERTICAL); // Sắp xếp ảnh và tên truyện theo chiều dọc

        // Cài đặt ảnh
        ImageView imageView = new ImageView(getContext());
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                convertDpToPx(170), convertDpToPx(250)); // Tăng kích thước ảnh mỗi chiều thêm 50dp
        imageParams.setMargins(10, 10, 15, 10); // Khoảng cách bên ngoài ảnh
        imageView.setLayoutParams(imageParams);
        Picasso.get().load(story.getImageUrl()).into(imageView);
        imageLayout.addView(imageView);

        // Tạo tên truyện
        TextView textView = new TextView(getContext());
        textView.setText(story.getName());
        textView.setMaxLines(2); // Giới hạn tối đa 2 dòng
        textView.setEllipsize(TextUtils.TruncateAt.END); // Thêm dấu 3 chấm khi quá dài

        // Giới hạn chiều dài chữ và cho phép xuống dòng
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                convertDpToPx(160), LinearLayout.LayoutParams.WRAP_CONTENT); // Chiều rộng tối đa 160dp
        textView.setLayoutParams(textParams);

        // Cài đặt padding cho tên truyện
        textView.setPadding(5, 10, 5, 10); // Thêm padding trên và dưới (10dp) cho tên truyện
        // In đậm tên truyện
        textView.setTypeface(null, Typeface.BOLD); // Đặt kiểu chữ thành đậm
        imageLayout.addView(textView);

        return imageLayout;
    }




    // Hàm chuyển đổi dp thành px
    private int convertDpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density);
    }









    private void returnToHomeFragment() {
        // Xóa tất cả các fragment trong back stack
        requireActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);


    }

}