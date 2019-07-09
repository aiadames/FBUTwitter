package com.codepath.apps.restclienttemplate.models;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.TweetAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class User {


    // list the attributes of User class
    public String name;

    public String screenName;
    public String profileImageUrl;
    public String createdOn;
    public String create;
    public long uid;
    public int followers;

    // constructor needed for Parcel
    public User() {
    }

    // deserialize the JSON and find attributes via API calls
    public static User fromJSON(JSONObject json) throws JSONException {
        User user = new User();
        // extract and fill the value
        user.name = json.getString("name");
        user.uid = json.getLong("id");
        user.screenName = json.getString("screen_name");
        user.create = json.getString("created_at");
        user.followers = json.getInt("followers_count");
        if (json.getString("profile_image_url_https").endsWith(".png")){
            String[] tokens = json.getString("profile_image_url_https").split("_normal");
            user.profileImageUrl= tokens [0] +".png";
        } else {
            String[] tokens = json.getString("profile_image_url_https").split("_normal");
            user.profileImageUrl= tokens [0] +".jpg";
        }
        return user;
    }


    public void loadProfilePic (Context context, String profileImageUrl, TweetAdapter.ViewHolder holder, ImageView imageView){
        // loading profile image into the ImageView for each RecyclerView (specifically the layout in item_tweet)
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(35)).format(DecodeFormat.PREFER_ARGB_8888);
        if (holder != null) {
            Glide.with(context)
                    .load(profileImageUrl)
                    .apply(requestOptions)
                    .into(holder.ivProfileImage);
        } else {
            Glide.with(context)
                    .load(profileImageUrl)
                    .apply(requestOptions)
                    .into(imageView);
        }
    }



}

