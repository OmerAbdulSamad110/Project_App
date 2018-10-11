package com.example.omer.project_app;

public class Privacy {
    int accId;
    //3-op
    String postVisible;
    String postLC;
    String friendsVisible;
    String dobVisible;
    String addressVisible;
    String emailVisible;
    //2-op
    String sendFR;
    String sendMsg;

    Privacy(int accId, String postVisible, String postLC, String friendsVisible, String dobVisible, String addressVisible, String emailVisible, String sendFR, String sendMsg) {
        this.accId = accId;
        this.postVisible = postVisible;
        this.postLC = postLC;
        this.friendsVisible = friendsVisible;
        this.dobVisible = dobVisible;
        this.addressVisible = addressVisible;
        this.emailVisible = emailVisible;
        this.sendFR = sendFR;
        this.sendMsg = sendMsg;
    }
}
