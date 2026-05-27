package com.carrental.gui;

import com.carrental.entity.UserEntity;

public class SessionManager {
    private static UserEntity currentUser;

    public static void setCurrentUser(UserEntity user) { currentUser = user; }
    public static UserEntity getCurrentUser() { return currentUser; }
    public static boolean isAdmin() {
        return currentUser != null && currentUser.getRole().equals("ADMIN");
    }
}