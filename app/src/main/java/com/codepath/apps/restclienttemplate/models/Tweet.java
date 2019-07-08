
package com.codepath.apps.restclienttemplate.models;

import android.content.Entity;

import org.json.JSONArray;
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
        public JSONObject entity;
        public JSONArray media;
        public String mediaUrl;
        public JSONObject yer;


        // user object reference
        public User user;

        // need to be able to take in a JSON object and instantiate 'Tweet' object from it
        // deserialize the JSONObject and find attributes via API calls
        public static com.codepath.apps.restclienttemplate.models.Tweet fromJSON (JSONObject jsonObject) throws JSONException {
            com.codepath.apps.restclienttemplate.models.Tweet tweet = new com.codepath.apps.restclienttemplate.models.Tweet();

            //extract the values from JSON
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON((jsonObject.getJSONObject("user")));
            tweet.retweets = jsonObject.getString("retweet_count");
            tweet.favorites = jsonObject.getInt("favorite_count");
            tweet.favorited = jsonObject.getBoolean("favorited");


            try {
                tweet.entity = jsonObject.getJSONObject("entities");
                tweet.media = tweet.entity.getJSONArray("media");
                tweet.yer = (JSONObject) (tweet.media).get(0);
                tweet.mediaUrl = tweet.yer.getString("media_url_https");

            } catch (JSONException r){
                tweet.entity = null;
                tweet.media = null;
                tweet.yer = null;
                tweet.mediaUrl = "";

            }

            return tweet;
        }
    }


