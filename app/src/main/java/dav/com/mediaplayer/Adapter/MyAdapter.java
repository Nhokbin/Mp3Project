package dav.com.mediaplayer.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.math.BigDecimal;

import dav.com.mediaplayer.R;

/**
 * Created by binhb on 20/05/2017.
 */

public class MyAdapter extends SimpleCursorAdapter {


    public MyAdapter(Context context, int layout, Cursor c) {
        super(context, layout, c,
                new String[] {
                        MediaStore.MediaColumns.DISPLAY_NAME,
                        MediaStore.MediaColumns.TITLE,
                        MediaStore.Audio.AudioColumns.DURATION
                },
                new int[] {
                        R.id.displayname,
                        R.id.title,
                        R.id.duration
                },
                FLAG_REGISTER_CONTENT_OBSERVER);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.one_item, parent, false);
        bindView(v, context, cursor);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView title = (TextView)view.findViewById(R.id.title);
        TextView name = (TextView)view.findViewById(R.id.displayname);
        TextView duration = (TextView)view.findViewById(R.id.duration);


        name.setText(cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)));
        title.setText(cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.TITLE)));
        try{
            long durationInMs = Long.parseLong(cursor.getString(
                    cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION)));
            double durationInMin = ((double)durationInMs/1000.0)/60.0;
            durationInMin = new BigDecimal(Double.toString(durationInMin)).setScale(2, BigDecimal.ROUND_UP).doubleValue();
            duration.setText("" + durationInMin);
        }catch (Exception e){

        }

        view.setTag(cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)));

    }
}

