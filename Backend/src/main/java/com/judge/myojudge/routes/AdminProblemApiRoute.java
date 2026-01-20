package com.judge.myojudge.routes;

public final class AdminProblemApiRoute {

    private AdminProblemApiRoute() {} // Prevent instantiation

    public static final String BASE = "/api/v1/admin/problems";

    // Fetch all problems
    public static final String GET_ALL = BASE;               // GET

    // Delete all problems
    public static final String DELETE_ALL = BASE;            // DELETE

    // Delete a problem by handle
    public static final String DELETE_BY_HANDLE = BASE + "/{handle}"; // DELETE
}
