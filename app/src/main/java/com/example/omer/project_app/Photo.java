package com.example.omer.project_app;

import android.graphics.Bitmap;

public class Photo {
    int imageId;
    int albumId;
    int accId;
    Bitmap image;

    Photo(int imageId, int albumId, int accId, Bitmap image) {
        this.imageId = imageId;
        this.albumId = albumId;
        this.accId = accId;
        this.image = image;
    }

    Photo(int imageId, int albumId, Bitmap image) {
        this.imageId = imageId;
        this.albumId = albumId;
        this.image = image;
    }
}
