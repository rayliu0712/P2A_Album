package rl.p2g;

import static rl.p2g.Lazybones.*;

import android.graphics.Bitmap;
import android.graphics.Matrix;
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

import java.io.OutputStream;

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
        if (isCurVideo())
            vv.setVideoURI(cc().getCurPS().uri);

        GestureDetector gestureDetector = new GestureDetector(cc(), new GestureDetector.SimpleOnGestureListener() {
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
                    if (cc().getIList() == cc().getCurList().size() - 1)
                        return false;
                    cc().changeIListByOne(true);
                }
                // 向右滑(-)
                else {
                    if (cc().getIList() == 0)
                        return false;
                    cc().changeIListByOne(false);
                }

                iv.setVisibility(View.VISIBLE);
                vv.setVisibility(View.GONE);

                glide();
                if (isCurVideo())
                    vv.setVideoURI(cc().getCurPS().uri);

                return false;
            }
        });

        ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(cc(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(@NonNull ScaleGestureDetector detector) {
                scaleFactor *= detector.getScaleFactor();  // 無上限和下限

//                ll("span x" + detector.getCurrentSpanX() + "  span y" + detector.getCurrentSpanY());
                if (detector.getCurrentSpanY() > 0)  // wired
                    iv.setRotation(iv.getRotation() - 1);
                else
                    iv.setRotation(iv.getRotation() + 1);

                iv.setScaleX(scaleFactor);
                iv.setScaleY(scaleFactor);

                Matrix matrix = new Matrix();
                matrix.postRotate(90); // 旋轉90度

                Bitmap currentBitmap = cc().getCurPS().bitmap;
                Bitmap rotatedBitmap = Bitmap.createBitmap(currentBitmap, 0, 0, currentBitmap.getWidth(), currentBitmap.getHeight(), matrix, true);


//                ll(scaleFactor);
                try {
                    OutputStream outputStream = cc().getContentResolver().openOutputStream(cc().getCurPS().uri);
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    tt(cc(), "Su");
                } catch (Exception e) {
                    ll(e);
                    tt(cc(), "Fa");

                }

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

    private void glide() {
        try {
            Drawable drawable = Drawable.createFromStream(
                    cc().getContentResolver().openInputStream(cc().getCurPS().uri),
                    cc().getCurPS().uri.toString());

            Glide.with(this).load(cc().getCurPS().uri).placeholder(drawable).into(iv);
        } catch (Exception e) {
        }
    }

    private boolean isCurVideo() {
        return cc().getCurPS().uri.toString().contains(Video.Media.EXTERNAL_CONTENT_URI + "/");
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
