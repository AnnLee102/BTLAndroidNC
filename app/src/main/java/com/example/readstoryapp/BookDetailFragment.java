package com.example.readstoryapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BookDetailFragment extends Fragment {
    private String name, author, imageUrl, contentUrl;
    private RecyclerView recyclerView;
    private StoryAdapter storyAdapter;

    public static BookDetailFragment newInstance(String name, String author, String imageUrl, String contentUrl) {
        BookDetailFragment fragment = new BookDetailFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putString("author", author);
        args.putString("imageUrl", imageUrl);
        args.putString("contentUrl", contentUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_detail, container, false);



        if (getArguments() != null) {
            name = getArguments().getString("name");
            author = getArguments().getString("author");
            imageUrl = getArguments().getString("imageUrl");
            contentUrl = getArguments().getString("contentUrl");
        }
        ImageView closeButton = view.findViewById(R.id.closeButton); // ID của nút "X"
        closeButton.setOnClickListener(v -> returnToPreviousFragment());
        // Lấy các view từ layout
        ImageView bookImage = view.findViewById(R.id.bookImage);
        TextView bookName = view.findViewById(R.id.bookName);
        TextView bookAuthor = view.findViewById(R.id.bookAuthor);
        TextView readBook = view.findViewById(R.id.readBook);
        recyclerView = view.findViewById(R.id.recyclerViewRelatedContent);

        // Kiểm tra nếu URL ảnh hợp lệ và tải ảnh bằng Picasso
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(bookImage);
        }

        // Hiển thị tên và tác giả sách
        if (name != null) {
            bookName.setText(name);
        }
        if (author != null) {
            bookAuthor.setText("Tác giả: " + author);
        }

//        // Mở URL nội dung khi người dùng nhấn vào nút "Đọc Sách"
//        readBook.setOnClickListener(v -> {
//            if (contentUrl != null && !contentUrl.isEmpty()) {
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(contentUrl));
//                startActivity(browserIntent);
//            }
//        });
        // Mở nội dung sách trong Activity mới
        readBook.setOnClickListener(v -> {
            if (contentUrl != null && !contentUrl.isEmpty()) {
                Intent intent = new Intent(getContext(), ContentActivity.class);
                intent.putExtra("contentUrl", contentUrl); // Truyền URL qua Intent
                startActivity(intent);
            }
        });


        // Thiết lập RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        loadRelatedStories();

        return view;
    }

    private void loadRelatedStories() {
        FirebaseDatabaseHelper databaseHelper = new FirebaseDatabaseHelper();
        databaseHelper.getStories(stories -> {
            storyAdapter = new StoryAdapter(requireContext(), new ArrayList<>(stories));

            recyclerView.setAdapter(storyAdapter);
        });
    }

    private void returnToPreviousFragment() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

}
