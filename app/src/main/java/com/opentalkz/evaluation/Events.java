package com.opentalkz.evaluation;

public interface Events {

    /* ********** Mutual Events ********** */
    String EVENT_POSTED = "posted";
    String EVENT_DIDNT_POST = "didnt_post";
    String EVENT_COMMENTED = "commented";
    String EVENT_SHARED = "shared";
    String EVENT_REFRESHED_POSTS = "refreshed_posts";

    /* ********** AB Events ********** */
    String EVENT_VISITED = "visited";
    String EVENT_VISITED_MY_POSTS = "visited_my_posts";
    String EVENT_CRASHED = "crashed";

}
