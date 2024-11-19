package com.example.readstoryapp;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.util.Log;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerViewImages;
    private List<Integer> images = Arrays.asList(
            R.drawable.book_einstein,
            R.drawable.book_thao_tung,
            R.drawable.book_einstein
    );

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
        textViewCategory4.setOnClickListener(v -> openBookListFragment("Sách Hiệu Sồi"));

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

        recyclerViewImages = view.findViewById(R.id.recyclerViewImages);  // Chắc chắn gọi findViewById ở đây


        // Kết nối các ImageView
        ImageView imageView1 = view.findViewById(R.id.imageView1);
        ImageView imageView2 = view.findViewById(R.id.imageView2);
        ImageView imageView3 = view.findViewById(R.id.imageView3);
        ImageView imageView4 = view.findViewById(R.id.imageView4);
        ImageView imageView5 = view.findViewById(R.id.imageView5);
        ImageView imageView6 = view.findViewById(R.id.imageView6);
        TextView textViewName1 = view.findViewById(R.id.textViewName1);
        TextView textViewName2 = view.findViewById(R.id.textViewName2);
        TextView textViewName3 = view.findViewById(R.id.textViewName3);
        TextView textViewName4 = view.findViewById(R.id.textViewName4);
        TextView textViewName5 = view.findViewById(R.id.textViewName5);
        TextView textViewName6 = view.findViewById(R.id.textViewName6);

        // Đoạn mã tải dữ liệu Firebase và hiển thị ảnh
        databaseReference.child("story1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Lấy URL ảnh và tên truyện từ Firebase
                    String imageUrl1 = snapshot.child("imageUrl").getValue(String.class);
                    String name1 = snapshot.child("name").getValue(String.class);

                    if (imageUrl1 != null) {
                        Picasso.get().load(imageUrl1).placeholder(R.drawable.book_einstein).into(imageView1);
                    } else {
                        Toast.makeText(getContext(), "Không tìm thấy URL ảnh cho story1", Toast.LENGTH_SHORT).show();
                        Log.e("Firebase", "Không tìm thấy URL ảnh cho story1");
                    }

                    // Hiển thị tên truyện trong TextView (giả sử bạn có một TextView để hiển thị tên)
                    if (name1 != null) {
                        textViewName1.setText(name1);
                    } else {
                        textViewName1.setText("Tên truyện không có sẵn");
                    }
                } else {
                    Toast.makeText(getContext(), "Dữ liệu không tồn tại", Toast.LENGTH_SHORT).show();
                    Log.e("Firebase", "Dữ liệu không tồn tại cho story1");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
                Log.e("Firebase", "Lỗi khi tải dữ liệu: " + error.getMessage());
            }
        });

        databaseReference.child("story2").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Lấy URL ảnh và tên truyện từ Firebase
                    String imageUrl2 = snapshot.child("imageUrl").getValue(String.class);
                    String name2 = snapshot.child("name").getValue(String.class);

                    if (imageUrl2 != null) {
                        Picasso.get().load(imageUrl2).placeholder(R.drawable.book_einstein).into(imageView2);
                    } else {
                        Toast.makeText(getContext(), "Không tìm thấy URL ảnh cho story2", Toast.LENGTH_SHORT).show();
                        Log.e("Firebase", "Không tìm thấy URL ảnh cho story2");
                    }

                    // Hiển thị tên truyện trong TextView (giả sử bạn có một TextView để hiển thị tên)
                    if (name2 != null) {
                        textViewName2.setText(name2);
                    } else {
                        textViewName2.setText("Tên truyện không có sẵn");
                    }
                } else {
                    Toast.makeText(getContext(), "Dữ liệu không tồn tại", Toast.LENGTH_SHORT).show();
                    Log.e("Firebase", "Dữ liệu không tồn tại cho story2");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
                Log.e("Firebase", "Lỗi khi tải dữ liệu: " + error.getMessage());
            }
        });

        databaseReference.child("story3").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Lấy URL ảnh và tên truyện từ Firebase
                    String imageUrl3 = snapshot.child("imageUrl").getValue(String.class);
                    String name3 = snapshot.child("name").getValue(String.class);

                    if (imageUrl3 != null) {
                        Picasso.get().load(imageUrl3).placeholder(R.drawable.book_einstein).into(imageView3);
                    } else {
                        Toast.makeText(getContext(), "Không tìm thấy URL ảnh cho story3", Toast.LENGTH_SHORT).show();
                        Log.e("Firebase", "Không tìm thấy URL ảnh cho story3");
                    }

                    // Hiển thị tên truyện trong TextView (giả sử bạn có một TextView để hiển thị tên)
                    if (name3 != null) {
                        textViewName3.setText(name3);
                    } else {
                        textViewName3.setText("Tên truyện không có sẵn");
                    }
                } else {
                    Toast.makeText(getContext(), "Dữ liệu không tồn tại", Toast.LENGTH_SHORT).show();
                    Log.e("Firebase", "Dữ liệu không tồn tại cho story3");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
                Log.e("Firebase", "Lỗi khi tải dữ liệu: " + error.getMessage());
            }
        });

        databaseReference.child("story4").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Lấy URL ảnh và tên truyện từ Firebase
                    String imageUrl4 = snapshot.child("imageUrl").getValue(String.class);
                    String name4 = snapshot.child("name").getValue(String.class);

                    if (imageUrl4 != null) {
                        Picasso.get().load(imageUrl4).placeholder(R.drawable.book_einstein).into(imageView4);
                    } else {
                        Toast.makeText(getContext(), "Không tìm thấy URL ảnh cho story4", Toast.LENGTH_SHORT).show();
                        Log.e("Firebase", "Không tìm thấy URL ảnh cho story4");
                    }

                    // Hiển thị tên truyện trong TextView (giả sử bạn có một TextView để hiển thị tên)
                    if (name4 != null) {
                        textViewName4.setText(name4);
                    } else {
                        textViewName4.setText("Tên truyện không có sẵn");
                    }
                } else {
                    Toast.makeText(getContext(), "Dữ liệu không tồn tại", Toast.LENGTH_SHORT).show();
                    Log.e("Firebase", "Dữ liệu không tồn tại cho story4");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
                Log.e("Firebase", "Lỗi khi tải dữ liệu: " + error.getMessage());
            }
        });

        databaseReference.child("story5").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Lấy URL ảnh và tên truyện từ Firebase
                    String imageUrl5 = snapshot.child("imageUrl").getValue(String.class);
                    String name5 = snapshot.child("name").getValue(String.class);

                    if (imageUrl5 != null) {
                        Picasso.get().load(imageUrl5).placeholder(R.drawable.book_einstein).into(imageView5);
                    } else {
                        Toast.makeText(getContext(), "Không tìm thấy URL ảnh cho story5", Toast.LENGTH_SHORT).show();
                        Log.e("Firebase", "Không tìm thấy URL ảnh cho story5");
                    }

                    // Hiển thị tên truyện trong TextView (giả sử bạn có một TextView để hiển thị tên)
                    if (name5 != null) {
                        textViewName5.setText(name5);
                    } else {
                        textViewName5.setText("Tên truyện không có sẵn");
                    }
                } else {
                    Toast.makeText(getContext(), "Dữ liệu không tồn tại", Toast.LENGTH_SHORT).show();
                    Log.e("Firebase", "Dữ liệu không tồn tại cho story5");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
                Log.e("Firebase", "Lỗi khi tải dữ liệu: " + error.getMessage());
            }
        });

        databaseReference.child("story6").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Lấy URL ảnh và tên truyện từ Firebase
                    String imageUrl6 = snapshot.child("imageUrl").getValue(String.class);
                    String name6 = snapshot.child("name").getValue(String.class);

                    if (imageUrl6 != null) {
                        Picasso.get().load(imageUrl6).placeholder(R.drawable.book_einstein).into(imageView6);
                    } else {
                        Toast.makeText(getContext(), "Không tìm thấy URL ảnh cho story2", Toast.LENGTH_SHORT).show();
                        Log.e("Firebase", "Không tìm thấy URL ảnh cho story2");
                    }

                    // Hiển thị tên truyện trong TextView (giả sử bạn có một TextView để hiển thị tên)
                    if (name6 != null) {
                        textViewName6.setText(name6);
                    } else {
                        textViewName6.setText("Tên truyện không có sẵn");
                    }
                } else {
                    Toast.makeText(getContext(), "Dữ liệu không tồn tại", Toast.LENGTH_SHORT).show();
                    Log.e("Firebase", "Dữ liệu không tồn tại cho story2");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
                Log.e("Firebase", "Lỗi khi tải dữ liệu: " + error.getMessage());
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





