package com.judge.myojudge.routes;

public final class UserApiRoute {

    private UserApiRoute() {} // Prevent instantiation

    public static final String BASE = "/api/v1/users";

    // Current logged-in user
    public static final String ME = BASE + "/me";                         // GET, PUT
    public static final String ME_PASSWORD = ME + "/password";            // PUT
    public static final String ME_PROFILE_IMAGE = ME + "/profile-image";  // PUT

    // Fetch user by username and ID (admin or other authorized roles)
    public static final String USER_BY_USERNAME = ME + "/{username}/{userId}"; // GET
}
