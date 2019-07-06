package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.codepath.apps.restclienttemplate.TimelineActivity.NEW_TWEET;


public class ComposeTweet extends AppCompatActivity {
    EditText etItemText;
    Button tweet;
    ImageView profilePicture;
    public String myTweetText;
    TwitterClient client = TwitterApp.getRestClient(this);
    String imageUrl;
    TextView userName;
    TextView name;
    public TextView tvFollowCount;
    public TextView tvMemberSince;

    TextView charCount;

    int charsLeft = 240;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_tweet);
        tweet = (Button) findViewById(R.id.bReply);
        profilePicture = (ImageView) findViewById(R.id.profile_pic);
        charCount = (TextView) findViewById(R.id.tvCharCount);
        charCount.setText(String.valueOf(charsLeft));
        etItemText = (EditText) findViewById(R.id.etReply);
        userName = (TextView) findViewById(R.id.tvUserName);
        name = (TextView) findViewById(R.id.tvName);


        tvFollowCount = (TextView) findViewById(R.id.tvFollowers);
       // tvMemberSince = (TextView) findViewById(R.id.tvMemberSince);

        tvFollowCount.setText("followers: " + getIntent().getStringExtra("followers"));
      //  tvMemberSince.setText("member since: "+ getIntent().getStringExtra("since"));






        tweetCount();


        userName.setText("@"+getIntent().getStringExtra("user_name"));
        name.setText(getIntent().getStringExtra("name"));


        Intent i = getIntent();
        imageUrl = i.getStringExtra("profile_image");


        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(35)).format(DecodeFormat.PREFER_ARGB_8888);


        Glide.with(this)
                .load(imageUrl)
                .apply(requestOptions)
                .into(profilePicture);


        // set edit text value from intent extra
        tweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTweetText = etItemText.getText().toString();
                Log.d("twitter", myTweetText);


                client.sendTweet(myTweetText, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Intent i = new Intent();
                        // pass updated item text as extra
                        i.putExtra(NEW_TWEET, myTweetText);
                        try {
                            Tweet tweet = Tweet.fromJSON(response);
                            i.putExtra("tweet", tweet);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // set the intent as the result of the activity
                        setResult(RESULT_OK, i);
                        // close the activity and redirect to main
                        finish();

                    }
                });

            }


        });



    }

    public void tweetCount(){
        etItemText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                charsLeft = charsLeft+ (before - count);
                charCount.setText(String.valueOf(charsLeft));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}