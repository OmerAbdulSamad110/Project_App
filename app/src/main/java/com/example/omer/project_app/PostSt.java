package com.example.omer.project_app;

import android.graphics.Bitmap;

public class PostSt {
    int postId;
    int accId;
    Bitmap accImg;
    String accName;
    String postDate;
    String postText;
    Bitmap postImage;
    int postLikes;
    int postComments;
    String postPrivacy;
    String postPrivacyLC;
    int hasImage;
    int hasText;

    PostSt(int postId, int accId, Bitmap accImg, String accName, String postDate, String postText, Bitmap postImage, int postLikes, int postComments, String postPrivacy, int hasImage, int hasText) {
        this.postId = postId;
        this.accId = accId;
        this.accName = accName;
        this.accImg = accImg;
        this.postDate = postDate;
        this.postText = postText;
        this.postImage = postImage;
        this.postLikes = postLikes;
        this.postComments = postComments;
        this.postPrivacy = postPrivacy;
        this.hasImage = hasImage;
        this.hasText = hasText;
    }

    PostSt(int postId, int accId, Bitmap accImg, String accName, String postDate, String postText, Bitmap postImage, String postPrivacy, int hasImage, int hasText) {
        this.postId = postId;
        this.accId = accId;
        this.accName = accName;
        this.accImg = accImg;
        this.postDate = postDate;
        this.postText = postText;
        this.postImage = postImage;
        this.postPrivacy = postPrivacy;
        this.hasImage = hasImage;
        this.hasText = hasText;
    }

    PostSt(int postId, int accId, Bitmap accImg, String accName, String postDate, String postText, Bitmap postImage, String postPrivacy, String postPrivacyLC, int hasImage, int hasText) {
        this.postId = postId;
        this.accId = accId;
        this.accName = accName;
        this.accImg = accImg;
        this.postDate = postDate;
        this.postText = postText;
        this.postImage = postImage;
        this.postPrivacy = postPrivacy;
        this.postPrivacyLC = postPrivacyLC;
        this.hasImage = hasImage;
        this.hasText = hasText;
    }
}
