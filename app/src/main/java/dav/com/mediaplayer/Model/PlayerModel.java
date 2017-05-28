package dav.com.mediaplayer.Model;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import dav.com.mediaplayer.Connect.DownLoadJSON;
import dav.com.mediaplayer.Object.Song;
import dav.com.mediaplayer.Util.StringFormater;

import static dav.com.mediaplayer.Connect.SERVER_IP.SONG;
import static dav.com.mediaplayer.Connect.SERVER_IP.SRC_IMAGE;
import static dav.com.mediaplayer.Connect.SERVER_IP.SRC_MUSIC;

/**
 * Created by binhb on 20/05/2017.
 */

public class PlayerModel {

    public List<Song> getListSongsOnline(int page){

        String url = SONG +"get-all";
        List<HashMap<String,String>> attrs = new ArrayList<>();

        HashMap<String,String> hmPage = new HashMap<>();
        hmPage.put("page", page+"");

        attrs.add(hmPage);

        DownLoadJSON downLoadJSON = new DownLoadJSON(url, attrs);
        downLoadJSON.execute();
        try {
            String data = downLoadJSON.get();
            Log.d("kt123123",data);

            return  getListSongs(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Song> getListSongs(String data) {
        List<Song> songs= new ArrayList<>();

        try {
            JSONArray array = new JSONArray(data);
            int count = array.length();

            for(int i=0; i<count; i++){
                JSONObject object = array.getJSONObject(i);
                Song song = new Song();
                song.setId(object.getInt("id")+"");
                song.setTitle(object.getString("title"));
                song.setData(SRC_MUSIC +object.getString("source")+".mp3");
                song.setSrcImage(SRC_IMAGE+ object.getString("thumbnail")+".jpg");

                songs.add(song);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return songs;
    }

    public List<Song> getListPlayers(Context context){

        String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3");
        String[] selectionArgsMp3 = new String[]{mimeType};

        Cursor cursor = context.getContentResolver().query(MediaStore.Files.getContentUri("external"), null, selectionMimeType, selectionArgsMp3, MediaStore.Audio.Media.TITLE + " ASC");
        List<Song> players = new ArrayList<>();
        if(!cursor.moveToFirst()){
            return null;
        }
        while (cursor.moveToNext()){

            Song song = new Song();

            song.setName(cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)));
            song.setData(cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)));

            String data[] = StringFormater.getNameSinger(cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.TITLE)));

            if(data.length >0 ){
                song.setTitle(data[0]);
                song.setSingerName(data[1]);
            }else{
                song.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.TITLE)));
                song.setSingerName("");
            }


            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(song.getData());
            byte[] art = retriever.getEmbeddedPicture();

            if(art != null){
                Bitmap bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
                song.setThumbnail(bitmap);
            }
            else{
                song.setThumbnail(null);
            }
            retriever.release();
            /*  long durationInMs = Long.parseLong(cursor.getString(
                    cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION)));
            double durationInMin = ((double)durationInMs/1000.0)/60.0;
            durationInMin = new BigDecimal(Double.toString(durationInMin)).setScale(2, BigDecimal.ROUND_UP).doubleValue();
            player.setDuration(durationInMin);*/

            players.add(song);
        }
        return players;
    }
}
