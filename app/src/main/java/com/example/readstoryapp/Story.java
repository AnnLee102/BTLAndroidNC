package com.example.readstoryapp;

public class Story {
    private String category;
    private String name;
    private String imageUrl;
    private String author;
    private String contentUrl;

    // No-argument constructor for Firebase
    public Story() {
        // Empty constructor needed for Firebase
    }

    // Constructor with parameters
    public Story(String name, String category, String author, String imageUrl, String contentUrl) {
        this.name = name;
        this.category = category;
        this.author = author;
        this.imageUrl = imageUrl;
        this.contentUrl = contentUrl;
    }


    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getContentUrl() { return contentUrl; }
    public void setContentUrl(String contentUrl) { this.contentUrl = contentUrl; }
}