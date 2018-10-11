package com.example.omer.project_app;

import android.graphics.Bitmap;

public class Account {
    int userId;
    int loggedIn;
    Bitmap userDP;
    String userName;
    String userFName;
    String userLName;
    String userGender;
    String userEmail;
    String userPassword;
    String userDOB;
    String userAddress;
    String userStudy;
    String userWork;
    //
    byte[] userImg;

    Account(int userId, int loggedIn, Bitmap userDP, String userName, String userFName, String userLName, String userGender,
            String userEmail, String userPassword, String userDOB, String userAddress, String userStudy,String userWork) {
        this.userId = userId;
        this.loggedIn = loggedIn;
        this.userDP = userDP;
        this.userName = userName;
        this.userFName = userFName;
        this.userLName = userLName;
        this.userGender = userGender;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userDOB = userDOB;
        this.userAddress = userAddress;
        this.userStudy = userStudy;
        this.userWork = userWork;

    }

    //For display constructor
    Account(int userId, Bitmap userDP, String userName) {
        this.userId = userId;
        this.userDP = userDP;
        this.userName = userName;
    }
}