package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ReplyTweetActivity extends AppCompatActivity {

    TwitterClient client = TwitterApp.getRestClient(this);

    public String myReplyTweetText;
    public int charsLeft = 150;

    @BindView (R.id.tvReplyUser) TextView replyUser;
    @BindView (R.id.etReply) EditText myReply;
    @BindView (R.id.tvCharCount) TextView charCount;
    @BindView(R.id.ibReply) Button replyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_tweet);
        ButterKnife.bind(this);

        // set initial
        charCount.setText(String.valueOf(charsLeft));
        tweetCharacterCount();

        // getting information from intent, and instantiating new User
        User user =  Parcels.unwrap(getIntent().getParcelableExtra("user"));
        replyUser.setText("@" + user.screenName);
        myReply.setText("@"+ user.screenName);

        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTweet();
            }
        });

    }

    // method to track on composition the amount of characters being typed in the EditText, will set text continuously
    public void tweetCharacterCount(){
        myReply.addTextChangedListener(new TextWatcher() {
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


    public void sendTweet(){
        myReplyTweetText = myReply.getText().toString();
        if (charsLeft > 0) {
            client.sendTweet(myReplyTweetText, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Intent intent = new Intent(ReplyTweetActivity.this, TimelineActivity.class);
                    try {
                        Tweet tweet = Tweet.fromJSON(response);
                        intent.putExtra("tweet", Parcels.wrap(tweet));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setResult(RESULT_OK, intent);
                    // close the activity and redirect to main
                    finish();

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