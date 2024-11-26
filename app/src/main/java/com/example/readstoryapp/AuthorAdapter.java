package com.example.readstoryapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
//adapter sau khi ấn tác giả
public class AuthorAdapter extends RecyclerView.Adapter<AuthorAdapter.ViewHolder> {

    private List<Story> stories;

    public AuthorAdapter(List<Story> stories) {
        this.stories = stories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Story story = stories.get(position);

        // Hiển thị "Tên truyện: " và tên truyện
        holder.storyName.setText("Tên truyện: " + story.getName());

        // Hiển thị "Thể loại: " và thể loại
        holder.storyCategory.setText("Thể loại: " + story.getCategory());

        // Tải ảnh từ imageUrl
        Glide.with(holder.itemView.getContext())
                .load(story.getImageUrl())
                .placeholder(R.drawable.loading) // Ảnh chờ
                .error(R.drawable.error) // Ảnh lỗi
                .into(holder.storyImage);
    }


    @Override
    public int getItemCount() {
        return stories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView storyName;
        TextView storyCategory; // TextView mới cho thể loại
        ImageView storyImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            storyName = itemView.findViewById(R.id.story_name);
            storyCategory = itemView.findViewById(R.id.story_category); // Khởi tạo TextView mới
            storyImage = itemView.findViewById(R.id.story_image);
        }
    }

}

