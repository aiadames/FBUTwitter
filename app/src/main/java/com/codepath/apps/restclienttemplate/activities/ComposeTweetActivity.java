package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;


public class ComposeTweetActivity extends AppCompatActivity {

    public TwitterClient client = TwitterApp.getRestClient(this);

    // utilizing ButterKnife to bind views and their initialization
    @BindView(R.id.etReply) EditText etItemText;
    @BindView(R.id.ibReply) Button tweetCompose;
    @BindView(R.id.ivProfileImage) ImageView profilePicture;
    @BindView(R.id.tvUserName) TextView userName;
    @BindView(R.id.tvName) TextView name;
    @BindView(R.id.tvFollowers) TextView tvFollowCount;
    @BindView(R.id.tvCharCount) TextView charCount;

    int charsLeft = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_tweet);
        ButterKnife.bind(this);

        // load User object from intent and set texts from user attributes
        User user =  Parcels.unwrap(getIntent().getParcelableExtra("user"));
        charCount.setText(String.valueOf(charsLeft));
        tvFollowCount.setText("followers: " + user.followers);
        userName.setText("@"+ user.screenName);
        name.setText(user.name);
        tweetCharCount();
        user.loadProfilePic(ComposeTweetActivity.this, user.profileImageUrl, null, profilePicture);

        // once user has clicked tweet button, grab text in edit text, send API client request,
        // and on success try to send tweet text back to Timeline Activity to post at position 0 (aka as newest tweet)
        tweetCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTweet();
            }
        });

    }



    // METHODS UTILIZED:
    // method to track on composition the amount of characters being typed in the EditText, will set text continuously
    public void tweetCharCount(){
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

    // method having client calling to send tweet text to server and upload, on success
    public void sendTweet(){
        String myTweetText = etItemText.getText().toString();
        if (charsLeft > 0) {
            client.sendTweet(myTweetText, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Intent intent = new Intent();
                    try {
                        Tweet tweet = Tweet.fromJSON(response);
                        intent.putExtra("tweet", (Parcelable) tweet);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // set the intent as the result of the activity
                    setResult(RESULT_OK, intent);
                    // close the activity and redirect to TimelineActivity (where call initially was made to ComposeTweet)
                    finish();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("TwitterClient", errorResponse.toString());
                    throwable.printStackTrace();
                }
            });
        } else if (charsLeft < 0){
            new AlertDialog.Builder(this)
                    .setTitle("Error: Character Count Exceeded")
                    .setMessage("Please enter a tweet under 150 characters.")
                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

    }


}





