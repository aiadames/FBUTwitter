package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.activities.DetailTweetActivity;
import com.codepath.apps.restclienttemplate.activities.ReplyTweetActivity;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

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
        holder.tvFavourites.setText(((Integer)tweet.favorites).toString());
        holder.tvCreatedAt.setText(tweet.relativeTime);

        User user = tweet.user;
        user.loadProfilePic(context, user.profileImageUrl, holder, null);

        // tinting icons continuously on basis of twee.favorited boolean being true or false
        if (tweet.isFavorited) {
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
        @BindView(R.id.tvUserName) public TextView tvUsername;
        @BindView(R.id.tvBody) TextView tvBody;
        @BindView(R.id.tvName) public TextView tvUser;
        @BindView(R.id.ibReply) public ImageButton ibReply;
        @BindView(R.id.ibRetweet) public ImageButton ibRetweet;
        @BindView(R.id.tvCreatedAt) public TextView tvCreatedAt;
        @BindView(R.id.tvRetweetsNum) public TextView tvRetweets;
        @BindView(R.id.tvFavourites) public TextView tvFavourites;
        @BindView(R.id.ibFavorite) public ImageButton ibFavorite;



        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            // set the image button corresponding to a tweet reply on a click listener
            // get the position of the tweet in the RecyclerView and get the specific tweet object in the ArrayList mTweets
            // create a new Intent to the ReplyTweet activity and pass through the user who originally made the tweet for reference
            ibReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    // make sure the position is valid, i.e. actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        // get the movie at the position, this won't work if the class is static
                        Tweet tweet = mTweets.get(position);
                        User user = tweet.user;
                        // create intent for the new activity
                        Intent intent = new Intent(context, ReplyTweetActivity.class);
                        intent.putExtra("user", Parcels.wrap(user));
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

                        if (!tweet.isFavorited) {
                            Log.d("yeet", "yeeee");
                            client.favoriteTweet(tweetId, new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    Log.d("tweet", "tweet");
                                    tweet.isFavorited = !(tweet.isFavorited);
                                    tweet.favorites+=1;
                                    notifyItemChanged(position);
                                }
                            });

                        } else if (tweet.isFavorited) {
                            client.destroyFavoriteTweet(tweetId, new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    Log.d("tweet", "ehhehhehe");
                                    tweet.isFavorited = !(tweet.isFavorited);
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
                        Intent detailIntent = new Intent(context, DetailTweetActivity.class);
                        detailIntent.putExtra("tweet", Parcels.wrap(tweet));
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






}

