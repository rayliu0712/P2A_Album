package rl.p2g;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.io.File;
import java.util.ArrayList;

public class BedFragment extends Fragment {
    private final Context context;
    private final Lazy ii;
    private VideoView vv;
    private ImageView iv;

    private int bedIndex = 0;
    private final ArrayList<String[]> bedList = new ArrayList<>();

    public BedFragment(Context context) {
        this.context = context;
        ii = new Lazy(context);
    }

    // not important
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vv = view.findViewById(R.id.vv);
        vv.setVisibility(View.GONE);
        iv = view.findViewById(R.id.iv);

        bedList.addAll(getMedia(MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
        bedList.addAll(getMedia(MediaStore.Video.Media.EXTERNAL_CONTENT_URI));
        bedList.sort((o1, o2) -> {
            return o2[1].compareTo(o1[1]);  // ascending order
        });


        glide();
        if (isVideo()) {
            vv.setVideoPath(getPath());
        }

        GestureDetector gestureDetector = new GestureDetector(context.getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
                if (velocityX < 0)  // 向左滑
                    bedIndex += 1;
                else  // 向右滑
                    bedIndex -= 1;

                glide();
                if (isVideo()) {
                    vv.setVideoPath(getPath());
                }
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

    private ArrayList<String[]> getMedia(Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri,
                null,
                "_data LIKE ?",
                new String[]{"%/Pictures/Twitter/%"},
                "date_added ASC");  // order

        ArrayList<String[]> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String data = cursor.getString(cursor.getColumnIndexOrThrow("_data"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date_added"));

                ii.ll(data);

                list.add(new String[]{data, date});
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    private void glide() {
        Glide.with(context).load(new File(getPath())).placeholder(Drawable.createFromPath(getPath())).into(iv);
    }

    private String getPath() {
        return bedList.get(bedIndex)[0];
    }

    private boolean isVideo() {
        return getPath().matches("(.*)\\.mp4$");
    }
}
