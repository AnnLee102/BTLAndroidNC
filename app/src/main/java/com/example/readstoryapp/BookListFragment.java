
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
}
