package dav.com.mediaplayer.Connect;

/**
 * Created by binhb on 28/04/2017.
 */

public final class SERVER_IP {

    public static final String IP = "192.168.1.13";
    public static final String PORT = "8080";
    public static final String SERVER ="http://"+ IP +":"+PORT+"/";

    public static final String SONG = SERVER + "song/";
    //http://localhost:8080/music/dem-vu-truong-remix-1495956911873.mp3
    public static final String SRC_MUSIC = SERVER+"music/";
    public static final String SRC_IMAGE = SERVER+"image/";
    //http://localhost:8080/image/category/sang-trong.png
}
