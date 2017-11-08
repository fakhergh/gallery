package com.benboubaker.gallery;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.benboubaker.gallery.adapters.RecyclerViewAdapter;
import com.benboubaker.gallery.models.Image;
import com.benboubaker.gallery.tasks.CallBack;
import com.benboubaker.gallery.tasks.Loader;
import com.benboubaker.gallery.tools.Utils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int CODE = 0;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView textView;
    private RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (Utils.isMarshMellowOrHigher()) {
            if (!Utils.isPermissionGranted(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CODE);
                return;
            }
        }
        loadGallery();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadGallery();
            } else {
                finish();
            }
        }
    }

    private void loadGallery() {
        intiUi();
        new Loader(this)
                .addCallBack(new CallBack() {
                    @Override
                    public void onStart() {
                        recyclerView.setVisibility(View.INVISIBLE);
                        textView.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFinish(List<Image> images) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if (images.size() == 0) {
                            textView.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
                            adapter.updateAndNotify(images);
                        }
                    }
                })
                .start();
    }

    private void intiUi() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textView = (TextView) findViewById(R.id.messageText);

        adapter = new RecyclerViewAdapter(this, new ArrayList<Image>());

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
