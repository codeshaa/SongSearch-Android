package com.shaanz.songsearch;

import android.content.Context;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.shaanz.songsearch.model.DataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button searchButton;
    EditText searchText;
    ArrayList<DataModel> dataModels;
    ProgressBar progressBar;
    ListView listView;
    Typeface font;
    String uri;
    private static final String KEY = "AIzaSyAbyLQ9SwOP1ZMxRtXxq_FPThnlZhxQxJk";
    private static CustomAdapter adapter;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchButton = (Button) findViewById(R.id.btnSearch);
        font = Typeface.createFromAsset( getAssets(), "fontawesome-webfont.ttf" );
        searchButton.setTypeface(font);
        searchText = (EditText)findViewById(R.id.editTextSearch);
        listView = (ListView) findViewById(R.id.listView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        mediaPlayer = new MediaPlayer();

        searchButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {

                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                        if (isOnline()) {

                            try {
                                uri = "https://www.googleapis.com/youtube/v3/search?q="+ URLEncoder.encode(searchText.getText().toString(), "UTF-8")+"&part=snippet&type=video&maxResults=7&key="+KEY;
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            SongTask songTask = new SongTask();
                            songTask.execute(uri);

                        }
                        else {
                            Toast.makeText(MainActivity.this, "Network is not Available", Toast.LENGTH_LONG).show();
                        }
                        searchText.setText(null);
                    }
                }
        );


    }


    protected boolean isOnline(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()){
            return true;
        }
        else {
            return false;
        }
    }

    private class SongTask extends AsyncTask<String, ArrayList<DataModel>, ArrayList<DataModel>> {

        @Override
        protected void onPreExecute() {

            progressBar.setVisibility(View.VISIBLE);
            Toast.makeText(MainActivity.this, "Searching songs", Toast.LENGTH_LONG);
        }

        @Override
        protected ArrayList<DataModel> doInBackground(String... params) {

            String searchResult = HttpManager.getData(params[0]);
            if (searchResult != null){
                try {
                    JSONObject jsonObject = new JSONObject(searchResult);
//                    Getting JSON Array node

                    JSONArray jsonItems = jsonObject.getJSONArray("items");

//                    Looping through each items
                    dataModels = new ArrayList<>();

                    for (int i = 0; i < jsonItems.length(); i++) {

                        JSONObject item = jsonItems.getJSONObject(i);
                        JSONObject id = item.getJSONObject("id");
                        JSONObject snippet = item.getJSONObject("snippet");
                        String songTitle = snippet.getString("title");
                        String videoId = id.getString("videoId");

                        dataModels.add(new DataModel(songTitle, videoId));


                    }
                } catch (final JSONException e) {
                    e.printStackTrace();
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(getApplicationContext(), "Parsing error: "+e.getMessage(), Toast.LENGTH_LONG);
//                        }
//                    });
                }catch (final Exception e){
                    e.printStackTrace();

                }
                return dataModels;
            }
            else {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), "No server response", Toast.LENGTH_LONG);
//                    }
//                });
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<DataModel> resultData) {

            if (resultData != null) {

                adapter = new CustomAdapter(resultData, getApplicationContext(), mediaPlayer);
                listView.setAdapter(adapter);
                Toast.makeText(MainActivity.this, "Search ended", Toast.LENGTH_LONG);
            }
            else {

                Toast.makeText(MainActivity.this, "Search ended: No response", Toast.LENGTH_LONG);
            }

            progressBar.setVisibility(View.INVISIBLE);

        }
    }

}
