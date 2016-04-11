package com.startogamu.musicalarm.module.spotify_api.object;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import lombok.Data;

/**
 * Created by josh on 29/03/16.
 */
@Data
public class SpotifyPlaylistWithTrack {

    @SerializedName("items")
    public ArrayList<SpotifyPlaylistItem> items;

}