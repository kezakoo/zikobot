
package com.startogamu.zikobot.module.spotify_api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

import lombok.Data;

@Generated("org.jsonschema2pojo")
@Data
public class SpotifyFeaturedPlaylist {

    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("playlists")
    SpotifyPlaylist spotifyPlaylist;
}
