package com.example.lenovo.huige;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;



public class LogoActivitty extends Activity {
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logo_layout);
        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(3000);
        ImageView img_logo = (ImageView) this.findViewById(R.id.img_logo);
        img_logo.setAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener(){
            public void onAnimationEnd(Animation animation)
            {
                Intent intent = new Intent(LogoActivitty.this, MainActivity.class);
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
