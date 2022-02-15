package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

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
    public String body = "";
    public String createdAt;
    public String relativeTimestamp;
    public String timestamp;
    public String nativeImageUrl;
    public User user;
    public int retweets;
    public int favorites;
    public boolean retweeted = false;
    public boolean liked = false;

    public Tweet() {
    }

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        boolean isRetweet = jsonObject.getString("full_text").startsWith("RT");
        try {
            tweet.nativeImageUrl = jsonObject.getJSONObject("entities").getJSONArray("media")
                                                                        .getJSONObject(0)
                                                                        .getString("media_url_https");
        } catch (JSONException ignored) {
        }
        tweet.body += isRetweet ? "RT " : "";
        tweet.body += HtmlCompat.fromHtml((isRetweet ? jsonObject.getJSONObject("retweeted_status") : jsonObject).getString("full_text"), HtmlCompat.FROM_HTML_MODE_LEGACY).toString();
        tweet.createdAt = (isRetweet ? jsonObject.getJSONObject("retweeted_status") : jsonObject).getString("created_at");
        tweet.user = User.fromJson((isRetweet ? jsonObject.getJSONObject("retweeted_status") : jsonObject).getJSONObject("user"));
        tweet.timestamp = TimeFormatter.getTimeStamp(tweet.createdAt);
        tweet.relativeTimestamp = TimeFormatter.getTimeDifference(tweet.createdAt);
        tweet.retweets = (isRetweet ? jsonObject.getJSONObject("retweeted_status") : jsonObject).getInt("retweet_count");
        tweet.favorites = (isRetweet ? jsonObject.getJSONObject("retweeted_status") : jsonObject).getInt("favorite_count");
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
