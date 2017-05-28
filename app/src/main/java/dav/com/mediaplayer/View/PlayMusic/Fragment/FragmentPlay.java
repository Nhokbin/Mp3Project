package dav.com.mediaplayer.View.PlayMusic.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import dav.com.mediaplayer.R;
import dav.com.mediaplayer.View.PlayMusic.PlayMusicActivity;

import static dav.com.mediaplayer.View.PlayMusic.PlayMusicActivity.imageSrc;

/**
 * Created by binhb on 24/05/2017.
 */

public class FragmentPlay extends Fragment {

    ImageView imgSong;
    Animation.AnimationListener listener;
    int duration;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_play_music, container, false);

        imgSong = (ImageView) view.findViewById(R.id.img_play_music);
        if(imageSrc.equals("")){
            imgSong.setImageBitmap(PlayMusicActivity.songImage);
        }else{
            Picasso.with(getContext()).load(imageSrc).fit().centerInside().into(imgSong);
        }


        listener = new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {

            }
        };
        duration = PlayMusicActivity.duration;
        loadAnimations();
        return view;
    }




    private void loadAnimations() {
        new AnimationUtils();
        Animation rotation = AnimationUtils.loadAnimation(getContext(), R.anim.rotateimage);
        rotation.setDuration(duration/2);
        rotation.setAnimationListener(listener);
        imgSong.startAnimation(rotation);
    }
}
