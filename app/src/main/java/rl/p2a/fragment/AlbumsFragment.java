package rl.p2a.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import rl.p2a.Database;
import rl.p2a.R;
import rl.p2a.adapter.AlbumsAdapter;
import rl.p2a.adapter.CellsAdapter;
import rl.p2a.struct.AlbumStruct;
import rl.p2a.MainActivity;

public class AlbumsFragment extends Fragment {
    public static RecyclerView rv;
    public static Parcelable scrollState = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_albums, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity ma = (MainActivity) getActivity();
        assert ma != null;

        rv = view.findViewById(R.id.rv);
        rv.setLayoutManager(new GridLayoutManager(ma, 2));
        rv.setAdapter(new AlbumsAdapter(ma));

        assert rv.getLayoutManager() != null;
        if (scrollState != null)
            rv.getLayoutManager().onRestoreInstanceState(scrollState);
    }

    @Override
    public void onDestroy() {
        assert rv.getAdapter() != null;
        ((AlbumsAdapter) rv.getAdapter()).ma = null;
        rv = null;

        super.onDestroy();
    }
}