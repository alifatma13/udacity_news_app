package com.udacity.fatma.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by fali10 on 7/2/2018.
 */

public class NewsAdapter extends ArrayAdapter<News> {

    private ViewHolderNews viewHolderNews = null;
    String publishedAtDate = "";
    String formattedDate = "";
    String formattedTime = "";
    String formattedPublishedAt = "";

    /**
     * Constructs a new {@link NewsAdapter}.
     *
     * @param context of the app
     * @param news   is the list of books, which is the data source of the adapter
     */

    public NewsAdapter(@NonNull Context context, List<News> news) {
        super(context, 0, news);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_list_item, parent, false);

            viewHolderNews = new ViewHolderNews();
            viewHolderNews.newsTitleTextView = listItemView.findViewById(R.id.news_title);
            viewHolderNews.newsAuthorTextView = listItemView.findViewById(R.id.news_author);
            viewHolderNews.newsSectionTextView = listItemView.findViewById(R.id.news_section);
            viewHolderNews.newsPublishedAtTextView = listItemView.findViewById(R.id.news_publishedAt);
            listItemView.setTag(viewHolderNews);
        }
        else
        {
            viewHolderNews= (ViewHolderNews) listItemView.getTag();
        }
        // Find the news title at the given position in the list of News
        News currentNews = getItem(position);
        // Get the News Title from the currentNews object and set this text on
        // the newsTitleTextView.
        if(currentNews.getTitle()!=null)
        {
            viewHolderNews.newsTitleTextView.setText(currentNews.getTitle());
        }
        // Get the News's Author name from the currentNews object and set this text on
        // the authorNameTextView.
        if(currentNews.getAuthor()!=null)
        {
            viewHolderNews.newsAuthorTextView.setText(currentNews.getAuthor());
        }
        // Get the News's Description from the currentNews object and set this text on
        // the newsDescriptionTextView.
        if(currentNews.getSection()!=null) {
            viewHolderNews.newsSectionTextView.setText(currentNews.getSection());
        }
        // Get the News's Published Date from the currentNews object and set this text on
        // the newsPublishedAtTextView.
        if(currentNews.getPublishedAt()!=null) {
            publishedAtDate = currentNews.getPublishedAt();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Date dateObject = null;
            try {
                dateObject = sdf.parse(publishedAtDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //Date dateObject = new Date(publishedAtDate);
            formattedDate = formatDate(dateObject);
            formattedTime = formatTime(dateObject);
            formattedPublishedAt = formattedDate + " " + formattedTime;
            viewHolderNews.newsPublishedAtTextView.setText(formattedPublishedAt);
        }

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

    /**
     * Return the formatted date string
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        String formattedDate = dateFormat.format(dateObject);
        return formattedDate.toString();
    }

    /**
     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
     */
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }
}
