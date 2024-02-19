package rl.p2a.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore.Video;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import rl.p2a.Database;
import rl.p2a.R;
import rl.p2a.MainActivity;

public class BedFragment extends Fragment {

    private float scaleFactor = 1.0f;

    private VideoView vv;
    private ImageView iv;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vv = view.findViewById(R.id.vv);
        iv = view.findViewById(R.id.small_iv);

        glide();
        if (isCurVideo())
            vv.setVideoURI(Database.getCurrent().uri);

        GestureDetector gestureDetector = new GestureDetector(cc(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(@NonNull MotionEvent e) {
                        if (vv.isPlaying()) {
                            vv.pause();
                        } else if (isCurVideo()) {
                            iv.setVisibility(View.GONE);
                            vv.setVisibility(View.VISIBLE);
                            vv.start();
                        }
                        return false;
                    }

                    @Override
                    public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
                        // 向左滑(+)
                        if (velocityX < 0) {
                            if (Database.iMedia == Database.getAlbum().mediaList.size() - 1)
                                return false;
                            Database.iMedia++;
                        }
                        // 向右滑(-)
                        else {
                            if (Database.iMedia == 0)
                                return false;
                            Database.iMedia--;
                        }

                        iv.setVisibility(View.VISIBLE);
                        vv.setVisibility(View.GONE);

                        glide();
                        if (isCurVideo())
                            vv.setVideoURI(Database.getCurrent().uri);

                        return false;
                    }
                });

        ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(cc(),
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    public boolean onScale(@NonNull ScaleGestureDetector detector) {
                        scaleFactor *= detector.getScaleFactor();  // 無上限和下限

                        if (detector.getCurrentSpanY() > 0)  // wired
                            iv.setRotation(iv.getRotation() - 1);
                        else
                            iv.setRotation(iv.getRotation() + 1);

                        iv.setScaleX(scaleFactor);
                        iv.setScaleY(scaleFactor);

                        return true;
                    }
                });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                scaleGestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }

    public void glide() {
        try {
            Drawable drawable = Drawable.createFromStream(
                    cc().getContentResolver().openInputStream(Database.getCurrent().uri),
                    Database.getCurrent().uri.toString());

            Glide.with(this).load(Database.getCurrent().uri).placeholder(drawable).into(iv);
        } catch (Exception e) {
        }
    }

    private boolean isCurVideo() {
        return Database.getCurrent().uri.toString().contains(Video.Media.EXTERNAL_CONTENT_URI + "/");
    }


    /* ==================================================================================================== */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bed, container, false);
    }

    private MainActivity cc() {
        return (MainActivity) getActivity();
    }
}
