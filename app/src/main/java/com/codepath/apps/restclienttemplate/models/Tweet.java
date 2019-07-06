package com.codepath.apps.restclienttemplate.models;

import android.content.Entity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;


public class Tweet implements Serializable {

    // list out attributes of Tweet
    public String body;
    public long uid;  // database ID for the tweet
    public String createdAt;
    public String retweets;
    public int favorites;
    public boolean favorited;
    public Entity entities;
    public String createdOn;
    public int followers;

    // user object reference
    public User user;

    // need to be able to take in a JSON object and instantiate 'Tweet' object from it
    // deserialize the JSON
    public static Tweet fromJSON (JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        //extract the values from JSON
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJSON((jsonObject.getJSONObject("user")));
        tweet.retweets = jsonObject.getString("retweet_count");
        tweet.favorites = jsonObject.getInt("favorite_count");
        tweet.favorited = jsonObject.getBoolean("favorited");





        return tweet;
    }



}
