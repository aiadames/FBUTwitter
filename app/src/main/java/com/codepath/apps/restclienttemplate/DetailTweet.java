package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DetailTweet extends AppCompatActivity {

    TwitterClient client = TwitterApp.getRestClient(this);
    String tweetBody;
    String tweetUserName;
    String tweetName;
    String tweetImage;
    TextView tweetTime;


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
        TextView screenName = (TextView) findViewById(R.id.tvScreenName);
        tweetTime = (TextView) findViewById(R.id.tvTweetTime);



        tweetUserName = i.getStringExtra("tweet_user");
        tweetName = i.getStringExtra("tweet_name");
        tweetBody = i.getStringExtra("tweet_body");
        tweetImage = i.getStringExtra("tweet_image");

        tweetTime.setText(getRelativeTimeAgo(i.getStringExtra("tweet_time")));



        userName.setText(tweetName);
        body.setText(tweetBody);
        screenName.setText("@" +tweetUserName);



        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(35)).format(DecodeFormat.PREFER_ARGB_8888);


        Glide.with(this)
                .load(tweetImage)
                .apply(requestOptions)
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

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;

    }




}



