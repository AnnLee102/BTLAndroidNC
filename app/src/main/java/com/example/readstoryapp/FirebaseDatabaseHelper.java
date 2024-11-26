package com.example.readstoryapp;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabaseHelper {
    private DatabaseReference mDatabase;

    public FirebaseDatabaseHelper() {
        mDatabase = FirebaseDatabase.getInstance().getReference("stories"); // Đường dẫn tới "stories" trong Firebase
    }

    public void getStories(Callback callback) {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Story> stories = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Story story = snapshot.getValue(Story.class);
                    stories.add(story);
                }
                callback.onCallback(stories);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Firebase", "Failed to read value.", databaseError.toException());
            }
        });
    }

    public interface Callback {
        void onCallback(List<Story> stories);
    }
}
