package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.codepath.apps.restclienttemplate.TimelineActivity.NEW_TWEET;

public class ReplyTweet extends AppCompatActivity {


    String userNameReply;
    public String myReplyTweetText;
    TwitterClient client = TwitterApp.getRestClient(this);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_tweet);

        TextView replyUser = (TextView) findViewById(R.id.tvReplyUser);
        final EditText myReply = (EditText) findViewById(R.id.etReply);



        Intent intent = getIntent();
        userNameReply = intent.getStringExtra("user_reply_name");
        replyUser.setText("@" + userNameReply);
        myReply.setText("@"+ userNameReply);




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
}
