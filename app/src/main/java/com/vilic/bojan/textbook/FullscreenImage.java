package com.vilic.bojan.textbook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

public class FullscreenImage extends AppCompatActivity {

    private PhotoView mPhotoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        mPhotoView = findViewById(R.id.photoView);
        String url = getIntent().getStringExtra("ImageUrl");
        Picasso.get().load(url).into(mPhotoView);
    }
}
