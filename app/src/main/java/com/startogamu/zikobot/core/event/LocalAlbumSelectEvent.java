package com.startogamu.zikobot.core.event;

import android.view.View;

import com.startogamu.zikobot.module.content_resolver.model.LocalAlbum;

import lombok.Data;

/**
 * Created by josh on 06/06/16.
 */
@Data
public class LocalAlbumSelectEvent {
    private final LocalAlbum model;
    private final View view;
}
