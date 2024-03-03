package rl.p2a;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import rl.p2a.fragment.BedFragment;

public class BedPager extends ViewPager {
    public static int pageScrollState = 0;
    private final MainActivity ma;
    private GestureDetector gestureDetector;

    public BedPager(@NonNull Context context) {
        super(context);
        this.ma = (MainActivity) context;
        setGestureDetector();
    }

    public BedPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.ma = (MainActivity) context;
        setGestureDetector();
    }

    public void setGestureDetector() {
        gestureDetector = new GestureDetector(ma, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(@NonNull MotionEvent e) {
                BedFragment.zoom();
                return true;
            }

            @Override
            public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
                float distance = e2.getY() - e1.getY();
                if (distance > 0 && pageScrollState == 0) {
                    ma.onBackPressed();
                }

                return true;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.onTouchEvent(ev);
    }
}
