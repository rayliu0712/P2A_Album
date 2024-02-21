package rl.p2a.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;

import java.io.FileNotFoundException;
import java.io.InputStream;

import rl.p2a.Database;
import rl.p2a.MainActivity;
import rl.p2a.R;
import rl.p2a.struct.MediaStruct;

public class BedFragment extends Fragment {
    private ViewPager viewPager;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = view.findViewById(R.id.vp);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Database.iMedia = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return Database.getAlbum().mediaList.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                View itemView = cc().getLayoutInflater().inflate(R.layout.viewpager_item, container, false);

                ImageView imageView = itemView.findViewById(R.id.iv);

                MediaStruct current = Database.getAlbum().getMedia(position);

                Drawable thumbnailDrawable = current.thumbnailDrawable;
                Glide.with(cc()).load(thumbnailDrawable).placeholder(thumbnailDrawable).into(imageView);

                new Thread(() -> {
                    try {
                        final InputStream is = cc().getContentResolver().openInputStream(current.uri);
                        final Drawable drawable = Drawable.createFromStream(is, current.uri.toString());
                        cc().handler.post(() -> {
                            Glide.with(cc()).load(drawable).placeholder(drawable).into(imageView);
                        });
                    } catch (FileNotFoundException e) {
                    }
                }).start();

//                Objects.requireNonNull(container).addView(itemView);
                container.addView(itemView);
                return itemView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((FrameLayout) object);
            }
        });


        /*
        if (isCurVideo())
            videoView.setVideoURI(Database.getCurrent().uri);

        GestureDetector gestureDetector = new GestureDetector(cc(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(@NonNull MotionEvent e) {
                        if (videoView.isPlaying()) {
                            videoView.pause();
                        } else if (isCurVideo()) {
                            iv.setVisibility(View.GONE);
                            videoView.setVisibility(View.VISIBLE);
                            videoView.start();
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
                        videoView.setVisibility(View.GONE);

                        glide();
                        if (isCurVideo())
                            videoView.setVideoURI(Database.getCurrent().uri);

                        return false;
                    }
                });

        ScaleGestureDetector scaleGestureDetector = new ScaleGestureDetector(cc(),
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    public boolean onScale(@NonNull ScaleGestureDetector detector) {
                        scaleFactor *= detector.getScaleFactor();  // 無上限和下限

                        iv.setScaleX(scaleFactor);
                        iv.setScaleY(scaleFactor);

                        return true;  // return false會造成縮放暴衝
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
         */
    }


    public void update() {
        viewPager.setCurrentItem(Database.iMedia, false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bed, container, false);
    }

    private MainActivity cc() {
        return (MainActivity) getActivity();
    }
}
