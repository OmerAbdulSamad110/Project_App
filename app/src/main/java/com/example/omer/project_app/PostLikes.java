package com.example.omer.project_app;

import android.graphics.Bitmap;

public class PostLikes {
    int likeId;
    int postId;
    int likerId;
    int likerName;
    byte[] likerImage1;
    Bitmap likerImage2;

    PostLikes(int likeId, int postId, int likerId, int likerName, Bitmap likerImage2) {
        this.likeId = likeId;
        this.postId = postId;
        this.likerId = likerId;
        this.likerName = likerName;
        this.likerImage2 = likerImage2;
    }
}
