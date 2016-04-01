package com.startogamu.musicalarm.model.spotify;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.util.ArrayList;

import lombok.Data;

/**
 * Created by josh on 29/03/16.
 */
@Data
public class SpotifyTrack {
    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("album")
    private SpotifyAlbum album;
    @SerializedName("artists")
    private ArrayList<SpotifyArtist> artists;
    @SerializedName("type")
    private String type;
    @SerializedName("duration_ms")
    private long duration;
}
