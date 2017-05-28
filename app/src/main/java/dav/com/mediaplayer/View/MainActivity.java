package dav.com.mediaplayer.View;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import dav.com.mediaplayer.Adapter.MyAdapter;
import dav.com.mediaplayer.Adapter.PlayerAdapter;
import dav.com.mediaplayer.Model.PlayerModel;
import dav.com.mediaplayer.Object.Song;
import dav.com.mediaplayer.R;
import dav.com.mediaplayer.Service.PlayerService;
import dav.com.mediaplayer.View.PlayMusic.PlayMusicActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener,
        SeekBar.OnSeekBarChangeListener, SwipeRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener {

    TextView tv_fileduocchon, txtStart, txtEnd, txtIsOnline;
    SeekBar seekbar;
    ImageButton btnPlay, btnReplay, btnRandom, btnRew, btnFF;
    ListView lv, recyclerSongs;
    SwipeRefreshLayout swipelayout;
    TabHost tabHost;

    PlayerModel playerModel;

    List<Song> players;
    public static List<Song> listOnlie;
    Song song;

    int songPosition, duration;

    boolean isConnection = false;
    boolean isPlay = true;
    boolean changedSeekbar = false;
    boolean isPlayOnline = false;
    boolean isReplay = false;

    String filehientai = "";

    String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";

    String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3");

    String[] selectionArgsMp3 = new String[]{mimeType};

    PlayerAdapter adapter, onlineAdapter;

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 133;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_fileduocchon = (TextView) findViewById(R.id.selectedfile);
        txtStart = (TextView) findViewById(R.id.txt_start);
        txtEnd = (TextView) findViewById(R.id.txt_end);
        txtIsOnline = (TextView) findViewById(R.id.txt_main_online);
        btnPlay = (ImageButton) findViewById(R.id.play);
        btnRew = (ImageButton) findViewById(R.id.rew);
        btnFF = (ImageButton) findViewById(R.id.ff);
        btnReplay = (ImageButton) findViewById(R.id.replay);
        btnRandom = (ImageButton) findViewById(R.id.radom);
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        lv = (ListView) findViewById(R.id.list);
        swipelayout = (SwipeRefreshLayout) findViewById(R.id.ln_online);
        recyclerSongs = (ListView) findViewById(R.id.recycler_songs);
        tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();
        TabHost.TabSpec mSpec = tabHost.newTabSpec("First Tab");
        mSpec.setContent(R.id.list);
        mSpec.setIndicator("Offline");
        tabHost.addTab(mSpec);
        mSpec = tabHost.newTabSpec("Second Tab");
        mSpec.setContent(R.id.ln_online);
        mSpec.setIndicator("Online");
        tabHost.addTab(mSpec);

        swipelayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );

        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        int res = this.checkCallingOrSelfPermission(permission);

        if (res != PackageManager.PERMISSION_GRANTED) {
            checkPermissionREAD_EXTERNAL_STORAGE(MainActivity.this);
        } else {
            playerModel = new PlayerModel();
            players = playerModel.getListPlayers(MainActivity.this);
            // player = new MediaPlayer();
            adapter = new PlayerAdapter(MainActivity.this, players);
            lv.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }

        seekbar.setOnSeekBarChangeListener(this);

        btnReplay.setOnClickListener(this);
        btnRandom.setOnClickListener(this);
        btnRew.setOnClickListener(this);
        btnFF.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        lv.setOnItemClickListener(this);
        recyclerSongs.setOnItemClickListener(this);
        recyclerSongs.setOnScrollListener(this);
        swipelayout.setOnRefreshListener(this);

        isConnection = isConnected(this);
        if (isConnection) {
            txtIsOnline.setVisibility(View.GONE);
            getDataInSerer();
        }

        IntentFilter filter = new IntentFilter("SendDuration");
        filter.addAction("NextSong");
        filter.addAction("SendProgress");
        filter.addAction("ShowData");
        LocalBroadcastManager.getInstance(this).registerReceiver(createMessageReceiver(), filter);

    }

    private void getDataInSerer() {
        listOnlie = playerModel.getListSongsOnline(1);
       /* LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerSongs.setLayoutManager(layoutManager);
        recyclerPlayerAdapter = new RecyclerPlayerAdapter(this, listOnlie);
        recyclerSongs.setAdapter(recyclerPlayerAdapter);*/
        onlineAdapter = new PlayerAdapter(MainActivity.this, listOnlie);
        recyclerSongs.setAdapter(onlineAdapter);
        onlineAdapter.notifyDataSetChanged();
//        recyclerPlayerAdapter.notifyDataSetChanged();
    }

    public static final boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
     /*   return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();*/
    }

    private BroadcastReceiver createMessageReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case "SendDuration":
                        duration = intent.getIntExtra("duration", 0);

                        String endTime = String.format("%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(duration),
                                TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));

                        txtEnd.setText(endTime);

                        break;
                    case "SendProgress":
                        int progress = intent.getIntExtra("progress", 0);
                        int currentPosition = intent.getIntExtra("currenPosition", 0);

                        String startTime = String.format("%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(currentPosition),
                                TimeUnit.MILLISECONDS.toSeconds(currentPosition) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentPosition)));

                        seekbar.setProgress(progress);
                        txtStart.setText(startTime);
                        Log.d("12312#",duration + " "+ currentPosition);
                        if(currentPosition >= duration){
                            if(songPosition >= players.size()){
                                songPosition = 0;
                            }else{
                                songPosition ++;
                            }
                            playMusicWithPosition(songPosition, isPlayOnline ? listOnlie : players);
                        }

                        break;
                    case "NextSong":
                        Log.d("13123", "NextSong");
                        if (songPosition < players.size()) {
                            songPosition++;
                        }
                        //stopService( new Intent(MainActivity.this, PlayerService.class));
                        Intent iPlaySong = new Intent("PlaySong");
                        playMusicWithPosition(songPosition, players);
                        break;
                    case "ShowData":
                        Log.d("checkConnection", 123333333 + "");

                        //new ShowProgressBarTask().execute(value);
                        Toast.makeText(MainActivity.this, "111111111111111", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
    }

    private Intent createIntentService(String url) {
        Intent iService = new Intent(this, PlayerService.class);
        iService.putExtra("songURL", url);
        iService.putExtra("title", song.getTitle());
        iService.putExtra("singerName", song.getSingerName());
        return iService;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, selectionMimeType, selectionArgsMp3, null);
                    MyAdapter adapter = new MyAdapter(MainActivity.this, R.layout.one_item, cursor);
                    lv.setAdapter(adapter);

                } else {
                    Toast.makeText(MainActivity.this, "Load File is denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    public static void showDialog(final String msg, final Context context, final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{permission},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(final Context context) {

        int currentAPIVersion = Build.VERSION.SDK_INT;

        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", this, Manifest.permission.READ_EXTERNAL_STORAGE);
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }


    @Override
    public void onClick(View v) {
        int seekto = 0;
        switch (v.getId()) {
            case R.id.play:
                playMusic();
                break;
            case R.id.replay:
                relplayOneSong();
                break;
            case R.id.rew:
                if (songPosition > 0) {
                    songPosition--;
                }
                if (isPlayOnline) {

                    playMusicWithPosition(songPosition, listOnlie);
                } else {

                    playMusicWithPosition(songPosition, players);
                }
                break;
            case R.id.ff:
                if (isPlayOnline) {
                    if (songPosition < listOnlie.size()) {
                        songPosition++;
                    }
                    playMusicWithPosition(songPosition, listOnlie);
                } else {
                    if (songPosition < players.size()) {
                        songPosition++;
                    }
                    playMusicWithPosition(songPosition, players);
                }

                break;
            case R.id.radom:


                break;
            case R.id.selectedfile:
                Intent iPlayMusicActivity = new Intent(MainActivity.this, PlayMusicActivity.class);
                iPlayMusicActivity.putExtra("duration", duration);
                iPlayMusicActivity.putExtra("songName", song.getTitle());
                iPlayMusicActivity.putExtra("singerName", song.getSingerName());
                //Convert to byte array
                if (song.getThumbnail() != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    song.getThumbnail().compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    iPlayMusicActivity.putExtra("songImage", byteArray);
                } else if (!song.getSrcImage().equals("")) {
                    iPlayMusicActivity.putExtra("songImageUrl", song.getSrcImage());
                }


                startActivity(iPlayMusicActivity);
                break;
        }
    }

    private void relplayOneSong() {
        Toast.makeText(this, "Click", Toast.LENGTH_SHORT).show();
        Intent iReplayOneSong = new Intent("ReplayOneSong");
        isReplay = !isReplay;
        iReplayOneSong.putExtra("isReplay", isReplay);
        LocalBroadcastManager.getInstance(this).sendBroadcast(iReplayOneSong);
    }


    private void changeStatusMedia() {
        Intent iChangeStatus = new Intent("ChangeStatus");
        isPlay = !isPlay;
        iChangeStatus.putExtra("isPlay", isPlay);
        LocalBroadcastManager.getInstance(this).sendBroadcast(iChangeStatus);
    }

    private void playMusic() {
        changeStatusMedia();
        btnPlay.setImageResource(isPlay ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        songPosition = position;
        if (parent.getAdapter() == adapter) {
            isPlayOnline = false;
            playMusicWithPosition(songPosition, players);
        } else {
            isPlayOnline = true;
            playMusicWithPosition(songPosition, listOnlie);
        }
    }

    private void playMusicWithPosition(int position, List<Song> songs) {
        //stopService( new Intent(MainActivity.this, PlayerService.class));
        if (position >= songs.size()) {
            position = 0;
        }
        song = songs.get(position);
        filehientai = songs.get(position).getData();

        isPlay = true;
        tv_fileduocchon.setText(song.getTitle());
        tv_fileduocchon.setOnClickListener(this);

        btnPlay.setImageResource(android.R.drawable.ic_media_pause);
        startService(createIntentService(filehientai));
    }

    private void sendCurrentPositionToService(int currentPosition) {
        Intent iSeekChange = new Intent("SeekChange");

        iSeekChange.putExtra("currentPosition", currentPosition);

        LocalBroadcastManager.getInstance(this).sendBroadcast(iSeekChange);

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (changedSeekbar == true)
            sendCurrentPositionToService(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        changedSeekbar = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        changedSeekbar = false;
    }


    @Override
    public void onRefresh() {
        if (isConnected(this)) {
            txtIsOnline.setVisibility(View.GONE);
            getDataInSerer();
        }
        swipelayout.setRefreshing(false);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int topRowVerticalPosition = (recyclerSongs == null || recyclerSongs.getChildCount() == 0) ?
                0 : recyclerSongs.getChildAt(0).getTop();
        swipelayout.setEnabled((topRowVerticalPosition >= 0));

        if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
            listOnlie.addAll(playerModel.getListSongsOnline((int) Math.ceil(listOnlie.size() / 20.0) + 1));
            onlineAdapter.notifyDataSetChanged();
        }
    }
}
