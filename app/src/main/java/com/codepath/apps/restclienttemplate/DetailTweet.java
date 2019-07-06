package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class DetailTweet extends AppCompatActivity {

    TwitterClient client = TwitterApp.getRestClient(this);
    String tweetBody;
    String tweetUserName;
    String tweetName;
    String tweetImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_tweet);

        // setting up PAGE
        Intent i = getIntent();
        // views from intent

        ImageView profilePic = (ImageView) findViewById(R.id.ivProfileImage);
        TextView userName = (TextView) findViewById(R.id.tvUserName);
        TextView name = (TextView) findViewById(R.id.tvName);
        TextView body = (TextView) findViewById(R.id.tvBody);
        ImageButton reply = (ImageButton) findViewById(R.id.ibReply);
        ImageButton retweet = (ImageButton) findViewById(R.id.ibRetweet);
        ImageButton favorite = (ImageButton) findViewById(R.id.ibFavorite);

        tweetUserName = i.getStringExtra("tweet_user");
        tweetName = i.getStringExtra("tweet_name");
        tweetBody = i.getStringExtra("tweet_body");
        tweetImage = i.getStringExtra("tweet_image");

        userName.setText(tweetUserName);
        name.setText(tweetName);
        body.setText(tweetBody);

        Glide.with(this)
                .load(tweetImage)
                .into(profilePic);



        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    // create intent for the new activity
                    Intent intent = new Intent(DetailTweet.this, ReplyTweet.class);
                    intent.putExtra("user_reply_name",tweetUserName);
                    // show the activity
                    startActivity(intent);
                }


        });




    }

}


