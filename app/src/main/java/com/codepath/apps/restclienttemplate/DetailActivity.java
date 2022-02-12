package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ActivityDetailBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.util.Objects;

public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding binding;
    ImageView ivProfilePicture;
    Toolbar toolbar;
    Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        binding.setTweet(tweet);

        ivProfilePicture = findViewById(R.id.ivProfilePicture);

        Glide.with(this)
                .load(tweet.user.publicImageUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_round_account_circle_24)
                .into(ivProfilePicture);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Tweet");
        binding.executePendingBindings();
    }
}