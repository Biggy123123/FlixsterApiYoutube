package com.morisseau.flixster;

import static com.morisseau.flixster.MainActivity.NOW_PLAYING_URL;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.morisseau.flixster.Models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;


public class DetailActivity extends YouTubeBaseActivity {
    private static final String YOUTUBE_API_KEY="AIzaSyAHpKACyrPHDXX1CXmkAhjnJLapTc_Z8B8";
    public static final String VIDEOS_URL="https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";

    YouTubePlayerView youTubePlayerView;
    TextView tvTitle;
    TextView tvOverview;
    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        tvTitle=findViewById(R.id.tvTitle);
        tvOverview=findViewById(R.id.tvOverview);
        ratingBar=findViewById(R.id.ratingBar);
        youTubePlayerView=findViewById(R.id.player);

        String title=getIntent().getStringExtra("title");
        Movie movie= Parcels.unwrap(getIntent().getParcelableExtra("movie"));
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());
        ratingBar.setRating((float)movie.getRating());


        AsyncHttpClient client=new AsyncHttpClient();
        client.get(String.format(VIDEOS_URL, movie.getMovieId()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    JSONArray results=json.jsonObject.getJSONArray("results");
                    String youtubeKey= results.getJSONObject(0).getString("key");
                    Log.d("DetailActivity",youtubeKey);
                    initializedYoutube(youtubeKey);
                } catch (JSONException e) {
                    Log.e("DetailActivity","Failed to parse json",e);
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }
        });

    }

    private void initializedYoutube(String youtubeKey) {
        youTubePlayerView.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.d("DetailActivity","onInitializationSuccess");
                youTubePlayer.cueVideo(youtubeKey);
            }
            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d("DetailActivity","onInitializationFailure");
            }
        });
    }
}
