package rl.p2a;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class BedPager extends ViewPager {
    public static int pageScrollState = 0;
    private final MainActivity ma;

    public BedPager(@NonNull Context context) {
        super(context);
        this.ma = (MainActivity) context;
    }

    public BedPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.ma = (MainActivity) context;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
