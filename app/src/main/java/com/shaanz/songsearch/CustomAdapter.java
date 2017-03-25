package com.shaanz.songsearch;

import android.app.DownloadManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shaanz.songsearch.MainActivity;
import com.shaanz.songsearch.R;
import com.shaanz.songsearch.model.DataModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static android.media.MediaPlayer.MEDIA_ERROR_TIMED_OUT;

/**
 * Created by SHAANZbook on 1/01/2017.
 */

public class CustomAdapter extends ArrayAdapter<DataModel> implements View.OnClickListener {

    private ArrayList<DataModel> dataSet;
    private Context mContext;
    private Typeface font;
    private String mediaURL;
    private MediaPlayer mediaPlayer;
    private View preItem = null;
    private int prePos = 100;

    private static class ViewHolder {
        TextView txtSongTitle;
        Button btnPlay;
        Button btnPause;
        Button btnDownload;
        String songID;
    }

    CustomAdapter(ArrayList<DataModel> data, Context context, MediaPlayer songPlayer) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext = context;
        this.font = Typeface.createFromAsset(mContext.getResources().getAssets(), "fontawesome-webfont.ttf");
        this.mediaPlayer = songPlayer;
        preItem = null;
    }

    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        Object object = getItem(position);
        DataModel dataModel = (DataModel)object;
        final String songTitle;
        songTitle = dataModel.getSongTitle();
        ViewGroup row = (ViewGroup) v.getParent();
        final Button bPlay = (Button) row.getChildAt(0);
        final Button bStop = (Button) row.getChildAt(1);

            if (prePos != 100) {
                ViewGroup list = (ViewGroup) row.getParent();
                ViewGroup listItem = (ViewGroup) list.getChildAt(prePos);
                listItem.getChildAt(0).setVisibility(View.VISIBLE);
                listItem.getChildAt(1).setVisibility(View.GONE);
            }

            if (preItem != null) {
                preItem.setBackgroundColor(Color.rgb(11, 188, 176));
            }


        switch (v.getId())
        {
            case R.id.btnPlay:

                prePos = position;

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
                View listItemView = (View) v.getParent();
                listItemView.setBackgroundColor(Color.rgb(5, 112, 104));
                preItem = listItemView;

                    mediaURL = "https://www.youtubeinmp3.com/fetch/?video=https://www.youtube.com/watch?v=" + dataModel.getSongID();

                try {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setDataSource(mediaURL);
                } catch (IOException | IllegalStateException e) {
                    e.printStackTrace();
                }

                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {

                        Toast.makeText(getContext(), "Playing: "+songTitle, Toast.LENGTH_SHORT).show();
                        mp.start();

                    }
                });
                mediaPlayer.prepareAsync();

                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    //Handle errors
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        switch (what) {
                            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK: " + extra);
                                break;
                            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED: " + extra);
                                break;
                            case MEDIA_ERROR_TIMED_OUT:
                                Log.d("Time Out", "Media player is timed out");
                                break;
                            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN: " + extra);
                                Toast.makeText(getContext(), "No access: Please try another", Toast.LENGTH_SHORT).show();
                                bPlay.setVisibility(View.VISIBLE);
                                bStop.setVisibility(View.GONE);
                                break;
                        }
                        preItem.setBackgroundColor(Color.rgb(11, 188, 176));
                        return false;
                    }

                });

                bPlay.setVisibility(View.GONE);
                bStop.setVisibility(View.VISIBLE);

                break;
            case R.id.btnStop:

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

                bPlay.setVisibility(View.VISIBLE);
                bStop.setVisibility(View.GONE);

                break;

            case R.id.btnDownload:

                if (prePos != 100) {
                    ViewGroup list = (ViewGroup) row.getParent();
                    ViewGroup listItem = (ViewGroup) list.getChildAt(prePos);
                    listItem.getChildAt(0).setVisibility(View.GONE);
                    ((Button) listItem.getChildAt(1)).setVisibility(View.VISIBLE);
                }

                if (preItem != null) {
                    preItem.setBackgroundColor(Color.rgb(5, 112, 104));
                }

                if (isDownloadManagerAvailable(this.mContext)){
                    String url = "https://www.youtubeinmp3.com/fetch/?video=https://www.youtube.com/watch?v=" + dataModel.getSongID();
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    request.setDescription("SongSearch " + dataModel.getSongTitle());
                    request.setTitle("SongSearch " + dataModel.getSongTitle());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    }
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, dataModel.getSongTitle() + ".mp3");

                    DownloadManager downloadManager = (DownloadManager) this.mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                    downloadManager.enqueue(request);

                    Toast.makeText(this.mContext, "Downloading: "+dataModel.getSongID(), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this.mContext, "Oops! Minimum Gingerbread version required ", Toast.LENGTH_LONG).show();
                }

                break;
        }
    }


    public static boolean isDownloadManagerAvailable(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return true;
        }
        return false;
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
            viewHolder.btnPause = (Button) convertView.findViewById(R.id.btnStop);
            viewHolder.btnPause.setTypeface(font);
            viewHolder.btnPause.setVisibility(View.GONE);
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
        viewHolder.btnPause.setOnClickListener(this);
        viewHolder.btnPause.setTag(position);
        viewHolder.btnDownload.setOnClickListener(this);
        viewHolder.btnDownload.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}

