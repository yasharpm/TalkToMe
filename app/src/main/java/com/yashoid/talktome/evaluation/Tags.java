package com.yashoid.talktome.evaluation;

/**
 * Google limits:
 *      - Up to 25 user property names are supported.
 *      - Values can be up to 36 characters long.
 *      - Setting the value to null removes the user property.
 */
public interface Tags {

    /* ********** Visits ********** */
    /* 01 */ String LAST_VISIT = "last_visit";
    /* 02 */ String VISITS_LAST_SEVEN_DAYS = "visits_last_seven_days";
    /* 03 */ String VISITS_LAST_MONTH = "visits_last_month";

    /* ********** Posts ********** */
    /* 04 */ String SENT_POSTS = "sent_posts";
    /* 05 */ String LAST_POST_TIME = "last_post_time";
    /* 06 */ String HAS_UNSENT_POST = "has_unsent_post";
    /* 07 */ String MY_POSTS_LAST_VISIT = "my_posts_last_visit";
    /* 08 */ String MY_POSTS_VISIT_COUNT_LAST_SEVEN_DAYS = "my_posts_visit_count_last_seven_days"; // 36 characters.

    /* ********** Comments ********** */
    /* 09 */ String LAST_COMMENT_TIME = "last_comment_time";
    /* 10 */ String SENT_COMMENTS = "sent_comments";
    /* 11 */ String POSTS_THAT_COMMENTED = "posts_that_commented";

    /* ********** Shares ********** */
    /* 12 */ String SHARED_OTHERS_POSTS = "shared_others_posts";
    /* 13 */ String SHARED_SELF_POSTS = "shared_self_posts";
    /* 14 */ String SHARED_POSTS = "shared_posts";

}
