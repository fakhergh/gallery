package com.benboubaker.gallery;

import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.benboubaker.gallery.models.Image;
import com.benboubaker.gallery.ui.SwipeableLayout;

public class PreviewActivity extends AppCompatActivity implements SwipeableLayout.OnLayoutCloseListener {

    private ImageView imageView;
    private SwipeableLayout swipeableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        setUpStatusBarColor();

        imageView = (ImageView) findViewById(R.id.imageView);
        swipeableLayout = (SwipeableLayout) findViewById(R.id.swipeableLayout);

        swipeableLayout.setSwipeDirection(SwipeableLayout.Direction.UP_DOWN);
        swipeableLayout.setOnLayoutCloseListener(this);

        Image image = getIntent().getParcelableExtra(Image.TAG);
        imageView.setImageURI(Uri.parse(image.getPath()));
    }

    private void setUpStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }
    }

    @Override
    public void onLayoutClosed() {
        onBackPressed();
    }
}
