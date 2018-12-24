package com.mohsenafana.movies.data;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class FavMoviesModelView extends AndroidViewModel {

    private LiveData<List<FavMovieEntity>> favMoviesList;

    public FavMoviesModelView(@NonNull Application application) {
        super(application);
        MoviesDatabase database = MoviesDatabase.getsInstance(this.getApplication());
          favMoviesList=database.favMovieDao().loadAllMovies();
    }

    public LiveData<List<FavMovieEntity>> getFavMoviesList() {
        return favMoviesList;
    }
}
 