package rl.p2a.fragment;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.Collections;

import rl.p2a.BedPager;
import rl.p2a.Database;
import rl.p2a.MainActivity;
import rl.p2a.R;
import rl.p2a.struct.MediaStruct;

public class BedFragment extends Fragment {
    private int restoreSystemUiVisibility;
    private WindowManager.LayoutParams restoreLayoutParams;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MainActivity ma = (MainActivity) context;

        restoreSystemUiVisibility = ma.getWindow().getDecorView().getSystemUiVisibility();
        restoreLayoutParams = ma.getWindow().getAttributes();

        ma.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        WindowManager.LayoutParams layoutParams = ma.getWindow().getAttributes();
        layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        ma.getWindow().setAttributes(layoutParams);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity ma = (MainActivity) getActivity();

        view.findViewById(R.id.btn).setOnClickListener(v -> {
            MediaStruct current = Database.getAlbum().getMedia();

            PendingIntent pi = MediaStore.createTrashRequest(ma.getContentResolver(), Collections.singletonList(current.uri), true);
            try {
                ma.startIntentSenderForResult(pi.getIntentSender(), 666, null, 0, 0, 0);
            } catch (Exception ignored) {
            }
        });

        BedPager viewPager = view.findViewById(R.id.vp);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Database.iMedia = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                BedPager.pageScrollState = state;
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
                View itemView = ma.getLayoutInflater().inflate(R.layout.bedpager_item, container, false);

                ((TextView) itemView.findViewById(R.id.tv)).setText(String.valueOf(position));

                VideoView videoView = itemView.findViewById(R.id.vv);
                PhotoView imageView = itemView.findViewById(R.id.iv);

                MediaStruct current = Database.getAlbum().getMedia(position);

                if (isVideo(current.uri)) {
                    videoView.setVideoURI(current.uri);
                    videoView.start();
                } else {
                    Drawable thumbnailDrawable = current.thumbnailDrawable;
                    Glide.with(ma).load(thumbnailDrawable).placeholder(thumbnailDrawable).into(imageView);

                    new Thread(() -> {
                        try {
                            Drawable drawable = Drawable.createFromStream(ma.getContentResolver().openInputStream(current.uri), current.uri.toString());

                            // https://github.com/bumptech/glide/issues/1227#issuecomment-220719313, 不加dontTransform()會導致setScaleType()後圖片不清晰
                            ma.handler.post(() -> Glide.with(ma).load(drawable).placeholder(drawable).dontTransform().into(imageView));
                        } catch (Exception ignored) {
                        }
                    }).start();
                }
                container.addView(itemView);
                return itemView;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((FrameLayout) object);
            }
        });

        viewPager.setCurrentItem(Database.iMedia, false);
    }

    @Override
    public void onDetach() {
        MainActivity ma = (MainActivity) getActivity();
        ma.getWindow().getDecorView().setSystemUiVisibility(restoreSystemUiVisibility);
        ma.getWindow().setAttributes(restoreLayoutParams);

        super.onDetach();
    }

    private boolean isVideo(Uri uri) {
        return uri.toString().contains(MediaStore.Video.Media.EXTERNAL_CONTENT_URI + "/");
    }
}