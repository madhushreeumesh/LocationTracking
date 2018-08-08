package com.squareandcube.locationtracking.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;

import com.daimajia.androidanimations.library.Techniques;
import com.squareandcube.locationtracking.R;

import wail.splacher.com.splasher.lib.SplasherActivity;
import wail.splacher.com.splasher.models.SplasherConfig;
import wail.splacher.com.splasher.utils.Const;

public class SplashActivity extends SplasherActivity {

    @Override
    public void initSplasher(SplasherConfig config) {
        config.setReveal_start(Const.START_TOP_LEFT)
                //---------------
                .setAnimationDuration(5000)
                //---------------
                .setLogo(R.drawable.ic_splash_screen_image)
                .setLogo_animation(Techniques.BounceIn)
                .setAnimationLogoDuration(2000)
                .setLogoWidth(500)
                //---------------
                .setTitle("Location Tracking")
                .setTitleColor(Color.parseColor("#ffffff"))
                .setTitleAnimation(Techniques.Bounce)
                .setTitleSize(24)
                //---------------
//                .setSubtitle("Enjoy with this library")
                .setSubtitleColor(Color.parseColor("#ffffff"))
                .setSubtitleAnimation(Techniques.FadeIn)
                .setSubtitleSize(16)
                //---------------
                .setSubtitleTypeFace(Typeface.createFromAsset(getAssets(), "diana.otf"))
                .setTitleTypeFace(Typeface.createFromAsset(getAssets(), "stc.otf"));

    }

    @Override
    public void onSplasherFinished() {

        Intent mManagerLoginIntent = new Intent(SplashActivity.this,ManagerLoginActivity.class);
        startActivity(mManagerLoginIntent);
        finish();
    }
}
