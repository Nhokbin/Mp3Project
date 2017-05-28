package dav.com.mediaplayer.Adapter;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import dav.com.mediaplayer.Object.Song;
import dav.com.mediaplayer.R;
import dav.com.mediaplayer.View.MainActivity;

import static dav.com.mediaplayer.View.MainActivity.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;

/**
 * Created by binhb on 20/05/2017.
 */

public class PlayerAdapter extends BaseAdapter{

    Context context;
    List<Song> songs;
    ViewHolderShowListPlayer viewHolder;

    public PlayerAdapter(Context context, List<Song> songs){
        this.context = context;
        this.songs = songs;
    }

    private class ViewHolderShowListPlayer{
        ImageView imgArt;
        TextView txtTitle,txtSingerName;
        ImageButton btnAddToList, btnMore;
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return songs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {

        View view = convertView;
        if(view == null){
            viewHolder = new ViewHolderShowListPlayer();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.one_row_song,parent,false);

            viewHolder.imgArt = (ImageView) view.findViewById(R.id.img_one_row_song);
            viewHolder.txtTitle = (TextView) view.findViewById(R.id.txt_one_row_song_title);
            viewHolder.txtSingerName = (TextView) view.findViewById(R.id.txt_one_row_singer);
            viewHolder.btnAddToList = (ImageButton) view.findViewById(R.id.btn_one_row_song_add_to_list);
            viewHolder.btnMore = (ImageButton) view.findViewById(R.id.btn_one_row_song_more);

            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolderShowListPlayer) view.getTag();
        }
        final Song song = songs.get(position);

        viewHolder.txtTitle.setText(song.getTitle());
        viewHolder.txtSingerName.setText(song.getSingerName());

        if(song.getThumbnail()!=null){
            viewHolder.imgArt.setImageBitmap(song.getThumbnail());
        }else if(song.getSrcImage()!= null && !song.getSrcImage().equals("")){
            Picasso.with(context).load(song.getSrcImage()).fit().centerInside().into(viewHolder.imgArt);
        }else{
            song.setThumbnail(BitmapFactory.decodeResource(context.getResources(), R.drawable.ava6));
            viewHolder.imgArt.setImageResource(R.drawable.ava6);
        }

        viewHolder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(song.getSrcImage()!=null && !song.getSrcImage().equals("")){
                    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            String action = intent.getAction();
                            if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                                Toast.makeText(context, "Download thành công", Toast.LENGTH_SHORT).show();
                            }
                        }
                    };

                    context.registerReceiver(broadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

                    String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                    int res = context.checkCallingOrSelfPermission(permission);
                    if (res != PackageManager.PERMISSION_GRANTED) {
                        checkPermissionWRITE_EXTERNAL_STORAGE(context);
                    } else {
                        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(song.getData()));

                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI)
                                .setTitle(song.getTitle()+song.getTitle()+".mp3")
                                .setDescription(song.getSingerName())
                                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, song.getTitle()+".mp3");

                        long enqueue = downloadManager.enqueue(request);

                        Intent iDownload = new Intent();
                        iDownload.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);

                        context.startActivity(iDownload);
                    }
                }
            }
        });

        return view;
    }

    public boolean checkPermissionWRITE_EXTERNAL_STORAGE(final Context context) {

        int currentAPIVersion = Build.VERSION.SDK_INT;

        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    MainActivity.showDialog("External storage", context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }
}
