package dav.com.mediaplayer.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import dav.com.mediaplayer.View.PlayMusic.Fragment.FragmentList;
import dav.com.mediaplayer.View.PlayMusic.Fragment.FragmentPlay;

/**
 * Created by binhb on 24/05/2017.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    List<Fragment> fragments = new ArrayList<>();
    List<String> titleFragemnts = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);

        fragments.add(new FragmentList());
        fragments.add(new FragmentPlay());

    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
