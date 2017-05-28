package dav.com.mediaplayer.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import dav.com.mediaplayer.Object.Song;
import dav.com.mediaplayer.R;

/**
 * Created by binhb on 25/05/2017.
 */
public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolderShowListSong> {

    Context context;
    List<Song> songs;
    ViewHolderShowListSong viewHolder;

    public SongAdapter(Context context, List<Song> songs) {
        this.context = context;
        this.songs = songs;
    }

    class ViewHolderShowListSong extends RecyclerView.ViewHolder {

        ImageView imgIcon;
        TextView txtSongName, txtSingerName;
        ImageButton imgMore;

        public ViewHolderShowListSong(View itemView) {
            super(itemView);

            imgIcon = (ImageView) itemView.findViewById(R.id.img_one_row_song_play_music);
            txtSongName = (TextView) itemView.findViewById(R.id.txt_one_row_song_play_music_song_name);
            txtSingerName = (TextView) itemView.findViewById(R.id.txt_one_row_song_play_music_singer_name);
            imgMore = (ImageButton) itemView.findViewById(R.id.btn_one_row_song_play_music_more);
        }
    }

    @Override
    public ViewHolderShowListSong onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.one_row_song_play_music, parent, false);

        ViewHolderShowListSong viewHolder = new ViewHolderShowListSong(view);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolderShowListSong holder, int position) {
        Song song = songs.get(position);

        holder.txtSingerName.setText(song.getSingerName());
        holder.txtSongName.setText(song.getTitle());

        if (song.getThumbnail() != null) {
            holder.imgIcon.setImageBitmap(song.getThumbnail());
        } else {
            holder.imgIcon.setImageResource(R.drawable.ava6);
        }

    }

    @Override
    public int getItemCount() {
        return songs.size();
    }
}
