package com.example.readstoryapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {
    private List<Story> stories;

    public StoryAdapter(List<Story> stories) {
        this.stories = stories;
    }

    @Override
    public StoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_related_story, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StoryViewHolder holder, int position) {
        Story story = stories.get(position);
        holder.bookName.setText(story.getName());
        holder.bookAuthor.setText(story.getAuthor());
        if (story.getImageUrl() != null && !story.getImageUrl().isEmpty()) {
            Picasso.get().load(story.getImageUrl()).into(holder.bookImage);
        }
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    public static class StoryViewHolder extends RecyclerView.ViewHolder {
        TextView bookName, bookAuthor;
        ImageView bookImage;

        public StoryViewHolder(View itemView) {
            super(itemView);
            bookName = itemView.findViewById(R.id.bookName);
            bookAuthor = itemView.findViewById(R.id.bookAuthor);
            bookImage = itemView.findViewById(R.id.bookImage);
        }
    }
}
