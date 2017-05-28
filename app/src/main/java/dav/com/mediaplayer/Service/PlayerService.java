package dav.com.mediaplayer.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import dav.com.mediaplayer.R;
import dav.com.mediaplayer.View.MainActivity;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static dav.com.mediaplayer.R.id.custom_notification_next;
import static dav.com.mediaplayer.R.id.custom_notification_play;
import static dav.com.mediaplayer.R.id.custom_notification_previous;

public class PlayerService extends Service {

    int UPDATE_FREQUENCY = 500;

    private final Handler handler = new Handler();
    private long mNotificationPostTime = 0;
    private final static MediaPlayer player = new MediaPlayer();


    String url, title, singerName;

    NotificationManager notificationManager;
    BroadcastReceiver notificationReceiver;
    static boolean isPlay;
    static boolean isPlayNoti;
    boolean isReplay = false;

    public PlayerService() {
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            url = intent.getStringExtra("songURL");
            title = intent.getStringExtra("title");
            singerName = intent.getStringExtra("singerName");
            Log.d("412312", url);
            playAudio();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String message = intent.getStringExtra("message");
            if (!message.equals("")) {
                sendDurationToActivity(player.getDuration());
                updatePosition();
            }
        } catch (Exception e) {

        }
        showNotification();

        IntentFilter filter = new IntentFilter("ChangeStatus");
        filter.addAction("SeekChange");
        filter.addAction("ReplayOneSong");
        filter.addAction("GetData");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver(), filter);

        return super.onStartCommand(intent, flags, startId);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void showNotification() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        final RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(),
                R.layout.custom_notification);


        IntentFilter filter = new IntentFilter("ChangeStatusNoti");
        filter.addAction("Next");
        filter.addAction("Previous");
        filter.addAction("stopAudio");
        registerReceiver(notificationReceiver, filter);

        /*Intent iChangeStatusNoti = new Intent("ChangeStatusNoti");
        iChangeStatusNoti.putExtra("isPlay",player.isPlaying());*/
        Intent iPlaying = new Intent(this, MainActivity.class);


        PendingIntent changeStatusIntent = PendingIntent.getBroadcast(this, 12, new Intent("ChangeStatusNoti"), FLAG_UPDATE_CURRENT);
        PendingIntent stopAudioIntent = PendingIntent.getBroadcast(this, 13, new Intent("stopAudio"), FLAG_UPDATE_CURRENT);
        PendingIntent nextIntent = PendingIntent.getBroadcast(this, 14, new Intent("Next"), FLAG_UPDATE_CURRENT);
        PendingIntent previousIntent = PendingIntent.getBroadcast(this, 15, new Intent("Previous"), FLAG_UPDATE_CURRENT);
        PendingIntent clickIntent = PendingIntent.getActivity(this, 0, iPlaying, FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(custom_notification_play, changeStatusIntent);
        remoteViews.setOnClickPendingIntent(custom_notification_previous, previousIntent);
        remoteViews.setOnClickPendingIntent(custom_notification_next, nextIntent);
        remoteViews.setOnClickPendingIntent(R.id.custom_notification_close, stopAudioIntent);
       /* final NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ava6)
                        .setContentTitle("Audio Player")
                        .setContentText(" here")
                        .setContent(remoteViews);*/
        if (mNotificationPostTime == 0) {
            mNotificationPostTime = System.currentTimeMillis();
        }

        int playButtonResId = player.isPlaying() ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play;

        final android.support.v4.app.NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ava6)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ava6))
                .setContentIntent(clickIntent)
                .setContentTitle(singerName)
                .setContentText(title)
                .setWhen(mNotificationPostTime)
                .addAction(android.R.drawable.ic_media_rew,
                        "",
                        previousIntent)
                .addAction(playButtonResId, "",
                        retrievePlaybackAction("ChangeStatusNoti"))
                .addAction(android.R.drawable.ic_media_ff,
                        "",
                        nextIntent);
        /*mBuilder.setContentIntent(changeStatusIntent);*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            builder.setShowWhen(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationCompat.MediaStyle style = new NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2, 3);
            builder.setStyle(style);
        }

        builder.setColor(getResources().getColor(android.R.color.background_dark));

        final Notification notification = builder.build();

        remoteViews.setTextViewText(R.id.txt_custom_notification_title, title);
        remoteViews.setTextViewText(R.id.txt_custom_notification_singer_name, singerName);

        notificationManager.notify(1, notification);

        notification.flags |= Notification.FLAG_ONGOING_EVENT;// Bằng chính nó đem so với thằng #
        startForeground(1, notification);

        notificationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case "ChangeStatusNoti":
                        //isPlay = intent.getBooleanExtra("isPlay",false);
                        if (player.isPlaying()) {
                            handler.removeCallbacks(updatePositionRunnable);
                            player.pause();
                        } else {
                            player.start();
                            updatePosition();
                        }
                        remoteViews.setImageViewResource(R.id.custom_notification_play, player.isPlaying() ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play);
                        //builder.setContent(remoteViews);
                        notificationManager.notify(1, notification);
                        break;
                    case "Next":
                        Intent iNextSong = new Intent("NextSong");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(iNextSong);
                        
                        notificationManager.notify(1, notification);
                        break;
                    case "stopAudio":
                        player.release();
                        handler.removeCallbacks(updatePositionRunnable);
                        notificationManager.cancelAll();
                        stopSelf();
                        break;
                }
            }
        };
    }

    private final PendingIntent retrievePlaybackAction(final String action) {
        //final ComponentName serviceName = new ComponentName(this, PlayerService.class);
        Intent intent = new Intent(action);
        //intent.setComponent(serviceName);

        return PendingIntent.getBroadcast(this, 0, intent, FLAG_UPDATE_CURRENT);
    }

    private BroadcastReceiver receiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case "ChangeStatus":
                        isPlay = intent.getBooleanExtra("isPlay", false);
                        if (!isPlay) {
                            handler.removeCallbacks(updatePositionRunnable);
                            player.pause();
                        } else {
                            player.start();
                            updatePosition();
                        }
                        break;
                    case "SeekChange":
                        int currentPosition = intent.getIntExtra("currentPosition", 0);
                        player.seekTo(currentPosition * player.getDuration() / 100);

                        break;
                    case "ReplayOneSong":
                        isReplay = intent.getBooleanExtra("isReplay", false);
                        updatePosition();
                        break;
                }
            }
        };
    }

    private void sendProgressToActivity(int progress, int currenPosition) {
        Intent iSendProgress = new Intent("SendProgress");

        iSendProgress.putExtra("progress", progress);
        iSendProgress.putExtra("currenPosition", currenPosition);

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(iSendProgress);

    }

    private void sendDurationToActivity(int duration) {
        Intent iSendDuration = new Intent("SendDuration");

        iSendDuration.putExtra("duration", duration);

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(iSendDuration);

    }

    private final Runnable updatePositionRunnable = new Runnable() {
        @Override
        public void run() {
            updatePosition();
        }
    };

    private void updatePosition() {
        handler.removeCallbacks(updatePositionRunnable);
        sendProgressToActivity(player.getCurrentPosition() * 100 / player.getDuration(), player.getCurrentPosition());
        handler.postDelayed(updatePositionRunnable, UPDATE_FREQUENCY);
        if (isReplay) {
            player.setLooping(true);
        } else if (player.getCurrentPosition() == player.getDuration()) {

        }
    }

    private void playAudio() {
        File file = new File(url);
        isPlayNoti = true;
        if (file.exists() && file.isFile()) {
            playMusicFromFile();
        } else {
            playMusicFromURL();
        }

    }

    private void playMusicFromURL() {
       /* if (player != null) {
            player.release();
        }*/
        player.stop();
        player.reset();
        //player = new MediaPlayer();

        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            player.setDataSource(url);
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    sendDurationToActivity(player.getDuration());
                    updatePosition();
                }
            });
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playMusicFromFile() {
       /* if (player != null) {
            player.release();
        }*/
        player.stop();
        player.reset();

        //player = null;
        //player = new MediaPlayer();
        try {
            player.setDataSource(url);
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    sendDurationToActivity(player.getDuration());
                    updatePosition();
                }
            });
            player.prepare();
            player.start();
            //isPlayNoti = true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Không thể phát nhạc", Toast.LENGTH_LONG).show();

        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
