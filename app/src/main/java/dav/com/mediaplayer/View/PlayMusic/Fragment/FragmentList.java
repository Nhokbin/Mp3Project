package dav.com.mediaplayer.View.PlayMusic.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dav.com.mediaplayer.Adapter.SongAdapter;
import dav.com.mediaplayer.R;
import dav.com.mediaplayer.View.PlayMusic.PlayMusicActivity;

/**
 * Created by binhb on 24/05/2017.
 */

public class FragmentList extends Fragment {

    RecyclerView recycler;
    SongAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_list_music, container, false);

        recycler = (RecyclerView) view.findViewById(R.id.recycler_songs);

        adapter = new SongAdapter(getContext(), PlayMusicActivity.songs);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        return view;
    }


}
