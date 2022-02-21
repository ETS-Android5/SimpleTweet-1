package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    public static final String TAG = "ComposeActivity";
    public static final int MAX_TWEET_LENGTH = 140;

    ActivityComposeBinding binding;
    boolean isReply;
    String parentScreenName;
    String replyPrefix;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_compose);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            isReply = extras.getBoolean("isReply", false);
            if (isReply) {
                binding.parentUserContainer.setVisibility(View.VISIBLE);
                parentScreenName = extras.getString("parent_user_screen_name");
                replyPrefix = String.format("@%s", parentScreenName);
                binding.parentScreenName.setText(parentScreenName);
                binding.etLayout.setPrefixText(replyPrefix);
            }
        }
        binding.etCompose.requestFocus();
        client = TwitterApp.getRestClient(this);

        // Set click listener on button
        binding.btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = binding.etCompose.getText().toString();
                if(tweetContent.isEmpty()) {
                    Snackbar.make(ComposeActivity.this, binding.btnTweet, getString(R.string.w_empty_tweet), Snackbar.LENGTH_LONG).show();
                    return;
                }
                if(tweetContent.length() > MAX_TWEET_LENGTH) {
                    Snackbar.make(ComposeActivity.this, binding.btnTweet, getString(R.string.w_max_tweet_length), Snackbar.LENGTH_LONG).show();
                    return;
                }
                Snackbar.make(ComposeActivity.this, binding.btnTweet, tweetContent, Snackbar.LENGTH_LONG).show();

                JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess to publish tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "Published tweet says: " + tweet);

                            if(isReply) {
                                Intent i = new Intent(ComposeActivity.this, TimelineActivity.class);
                                i.putExtra("tweet", Parcels.wrap(tweet));
                                startActivity(i);
                            }
                            Intent i = new Intent();
                            // set result code and bundle data
                            i.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK, i);
                            // closes the activity, pass data to parent
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure to publish tweet", throwable);
                    }
                };

                // Make API call to Twitter to publish
                if(isReply) {
                    client.publishTweet(String.format("%s %s", binding.etLayout.getPrefixText(), tweetContent), extras.getLong("parent_id"), handler);
                } else {
                    client.publishTweet(tweetContent, handler);
                }
            }
        });


    }
}