package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_tweet);
        tweet = (Button) findViewById(R.id.bReply);
        profilePicture = (ImageView) findViewById(R.id.profile_pic);

        Intent i = getIntent();
        imageUrl= i.getStringExtra("profile_image");


        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(35));
        
        Glide.with(this)
                .load(imageUrl)
                .apply(requestOptions)
                .into(profilePicture);


        // set edit text value from intent extra
        tweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etItemText = (EditText)findViewById(R.id.etReply);
                myTweetText= etItemText.getText().toString();
                Log.d("twitter", myTweetText);


                client.sendTweet(myTweetText, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Intent i = new Intent();
                        // pass updated item text as extra
                        i.putExtra(NEW_TWEET, myTweetText);
                        try {
                            Tweet tweet = Tweet.fromJSON(response);
                            i.putExtra("tweet",tweet);
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





    }
