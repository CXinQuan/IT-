package com.example.lenovo.it2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.example.lenovo.it2.sl.MyLinearLayoutManager;

public class LogoActivitty extends Activity {
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logo_layout);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        MyLinearLayoutManager.width=width;

        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(3000);
        ImageView img_logo = (ImageView) this.findViewById(R.id.img_logo);
        img_logo.setAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener(){
            public void onAnimationEnd(Animation animation)
            {
                Intent intent = new Intent(LogoActivitty.this, FirstActivity.class);
                startActivity(intent);
                finish();
            }

            public void onAnimationRepeat(Animation animation)
            {
            }

            public void onAnimationStart(Animation animation)
            {
            }});
    }
}
