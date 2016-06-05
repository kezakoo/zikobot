package com.startogamu.musicalarm.view.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.startogamu.musicalarm.R;
import com.startogamu.musicalarm.databinding.FragmentSpotifyPlaylistTracksBinding;
import com.startogamu.musicalarm.viewmodel.fragment.SpotifyPlaylistTracksViewModel;

/**
 * Created by josh on 31/03/16.
 */
public class FragmentSpotifyPlaylistTracks extends BaseFragment {

    public final static String TAG = FragmentSpotifyPlaylistTracks.class.getSimpleName();

    FragmentSpotifyPlaylistTracksBinding binding;
    private SpotifyPlaylistTracksViewModel spotifyPlaylistTracksViewModel;

    public static FragmentSpotifyPlaylistTracks newInstance(Bundle bundle) {
        FragmentSpotifyPlaylistTracks fragment = new FragmentSpotifyPlaylistTracks();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_spotify_playlist_tracks, container, false);
        binding.rvItems.setLayoutManager(new LinearLayoutManager(getActivity()));

        spotifyPlaylistTracksViewModel = new SpotifyPlaylistTracksViewModel(this, binding);
        binding.setSpotifyPlaylistTracksViewModel(spotifyPlaylistTracksViewModel);
        setHasOptionsMenu(true);

        return binding.getRoot();
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_music, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }



}