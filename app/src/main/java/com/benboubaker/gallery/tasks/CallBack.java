package com.benboubaker.gallery.tasks;

import com.benboubaker.gallery.models.Image;

import java.util.List;

/**
 * Created by fekhe on 06/11/2017.
 */

public interface CallBack{
    void onStart();
    void onFinish(List<Image> images);
}
