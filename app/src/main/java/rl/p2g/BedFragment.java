package rl.p2g;

import static rl.p2g.Lazybones.*;

import android.graphics.drawable.Drawable;
import android.net.Uri;
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

public class BedFragment extends Fragment {

    private float scaleFactor = 1.0f;

    private VideoView vv;
    private ImageView iv;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vv = view.findViewById(R.id.vv);
        iv = view.findViewById(R.id.iv);

        glide();
        if (isCurrentVideo())
            vv.setVideoURI(getCurrentUri());

        GestureDetector gestureDetector = new GestureDetector(cc(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(@NonNull MotionEvent e) {
                if (vv.isPlaying()) {
                    vv.pause();
                } else if (isCurrentVideo()) {
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
                    if (cc().iList == cc().currentList.size() - 1)
                        return false;
                    cc().iList++;
                }
                // 向右滑(-)
                else {
                    if (cc().iList == 0)
                        return false;
                    cc().iList--;
                }

                iv.setVisibility(View.VISIBLE);
                vv.setVisibility(View.GONE);

                glide();
                if (isCurrentVideo())
                    vv.setVideoURI(getCurrentUri());

                return false;
            }
        });

        ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(cc(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(@NonNull ScaleGestureDetector detector) {
                scaleFactor *= detector.getScaleFactor();  // 無上限和下限

                iv.setScaleX(scaleFactor);
                iv.setScaleY(scaleFactor);
                ll(scaleFactor);
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

    // call it before switch to this fragment
    public void refresh(int iList) {
        cc().iList = iList;
        glide();
    }

    private void glide() {
        try {
            Drawable drawable = Drawable.createFromStream(
                    cc().getContentResolver().openInputStream(getCurrentUri()),
                    getCurrentUri().toString());

            Glide.with(this).load(getCurrentUri()).placeholder(drawable).into(iv);
        } catch (Exception e) {
        }
    }

    private Uri getCurrentUri() {
        return cc().currentList.get(cc().iList).uri;
    }

    private boolean isCurrentVideo() {
        return getCurrentUri().toString().contains(Video.Media.EXTERNAL_CONTENT_URI + "/");
    }


    /* ==================================================================================================== */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bed, container, false);
    }

    private MainActivity cc() {
        return (MainActivity) getActivity();
    }
}
