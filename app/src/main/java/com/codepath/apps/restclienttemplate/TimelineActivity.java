package com.codepath.apps.restclienttemplate;

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

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {


    TwitterClient client;
    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;

    private SwipeRefreshLayout swipeContainer;

    String imageUrl;
    String userName;
    String name;
    String createdOn;
    String followers;

    // using in Intent calls to unwrap data
    public final static String NEW_TWEET  = "new tweet";
    public final static int TWEET_REQUEST_CODE = 20;
    public final static int TWEET_REPLY_REQUEST_CODE = 25;


    public String profilePic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        client = TwitterApp.getRestClient(this);

        // find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // sets the Toolbar to act as the ActionBar for this Activity window.
        // make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);


        // for each tweet, call to the Twitter endpoint for "Verifying_Credentials" to retrieve each
        // user's profile image, screen name, name, created date, and followers
        client.getTwitterDetails(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    User user = User.fromJSON(response);
                    profilePic = user.profileImageUrl;
                    userName = user.screenName;
                    name = user.name;
                    createdOn = user.createdOn;
                    followers = (String.valueOf(user.followers));

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });


        // find the RecyclerView
        rvTweets = (RecyclerView) findViewById(R.id.rvTweet);
        // initialize the arraylist (data source)
        tweets = new ArrayList<>();
        // construct the adapter from this datasource
        tweetAdapter = new TweetAdapter(tweets);
        //recyclerView setup (layout manager, use adapter)
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        // set the adapter
        rvTweets.setAdapter(tweetAdapter);
        rvTweets.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));


        // set up of refresh on swipe up:
        // lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
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
                Log.i("Twitter", "Go through");
                Log.d("TwitterClient", response.toString());
                // iterate through the JSON array
                // for each entry, deserialize the JSON object
                for (int i = 0; i < response.length(); i++){
                    // convert each object to a Tweet model
                    // add that Tweet model to our data source
                    // notify adapter that we've added an item (RecyclerView specific: notify what has changed)
                    try {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        tweets.add(tweet);
                        String userScreen =tweet.user.screenName;
                        tweetAdapter.notifyItemInserted(tweets.size()-1);
                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                }

            }

            // deals with all types of returns/responses from specific client call
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("TwitterClient", response.toString());
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", responseString);
                throwable.printStackTrace();
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

    public void onComposeAction(MenuItem mi) {
        // handle click of menu item (compose icon), pass in data of user for the ComposeTweet Activity through intent
        Intent i = new Intent(this, ComposeTweet.class);
        i.putExtra(NEW_TWEET, "" );
        i.putExtra("profile_image", profilePic);
        i.putExtra("name",name );
        i.putExtra("user_name",userName);
        i.putExtra("followers" , followers);
        i.putExtra("since" , createdOn);
        startActivityForResult(i, TWEET_REQUEST_CODE);
    }


    // returning from other activities via startActivityForResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if the edit activity completed ok
        if (resultCode == RESULT_OK && requestCode == TWEET_REQUEST_CODE) {
            // extract updated item text from result intent extras
            // extract original position of edited item
            // update the model with the new item text at the edited position
            Tweet tweet = (Tweet) data.getSerializableExtra("tweet");
            // notify the adapter that the model changed
            tweets.add(0, tweet);
            tweetAdapter.notifyItemInserted(0);
            rvTweets.scrollToPosition(0);
            // persist the changed model
            // notify the user the operation completed ok
            Toast.makeText(this, "tweet added", Toast.LENGTH_SHORT).show();
        }
        else if(resultCode == RESULT_OK && requestCode == TWEET_REPLY_REQUEST_CODE){
            Log.d("tweet", "reply code");
        }
    }


    public void fetchTimelineAsync ( int page){
        // send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        client.getHomeTimeline(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                tweetAdapter.clear();
                tweets.clear();

                for (int i = 0; i < response.length(); i++) {
                    // convert each object to a Tweet model
                    // add that Tweet model to our data source
                    // notify adapter that we've added an item (RecyclerView specific: notify what has changed)
                    try {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        if (tweet.favorited == true){

                        }
                        tweets.add(tweet);
                        tweetAdapter.notifyItemInserted(tweets.size() - 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                swipeContainer.setRefreshing(false);


            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("DEBUG", "Fetch timeline error: " + errorResponse.toString());
            }

        });

    }


}


