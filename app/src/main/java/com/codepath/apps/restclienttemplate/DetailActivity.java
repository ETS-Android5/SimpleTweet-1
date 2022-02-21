package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.databinding.ActivityDetailBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Headers;

public class DetailActivity extends AppCompatActivity {

    public static final String TAG = "DetailActivity";
    ActivityDetailBinding binding;
    ImageView ivProfilePicture;
    ImageView ivTweetImage;
    TwitterClient client;
    Toolbar toolbar;
    Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        Map<String, int[]> imageMap = new HashMap<>();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        tweet = (Tweet) Parcels.unwrap(extras.getParcelable("tweet"));
        imageMap = (Map<String, int[]>) extras.getSerializable("image_map");
        client = TwitterApp.getRestClient(this);

        binding.setTweet(tweet);

        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        ivTweetImage = findViewById(R.id.ivTweetImage);

        Glide.with(this)
                .load(tweet.user.publicImageUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_round_account_circle_24)
                .into(ivProfilePicture);

        if(tweet.nativeImageUrl != null) {
            int[] dimens = imageMap.get(tweet.nativeImageUrl);
            final int RADIUS = 30;
            Glide.with(this)
                    .load(tweet.nativeImageUrl)
                    .fitCenter()
                    .transform(new RoundedCorners(RADIUS))
                    .override(dimens[0], dimens[1])
                    .into(ivTweetImage);
            //ivTweetImage.setVisibility(View.VISIBLE);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Tweet");
        binding.executePendingBindings();

        binding.ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.replyTextEdit.requestFocus();
            }
        });

        binding.ivRetweet.setSelected(tweet.retweeted);
        binding.ivRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tweet.retweet(!tweet.retweeted);
                view.setSelected(tweet.retweeted);
                tweet.retweetCount = tweet.retweeted ? tweet.retweetCount + 1 : tweet.retweetCount - 1;
                binding.tvRetweetCount.setText(String.format("%d Retweets", tweet.retweetCount));
            }
        });

        binding.ivLike.setSelected(tweet.liked);
        binding.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tweet.like(!tweet.liked);
                view.setSelected(tweet.liked);
                tweet.favoritesCount = tweet.liked ? tweet.favoritesCount + 1 : tweet.favoritesCount - 1;
                binding.tvLikeCount.setText(String.format("%d Likes", tweet.favoritesCount));
            }
        });

        binding.replyTextEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                binding.btnCreateReply.setVisibility(b ? View.VISIBLE: View.GONE);
            }
        });

        binding.replyTextLayout.setPrefixText(String.format("%s ", binding.tvScreenName.getText().toString()));
        binding.btnCreateReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = binding.replyTextLayout.getPrefixText() + binding.replyTextEdit.getText().toString();
                client.publishTweet(tweetContent, tweet.id, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess to publish tweet");
                        Snackbar.make(view, String.format("Reply to @%s sent!", tweet.user.screenName), Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.i(TAG, "onFailure to publish tweet");
                    }
                });
            }
        });
    }
}