package com.example.readstoryapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
//adapter cho list homefragment
public class CommonBooksAdapter extends RecyclerView.Adapter<CommonBooksAdapter.ViewHolder> {

    private List<String> imageUrls;
    private List<String> bookNames;

    public CommonBooksAdapter(List<String> imageUrls, List<String> bookNames) {
        this.imageUrls = imageUrls;
        this.bookNames = bookNames;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        String name = bookNames.get(position);

        // Load ảnh vào ImageView
        Picasso.get().load(imageUrl).into(holder.imageView);

        // Gán tên sách vào TextView
        holder.textView.setText(name);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public void updateData(List<String> newImageUrls, List<String> newBookNames) {
        // Cập nhật lại danh sách dữ liệu
        this.imageUrls.clear();
        this.imageUrls.addAll(newImageUrls != null ? newImageUrls : new ArrayList<>());

        this.bookNames.clear();
        this.bookNames.addAll(newBookNames != null ? newBookNames : new ArrayList<>());

        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewItem);
            textView = itemView.findViewById(R.id.textViewName);
        }
    }


}

