package com.example.readstoryapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AuthorFragment extends Fragment {
    private RecyclerView recyclerView;
    private AuthorAdapter adapter;
    private List<Story> stories;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_author, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        stories = new ArrayList<>();
        adapter = new AuthorAdapter(stories);
        recyclerView.setAdapter(adapter);

        // Lấy tên tác giả từ Bundle
        String authorName = getArguments() != null ? getArguments().getString("author_name") : "";

        // Hiển thị tiêu đề
        TextView title = view.findViewById(R.id.title);
        title.setText("Tên tác giả: " + authorName);

        // Truy vấn dữ liệu từ Firebase
        fetchStoriesByAuthor(authorName);

        return view;
    }

    private void fetchStoriesByAuthor(String authorName) {
        DatabaseReference storiesRef = FirebaseDatabase.getInstance().getReference("stories");

        storiesRef.orderByChild("author").equalTo(authorName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                stories.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Story story = snapshot.getValue(Story.class);
                    stories.add(story);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load stories", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
