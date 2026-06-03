package com.carrental.gui;

public class SessionManager {
    private static Long userId;
    private static String userEmail;
    private static String userRole;
    private static String userFullName;



    public static void login(Long id, String email, String role, String fullName) {
        userId = id; userEmail = email; userRole = role; userFullName = fullName;
    }
    public static void logout() {
        userId = null; userEmail = null; userRole = null; userFullName = null;
    }
    public static Long getUserId()         { return userId; }
    public static String getUserEmail()    { return userEmail; }
    public static String getUserRole()     { return userRole; }
    public static String getUserFullName() { return userFullName; }
    public static boolean isAdmin()        { return "ADMIN".equalsIgnoreCase(userRole); }
    public static boolean isLoggedIn()     { return userId != null; }
    public static void clear(){
        userId = null; userEmail = null; userRole = null; userFullName = null;
    }
}