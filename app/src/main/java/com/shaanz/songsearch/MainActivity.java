package com.shaanz.songsearch;

import android.os.AsyncTask;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ImageButton searchButton;
    EditText searchText;
    ListView listViewSong;
    ProgressBar progressBar;
    ArrayList<String> listItems = new ArrayList<>();
    ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchButton = (ImageButton) findViewById(R.id.btnSearch);
        searchText = (EditText)findViewById(R.id.editTextSearch);
        listViewSong = (ListView)findViewById(R.id.listViewSearch);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        listAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, listItems);
        listViewSong.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();

        searchButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        updateDisplay(searchText.getText().toString());
                        SongTask newTask = new SongTask();
                        newTask.execute("Param 1", "Param 2");
                        searchText.setText(null);
                    }
                }
        );


    }

    protected void updateDisplay(String message){

        listItems.add(message);
        listAdapter.notifyDataSetChanged();
    }

    private class SongTask extends AsyncTask<String, String, String>{

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
           updateDisplay("Starting Task");
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Task complete";
        }

        @Override
        protected void onPostExecute(String s) {
            updateDisplay(s);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
