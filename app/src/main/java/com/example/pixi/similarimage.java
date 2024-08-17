package com.example.pixi;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
//import com.example.pixi.ImageAdapter;


public class similarimage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_similarimage);

        // Retrieve the URL of the reference image from intent extras
        String referenceImageUrl = getIntent().getStringExtra("reference_image_url");

        // Load the reference image into the ImageView
        ImageView imageViewReference = findViewById(R.id.gridView);
        // You can use libraries like Picasso or Glide to load images from URLs
        // Here's a simple example using Glide:
        Glide.with(this)
                .load(referenceImageUrl)
                //.placeholder(R.drawable.placeholder_image) // Placeholder image while loading
                //.error(R.drawable.error_image) // Placeholder image in case of error
                .into(imageViewReference);
    }
}
