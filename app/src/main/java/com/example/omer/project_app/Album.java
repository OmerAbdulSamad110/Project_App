package com.example.omer.project_app;

import android.graphics.Bitmap;

public class Album {
    int albumId;
    int accId;
    String albumName;
    String albumPrivacy;
    Bitmap thumbnail;

    Album(int albumId, int accId, String albumName, String albumPrivacy, Bitmap thumbnail) {
        this.albumId = albumId;
        this.accId = accId;
        this.albumName = albumName;
        this.albumPrivacy = albumPrivacy;
        this.thumbnail = thumbnail;
    }
}
