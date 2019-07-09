package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailTweetActivity extends AppCompatActivity {

    TwitterClient client = TwitterApp.getRestClient(this);
    Tweet tweet;

    @BindView(R.id.ivProfileImage) ImageView profilePic;
    @BindView(R.id.tvName) TextView name;
    @BindView(R.id.tvBody) TextView body;
    @BindView(R.id.tvScreenName) TextView screenName;
    @BindView(R.id.tvTweetTime) TextView relativeTime;
    @BindView(R.id.ibReply) ImageButton reply;
    @BindView(R.id.ibFavorite) ImageButton favorite;
    @BindView(R.id.ibRetweet) ImageButton retweet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_tweet);
        ButterKnife.bind(this);

        // unwrap Parcelable
        tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        relativeTime.setText(tweet.relativeTime);
        name.setText(tweet.user.name);
        body.setText(tweet.body);
        screenName.setText("@" +tweet.user.screenName);
        (tweet.user).loadProfilePic(this,tweet.user.profileImageUrl, null, profilePic);

        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create intent for the new activity
                Intent intent = new Intent(DetailTweetActivity.this, ReplyTweetActivity.class);
                intent.putExtra("user", Parcels.wrap(tweet.user));
                // show the activity
                startActivity(intent);
            }
        });
    }

}



