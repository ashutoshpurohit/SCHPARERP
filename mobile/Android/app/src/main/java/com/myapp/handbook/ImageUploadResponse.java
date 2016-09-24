package com.myapp.handbook;

/**
 * Created by SAshutosh on 9/23/2016.
 */

public class ImageUploadResponse {
    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    String imageName;
    String imageUrl;
}
