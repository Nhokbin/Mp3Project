package dav.com.mediaplayer.View.PlayMusic;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.TimeUnit;

import dav.com.mediaplayer.Adapter.ViewPagerAdapter;
import dav.com.mediaplayer.Model.PlayerModel;
import dav.com.mediaplayer.Object.Song;
import dav.com.mediaplayer.R;
import dav.com.mediaplayer.Service.PlayerService;

import static dav.com.mediaplayer.View.MainActivity.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
import static dav.com.mediaplayer.View.MainActivity.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;


public class PlayMusicActivity extends AppCompatActivity implements View.OnClickListener {

    public static int duration;

    TextView txtStart, txtEnd, txtSongName, txtSingerName;
    SeekBar seekBar;
    ImageButton btnRandom, btnRew, btnPlay, btnFF, btnReplay;
    ViewPager viewPager;
    Toolbar toolbar;


    boolean isPlay = true;
    boolean isReplay = false;
    int songPosition;
    String songName, singerName;
    PlayerModel playerModel;

    public static Bitmap songImage;
    public static String imageSrc="";
    public static List<Song> songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);

        txtStart = (TextView) findViewById(R.id.txt_play_music_start);
        txtEnd = (TextView) findViewById(R.id.txt_play_music_end);
        txtSongName = (TextView) findViewById(R.id.txt_play_music_name);
        txtSingerName = (TextView) findViewById(R.id.txt_play_music_singer_name);
        seekBar = (SeekBar) findViewById(R.id.seekbar_play_music);
        btnRandom = (ImageButton) findViewById(R.id.btn_play_music_random);
        btnRew = (ImageButton) findViewById(R.id.btn_play_music_rew);
        btnPlay = (ImageButton) findViewById(R.id.btn_play_music_play);
        btnFF = (ImageButton) findViewById(R.id.btn_play_music_ff);
        btnReplay = (ImageButton) findViewById(R.id.btn_play_music_replay);
        toolbar = (Toolbar) findViewById(R.id.tb_play_music);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        checkPermission();


        btnRandom.setOnClickListener(this);
        btnRew.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnFF.setOnClickListener(this);
        btnReplay.setOnClickListener(this);

        Intent i = getIntent();
        duration = i.getIntExtra("duration", 0);
        byte[] byteArray = i.getByteArrayExtra("songImage");
        if(byteArray!=null){
            songImage =  BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        }else{
            imageSrc = i.getStringExtra("songImageUrl");
        }
        songName = i.getStringExtra("songName");
        singerName = i.getStringExtra("singerName");

        IntentFilter filter = new IntentFilter("SendDuration");
        filter.addAction("SendProgress");
        LocalBroadcastManager.getInstance(this).registerReceiver(createMessageReceiver(), filter);

        startService(createIntentService("playMusic"));


        setSupportActionBar(toolbar);

        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_keyboard_backspace_white_24dp);
        upArrow.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        txtSongName.setText(songName);
        txtSingerName.setText(singerName);

    }

    private Intent createIntentService(String message) {
        Intent iService = new Intent(this, PlayerService.class);
        iService.putExtra("message", message);
        return iService;
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

                        seekBar.setProgress(progress);
                        txtStart.setText(startTime);

                        break;
                }
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_play_music_play:
                playMusic();
                break;
            case R.id.btn_play_music_replay:
                relplayOneSong();
                break;
            case R.id.btn_play_music_rew:
                if (songPosition > 0) {
                    songPosition--;
                }
                playMusicWithPosition(songPosition);
                break;
            case R.id.btn_play_music_ff:
                if (songPosition < songs.size()) {
                    songPosition++;
                }
                playMusicWithPosition(songPosition);
                break;
            case R.id.radom:

                break;
        }
    }

    private void playMusic() {
        changeStatusMedia();
        btnPlay.setImageResource(isPlay ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play);
    }

    private void changeStatusMedia() {
        Intent iChangeStatus = new Intent("ChangeStatus");
        isPlay = !isPlay;
        iChangeStatus.putExtra("isPlay", isPlay);
        LocalBroadcastManager.getInstance(this).sendBroadcast(iChangeStatus);
    }

    private void relplayOneSong() {
        Toast.makeText(this, "Click", Toast.LENGTH_SHORT).show();
        Intent iReplayOneSong = new Intent("ReplayOneSong");
        isReplay = !isReplay;
        iReplayOneSong.putExtra("isReplay", isReplay);
        LocalBroadcastManager.getInstance(this).sendBroadcast(iReplayOneSong);
    }

    private void playMusicWithPosition(int position) {
        Song song = songs.get(position);
        isPlay = true;
        btnPlay.setImageResource(android.R.drawable.ic_media_pause);
        startService(createIntentService(song.getName(), song.getData()));
    }

    private Intent createIntentService(String name, String data) {
        Intent iService = new Intent(this, PlayerService.class);
        iService.putExtra("name", name);
        iService.putExtra("data", data);
        return iService;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    playerModel = new PlayerModel();
                    songs = playerModel.getListPlayers(PlayMusicActivity.this);
                } else {
                    Toast.makeText(PlayMusicActivity.this, "Load File is denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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

    public boolean checkPermissionWRITE_EXTERNAL_STORAGE(final Context context) {

        int currentAPIVersion = Build.VERSION.SDK_INT;

        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showDialog("External storage", this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
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

    public void showDialog(final String msg, final Context context, final String permission) {
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

    private void checkPermission() {
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        int res = this.checkCallingOrSelfPermission(permission);
        if (res != PackageManager.PERMISSION_GRANTED) {
            checkPermissionREAD_EXTERNAL_STORAGE(PlayMusicActivity.this);
        } else {
            playerModel = new PlayerModel();
            songs = playerModel.getListPlayers(PlayMusicActivity.this);
        }
    }
}
