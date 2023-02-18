package com.example.onlinewaiter.Models;

public class ViewPagerItem {
    String imageId, header, description;

    public ViewPagerItem(String imageId, String heading, String description) {
        this.imageId = imageId;
        this.header = heading;
        this.description = description;
    }

    public String getImageID() {
        return imageId;
    }

    public String getHeader() {
        return header;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
