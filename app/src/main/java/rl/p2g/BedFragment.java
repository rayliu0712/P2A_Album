package rl.p2g;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Video;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class BedFragment extends Fragment {
    private final MainActivity context;
    private final Lazy ii;

    public static ArrayList<Uri> uris;
    public static int iUris;

    private VideoView vv;
    private ImageView iv;

    public BedFragment(MainActivity context) {
        this.context = context;
        ii = new Lazy(context);
    }

    @Nullable
    @Override  // not important
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vv = view.findViewById(R.id.vv);
        iv = view.findViewById(R.id.iv);

        glide();
        if (isVideo())
            vv.setVideoURI(getUri());

        GestureDetector gestureDetector = new GestureDetector(context.getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(@NonNull MotionEvent e) {
                if (vv.isPlaying()) {
                    vv.pause();
                } else if (isVideo()) {
                    iv.setVisibility(View.GONE);
                    vv.setVisibility(View.VISIBLE);
                    vv.start();
                }

                return false;
            }

            @Override
            public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
                iv.setVisibility(View.VISIBLE);
                vv.setVisibility(View.GONE);

                if (velocityX < 0)
                    iUris += 1;  // 向左滑
                else
                    iUris -= 1;  // 向右滑

                glide();
                if (isVideo())
                    vv.setVideoURI(getUri());

                return false;
            }
        });
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }

    private void glide() {
        try {
            Drawable drawable = Drawable.createFromStream(
                    context.getContentResolver().openInputStream(getUri()),
                    getUri().toString());

            Glide.with(this).load(getUri()).placeholder(drawable).into(iv);
        } catch (Exception e) {
        }
    }

    private boolean isVideo() {
        return getUri().toString().contains(Video.Media.EXTERNAL_CONTENT_URI + "/");
    }

    private Uri getUri() {
        return uris.get(iUris);
    }
}
