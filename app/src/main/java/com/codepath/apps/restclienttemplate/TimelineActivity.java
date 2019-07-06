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
    private EndlessRecyclerViewScrollListener scrollListener;


    String imageUrl;
    String userName;
    String name;
    String createdOn;
    String followers;


    //MenuItem miActionProgressItem;

    public final static String NEW_TWEET  = "new tweet";
    public final static int TWEET_REQUEST_CODE = 20;
    public final static int TWEET_REPLY_REQUEST_CODE = 25;


    public String profilePic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        client = TwitterApp.getRestClient(this);

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
                                        }
        );



        // find the RecyclerView
        rvTweets = (RecyclerView) findViewById(R.id.rvTweet);
        // initialize the arraylist (data source)
        tweets = new ArrayList<>();
        // construct the adapter from this datasource
        tweetAdapter = new TweetAdapter(tweets);
        //RecyclerView setup (layout manager, use adapter)
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        // set the adapter
        rvTweets.setAdapter(tweetAdapter);

        rvTweets.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener);





        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        populateTimeLine();
    }




    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void populateTimeLine(){
            // make network request and make new anonymous class to deal with
            // handling response from network call
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
        // handle click here
        Intent i = new Intent(this, ComposeTweet.class);
        i.putExtra(NEW_TWEET, "" );
        i.putExtra("profile_image", profilePic);
        i.putExtra("name",name );
        i.putExtra("user_name",userName);
        i.putExtra("followers" , followers);
        i.putExtra("since" , createdOn);
        startActivityForResult(i, TWEET_REQUEST_CODE);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if the edit activity completed ok
        if (resultCode == RESULT_OK && requestCode == TWEET_REQUEST_CODE) {
            // extract updated item text from result intent extras
            //           String updatedItem = data.getExtras().getString(NEW_TWEET);
            // extract original position of edited item
            // update the model with the new item text at the edited position
            Tweet tweet = (Tweet) data.getSerializableExtra("tweet");
            // notify the adapter that the model changed
            tweets.add(0, tweet);
            tweetAdapter.notifyItemInserted(0);
            rvTweets.scrollToPosition(0);
            // persist the changed model
            // notify the user the operation completed ok
            //Toast.makeText(this, "tweet added", Toast.LENGTH_SHORT).show();
        }
        else if(resultCode == RESULT_OK && requestCode == TWEET_REPLY_REQUEST_CODE){
            Log.d("tweet", "reply code");
        }
    }


        public void fetchTimelineAsync ( int page){
            // Send the network request to fetch the updated data
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


