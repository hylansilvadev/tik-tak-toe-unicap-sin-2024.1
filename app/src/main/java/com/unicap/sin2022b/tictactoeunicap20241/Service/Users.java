package com.unicap.sin2022b.tictactoeunicap20241.Service;

public class Users {

    private static Users instance; // Private static instance variable

    private String userId;
    private String name;
    private String profile;

    private Users(String userId, String name, String profile) {
        this.userId = userId;
        this.name = name;
        this.profile = profile;
    }

    public static Users getInstance() {
        if (instance == null) {
            instance = new Users("", "", "");
        }
        return instance;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
