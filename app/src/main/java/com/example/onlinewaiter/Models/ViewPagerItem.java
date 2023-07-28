package com.example.onlinewaiter.Models;

public class ViewPagerItem {
    private String imageId, header, description;
    private boolean showScrollIcon;

    public ViewPagerItem() {}

    public ViewPagerItem(String imageId, String heading, String description, boolean showScrollIcon) {
        this.imageId = imageId;
        this.header = heading;
        this.description = description;
        this.showScrollIcon = showScrollIcon;
    }

    public String getHeader() {
        return header;
    }

    public String getDescription() {
        return description;
    }

    public String getImageId() {
        return imageId;
    }

    public boolean isShowScrollIcon() {
        return showScrollIcon;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
