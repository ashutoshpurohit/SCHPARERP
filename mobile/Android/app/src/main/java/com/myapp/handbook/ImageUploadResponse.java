package com.myapp.handbook;

/**
 * Created by SAshutosh on 9/23/2016.
 */

public class ImageUploadResponse {

    public ImageUploadResponse(String imageName, String imageUrl){
        this.ImageName=imageName;
        this.ImageUrl=imageUrl;
    }
    public String getImageName() {
        return ImageName;
    }

    public void setImageName(String imageName) {
        this.ImageName = imageName;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.ImageUrl = imageUrl;
    }

    String ImageName;
    String ImageUrl;
}
