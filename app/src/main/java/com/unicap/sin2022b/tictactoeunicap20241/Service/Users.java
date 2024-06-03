package com.unicap.sin2022b.tictactoeunicap20241.Service;

public class Users {
    private String name;
    private String profile;

    private static Users instance;

    public static Users getInstance() {
        if (instance == null) {
            instance = new Users();
        }
        return instance;
    }

    public String getName() {
        return name;
    }

    public String getProfile() {
        return profile;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}

