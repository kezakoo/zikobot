package com.startogamu.zikobot.module.spotify_api.model;

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
