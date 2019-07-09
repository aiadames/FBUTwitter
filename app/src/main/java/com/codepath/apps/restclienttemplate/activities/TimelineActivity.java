package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TweetAdapter;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;


import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class TimelineActivity extends AppCompatActivity {

    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;
    SwipeRefreshLayout swipeContainer;
    TwitterClient client;

    User user;

    // using in Intent calls to unwrap data on type of call
    public final static int TWEET_REQUEST_CODE = 20;
    public final static int TWEET_REPLY_REQUEST_CODE = 25;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        client = TwitterApp.getRestClient(this);

        // find the toolbar view inside the activity layout
        // sets the Toolbar to act as the ActionBar for this Activity window (make sure the toolbar exists in the activity and is not null)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // call method to set up the SwipeRefreshLayout:
        swipeContainer = findViewById(R.id.swipeContainer);
        setUpRefreshSwipe(swipeContainer);

        // call method to set up the RecyclerView:
        setUpRecyclerView(rvTweets);

        // for each tweet, call to the Twitter endpoint for "Verifying_Credentials" to retrieve each
        // user's profile image, screen name, name, created date, and followers to instantiate user for activities
        client.getTwitterDetails(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    user = User.fromJSON(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        populateTimeLine();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    private void populateTimeLine(){
        // make network request and make new anonymous class to deal with handling response from network call
        client.getHomeTimeline(new JsonHttpResponseHandler(){
            // network call results
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // iterate through the JSON array and for each entry, deserialize the JSON object
                updateReturnedTweets(response);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }
        });
    }


    public void onComposeAction(MenuItem miCompose) {
        // handle click of menu item (compose icon), pass in data of user for the ComposeTweet Activity through intent
        Intent intent = new Intent(this, ComposeTweetActivity.class);
        intent.putExtra("user", Parcels.wrap(user));
        startActivityForResult(intent, TWEET_REQUEST_CODE);
    }


    // returning from other activities via startActivityForResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        // if the activity completed ok:
        if (resultCode == RESULT_OK && requestCode == TWEET_REQUEST_CODE) {
            Tweet tweet = (Tweet) intent.getParcelableExtra("tweet");               // extract Tweet object from Intent
            tweets.add(0, tweet);                                                   // add new Tweet composed to ArrayList of tweets
            tweetAdapter.notifyItemInserted(0);                                   // notify the adapter that the model changed
            rvTweets.scrollToPosition(0);                                                 // scroll to top of RecyclerView
            Toast.makeText(this, "tweet added", Toast.LENGTH_SHORT).show();  // notify the user the operation completed ok
        }
        else if(resultCode == RESULT_OK && requestCode == TWEET_REPLY_REQUEST_CODE){
            Log.d("tweet", "reply code");
        }
    }


    public void fetchTimelineAsync (int page){
        // send the network request to fetch the updated data 'client' here is an instance of Android Async HTTP
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                tweetAdapter.clear();
                tweets.clear();
                updateReturnedTweets(response);
                swipeContainer.setRefreshing(false);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("DEBUG", "Fetch timeline error: " + errorResponse.toString());
            }
        });
    }



    // METHODS UTILIZED:

    // method to set up the rvTweets RecyclerView
    public void setUpRecyclerView(RecyclerView rvTweets){
        rvTweets = (RecyclerView) findViewById(R.id.rvTweet);
        // initialize the arraylist (data source)
        tweets = new ArrayList<>();
        // construct the adapter from this datasource
        tweetAdapter = new TweetAdapter(tweets);
        //recyclerView setup (layout manager, use adapter)
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        // set the adapter
        rvTweets.setAdapter(tweetAdapter);
        // add divider between RecyclerView items
        rvTweets.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
    }

    // method to set up the swipeContainer SwipeRefreshLayout
    public void setUpRefreshSwipe (SwipeRefreshLayout swipeContainer){
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // once the network request has completed successfully, call swipeContainer.setRefreshing(false)
                fetchTimelineAsync(0);
            }
        });

        // configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }


    public void updateReturnedTweets(JSONArray response){
        // iterate through the JSON array and for each entry, deserialize the JSON object
        for (int i = 0; i < response.length(); i++){
            try {
                Tweet tweet = Tweet.fromJSON(response.getJSONObject(i)); // convert each object to a Tweet mode
                tweets.add(tweet); // add that Tweet model to our data source
                String userScreen =tweet.user.screenName;
                tweetAdapter.notifyItemInserted(tweets.size()-1); // notify adapter that we've added an item (RecyclerView specific: notify what has changed)
            } catch (JSONException e){
                e.printStackTrace();
            }

        }
    }



}


