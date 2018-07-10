package com.udacity.fatma.newsapp;

import android.graphics.Bitmap;

/**
 * News class having properties of News
 */


public class News {

    /**
     * Title of the news
     */
    private String mTitle;
    /**
     * Author of the news article
     */
    private String mAuthor;
    /**
     * Description of the news
     */
    private String mSection;
    /**
     * URL with elaborated news details
     */
    private String mURL;
    /**
     * Date when URL was published
     */
    private String mPublishedAt;



    /**
     * Constructs a new {@link News} object.
     *
     * @param title       is the title of the news
     * @param author      is the author of the news
     * @param section is the description of the news
     * @param url         is the URL of the news
     * @param publishedAt is the URL of the image of book's cover
     */

    public News(String title, String author, String section, String url, String publishedAt) {
        mTitle = title;
        mAuthor = author;
        mSection = section;
        mURL = url;
        mPublishedAt = publishedAt;
    }

    /**
     * Returns the Title of the News
     */
    String getTitle() {
        return mTitle;
    }

    /**
     * Returns the Author of the News
     */
    String getAuthor() {
        return mAuthor;
    }

    /**
     * Returns the Description of the News
     */
    String getSection() {
        return mSection;
    }

    /**
     * Returns the URL of the News
     */
    String getURL() {
        return mURL;
    }

    /**
     * Returns the Publishing Date of the News
     */
    String getPublishedAt() {
        return mPublishedAt;
    }


}
