/**
 * Created by SHAANZbook on 1/01/2017.
 */
package com.shaanz.songsearch.model;

public class DataModel {

    private String songTitle;
    private String songID;

    public DataModel(String videoTitle, String videoID){
        this.songTitle = videoTitle;
        this.songID = videoID;
    }

    public String getSongTitle() {
        return songTitle;
    }


    public String getSongID() {
        return songID;
    }

}
