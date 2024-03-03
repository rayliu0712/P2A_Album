package rl.p2a.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import rl.p2a.MainActivity;
import rl.p2a.R;
import rl.p2a.adapter.AlbumsAdapter;

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

        rv = view.findViewById(R.id.rv);
        rv.setLayoutManager(new GridLayoutManager(ma, 2));
        rv.setAdapter(new AlbumsAdapter(ma));

        if (scrollState != null)
            rv.getLayoutManager().onRestoreInstanceState(scrollState);
    }

    @Override
    public void onDestroy() {
        ((AlbumsAdapter) rv.getAdapter()).ma = null;
        rv = null;

        super.onDestroy();
    }
}