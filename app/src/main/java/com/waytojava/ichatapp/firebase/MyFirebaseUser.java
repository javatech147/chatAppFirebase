package com.waytojava.ichatapp.firebase;

public class MyFirebaseUser {
    public String name;
    public String email;
    public String deviceToken;
    public String profileimage;

    public MyFirebaseUser() {
    }

    public MyFirebaseUser(String name, String email, String deviceToken, String profileimage) {
        this.name = name;
        this.email = email;
        this.deviceToken = deviceToken;
        this.profileimage = profileimage;
    }

    public MyFirebaseUser(String name, String email, String deviceToken) {
        this.name = name;
        this.email = email;
        this.deviceToken = deviceToken;
    }
}
