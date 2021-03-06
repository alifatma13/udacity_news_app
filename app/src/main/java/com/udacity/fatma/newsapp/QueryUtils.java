package com.udacity.fatma.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fali10 on 7/3/2018.
 */

class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Google News API and return a list of {@link News} objects.
     */
    public static List<News> fetchNewsDetails(String requestUrl) {

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link News}s
        // Return the list of {@link News}s
        return extractFeatureFromJson(jsonResponse);
    }

    private static List<News> extractFeatureFromJson(String jsonResponse) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        String description;

        // Create an empty ArrayList that we can start adding news stories
        List<News> news = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.

        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            JSONObject responseJsonOBj = baseJsonResponse.getJSONObject("response");
            // Extract the JSONArray associated with the key called "items",
            // which represents a list of items (or news).

            JSONArray newsArray = responseJsonOBj.getJSONArray("results");
            JSONArray references = null;
            JSONArray tags = null;
            String author = "";

            // For each News in the newsArray, create an {@link News} object
            for (int i = 0; i < newsArray.length(); i++) {

                // Get a single News Item at position i within the list of News
                JSONObject currentNews = newsArray.getJSONObject(i);

                // For a given newsItem, extract the string with the news item Title
                String title = currentNews.getString("webTitle");

                // For a given newsItem, extract the string with the news Section Id
                String sectionId = currentNews.getString("sectionId");

                // For a given newsItem, extract the string with the news webUrl
                String url = currentNews.getString("webUrl");

                // For a given newsItem, extract the string with the news webPublicationDate
                String publicationDate = currentNews.getString("webPublicationDate");

                //get the author name of the News
                if (currentNews.has("tags")) {
                    tags = currentNews.getJSONArray("tags");


                    //get the references of the News
                    if (tags != null && tags.length() > 0) {
                        references = currentNews.getJSONArray("references");
                    }

                    //get the author name of the News
                    if (references != null && references.length() > 0) {
                        if (currentNews.getString("author") != null) {
                            author = currentNews.getString("author");
                        }
                    }
                }


                // Create a new {@link News} object with the News name, author name and
                // and url from the JSON response.
                News newsObj = new News(title, author, sectionId, url, publicationDate);
                Log.d("Title", title);
                Log.d("Author", author);
                Log.d("Section Id", sectionId);
                Log.d("url", url);
                Log.d("publicationDate", publicationDate);

                // Add the new {@link News} to the list of News.
                news.add(newsObj);

            }


        } catch (Exception e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
        }

        return news;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the News JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }



    /**
     * Convert the {@link InputStream} i
     * whole JSON response from the server.nto a String which contains the
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String requestUrl) {
        URL url = null;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }
}
