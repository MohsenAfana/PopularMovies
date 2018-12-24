package com.mohsenafana.movies.Activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mohsenafana.movies.Adapter.MovieAdapter;
import com.mohsenafana.movies.R;
import com.mohsenafana.movies.data.FavMovieEntity;
import com.mohsenafana.movies.BrodcastReciverNetwork.ConnectivityReceiver;
import com.mohsenafana.movies.data.FavMoviesModelView;
import com.mohsenafana.movies.model.Movie;
import com.mohsenafana.movies.model.MoviesList;
import com.mohsenafana.movies.network.APIClient;
import com.mohsenafana.movies.network.ApiRequests;
import com.mohsenafana.movies.util.MyApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mohsenafana.movies.util.CONSTANT.EXTRA_MOVIE;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, ConnectivityReceiver.ConnectivityReceiverListener  {


    private static String KEY_MOVIE ="callBack" ;
    @BindView(R.id.rv_movies)
     RecyclerView mrecyclerView_movie;

    private MovieAdapter movieAdapter;
    private static final String TAG = MainActivity.class.getSimpleName();
    private ApiRequests apiService = APIClient.getClient().create(ApiRequests.class);
    ArrayList<Movie> popularMoviesList;
    private BroadcastReceiver mNetworkReceiver;
    @BindView(R.id.container_refresh)
     SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.leinearlayout_icon_connection)
    LinearLayout leinearlayout_icon_connection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

           setLanguage();

        mSwipeRefreshLayout.setOnRefreshListener(this);

              mNetworkReceiver = new ConnectivityReceiver();
        registerNetworkBroadcastForNougat();

        getSupportActionBar().setTitle(Html.fromHtml("<font color='#f2eb12'>Movies</font>"));

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        movieAdapter = new MovieAdapter(MainActivity.this, new MovieAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Movie movie) {
                Intent intent = new Intent(MainActivity.this, DetailsMovieActivity.class);
                intent.putExtra(EXTRA_MOVIE, movie);
                startActivity(intent);
            }
        });
        mrecyclerView_movie.setHasFixedSize(true);
        mrecyclerView_movie.setAdapter(movieAdapter);
        mrecyclerView_movie.setLayoutManager(layoutManager);
          if (savedInstanceState!=null){
              if (savedInstanceState.containsKey(KEY_MOVIE)){
                  popularMoviesList=savedInstanceState.getParcelableArrayList(KEY_MOVIE);
                  movieAdapter.addAll(popularMoviesList); } }
                  else { getPopularMovies(); }


        checkConnection();
    }

    private void setLanguage() {
        Locale locale2 = new Locale("en");
        Locale.setDefault(locale2);
        Configuration config2 = new Configuration();
        config2.locale = locale2;
        getBaseContext().getResources().updateConfiguration(config2, getBaseContext().getResources().getDisplayMetrics());
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private void registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }

    public void changeTextStatus(boolean isConnected) {
        // Change status according to boolean value
        if (isConnected) {
            leinearlayout_icon_connection.setVisibility(View.GONE);
            mrecyclerView_movie.setVisibility(View.VISIBLE);
            Snackbar snack = Snackbar.make(findViewById(android.R.id.content),"Internet Connected", Snackbar.LENGTH_LONG);
            View vieww = snack.getView();
            vieww.setBackgroundColor(Color.parseColor("#00ff00"));
            TextView tv = (TextView) vieww.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.white));
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            snack.show();
            getPopularMovies();
        } else {
            leinearlayout_icon_connection.setVisibility(View.VISIBLE);
            mrecyclerView_movie.setVisibility(View.GONE);
            Snackbar snack = Snackbar.make(findViewById(android.R.id.content),"No Internet Connection", Snackbar.LENGTH_LONG);
            View vieww = snack.getView();
            vieww.setBackgroundColor(Color.parseColor("#ff0000"));
            TextView tv = (TextView) vieww.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.white));
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            snack.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_popular_movies:
                getPopularMovies();
                break;
            case R.id.action_top_rated:
                getTopRatedMovies();
                break;
            case R.id.action_favorites_movies:

                getFavoritesMovies();
                break;
            case R.id.action_rating_movies:
                rateApp();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getPopularMovies() {
        apiService.getPopularMovies(null, null, null).enqueue(new Callback<MoviesList>() {
            @Override
            public void onResponse(Call<MoviesList> call, Response<MoviesList> response) {
                final MoviesList moviesResponse = response.body();
                movieAdapter.clear();
                movieAdapter.addAll(moviesResponse.getMovieList());
                popularMoviesList = (ArrayList<Movie>) moviesResponse.getMovieList();
            }

            @Override
            public void onFailure(Call<MoviesList> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private void getTopRatedMovies() {
        apiService.getTopRatedMovies(null, null, null).enqueue(new Callback<MoviesList>() {
            @Override
            public void onResponse(Call<MoviesList> call, Response<MoviesList> response) {
                final MoviesList moviesResponse = response.body();
                movieAdapter.clear();
                movieAdapter.addAll(moviesResponse.getMovieList());
                popularMoviesList = (ArrayList<Movie>) moviesResponse.getMovieList();

            }

            @Override
            public void onFailure(Call<MoviesList> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    public void getFavoritesMovies() {


        FavMoviesModelView favviewModal = ViewModelProviders.of(this).get(FavMoviesModelView.class);
        favviewModal.getFavMoviesList().observe(this, new Observer<List<FavMovieEntity>>() {
            @Override
            public void onChanged(@Nullable List<FavMovieEntity> favMovieEntities) {
                movieAdapter.clear();
                movieAdapter.addAllFav(favMovieEntities);
            }
        });
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(KEY_MOVIE, popularMoviesList);
        super.onSaveInstanceState(outState);
    }
    @Override
    public void onRefresh() {
         new android.os.Handler().postDelayed(new Runnable() {
             @Override
             public void run() {
                 mSwipeRefreshLayout.setRefreshing(false);
                 getPopularMovies();
             }
         },500);
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        changeTextStatus(isConnected);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().setConnectivityListener((ConnectivityReceiver.ConnectivityReceiverListener) this);
    }
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
  changeTextStatus(isConnected);
    }


    private void rateApp() {
        Uri uri = Uri.parse("market://details?id=" + MainActivity.this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + MainActivity.this.getPackageName())));
        }
    }
}
