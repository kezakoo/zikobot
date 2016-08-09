package com.startogamu.zikobot.core.fragmentmanager;

import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;
import com.bumptech.glide.Glide;
import com.deezer.sdk.model.Playlist;
import com.joxad.easydatabinding.activity.IPermission;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.startogamu.zikobot.R;
import com.startogamu.zikobot.core.event.EventShowArtistDetail;
import com.startogamu.zikobot.core.event.LocalAlbumSelectEvent;
import com.startogamu.zikobot.core.event.SelectItemPlaylistEvent;
import com.startogamu.zikobot.core.event.deezer.SelectDeezerItemPlaylistEvent;
import com.startogamu.zikobot.core.event.drawer.EventMenuDrawerAlarms;
import com.startogamu.zikobot.core.event.drawer.EventMenuDrawerLocal;
import com.startogamu.zikobot.core.event.navigation_manager.EventAccountSelect;
import com.startogamu.zikobot.core.event.navigation_manager.EventCollapseToolbar;
import com.startogamu.zikobot.core.event.navigation_manager.EventTabBars;
import com.startogamu.zikobot.core.event.soundcloud.SelectSCItemPlaylistEvent;
import com.startogamu.zikobot.core.utils.REQUEST;
import com.startogamu.zikobot.databinding.ActivityMainBinding;
import com.startogamu.zikobot.module.content_resolver.model.LocalAlbum;
import com.startogamu.zikobot.module.soundcloud.model.SoundCloudPlaylist;
import com.startogamu.zikobot.module.spotify_api.model.Item;
import com.startogamu.zikobot.view.activity.ActivityMain;
import com.startogamu.zikobot.view.fragment.alarm.FragmentAlarms;
import com.startogamu.zikobot.view.fragment.deezer.FragmentDeezerPlaylists;
import com.startogamu.zikobot.view.fragment.deezer.FragmentDeezerTracks;
import com.startogamu.zikobot.view.fragment.local.FragmentLocalAlbums;
import com.startogamu.zikobot.view.fragment.local.FragmentLocalArtists;
import com.startogamu.zikobot.view.fragment.local.FragmentLocalPlaylists;
import com.startogamu.zikobot.view.fragment.local.FragmentLocalTracks;
import com.startogamu.zikobot.view.fragment.permission.FragmentPermission;
import com.startogamu.zikobot.view.fragment.search.FragmentSearch;
import com.startogamu.zikobot.view.fragment.soundcloud.FragmentSoundCloudPlaylists;
import com.startogamu.zikobot.view.fragment.soundcloud.FragmentSoundCloudTracks;
import com.startogamu.zikobot.view.fragment.spotify.FragmentSpotifyPlaylists;
import com.startogamu.zikobot.view.fragment.spotify.FragmentSpotifyTracks;
import com.startogamu.zikobot.viewmodel.activity.ActivityMainVM;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import lombok.Data;

/**
 * Created by josh on 10/06/16.
 */
@Data
public class NavigationManager implements IFragmentManager, IPermission {


    public enum Account {local, spotify, deezer, soundcloud}

    private Account current = Account.local;

    private final ActivityMainVM activityMainVM;
    private final ActivityMain activity;
    private final ActivityMainBinding binding;
    private final android.support.v4.app.FragmentManager supportFragmentManager;

    public void init() {
        showLocals();
    }

    /***
     * Show the fragment of local playlist tracks (user has to create them)
     */
    public void showLocals() {
        initTabLayoutTracks();
        replaceFragment(FragmentLocalAlbums.newInstance(null), true, false);
    }


    public void showSearch() {
        replaceFragment(FragmentSearch.newInstance(), false, true);
    }

    @Subscribe
    public void onEvent(EventAccountSelect eventAccountSelect) {
        current = eventAccountSelect.getAccount();
        switch (current) {
            case spotify:
                showSpotifys();
                break;
            case soundcloud:
                showSoundClouds();
                break;
            case local:
                showLocals();
                break;
            case deezer:
                showDeezers();
                break;
        }
    }

    /****
     * filter on spotify playlists
     */
    public void showSpotifys() {
        initTabLayoutTracks();
        replaceFragment(FragmentSpotifyPlaylists.newInstance(), true, false);
    }

    /****
     * filter on soundcloud playlists
     */
    public void showSoundClouds() {
        initTabLayoutTracks();
        replaceFragment(FragmentSoundCloudPlaylists.newInstance(), true, false);
    }

    /****
     * filter on deezer playlists
     */
    public void showDeezers() {
        initTabLayoutTracks();
        replaceFragment(FragmentDeezerPlaylists.newInstance(), true, false);
    }


    /***
     * filter on the alarms
     */
    public void showAlarms() {
        initTabLayoutAlarms();
        replaceFragment(FragmentAlarms.newInstance(), true, false);
    }

    /***
     * Show the libs used in the projects
     */
    public void showAbout() {
        new LibsBuilder()
                .withActivityTheme(R.style.MaterialDrawerTheme)
                .withActivityStyle(Libs.ActivityStyle.LIGHT)
                .start(activity);
    }

    /***
     * EVENTS
     */
    @Subscribe
    public void onEvent(EventMenuDrawerLocal eventMenuDrawerLocal) {
        initTabLayoutTracks();
        replaceFragment(FragmentLocalArtists.newInstance(), true, false);
    }

    @Subscribe
    public void onEvent(EventMenuDrawerAlarms eventMenuDrawerAlarms) {
        initTabLayoutAlarms();
        replaceFragment(FragmentAlarms.newInstance(), true, false);
    }

    @Subscribe
    public void onEvent(SelectItemPlaylistEvent selectItemPlaylistEvent) {
        Item item = selectItemPlaylistEvent.getItem();
        replaceFragment(FragmentSpotifyTracks.newInstance(item, BR.trackVM, R.layout.item_track), false, true);
    }

    @Subscribe
    public void onEvent(SelectSCItemPlaylistEvent selectItemPlaylistEvent) {
        SoundCloudPlaylist item = selectItemPlaylistEvent.getItem();
        replaceFragment(FragmentSoundCloudTracks.newInstance(item, BR.trackVM, R.layout.item_track), false, true);
    }

    @Subscribe
    public void onEvent(SelectDeezerItemPlaylistEvent selectItemPlaylistEvent) {
        Playlist item = selectItemPlaylistEvent.getItem();
        replaceFragment(FragmentDeezerTracks.newInstance(item, BR.trackVM, R.layout.item_track), false, true);
    }

    @Subscribe
    public void onEvent(EventShowArtistDetail eventShowArtistDetail) {
        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(activity, eventShowArtistDetail.getView(), activity.getString(R.string.transition));
        activity.startActivity(IntentManager.goToArtist(eventShowArtistDetail.getArtist()), options.toBundle());
    }


    @Subscribe
    public void onEvent(LocalAlbumSelectEvent localAlbumSelectEvent) {
        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(activity, localAlbumSelectEvent.getView(), activity.getString(R.string.transition));
        activity.startActivity(IntentManager.goToAlbum(localAlbumSelectEvent.getModel()), options.toBundle());
    }


    public void subscribe() {
        EventBus.getDefault().register(this);
    }

    public void unsubscribe() {
        EventBus.getDefault().unregister(this);
    }


    /***
     * Will contains the filters (artist/album/tracks)
     */
    private void initTabLayoutAlarms() {
        TabLayoutManager.initTabLayoutAlarms(activity, binding.tabLayout, tab -> {
            switch (tab.getPosition()) {
                case 0:
                    replaceFragment(FragmentAlarms.newInstance(), true, false);
                    break;
            }
        });
        activityMainVM.fabVisible.set(true);
    }

    /***
     * Will contains the filters (artist/album/tracks)
     */
    private void initTabLayoutTracks() {
        TabLayoutManager.initTabLayoutTracks(activity, current, binding.tabLayout, tab -> {
            Fragment fragment = null;
            switch (tab.getPosition()) {
                case 0:
                    switch (current) {
                        case spotify:
                            fragment = FragmentSpotifyPlaylists.newInstance();
                            break;
                        case soundcloud:
                            fragment = FragmentSoundCloudPlaylists.newInstance();
                            break;
                        case deezer:
                            fragment = FragmentDeezerPlaylists.newInstance();
                            break;
                        case local:
                            fragment = FragmentLocalAlbums.newInstance(null);
                    }
                    break;
                case 1:
                    switch (current) {
                        case local:
                            fragment = FragmentLocalArtists.newInstance();
                            break;
                    }
                    break;
                case 2:
                    switch (current) {
                        case local:
                            fragment = FragmentLocalTracks.newInstance(null, BR.trackVM, R.layout.item_track);
                            break;
                    }
                    break;
            }
            replaceFragment(fragment, false, false);

        });
        activityMainVM.fabVisible.set(false);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST.PERMISSION_STORAGE_ALBUM:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    replaceFragment(FragmentLocalAlbums.newInstance(null), false, true);
                } else {
                    replaceFragment(FragmentPermission.newInstance(activity.getString(R.string.permission_local), REQUEST.PERMISSION_STORAGE_ALBUM), false, false);
                }
                break;
            case REQUEST.PERMISSION_STORAGE_ARTIST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    replaceFragment(FragmentLocalArtists.newInstance(), false, true);
                } else {
                    replaceFragment(FragmentPermission.newInstance(activity.getString(R.string.permission_local), REQUEST.PERMISSION_STORAGE_ARTIST), false, false);
                }
                break;
            case REQUEST.PERMISSION_STORAGE_TRACKS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    replaceFragment(FragmentLocalTracks.newInstance(null, BR.trackVM, R.layout.item_track), false, false);
                } else {
                    replaceFragment(FragmentPermission.newInstance(activity.getString(R.string.permission_local), REQUEST.PERMISSION_STORAGE_TRACKS), false, false);
                }
                break;
            case REQUEST.PERMISSION_STORAGE_PLAYLIST:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    replaceFragment(FragmentLocalPlaylists.newInstance(), false, true);
                } else {
                    replaceFragment(FragmentPermission.newInstance(activity.getString(R.string.permission_local), REQUEST.PERMISSION_STORAGE_PLAYLIST), false, false);
                }
                break;
        }
    }


    @Override
    public void addFragment(Fragment fragment, boolean withBackstack) {
        FragmentManager.addFragment(activity, fragment, withBackstack);
    }

    Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void replaceFragment(Fragment fragment, boolean clearBackStack, boolean addToBackstack) {
        FragmentManager.replaceFragment(activity, fragment, clearBackStack, addToBackstack);
    }


    private void updateToolbar(String name, @Nullable String image) {

        if (name.equals(getActivity().getString(R.string.drawer_filter_search))) {
            //TODO handle search
            binding.title.setText("");
            binding.rlToolbarImage.setVisibility(View.GONE);

        } else {

            binding.mainCollapsing.setTitle(name);
            if (image == null) {
                binding.rlToolbarImage.setVisibility(View.GONE);
                binding.title.setText(name);
            } else {
                binding.title.setText("");
                binding.rlToolbarImage.setVisibility(View.VISIBLE);
                Glide.with(activity).load(image).placeholder(R.drawable.ic_vinyl).into(binding.ivToolbar);
            }
        }
    }


    public void showTabLayout() {
        activityMainVM.tabLayoutVisible.set(true);
    }

    public void hideTabLayout() {
        activityMainVM.tabLayoutVisible.set(false);
    }


    @Subscribe
    public void onEvent(EventCollapseToolbar eventCollapseToolbar) {
        String title = "";
        if (eventCollapseToolbar.getName() == null)
            title = activity.getString(R.string.drawer_filter_music);
        else {
            title = eventCollapseToolbar.getName();
        }

        updateToolbar(title, eventCollapseToolbar.getImageUrl());
    }

    @Subscribe
    public void onEvent(EventTabBars eventTabBars) {
        if (eventTabBars.isVisible()) {
            showTabLayout();
        } else {
            hideTabLayout();
        }
    }


}
