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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private List<Tweet> mTweets;
    Context context;
    TwitterClient client = TwitterApp.getRestClient(context);

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

        // populate the views according to this data  WHAT?????????
        holder.tvUsername.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        holder.tvUser.setText("@" + tweet.user.screenName);
        holder.tvRetweets.setText(tweet.retweets);
        Integer faves = tweet.favorites;
        holder.tvFavourites.setText(faves.toString());
        holder.tvCreatedAt.setText(getRelativeTimeAgo(tweet.createdAt));


   //     if (tweet.mediaUrl.equalsIgnoreCase("")){
   //         Glide.with(context)
    //                .load(tweet.mediaUrl)
    //                .into(holder.ivMedia);

    //    }


        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(35));

        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .apply(requestOptions)
                .into(holder.ivProfileImage);

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

    // create ViewHolder class

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProfileImage;
        public TextView tvUsername;
        public TextView tvBody;
        public TextView tvUser;
        public ImageButton ibReply;
        public ImageButton ibRetweet;
        public TextView tvCreatedAt;
        public TextView tvRetweets;
        public TextView tvFavourites;
        public ImageButton ibFavorite;
        public ImageView ivMedia;



        public ViewHolder(View itemView) {
            super(itemView);


            // perform findViewById lookups for attributes
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUsername = (TextView) itemView.findViewById(R.id.tvName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvUser = (TextView) itemView.findViewById(R.id.tvUserName);
            ibReply = (ImageButton) itemView.findViewById(R.id.ibReply);
            ibRetweet = (ImageButton) itemView.findViewById(R.id.ibRetweet);
            ibFavorite = (ImageButton) itemView.findViewById(R.id.ibFavorite);
            tvCreatedAt = (TextView) itemView.findViewById(R.id.tvCreatedAt);
            tvRetweets = (TextView) itemView.findViewById(R.id.tvRetweetsNum);
            tvFavourites = (TextView) itemView.findViewById(R.id.tvFavourites);
            ivMedia = (ImageView) itemView.findViewById(R.id.ivMedia);



            ibReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "heyyeyyeyehd", Toast.LENGTH_SHORT).show();
                    int position = getAdapterPosition();
                    // make sure the position is valid, i.e. actually exists in the view
                    if (position != RecyclerView.NO_POSITION) {
                        // get the movie at the position, this won't work if the class is static
                        Tweet tweet = mTweets.get(position);
                        // create intent for the new activity
                        Intent intent = new Intent(context, ReplyTweet.class);
                        intent.putExtra("user_reply_name", tweet.user.screenName);
                        // serialize the movie using parceler, use its short name as a key
                        // show the activity
                        // FIX THIS LATER
                        context.startActivity(intent);
                    }

                }
            });


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


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "clicked on tweet now time for detail", Toast.LENGTH_SHORT).show();
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
                        // serialize the movie using parceler, use its short name as a key
                        // show the activity
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
