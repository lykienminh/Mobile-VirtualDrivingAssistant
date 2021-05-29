package com.drivingassistant.utils.player;

import android.net.Uri;

import androidx.annotation.IdRes;

public interface PlayerAdapter {

    void loadMedia(@IdRes int resId);

    void reset();

}
