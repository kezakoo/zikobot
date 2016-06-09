package com.startogamu.zikobot.viewmodel.fragment.local;

import android.Manifest;
import android.content.pm.PackageManager;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.joxad.easydatabinding.fragment.FragmentBaseVM;
import com.startogamu.zikobot.BR;
import com.startogamu.zikobot.R;
import com.startogamu.zikobot.core.utils.REQUEST;
import com.startogamu.zikobot.databinding.FragmentLocalArtistsBinding;
import com.startogamu.zikobot.module.alarm.model.AlarmTrack;
import com.startogamu.zikobot.module.component.Injector;
import com.startogamu.zikobot.module.content_resolver.model.LocalArtist;
import com.startogamu.zikobot.module.content_resolver.model.LocalTrack;
import com.startogamu.zikobot.view.fragment.local.FragmentLocalArtists;
import com.startogamu.zikobot.viewmodel.base.ArtistVM;
import com.startogamu.zikobot.viewmodel.base.TrackVM;

import me.tatarka.bindingcollectionadapter.ItemView;

/**
 * Created by josh on 06/06/16.
 */
public class FragmentLocalArtistVM extends FragmentBaseVM<FragmentLocalArtists, FragmentLocalArtistsBinding>{


    private static final String TAG = FragmentLocalArtistVM.class.getSimpleName();
    public ObservableBoolean showZmvMessage;

    public String zmvMessage;

    public ItemView itemView = ItemView.of(BR.artistVM, R.layout.item_artist);
    public ObservableArrayList<ArtistVM> items;
    /***
     * @param fragment
     * @param binding
     */
    public FragmentLocalArtistVM(FragmentLocalArtists fragment, FragmentLocalArtistsBinding binding) {
        super(fragment, binding);
    }

    @Override
    public void init() {
        items = new ObservableArrayList<>();
        showZmvMessage = new ObservableBoolean(false);
        zmvMessage = "";
        Injector.INSTANCE.contentResolverComponent().init(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(fragment.getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                askPermission();
            } else {
                loadLocalMusic();
            }
        } else {
            loadLocalMusic();
            // continue with your code
        }
    }

    /***
     * Load the local music
     */
    public void loadLocalMusic() {
        Injector.INSTANCE.contentResolverComponent().localMusicManager().getLocalArtists().subscribe(localArtists -> {
            Log.d(TAG, "" + localArtists.size());
            for (LocalArtist localArtist : localArtists) {
                items.add(new ArtistVM(fragment.getContext(), localArtist));
            }
            if (localArtists.isEmpty()) {
                updateMessage(fragment.getString(R.string.no_music));
            }
            else {
                showZmvMessage.set(false);
            }
        }, throwable -> {
            updateMessage(fragment.getString(R.string.no_music));

        });
    }
    /***
     * Update t
     *
     * @param string
     */
    private void updateMessage(String string) {
        showZmvMessage.set(true);
        zmvMessage = string;
        binding.zmv.setZmvMessage(zmvMessage);
    }

    public void permissionDenied() {
        updateMessage(fragment.getString(R.string.permission_local));
        binding.zmv.setOnClickListener(v -> askPermission());
    }

    /***
     * Method to ask storage perm
     */
    private void askPermission() {
        ActivityCompat.requestPermissions(fragment.getActivity(), new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST.PERMISSION_STORAGE);

    }

    @Override
    public void destroy() {

    }
}