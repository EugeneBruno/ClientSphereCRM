package com.clientsphere.crm.util;

import com.clientsphere.crm.model.User;

public class CurrentUser {

    private static User user;

    private CurrentUser() {
        // Prevent creating instances
    }

    public static void login(User loggedInUser) {
        user = loggedInUser;
    }

    public static void logout() {
        user = null;
    }

    public static User getUser() {
        return user;
    }

    public static boolean isLoggedIn() {
        return user != null;
    }

    public static boolean isAdmin() {
        return user != null &&
                user.getRole() == User.Role.ADMIN;
    }

    public static boolean isUser() {
        return user != null &&
                user.getRole() == User.Role.USER;
    }
}