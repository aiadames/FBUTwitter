package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.codepath.apps.restclienttemplate.TimelineActivity.NEW_TWEET;

public class ReplyTweet extends AppCompatActivity {

    TwitterClient client = TwitterApp.getRestClient(this);

    public String myReplyTweetText;
    public int charsLeft = 240;
    public String userNameReply;

    TextView charCount;
    TextView replyUser;
    EditText myReply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_tweet);
        replyUser = (TextView) findViewById(R.id.tvReplyUser);
        myReply = (EditText) findViewById(R.id.etReply);
        charCount = (TextView) findViewById(R.id.tvCharCount);
        charCount.setText(String.valueOf(charsLeft));
        tweetCount();

        // getting information from intent
        Intent intent = getIntent();
        userNameReply = intent.getStringExtra("user_reply_name");
        replyUser.setText("@" + userNameReply);
        myReply.setText("@"+ userNameReply);


        // set the tweet button that is on the ReplyTweet Activity on a click listener
        // create new Intent to send back to TimelineActivity and place the reply inside, setting RESULT_OK on success and finish activity
        Button replyButton = (Button)findViewById(R.id.bReply);
        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myReplyTweetText = myReply.getText().toString();
                client.sendTweet(myReplyTweetText, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Intent i = new Intent(ReplyTweet.this, TimelineActivity.class);
                        // pass updated item text as extra
                        i.putExtra(NEW_TWEET, myReplyTweetText);
                        // set the intent as the result of the activity
                        setResult(RESULT_OK, i);
                        // close the activity and redirect to main
                        finish();

                    }
                });


            }
        });

    }

    // method to track on composition the amount of characters being typed in the EditText, will set text continuously
    public void tweetCount(){
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

}