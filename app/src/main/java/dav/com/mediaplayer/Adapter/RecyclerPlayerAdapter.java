package dav.com.mediaplayer.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import dav.com.mediaplayer.Object.Song;
import dav.com.mediaplayer.R;

/**
 * Created by binhb on 28/05/2017.
 */

public class RecyclerPlayerAdapter extends RecyclerView.Adapter<RecyclerPlayerAdapter.ViewHolderShowListSong> {

    Context context;
    List<Song> songs;

    public RecyclerPlayerAdapter(Context context, List<Song> songs){
        this.context = context;
        this.songs = songs;
    }

    public class ViewHolderShowListSong extends RecyclerView.ViewHolder {

        ImageView imgArt;
        TextView txtTitle,txtSingerName;
        ImageButton btnAddToList, btnMore;

        public ViewHolderShowListSong(View itemView) {
            super(itemView);

            imgArt = (ImageView) itemView.findViewById(R.id.img_one_row_song);
            txtTitle = (TextView) itemView.findViewById(R.id.txt_one_row_song_title);
            txtSingerName = (TextView) itemView.findViewById(R.id.txt_one_row_singer);
            btnAddToList = (ImageButton) itemView.findViewById(R.id.btn_one_row_song_add_to_list);
            btnMore = (ImageButton) itemView.findViewById(R.id.btn_one_row_song_more);

        }
    }

    @Override
    public ViewHolderShowListSong onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.one_row_song, parent, false);
        ViewHolderShowListSong viewHolderShowListItem = new ViewHolderShowListSong(view);

        return viewHolderShowListItem;
    }

    @Override
    public void onBindViewHolder(ViewHolderShowListSong holder, int position) {
        Song song = songs.get(position);

        if(!song.getSrcImage().equals("")){
            Picasso.with(context).load(song.getSrcImage()).fit().centerInside().into(holder.imgArt);
        }else{
            holder.imgArt.setImageDrawable(null);
        }
        holder.txtTitle.setText(song.getTitle());
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }
}
