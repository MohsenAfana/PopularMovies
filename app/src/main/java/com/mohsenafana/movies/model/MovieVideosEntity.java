package com.mohsenafana.movies.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieVideosEntity {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<MovieTrailerEntity> trailers = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<MovieTrailerEntity> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<MovieTrailerEntity> trailers) {
        this.trailers = trailers;
    }

    @Override
    public String toString() {
        return "MovieVideosEntity{" +
                "id=" + id +
                ", trailers=" + trailers +
                '}';
    }
}
