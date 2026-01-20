package com.judge.myojudge.routes;

public final class AuthApiRoute {

    private AuthApiRoute() {} // Prevent instantiation

    public static final String BASE = "/api/v1/auth";

    // User Authentication
    public static final String REGISTER = BASE + "/register";       // POST
    public static final String LOGIN = BASE + "/login";             // POST
    public static final String GOOGLE_LOGIN = BASE + "/login/google"; // POST
    public static final String LOGOUT = BASE + "/logout";           // POST
}
