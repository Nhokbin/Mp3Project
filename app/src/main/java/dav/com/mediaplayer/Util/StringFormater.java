package dav.com.mediaplayer.Util;

import android.util.Log;

/**
 * Created by binhb on 21/05/2017.
 */

public class StringFormater {
    public static String[] getNameSinger(String songTitle){

        String[] data = new String[0];
        if(songTitle.contains("_")){
            data = songTitle.split("_");

            if(data[0].contains("-")){
                data = data[0].split("-");
            }
            Log.d("1231222 Song name", data[0]);
            Log.d("1231222 Singer name", data[1]);
        }else if(songTitle.contains("-")){
            data = songTitle.split("-");

            Log.d("1231222 Song name", data[0]);
            Log.d("1231222 Singer name", data[1]);
        }

        return data;
    }
}
