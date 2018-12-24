package com.mohsenafana.movies.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import static com.mohsenafana.movies.util.CONSTANT.BASE_YOUTUBE_URL;


public class IntentsUtill {

    public static void watchYoutubeVideo(Context context, String id) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(BASE_YOUTUBE_URL + id));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }
}
