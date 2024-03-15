package rl.p2a.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import rl.p2a.Database;
import rl.p2a.Tools;
import rl.p2a.MainActivity;
import rl.p2a.R;
import rl.p2a.adapter.CellsAdapter;

public class CellsFragment extends Fragment {
    public static RecyclerView rv = null;
    public static Parcelable scrollState = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cells, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity ma = (MainActivity) getActivity();

        ((TextView) view.findViewById(R.id.tv)).setText(Database.getAlbum().galleryName);

        final int spanCount = 7;
        rv = view.findViewById(R.id.rv);
        rv.setLayoutManager(new GridLayoutManager(ma, spanCount));
        rv.setAdapter(new CellsAdapter(ma));


        if (scrollState != null)
            rv.getLayoutManager().onRestoreInstanceState(scrollState);
    }
}