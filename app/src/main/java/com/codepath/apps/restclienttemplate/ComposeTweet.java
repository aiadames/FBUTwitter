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

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static com.codepath.apps.restclienttemplate.TimelineActivity.NEW_TWEET;


public class ComposeTweet extends AppCompatActivity {

    TwitterClient client = TwitterApp.getRestClient(this);

    // utilizing ButterKnife to bind views and their initialization
    @BindView(R.id.etReply) EditText etItemText;
    @BindView(R.id.bReply) Button tweetCompose;
    @BindView(R.id.profile_pic) ImageView profilePicture;
    @BindView(R.id.tvUserName) TextView userName;
    @BindView(R.id.tvName) TextView name;
    @BindView(R.id.tvFollowers) TextView tvFollowCount;
    @BindView(R.id.tvCharCount) TextView charCount;

    String myTweetText;
    String imageUrl;
    int charsLeft = 240;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_tweet);

        // ButterKnife values bound to onCreate
        ButterKnife.bind(this);

        // load and set texts from intent values
        charCount.setText(String.valueOf(charsLeft));
        tvFollowCount.setText("followers: " + getIntent().getStringExtra("followers"));
        userName.setText("@"+getIntent().getStringExtra("user_name"));
        name.setText(getIntent().getStringExtra("name"));

        tweetCount();


        // from intent, retrieve imageUrl and use Glide to load the url, apply the rounded corners/resolution, and load into ImageView as profile picture
        Intent i = getIntent();
        imageUrl = i.getStringExtra("profile_image");
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(35)).format(DecodeFormat.PREFER_ARGB_8888);
        Glide.with(this)
                .load(imageUrl)
                .apply(requestOptions)
                .into(profilePicture);


        // once user has clicked tweet button, grab text in edit text, send API client request,
        // and on success try to send tweet text back to Timeline Activity to post at position 0 (aka as newest tweet)
        tweetCompose.setOnClickListener(new View.OnClickListener() {
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
                        // close the activity and redirect to TimelineActivity (where call initially was made to ComposeTweet)
                        finish();
                    }
                });
            }


        });

    }

    // method to track on composition the amount of characters being typed in the EditText, will set text continuously
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