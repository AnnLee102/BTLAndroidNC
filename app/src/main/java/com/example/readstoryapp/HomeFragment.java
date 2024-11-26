package com.example.readstoryapp;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerViewImages;
    private List<Integer> images = Arrays.asList(
            R.drawable.truyen_1,
            R.drawable.truyen_2,
            R.drawable.truyen_3
    );
    private RecyclerView recyclerViewSuggestions;
    private RecyclerView recyclerViewNewest;

    private CommonBooksAdapter suggestionsAdapter;
    private CommonBooksAdapter newestAdapter;

    private List<String> imageUrls = new ArrayList<>();
    private List<String> bookNames = new ArrayList<>();


    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("stories");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout cho fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerViewImages = rootView.findViewById(R.id.recyclerViewImages);


        // Ánh xạ các TextView
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

        // Xử lý click cho TextView "Sách Hiệu Sồi"
        textViewCategory4.setOnClickListener(v -> openBookListFragment("Hiệu Sồi"));

        // Xử lý click cho TextView "Sách giấy"
        textViewCategory5.setOnClickListener(v -> openBookListFragment("Sách giấy"));

        // Xử lý click cho TextView "Podcast"
        textViewCategory6.setOnClickListener(v -> openBookListFragment("Podcast"));


        // Cấu hình RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewImages.setLayoutManager(layoutManager);

        ImageAdapter adapter = new ImageAdapter(requireContext(), images);
        recyclerViewImages.setAdapter(adapter);

        // SnapHelper để cố định phần tử chính giữa
        SnapHelper snapHelper = new androidx.recyclerview.widget.LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerViewImages);

        // Lắng nghe sự thay đổi cuộn
        recyclerViewImages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scaleCenterItem();
            }
        });

        // Cuộn đến giữa danh sách
        recyclerViewImages.scrollToPosition(images.size() * 50);

        recyclerViewImages.post(() -> {
            int initialPosition = adapter.getItemCount() / 2; // Xác định vị trí item chính giữa
            int offset = (recyclerViewImages.getWidth() - dpToPx(262)) / 2; // Độ rộng ảnh chính giữa là 262dp
            layoutManager.scrollToPositionWithOffset(initialPosition, offset);
        });


        return rootView;

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerViewImages = view.findViewById(R.id.recyclerViewImages);
        // Ánh xạ RecyclerView
        recyclerViewSuggestions = view.findViewById(R.id.recyclerViewSuggestions);
        recyclerViewNewest = view.findViewById(R.id.recyclerViewNewest);

        // Khởi tạo adapter
        suggestionsAdapter = new CommonBooksAdapter(new ArrayList<>(), new ArrayList<>());
        newestAdapter = new CommonBooksAdapter(new ArrayList<>(), new ArrayList<>());

        // Cài đặt RecyclerView
        setupRecyclerView1(recyclerViewSuggestions, suggestionsAdapter);
        setupRecyclerView1(recyclerViewNewest, newestAdapter);

        // Lấy dữ liệu từ Firebase
        fetchStoriesFromFirebase();


        // Cấu hình RecyclerView cho từng thể loại
        setupRecyclerView(view, R.id.recycler_electronic_books, "Sách điện tử");
        setupRecyclerView(view, R.id.recycler_audio_books, "Sách nói");
        setupRecyclerView(view, R.id.recycler_comics, "Truyện tranh");

    }

    private void setupRecyclerView(View view, int recyclerViewId, String category){
        RecyclerView recyclerView = view.findViewById(recyclerViewId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        List<String> imageUrls = new ArrayList<>();
        List<String> bookNames = new ArrayList<>();
        CommonBooksAdapter adapter = new CommonBooksAdapter(imageUrls, bookNames);

        recyclerView.setAdapter(adapter);

        // Tải dữ liệu từ Firebase
        databaseReference.orderByChild("category").equalTo(category).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot bookSnapshot : snapshot.getChildren()) {
                    String imageUrl = bookSnapshot.child("imageUrl").getValue(String.class);
                    String name = bookSnapshot.child("name").getValue(String.class);
                    if (imageUrl != null && name != null) {
                        imageUrls.add(imageUrl);
                        bookNames.add(name);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView1(RecyclerView recyclerView, CommonBooksAdapter adapter) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }

    private void fetchStoriesFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imageUrls.clear();
                bookNames.clear();

                // Lấy tất cả dữ liệu từ Firebase
                for (DataSnapshot storySnapshot : snapshot.getChildren()) {
                    String imageUrl = storySnapshot.child("imageUrl").getValue(String.class);
                    String bookName = storySnapshot.child("name").getValue(String.class);

                    if (imageUrl != null && bookName != null) {
                        imageUrls.add(imageUrl);
                        bookNames.add(bookName);
                    }
                }

                // Tách dữ liệu cho Đề xuất (10 cuối) và Mới nhất (10 đầu tiên)
                List<String> suggestionsImages = imageUrls.subList(Math.max(imageUrls.size() - 10, 0), imageUrls.size());
                List<String> suggestionsNames = bookNames.subList(Math.max(bookNames.size() - 10, 0), bookNames.size());

                List<String> newestImages = imageUrls.subList(0, Math.min(imageUrls.size(), 10));
                List<String> newestNames = bookNames.subList(0, Math.min(bookNames.size(), 10));

                // Cập nhật adapter
                suggestionsAdapter.updateData(suggestionsImages, suggestionsNames);
                newestAdapter.updateData(newestImages, newestNames);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Không thể tải dữ liệu từ Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }



        private void openBookListFragment(String category) {
        // Tạo instance của BookListFragment
        BookListFragment bookListFragment = BookListFragment.newInstance(category);

        // Chuyển đổi sang fragment mới
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, bookListFragment) // fragment_container là ViewGroup chứa các Fragment
                .addToBackStack(null) // Cho phép quay lại fragment trước đó
                .commit();

    }

    private void scaleCenterItem() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerViewImages.getLayoutManager();
        if (layoutManager == null) return;

        float centerX = recyclerViewImages.getWidth() / 2f;

        for (int i = 0; i < recyclerViewImages.getChildCount(); i++) {
            View child = recyclerViewImages.getChildAt(i);
            if (child == null) continue;

            // Tính khoảng cách từ tâm RecyclerView đến tâm của từng item
            float childCenterX = child.getX() + child.getWidth() / 2f;
            float distanceFromCenter = Math.abs(centerX - childCenterX);

            // Tính toán tỉ lệ thay đổi
            float scale = 1 - Math.min(distanceFromCenter / centerX, 1f) * 0.5f;
            child.setScaleX(scale);
            child.setScaleY(scale);

            // Cập nhật kích thước ảnh
            ImageView imageView = child.findViewById(R.id.imageViewItem);
            if (imageView != null) {
                // Thay đổi kích thước ảnh theo tỉ lệ
                float sizeFactor = (scale == 1) ? 250 : 100; // Ảnh chính giữa to hơn (250dp)
                ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                layoutParams.width = dpToPx((int) (sizeFactor * 1.5));
                layoutParams.height = dpToPx((int) (sizeFactor * 2.5)); // Tăng chiều cao tương ứng
                imageView.setLayoutParams(layoutParams);
            }
        }
    }


    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Đảm bảo RecyclerView được cuộn lại đúng vị trí khi quay lại fragment
        recyclerViewImages.post(() -> {
            if (recyclerViewImages.getAdapter() != null) {
                int initialPosition = recyclerViewImages.getAdapter().getItemCount() / 2; // Item giữa
                int offset = (recyclerViewImages.getWidth() - dpToPx(262)) / 2; // Căn giữa item
                ((LinearLayoutManager) recyclerViewImages.getLayoutManager())
                        .scrollToPositionWithOffset(initialPosition, offset);
            }
        });
    }

}





