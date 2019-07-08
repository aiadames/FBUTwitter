package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class User implements Serializable {


    // list the attributes of User class
    public String name;

    public String screenName;
    public String profileImageUrl;
    public String createdOn;
    public String create;
    public long uid;
    public int followers;

    // deserialize the JSON
    public static User fromJSON(JSONObject json) throws JSONException {
        User user = new User();

        // extract and fill the value
        user.name = json.getString("name");
        user.uid = json.getLong("id");
        user.screenName = json.getString("screen_name");

        // String[] tokens = json.getString("profile_image_url_https").split("_normal");
        if (json.getString("profile_image_url_https").endsWith(".png")){
            String[] tokens = json.getString("profile_image_url_https").split("_normal");
            user.profileImageUrl= tokens [0] +".png";
        } else {
            String[] tokens = json.getString("profile_image_url_https").split("_normal");
            user.profileImageUrl= tokens [0] +".jpg";
        }

        user.create = json.getString("created_at");
        user.followers = json.getInt("followers_count");

        return user;
    }



}

