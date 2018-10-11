package com.example.omer.project_app;

import android.graphics.Bitmap;

public class Comment {
    int commentId;
    int postId;
    int commenterId;
    Bitmap commenterImg;
    String commenterName;
    String comment;
    String commentDate;
    int commentLikes;

    Comment(int commentId, int postId, int commenterId, Bitmap commenterImg, String commenterName, String comment, String commentDate, int commentLikes) {
        this.commentId = commentId;
        this.postId = postId;
        this.commenterId = commenterId;
        this.commenterImg = commenterImg;
        this.commenterName = commenterName;
        this.comment = comment;
        this.commentDate = commentDate;
        this.commentLikes = commentLikes;
    }
}

