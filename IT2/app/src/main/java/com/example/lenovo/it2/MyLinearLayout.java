package com.example.lenovo.it2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lenovo.it2.sl.MyLinearLayoutManager;

public class MyLinearLayout extends LinearLayout {
    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context,  AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayout(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
   protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final TextView view = (TextView) this.getChildAt(0);
        super.onLayout(changed, l-view.getMeasuredWidth(), t, r, b); //将父布局的左边Left移动过去，使得TextView才可以放在外面

        view.layout(-view.getWidth(), t, 0, b);//这行代码必须有
      //  super.layout( l-view.getMeasuredWidth()/2, t, r, b);
        final TranslateAnimation ta = new TranslateAnimation(view.getLeft(), MyLinearLayoutManager.width+view.getMeasuredWidth(), 0, 0);
        ta.setDuration(MyLinearLayoutManager.time);
        ta.setFillAfter(true);
        ta.setAnimationListener(new Animation.AnimationListener() {

            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                view.startAnimation(ta);
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });
        view.startAnimation(ta);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        View view = this.getChildAt(0);
        int measureWidth = MeasureSpec.makeMeasureSpec(view.getLayoutParams().width, MeasureSpec.AT_MOST);
        int measureHeight = MeasureSpec.makeMeasureSpec(view.getLayoutParams().height, MeasureSpec.AT_MOST);
        view.measure(measureWidth, measureHeight);

    }

}
