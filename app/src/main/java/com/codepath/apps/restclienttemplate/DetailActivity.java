package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.databinding.ActivityDetailBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding binding;
    ImageView ivProfilePicture;
    ImageView ivTweetImage;
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
    }
}