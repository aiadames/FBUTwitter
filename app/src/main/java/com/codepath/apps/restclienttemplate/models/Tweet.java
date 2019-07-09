
package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Parcel
public class Tweet {

    // empty constructor needed for Parcel
    public Tweet() {

    }

    // list out attributes of Tweet
    public String body;
    public long uid;  // database ID for the tweet
    public String relativeTime;
    public String retweets;
    public int favorites;
    public boolean isFavorited;
    public String createdOn;
    public int followers;

    public String mediaUrl;
    // user object reference
    public User user;

    // need to be able to take in a JSON object and instantiate 'Tweet' object from it
    // deserialize the JSONObject and find attributes via API calls
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        //extract the values from JSON
        tweet.body = jsonObject.getString("text");
        tweet.uid = jsonObject.getLong("id");
        tweet.relativeTime = getRelativeTimeAgo(jsonObject.getString("created_at"));
        tweet.user = User.fromJSON((jsonObject.getJSONObject("user")));
        tweet.retweets = jsonObject.getString("retweet_count");
        tweet.favorites = jsonObject.getInt("favorite_count");
        tweet.isFavorited = jsonObject.getBoolean("favorited");


        // retrieving media in entities
        if (jsonObject.getJSONObject("entities").has("media")) {
            JSONArray media = jsonObject.getJSONObject("entities").getJSONArray("media");
            JSONObject firstMediaObject = (JSONObject) ((media).get(0));
            tweet.mediaUrl = firstMediaObject.getString("media_url_https");
        }
        return tweet;
    }


    // method to retrieve a Tweet's relative time posted
    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }


}


