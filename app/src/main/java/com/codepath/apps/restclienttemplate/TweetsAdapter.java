package com.codepath.apps.restclienttemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.core.util.Pair;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    public static final String TAG = "ADAPTER";
    Context context;
    List<Tweet> tweets;

    // Pass in context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // For each row, inflate a layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    // Bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data at position
        Tweet tweet = tweets.get(position);
        // Bind the tweet with view holder
        holder.bind(tweet);

    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public void clear() {
        tweets.clear();
        notifyItemRangeRemoved(0, tweets.size());
    }

    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyItemRangeChanged(0, tweets.size());
    }

    // Define a viewholder
    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemTweetBinding binding;

        View mView;
        ImageView ivProfilePicture;
        ImageView ivNativeImage;
        ImageView ivRetweet;
        ImageView ivLike;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemTweetBinding.bind(itemView);
            ivRetweet = itemView.findViewById(R.id.ivRetweet);
            ivLike = itemView.findViewById(R.id.ivLike);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            ivNativeImage = itemView.findViewById(R.id.ivTweetImage);
        }

        public void bind(Tweet tweet) {
            binding.setTweet(tweet);

            binding.tweetContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, DetailActivity.class);
                    Bundle extras = new Bundle();
                    extras.putSerializable("image_map", tweet.nativeImagePair);
                    extras.putParcelable("tweet", Parcels.wrap(tweet));
                    i.putExtras(extras);
                    Pair<View, String> p1 = Pair.create(binding.tvBody, "status");
                    Pair<View, String> p2 = Pair.create(ivProfilePicture, "pfp");
                    Pair<View, String> p3 = Pair.create(ivNativeImage, "twimage");
                    ActivityOptionsCompat options = ActivityOptionsCompat
                            .makeSceneTransitionAnimation((Activity) context, p1, p2, p3);
                    context.startActivity(i, options.toBundle());
                }
            });

            ivRetweet.setSelected(tweet.retweeted);
            ivRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tweet.retweet(!tweet.retweeted);
                    view.setSelected(tweet.retweeted);
                    tweet.retweetCount = tweet.retweeted ? tweet.retweetCount + 1 : tweet.retweetCount - 1;
                    binding.tvRetweetCount.setText(String.valueOf(tweet.retweetCount));
                }
            });

            ivLike.setSelected(tweet.liked);
            ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tweet.like(!tweet.liked);
                    view.setSelected(tweet.liked);
                    tweet.favoritesCount = tweet.liked ? tweet.favoritesCount + 1 : tweet.favoritesCount - 1;
                    binding.tvFavCount.setText(String.valueOf(tweet.favoritesCount));
                }
            });

            Glide.with(context).load(tweet.user.publicImageUrl)
                    .circleCrop()
                    .placeholder(R.drawable.ic_round_account_circle_24)
                    .into(ivProfilePicture);

            if(tweet.nativeImageUrl != null) {
                int[] dimens = tweet.nativeImagePair.get(tweet.nativeImageUrl);
                Glide.with(context).load(tweet.nativeImageUrl)
                        .fitCenter()
                        .override(dimens[0], dimens[1])
                        .into(ivNativeImage);
            } else {
                ivNativeImage.setVisibility(View.GONE);
            }

            binding.executePendingBindings();
        }
    }
}
