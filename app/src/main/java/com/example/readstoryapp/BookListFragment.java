
package com.example.readstoryapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
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
    private String category;

    public static BookListFragment newInstance(String category) {
        BookListFragment fragment = new BookListFragment();
        Bundle args = new Bundle();
        args.putString("category", category);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_list, container, false);

        category = getArguments() != null ? getArguments().getString("category") : "Unknown";
        TextView textViewCategory = view.findViewById(R.id.textViewCategory);
        textViewCategory.setText("Thể loại: " + category);


        ImageView closeButton = view.findViewById(R.id.closeButton); // ID của nút "X"
        closeButton.setOnClickListener(v -> returnToHomeFragment());

        // Ánh xạ các TextView thể loại
        TextView textViewCategory1 = view.findViewById(R.id.textViewCategory1);
        TextView textViewCategory2 = view.findViewById(R.id.textViewCategory2);
        TextView textViewCategory3 = view.findViewById(R.id.textViewCategory3);
        TextView textViewCategory4 = view.findViewById(R.id.textViewCategory4);
        TextView textViewCategory5 = view.findViewById(R.id.textViewCategory5);
        TextView textViewCategory6 = view.findViewById(R.id.textViewCategory6);

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
        loadStories(view);

        return view;
    }

    private void loadStories(View view) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("stories");
        ref.orderByChild("category").equalTo(category).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Story> stories = new ArrayList<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Story story = postSnapshot.getValue(Story.class);
                    stories.add(story);
                }
                displayStories(view, stories);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void displayStories(View view, List<Story> stories) {
        GridLayout layout = view.findViewById(R.id.layoutImages);
        layout.removeAllViews();

        for (Story story : stories) {
            // Inflate layout của mỗi mục
            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_image, layout, false);

            // Ánh xạ các thành phần
            ImageView imageView = itemView.findViewById(R.id.imageViewItem);
            TextView textView = itemView.findViewById(R.id.textViewItemName);

            // Gán dữ liệu
            textView.setText(story.getName());
            Picasso.get().load(story.getImageUrl()).into(imageView);

            // Set sự kiện click
            itemView.setOnClickListener(v -> openDetailFragment(story));

            // Thêm vào GridLayout
            layout.addView(itemView);
        }
    }


    private void openBookListFragment(String category) {
        // Tạo instance mới của BookListFragment với category tương ứng
        BookListFragment bookListFragment = BookListFragment.newInstance(category);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, bookListFragment) // fragment_container là ID của ViewGroup chứa các Fragment
                .addToBackStack(null) // Thêm vào BackStack để quay lại fragment trước đó
                .commit();
    }
    private void openDetailFragment(Story story) {
        BookDetailFragment fragment = BookDetailFragment.newInstance(
                story.getName(),
                story.getAuthor(),
                story.getImageUrl(),
                story.getContentUrl()
        );

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
    private void returnToHomeFragment() {
        // Xóa tất cả các fragment trong back stack
        requireActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);


    }

}
