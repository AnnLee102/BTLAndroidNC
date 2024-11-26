package com.example.readstoryapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

// Adapter sau khi ấn search
public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    private Context context;
    private ArrayList<Story> storyList;

    // Constructor nhận context và list các câu chuyện
    public StoryAdapter(Context context, ArrayList<Story> storyList) {
        this.context = context;
        this.storyList = storyList;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.result_item, parent, false);
        return new StoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        Story story = storyList.get(position);

        // Đặt tên truyện, tác giả và thể loại
        holder.storyName.setText(story.getName());
        holder.storyAuthor.setText(story.getAuthor());
        holder.storyCategory.setText(story.getCategory());

        // Đặt ảnh truyện (nếu có)
        Picasso.get().load(story.getImageUrl()).into(holder.storyImage);
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    // ViewHolder cho từng item trong RecyclerView
    public class StoryViewHolder extends RecyclerView.ViewHolder {
        TextView storyName, storyAuthor, storyCategory;
        ImageView storyImage;

        public StoryViewHolder(View itemView) {
            super(itemView);
            storyName = itemView.findViewById(R.id.story_name);
            storyAuthor = itemView.findViewById(R.id.story_author);
            storyCategory = itemView.findViewById(R.id.story_category);
            storyImage = itemView.findViewById(R.id.story_image);
        }
    }
}
