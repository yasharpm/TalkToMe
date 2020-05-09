package com.opentalkz.evaluation;

/**
 * Google limits:
 *      - Up to 25 user property names are supported.
 *      - Values can be up to 24 characters long.
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
    /* 08 */ String MY_POSTS_VISIT_COUNT_LAST_SEVEN_DAYS = "myposts_visit_cnt_seven"; // 23 characters.

    /* ********** Comments ********** */
    /* 09 */ String LAST_COMMENT_TIME = "last_comment_time";
    /* 10 */ String SENT_COMMENTS_LAST_SEVEN_DAYS = "sent_comments_last_seven";
    /* 11 */ String SENT_COMMENT_LAST_MONTH = "sent_comments_last_month";
    /* 12 */ String POSTS_THAT_COMMENTED = "posts_that_commented";

    /* ********** Shares ********** */
    /* 13 */ String SHARED_OTHERS_POSTS_LAST_MONTH = "shared_oposts_last_month";
    /* 14 */ String SHARED_SELF_POSTS_LAST_MONTH = "shared_sposts_last_month";
    /* 15 */ String SHARED_POSTS_LAST_MONTH = "shared_posts_last_month";

}
