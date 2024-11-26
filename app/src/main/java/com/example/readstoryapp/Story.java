package com.example.readstoryapp;

public class Story {
    private String name;
    private String author;
    private String category;
    private String imageUrl;

    public Story() { }

    public Story(String name, String author, String category, String imageUrl) {
        this.name = name;
        this.author = author;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}



