package com.example.readstoryapp;

public class Story {
    private String category;
    private String name;
    private String imageUrl;

    // Constructor không tham số (để Firebase có thể tạo đối tượng tự động)
    public Story() {
    }

    // Getter và Setter
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}


