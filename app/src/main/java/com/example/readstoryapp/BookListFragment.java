package com.example.readstoryapp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
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

        // Ánh xạ và xử lý sự kiện click cho các thể loại
        setUpCategoryClickEvents(view);

        // Tải dữ liệu
        loadStories(view);

        return view;
    }

    private void setUpCategoryClickEvents(View view) {
        TextView textViewCategory1 = view.findViewById(R.id.textViewCategory1);
        TextView textViewCategory2 = view.findViewById(R.id.textViewCategory2);
        TextView textViewCategory3 = view.findViewById(R.id.textViewCategory3);
        TextView textViewCategory4 = view.findViewById(R.id.textViewCategory4);
        TextView textViewCategory5 = view.findViewById(R.id.textViewCategory5);
        TextView textViewCategory6 = view.findViewById(R.id.textViewCategory6);

        textViewCategory1.setOnClickListener(v -> openBookListFragment("Sách nói"));
        textViewCategory2.setOnClickListener(v -> openBookListFragment("Sách điện tử"));
        textViewCategory3.setOnClickListener(v -> openBookListFragment("Truyện tranh"));
        textViewCategory4.setOnClickListener(v -> openBookListFragment("Hiệu Sồi"));
        textViewCategory5.setOnClickListener(v -> openBookListFragment("Sách giấy"));
        textViewCategory6.setOnClickListener(v -> openBookListFragment("Podcast"));
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
            View itemView = createImageWithTitle(story);
            itemView.setOnClickListener(v -> openDetailFragment(story));
            layout.addView(itemView);
        }
    }

    private View createImageWithTitle(Story story) {
        // Tạo layout cho ảnh và tên truyện
        LinearLayout imageLayout = new LinearLayout(getContext());
        imageLayout.setOrientation(LinearLayout.VERTICAL);

        // Tạo CardView bọc ảnh
        CardView cardView = new CardView(getContext());
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                convertDpToPx(180), convertDpToPx(250));
        cardParams.setMargins(25, 30, 20, 10);
        cardView.setLayoutParams(cardParams);
        cardView.setRadius(convertDpToPx(8));
        cardView.setCardElevation(8);
        cardView.setPreventCornerOverlap(true);
        cardView.setUseCompatPadding(true);

        // Cài đặt ảnh bên trong CardView
        ImageView imageView = new ImageView(getContext());
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(imageParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Picasso.get().load(story.getImageUrl()).into(imageView);

        cardView.addView(imageView);
        imageLayout.addView(cardView);

        // Tạo tên truyện
        TextView textView = new TextView(getContext());
        textView.setText(story.getName());
        textView.setMaxLines(2);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setTypeface(null, Typeface.BOLD);

        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                convertDpToPx(180), LinearLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(textParams);
        textView.setPadding(35, 10, 0, 20);
        imageLayout.addView(textView);

        return imageLayout;
    }

    private int convertDpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density);
    }

    private void openBookListFragment(String category) {
        BookListFragment bookListFragment = BookListFragment.newInstance(category);
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, bookListFragment)
                .addToBackStack(null)
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
        requireActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}
