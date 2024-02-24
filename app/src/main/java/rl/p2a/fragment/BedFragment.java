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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity ma = (MainActivity) getActivity();
        assert ma != null;

        ViewPager viewPager = view.findViewById(R.id.vp);
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
                View itemView = ma.getLayoutInflater().inflate(R.layout.viewpager_item, container, false);

                ImageView imageView = itemView.findViewById(R.id.iv);

                MediaStruct current = Database.getAlbum().getMedia(position);

                Drawable thumbnailDrawable = current.thumbnailDrawable;
                Glide.with(ma).load(thumbnailDrawable).placeholder(thumbnailDrawable).into(imageView);


                new Thread(() -> {
                    try {
                        final InputStream is = ma.getContentResolver().openInputStream(current.uri);
                        final Drawable drawable = Drawable.createFromStream(is, current.uri.toString());
                        MainActivity.handler.post(() -> Glide.with(ma).load(drawable).placeholder(drawable).into(imageView));
                    } catch (FileNotFoundException ignored) {
                    }
                }).start();

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
}
