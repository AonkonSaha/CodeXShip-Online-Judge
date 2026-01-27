package com.judge.myojudge.keys;


public class RedisKey {

    private RedisKey(){

    }

    /** User Redis Key Management
     * Profile Page
     * Auth
     * Rank Page
     */
    public static final String BASE_USER_KEY="USERS:";
    public static final String USER_SOLVED_PROBLEMS=BASE_USER_KEY+"SOLVED:";

    public static final String USER_AUTH =  BASE_USER_KEY+"USER:AUTH:";
    public static final String USER_PROFILE = BASE_USER_KEY+"USER:PROFILE:";
    public static final String USER_STATS =BASE_USER_KEY+ "USER:STATS:";
    public static final String LEADERBOARD_GLOBAL = "LEADERBOARD:GLOBAL";
    public static final String PER_USER_BASE_KEY=BASE_USER_KEY+"USER:EMAIL:";
    public static final String CACHED_TOTAL_USERS=BASE_USER_KEY+":total";
    public static final String RANK_PAGE_BASE_KEY="ranking:page:";
    public static final String RANK_BASE_KEY="ranking:";
    public static final String CACHED_TOTAL_PAGE_RANK_USERS=RANK_BASE_KEY+"total";

    /** Problem Redis Key Management
     * Problem page
     * Problem Category Page
     */
    public static final String BASE_PROBLEM_KEY="PROBLEMS:";
    public static final String PROBLEM_KEY=BASE_PROBLEM_KEY+"PROBLEM:";

}
