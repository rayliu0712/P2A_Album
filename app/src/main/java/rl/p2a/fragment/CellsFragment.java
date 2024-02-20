package rl.p2a.fragment;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import rl.p2a.Database;
import rl.p2a.MainActivity;
import rl.p2a.R;
import rl.p2a.struct.MediaStruct;

public class CellsFragment extends Fragment {

    private GridView gv;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gv = view.findViewById(R.id.gv);
        gv.setOnItemClickListener((parent, view1, i, id) -> {
            cc().switchDisplayedFragment(MainActivity.BED_FRAGMENT, i, true);
        });
        setAdapter();
    }

    public void setAdapter() {
        gv.setAdapter(new ArrayAdapter<MediaStruct>(cc(), R.layout.cells_item, Database.getAlbum().mediaList) {
            @NonNull
            @Override
            public View getView(int i, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.cells_item, parent, false);

                ImageView iv = convertView.findViewById(R.id.iv);
                Drawable drawable = Database.getAlbum().getMedia(i).thumbnailDrawable;
                Glide.with(cc()).load(drawable).placeholder(drawable).into(iv);
                return convertView;
            }
        });
    }


    /* ==================================================================================================== */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cells, container, false);
    }

    private MainActivity cc() {
        return (MainActivity) getActivity();
    }
}