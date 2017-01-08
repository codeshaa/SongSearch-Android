package com.shaanz.songsearch;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shaanz.songsearch.MainActivity;
import com.shaanz.songsearch.R;
import com.shaanz.songsearch.model.DataModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by SHAANZbook on 1/01/2017.
 */

public class CustomAdapter extends ArrayAdapter<DataModel> implements View.OnClickListener {

    private ArrayList<DataModel> dataSet;
    Context mContext;
    Typeface font;
    String mediaURL;
    MediaPlayer mediaPlayer;

    private static class ViewHolder {
        TextView txtSongTitle;
        Button btnPlay;
        Button btnDownload;
        String songID;
    }

    public CustomAdapter(ArrayList<DataModel> data, Context context, MediaPlayer songPlayer) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext = context;
        this.font = Typeface.createFromAsset(mContext.getResources().getAssets(), "fontawesome-webfont.ttf");
        this.mediaPlayer = songPlayer;
    }

    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        Object object = getItem(position);
        DataModel dataModel = (DataModel)object;

        switch (v.getId())
        {
            case R.id.btnPlay:

                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        try {
                            mediaPlayer.stop();

                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                    }
                    mediaPlayer.reset();
                }

                mediaURL = "https://www.youtubeinmp3.com/fetch/?video=https://www.youtube.com/watch?v=" + dataModel.getSongID();
                try {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setDataSource(mediaURL);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e){
                    e.printStackTrace();
                }

                Toast.makeText(this.mContext, "Playing: "+dataModel.getSongTitle(), Toast.LENGTH_SHORT).show();

                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });

                mediaPlayer.prepareAsync();


                break;
            case R.id.btnDownload:
                Toast.makeText(this.mContext, "Download ID: "+dataModel.getSongID(), Toast.LENGTH_SHORT).show();
                break;
        }
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.txtSongTitle = (TextView) convertView.findViewById(R.id.textSongTitle);
            viewHolder.btnPlay = (Button) convertView.findViewById(R.id.btnPlay);
            viewHolder.btnPlay.setTypeface(font);
            viewHolder.btnDownload = (Button) convertView.findViewById(R.id.btnDownload);
           viewHolder.btnDownload.setTypeface(font);

            result = convertView;

            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.txtSongTitle.setText(dataModel.getSongTitle());
        viewHolder.songID = dataModel.getSongID();
        viewHolder.btnPlay.setOnClickListener(this);
        viewHolder.btnPlay.setTag(position);
        viewHolder.btnDownload.setOnClickListener(this);
        viewHolder.btnDownload.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}

