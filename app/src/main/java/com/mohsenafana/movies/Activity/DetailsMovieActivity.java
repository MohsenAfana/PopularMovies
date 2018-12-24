package com.mohsenafana.movies.Activity;


import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.mohsenafana.movies.R;
import com.mohsenafana.movies.data.FavMovieEntity;
import com.mohsenafana.movies.data.MoviesDatabase;
import com.mohsenafana.movies.model.Movie;
import com.mohsenafana.movies.model.MovieVideosEntity;
import com.mohsenafana.movies.network.APIClient;
import com.mohsenafana.movies.network.ApiRequests;
import com.mohsenafana.movies.util.IntentsUtill;
import com.mohsenafana.movies.Adapter.MoviesReviewsAdapter;
import com.mohsenafana.movies.Adapter.MoviesTrailersAdapter;

import com.mohsenafana.movies.model.MovieReviewsEntity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mohsenafana.movies.util.CONSTANT.BASE_POSTERS_URL_second;
import static com.mohsenafana.movies.util.CONSTANT.EXTRA_MOVIE;


public class DetailsMovieActivity extends AppCompatActivity implements RewardedVideoAdListener {

    private static final String TAG = DetailsMovieActivity.class.getSimpleName();
    private int dosply_splash = 500;
    private static final String AD_UNIT_ID = "ca-app-pub-4782277956423401/9169863883";
    private static final String APP_ID = "ca-app-pub-4782277956423401/9411515735";

    private RewardedVideoAd mRewardedVideoAd;

    @BindView(R.id.tv_movie_name)
    TextView mNameTextView;
    @BindView(R.id.iv_movie_poster)
    ImageView mPosterImageView;
    @BindView(R.id.tv_movie_release_date)
    TextView mReleaseDateTextView;
    @BindView(R.id.tv_movie_user_rating)
    TextView mUserRatingTextView;
    @BindView(R.id.tv_movie_length)
    TextView movieLengthTextView;
    @BindView(R.id.tv_movie_overview)
    TextView mOverviewTextView;
    @BindView(R.id.rv_trailers)
    RecyclerView mTrailersRecyclerView;
    @BindView(R.id.rv_reviews)
    RecyclerView mReviewsRecyclerView;
    @BindView(R.id.favoriteFab)
    FloatingActionButton fab;

    private ApiRequests apiService = APIClient.getClient().create(ApiRequests.class);
    private MoviesTrailersAdapter mTrailersAdapter;
    private MoviesReviewsAdapter mReviewsAdapter;
    private MoviesDatabase mDb;
    private Movie movie;
    private int movieId;
    private boolean isFavourite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_details_movie);
        ButterKnife.bind(this);
        setLanguage();
        mDb = MoviesDatabase.getsInstance(this);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#f2eb12'>Movies</font>"));

        mTrailersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTrailersAdapter = new MoviesTrailersAdapter(this,
                trailer -> IntentsUtill.watchYoutubeVideo(DetailsMovieActivity.this, trailer.getKey()));
        mTrailersRecyclerView.setAdapter(mTrailersAdapter);
        mTrailersRecyclerView.setNestedScrollingEnabled(false);

        mReviewsAdapter = new MoviesReviewsAdapter(this);
        mReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mReviewsRecyclerView.setAdapter(mReviewsAdapter);
        mReviewsRecyclerView.setNestedScrollingEnabled(false);

        movie = getIntent().getExtras().getParcelable(EXTRA_MOVIE);
        movieId = movie.getId();

        mNameTextView.setText(movie.getTitle());
        mReleaseDateTextView.setText(movie.getReleaseDate());
        mUserRatingTextView.setText(String.valueOf(movie.getVoteAverage()));
        mOverviewTextView.setText(movie.getOverview());
        Glide.with(this).load(BASE_POSTERS_URL_second + movie.getPosterPath()).into(mPosterImageView);

        getMovieVideos();
        getMovieReviews();
        setupViewModel();
          goToAds();
    }
    private void setLanguage() {
        Locale locale2 = new Locale("en");
        Locale.setDefault(locale2);
        Configuration config2 = new Configuration();
        config2.locale = locale2;
        getBaseContext().getResources().updateConfiguration(config2, getBaseContext().getResources().getDisplayMetrics());
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    private void goToAds() {
        MobileAds.initialize(this, APP_ID);

        // Use an activity context to get the rewarded video instance.
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);


        new Handler().postDelayed(new Runnable() {

            @SuppressLint("ResourceAsColor")
            @Override

            public void run() {
//
////                  startActivity(new Intent(BlockActivity.this,AdsActivity.class));
////                  BlockActivity.this.finish();
                loadRewardedVideoAd();

            }

        }, dosply_splash);

    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd(APP_ID,
                new AdRequest.Builder().build());
    }

    @Override
    public void onRewarded(RewardItem reward) {
        Toast.makeText(this, "onRewarded! currency: " + reward.getType() + "  amount: " +
                reward.getAmount(), Toast.LENGTH_SHORT).show();
        // Reward the user.
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Toast.makeText(this, "onRewardedVideoAdLeftApplication",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() {

          }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        Toast.makeText(this, "Failed to load Rewarded video Ad", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRewardedVideoAdLoaded() {
        mRewardedVideoAd.show();
    }

    @Override
    public void onRewardedVideoAdOpened() {
    }

    @Override
    public void onRewardedVideoStarted() {
    }

    @Override
    public void onRewardedVideoCompleted() {
    }


    @Override
    public void onResume() {
        mRewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }



    private void setupViewModel() {
        if (mDb.favMovieDao().loadMovieById(movieId) != null) {
            fab.setImageResource(R.drawable.ic_star_fill_24dp);
            isFavourite = true;
        } else {
            fab.setImageResource(R.drawable.ic_star_empty_24dp);
            isFavourite = false;
        }
    }




    private void getMovieVideos() {
        apiService.getMovieVideos(movieId + "").enqueue(new Callback<MovieVideosEntity>() {
            @Override
            public void onResponse(Call<MovieVideosEntity> call, Response<MovieVideosEntity> response) {
                final MovieVideosEntity movieVideosEntity = response.body();
                mTrailersAdapter.addAll(movieVideosEntity.getTrailers());
            }

            @Override
            public void onFailure(Call<MovieVideosEntity> call, Throwable t) {
                Log.e(TAG, t.toString());
                Toast.makeText(DetailsMovieActivity.this, "Error Loading Trailers", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getMovieReviews() {
        apiService.getMovieReviews(movieId + "").enqueue(new Callback<MovieReviewsEntity>() {
            @Override
            public void onResponse(Call<MovieReviewsEntity> call, Response<MovieReviewsEntity> response) {
                final MovieReviewsEntity movieReviewsEntity = response.body();
                mReviewsAdapter.addAll(movieReviewsEntity.getReviewList());
            }

            @Override
            public void onFailure(Call<MovieReviewsEntity> call, Throwable t) {
                Log.e(TAG, t.toString());
                Toast.makeText(DetailsMovieActivity.this, "Error Loading Reviews", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onFabClicked(View view) {
        if (isFavourite) {
            mDb.favMovieDao().deleteMovie(mDb.favMovieDao().loadMovieById(movieId));
            fab.setImageResource(R.drawable.ic_star_empty_24dp);
            isFavourite = false;

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadRewardedVideoAd();
                }
            },dosply_splash);

            mDb.favMovieDao().insertMovie(new FavMovieEntity(movieId, movie.getOriginalTitle(), movie.getPosterPath(),movie.getReleaseDate(),String.valueOf(movie.getVoteAverage()),movie.getOverview()));
            fab.setImageResource(R.drawable.ic_star_fill_24dp);
            isFavourite = true;
        }
    }
}
