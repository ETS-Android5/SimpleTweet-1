package com.codepath.apps.restclienttemplate.models;

import androidx.core.text.HtmlCompat;

import com.codepath.apps.restclienttemplate.utils.TimeFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Tweet {
    public String body;
    public String createdAt;
    public String relativeTimestamp;
    public String timestamp;
    public User user;
    public boolean retweeted = false;
    public boolean liked = false;

    public Tweet() {
    }

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = HtmlCompat.fromHtml(jsonObject.getString("text"), HtmlCompat.FROM_HTML_MODE_LEGACY).toString();
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.timestamp = TimeFormatter.getTimeStamp(tweet.createdAt);
        tweet.relativeTimestamp = TimeFormatter.getTimeDifference(tweet.createdAt);
        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;

    }

    public void retweet(boolean status) {
        this.retweeted = status;
    }

    public void like(boolean status) {
        this.liked = status;
    }
}
