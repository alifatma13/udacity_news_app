package com.udacity.fatma.newsapp;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>, View.OnClickListener{

    private static final String LOG_TAG=NewsActivity.class.getName();

    /**
     *
     * Constant value for News Loader ID
     */
    private static final int NEWS_LOADER_ID = 1;
    private String queryString = "politics";
    private String api_key = "909d2827-1f3b-48e9-9c1a-3b9cf1228827";
    private ListView newsListView;
    private EditText searchBox;
    private ImageView search;

    /**
     * URL of the API
     */
    private String REQUEST_URL="https://content.guardianapis.com/search";

    /**
     * Adapter for the list of News
     */

    private NewsAdapter mNewsAdapter;

    /**
     * View that is displayed when the list is empty
     */
    private TextView mEmptyStateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        searchBox = findViewById(R.id.search);
        search = findViewById(R.id.search_icon);
        search.setOnClickListener(this);

        // Find a reference to the {@link ListView} in the layout
        newsListView = findViewById(R.id.list);

        mEmptyStateView = findViewById(R.id.empty_view);

        newsListView.setEmptyView(mEmptyStateView);

        // Create a new adapter that takes an empty list of news as input
        mNewsAdapter = new NewsAdapter(this, new ArrayList<News>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(mNewsAdapter);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        // Get details on the currently active default data network
        if (connMgr.getActiveNetworkInfo() != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    News news = mNewsAdapter.getItem(position);

                    // Convert the String URL into a URI object (to pass into the Intent constructor)
                    Uri newsURI = Uri.parse(news.getURL());
                    // Create a new intent to view the news URI
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsURI);
                    // Send the intent to launch a new activity
                    startActivity(websiteIntent);

                            }
                        });

                // give message to user regarding the search
                mEmptyStateView.setText(R.string.news_search);
                // fetch the queryString
            if (!searchBox.getText().toString().equals("")) {
                queryString = searchBox.getText().toString();
            }
                // Reload the loader on orientation change
                getLoaderManager().initLoader(NEWS_LOADER_ID, null, this);

        }
        else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            // Update empty state with no connection error message
            mEmptyStateView.setText(R.string.no_internet_connection);
        }
    }



    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.VISIBLE);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));
        Uri baseUri = Uri.parse(REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("q", queryString);
        uriBuilder.appendQueryParameter("format", "json");
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("api-key", api_key);
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, final List<News> news) {
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display in case there are no search result for this search criteria
        mEmptyStateView.setText(R.string.init);

        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (news != null && !news.isEmpty()) {
            updateUi(news);

        } else {
            // Set empty state text
            mEmptyStateView.setText(R.string.empty_news_search);

            // Clear the adapter of previous data
            mNewsAdapter.clear();

        }

    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {

        mNewsAdapter.clear();

    }
    private void updateUi(List<News> news) {
        mNewsAdapter.addAll(news);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.search_icon:
                // destroy the previous search result present in the loader
                getLoaderManager().destroyLoader(NEWS_LOADER_ID);

                // fetch the book's name from the search box
                queryString = searchBox.getText().toString();

                // Get a reference to the ConnectivityManager to check state of network connectivity
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                // Get details on the currently active default data network
                NetworkInfo networkInfo = null;
                if (connMgr.getActiveNetworkInfo() != null) {
                    networkInfo = connMgr.getActiveNetworkInfo();
                }
                // If there is a network connection, fetch data
                if (networkInfo != null && networkInfo.isConnected()) {
                    // Get a reference to the LoaderManager, in order to interact with loaders.
                    LoaderManager loaderManager = getLoaderManager();
                    // Initialize the loader.
                    loaderManager.initLoader(NEWS_LOADER_ID, null, this);
                    hideKeyboard(this);

                } else {
                    // Otherwise, display error
                    // Hiding loading indicator so error message will be visible
                    View loadingIndicator = findViewById(R.id.loading_indicator);
                    loadingIndicator.setVisibility(View.GONE);
                    hideKeyboard(this);

                    // Update empty state with no connection error message
                    mEmptyStateView.setText(R.string.no_internet_connection);
                }
                break;
        }

    }

    // In order to change the background according to screen orientation
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getLoaderManager().initLoader(NEWS_LOADER_ID, null, this);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getLoaderManager().initLoader(NEWS_LOADER_ID, null, this);
        }
        hideKeyboard(this);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            // destory the previous search result present in the loader
            getLoaderManager().destroyLoader(NEWS_LOADER_ID);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        //Refresh your stuff here
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr.getActiveNetworkInfo() != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            // Initialize the loader.
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        }
    }
}
