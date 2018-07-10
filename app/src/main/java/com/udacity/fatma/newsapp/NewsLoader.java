package com.udacity.fatma.newsapp;

import android.content.Context;

import android.content.AsyncTaskLoader;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * Created by fali10 on 7/3/2018.
 */

public class NewsLoader extends AsyncTaskLoader {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = NewsLoader.class.getName();

    /**
     * Query URL
     */
    private String mUrl;
    /**
     * Constructs a new {@link NewsLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
     NewsLoader(Context context,String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }


    @Nullable
    @Override
    public List<News> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of News.
        return QueryUtils.fetchNewsDetails(mUrl);
    }
}
