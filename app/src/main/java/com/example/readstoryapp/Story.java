package com.example.readstoryapp;

public class Story {
    private String name;
    private String category;
    private String author;
    private String imageUrl;
    private String contentUrl;

    // Constructor không tham số (cần thiết cho Firebase)
    public Story() {
        // Empty constructor needed for Firebase
    }

    // Constructor với tham số
    public Story(String name, String category, String author, String imageUrl, String contentUrl) {
        this.name = name;
        this.category = category;
        this.author = author;
        this.imageUrl = imageUrl;
        this.contentUrl = contentUrl;
    }

    // Getter và Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }
}
