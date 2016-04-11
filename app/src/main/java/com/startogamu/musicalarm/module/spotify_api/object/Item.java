
package com.startogamu.musicalarm.module.spotify_api.object;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Generated("org.jsonschema2pojo")
@Data
public class Item {

    @SerializedName("collaborative")
    @Expose
    public Boolean collaborative;
    @SerializedName("external_urls")
    @Expose
    public ExternalUrls externalUrls;
    @SerializedName("href")
    @Expose
    public String href;
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("images")
    @Expose
    public List<com.startogamu.musicalarm.model.spotify.Image> images = new ArrayList<com.startogamu.musicalarm.model.spotify.Image>();
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("owner")
    @Expose
    public Owner owner;
    @SerializedName("public")
    @Expose
    public Boolean _public;
    @SerializedName("snapshot_id")
    @Expose
    public String snapshotId;
    @SerializedName("tracks")
    @Expose
    public Tracks tracks;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("uri")
    @Expose
    public String uri;

}