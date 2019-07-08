package com.codepath.apps.restclienttemplate;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    public Context context;
    TwitterClient client = TwitterApp.getRestClient(context);
    private List<Tweet> mTweets;


    // pass in the Tweets array in the constructor
    public TweetAdapter(List<Tweet> tweets) {
        mTweets = tweets;
    }
    // for each row, inflate the layout and cache references into ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // viewHolder caches all the findById lookups
        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }


    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // get the data according to position (in previously cached ViewHolder)
        Tweet tweet = mTweets.get(position);
        // populate the views according to tweet/user specific data
        holder.tvUsername.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        holder.tvUser.setText("@" + tweet.user.screenName);
        holder.tvRetweets.setText(tweet.retweets);
        Integer faves = tweet.favorites;
        holder.tvFavourites.setText(faves.toString());
        holder.tvCreatedAt.setText(getRelativeTimeAgo(tweet.createdAt));

        // loading profile image into the ImageView for each RecyclerView (specifically the layout in item_tweet)
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(35)).format(DecodeFormat.PREFER_ARGB_8888);
        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .apply(requestOptions)
                .into(holder.ivProfileImage);

        // tinting icons continuously on basis of twee.favorited boolean being true or false
        if (tweet.favorited) {
            holder.ibFavorite.setColorFilter(ContextCompat.getColor(context, R.color.medium_red));
        } else {
            holder.ibFavorite.setColorFilter(ContextCompat.getColor(context, R.color.black));
        }

    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    // create ViewHolder class utilizing ButterKnife
    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivProfileImage) public ImageView ivProfileImage;
        @BindView(R.id.tvName) public TextView tvUsername;
        @BindView(R.id.tvBody) TextView tvBody;
        @BindView(R.id.tvUserName) public TextView tvUser;
        @BindView(R.id.ibReply) public ImageButton ibReply;
        @BindView(R.id.ibRetweet) public ImageButton ibRetweet;
        @BindView(R.id.tvCreatedAt) public TextView tvCreatedAt;
        @BindView(R.id.tvRetweetsNum) public TextView tvRetweets;
        @BindView(R.id.tvFavourites) public TextView tvFavourites;
        @BindView(R.id.ibFavorite) public ImageButton ibFavorite;
        //public ImageView ivMedia;



        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);


            // set the image button corresponding to a tweet reply on a click listener
            // get the position of the tweet in the RecyclerView and get the specific tweet object in the ArrayList mTweets
            // create a new Intent to the ReplyTweet activity and pass through the user who originally made the tweet for reference
            ibReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context, "heyyeyyeyehd", Toast.LENGTH_SHORT).show();
                    int position = getAdapterPosition();
                    // make sure the position is valid, i.e. actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        // get the movie at the position, this won't work if the class is static
                        Tweet tweet = mTweets.get(position);
                        // create intent for the new activity
                        Intent intent = new Intent(context, ReplyTweet.class);
                        intent.putExtra("user_reply_name", tweet.user.screenName);
                        context.startActivity(intent);
                    }

                }
            });

            // set the image button corresponding to a tweet favorite on a click listener
            // get the position of the tweet in the RecyclerView, and get the tweet.favorited status (if the tweet is already favorited or not)
            // if not favorited: that means want to send a request to Twitter to like the tweet and on success, change status of tweet.favorited and add one to its favorited count
            // else: if tweet.favorited is already true, we are trying to unlike the tweet, and on success, change status of tweet.favorited and subtract one from its favorited count
            ibFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = getAdapterPosition();
                    // make sure the position is valid, i.e. actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        // get the movie at the position, this won't work if the class is static
                        final Tweet tweet = mTweets.get(position);
                        long tweetId = tweet.uid;

                        if (!tweet.favorited) {
                            Log.d("yeet", "yeeee");
                            client.favoriteTweet(tweetId, new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    Log.d("tweet", "tweet");
                                    tweet.favorited = !(tweet.favorited);
                                    tweet.favorites+=1;
                                    notifyItemChanged(position);
                                }
                            });

                        } else if (tweet.favorited) {
                            client.destroyFavoriteTweet(tweetId, new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    Log.d("tweet", "ehhehhehe");
                                    tweet.favorited = !(tweet.favorited);
                                    tweet.favorites-=1;
                                    notifyItemChanged(position);
                                }
                            });

                        }
                    }
                }
            });

            // set the item tweets in RecyclerView to an click listener
            // get the position of the tweet in the RecyclerView and get the specific tweet object in the ArrayList mTweets
            // pass through all the details of the tweet through a new Intent to begin the DetailsTweet Activity
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context, "clicked on tweet now time for detail", Toast.LENGTH_SHORT).show();
                    int position = getAdapterPosition();
                    // make sure the position is valid, i.e. actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        // get the movie at the position, this won't work if the class is static
                        Tweet tweet = mTweets.get(position);
                        // create intent for the new activity
                        Intent detailIntent = new Intent(context, DetailTweet.class);
                        detailIntent.putExtra("tweet_user", tweet.user.screenName);
                        detailIntent.putExtra("tweet_body", tweet.body);
                        detailIntent.putExtra("tweet_name", tweet.user.name);
                        detailIntent.putExtra("tweet_image", tweet.user.profileImageUrl);
                        detailIntent.putExtra("tweet_time", tweet.createdAt);
                        context.startActivity(detailIntent);
                    }
                }
            });

        }


    }


    // Clean all elements of the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }



    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(String rawJsonDate) {
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

