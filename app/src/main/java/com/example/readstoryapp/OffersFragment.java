package com.example.readstoryapp;

import android.graphics.Paint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OffersFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offers, container, false);

        TextView countdownTimer = view.findViewById(R.id.countdown_timer);

        TextView tvTeacherDay = view.findViewById(R.id.tv_teacher_day);
        tvTeacherDay.setSelected(true); // Kích hoạt marquee
        tvTeacherDay.requestFocus(); // Yêu cầu tiêu điểm cho TextView


        long twoWeeksInMillis = 14 * 24 * 60 * 60 * 1000L; // 2 tuần
        new CountDownTimer(twoWeeksInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long hours = (millisUntilFinished / (1000 * 60 * 60)) % 24;
                long minutes = (millisUntilFinished / (1000 * 60)) % 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                countdownTimer.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            }

            @Override
            public void onFinish() {
                countdownTimer.setText("00:00:00");
            }
        }.start();

        LinearLayout container2 = view.findViewById(R.id.story_container);

        // Truy vấn Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("stories");
        databaseReference.orderByChild("category").equalTo("Hiệu Sồi").limitToFirst(3)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Kiểm tra nếu không có dữ liệu
                        if (!dataSnapshot.exists()) {
                            Toast.makeText(requireContext(), "Không tìm thấy dữ liệu.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Lặp qua các mục phù hợp
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String name = snapshot.child("name").getValue(String.class);
                            String imageUrl = snapshot.child("imageUrl").getValue(String.class);

                            if (name == null || imageUrl == null) continue;

                            // Inflate layout cho mỗi mục
                            View itemView = LayoutInflater.from(requireContext()).inflate(R.layout.item_offers, container2, false);

                            // Set giá trị
                            TextView tvName = itemView.findViewById(R.id.tv_name);
                            TextView tvNewPrice = itemView.findViewById(R.id.tv_new_price);
                            TextView tvOldPrice = itemView.findViewById(R.id.tv_old_price);
                            tvOldPrice.setPaintFlags(tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);  // gạch ngang chữ
                            ImageView ivImage = itemView.findViewById(R.id.iv_image);

                            tvName.setText(name);
                            tvNewPrice.setText("100.000đ"); // Giá cố định
                            tvOldPrice.setText("150.000đ"); // Giá cố định

                            // Tải ảnh bằng Glide
                            Glide.with(requireContext()).load(imageUrl).into(ivImage);

                            // Thêm mục vào container
                            container2.addView(itemView);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Xử lý lỗi
                        Toast.makeText(requireContext(), "Lỗi kết nối Firebase.", Toast.LENGTH_SHORT).show();
                        Log.e("Firebase", "Database error: " + databaseError.getMessage());
                    }
                });


        return view;
    }
}
