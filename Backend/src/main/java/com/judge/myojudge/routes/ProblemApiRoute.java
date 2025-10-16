package com.judge.myojudge.routes;

public final class ProblemApiRoute {
    public static final String PROBLEM_DELETE_ALL = "/api/problem/v1/remove/all";
    public static final String PROBLEM_DELETE_BY_HANDLE = "/api/problem/v1/remove/{handle}";
    public static final String PROBLEM_FETCH_BY_ID_V1 = "/api/problem/v1/get/{id}";
    public static final String PROBLEM_FETCH_ALL = "/api/problem/v1/all";
    public static final String PROBLEM_FETCH_BY_CATEGORY = "/api/problem/v1/category/{category}";
    public static final String PROBLEM_FETCH_BY_ID_V2= "/api/problem/v2/get/{id}";
    public static final String PROBLEM_SAVE= "/api/problem/v1/save";
    public static final String PROBLEM_UPDATE_BY_ID= "/api/problem/v1/update/{id}";

}
