package com.startogamu.zikobot.viewmodel.activity;

import android.animation.Animator;
import android.content.Context;
import android.media.AudioManager;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;
import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;
import com.joxad.android_easy_spotify.SpotifyPlayerManager;
import com.joxad.easydatabinding.activity.ActivityBaseVM;
import com.startogamu.zikobot.R;
import com.startogamu.zikobot.core.event.player.TrackChangeEvent;
import com.startogamu.zikobot.core.notification.PlayerNotification;
import com.startogamu.zikobot.core.utils.AnimationEndListener;
import com.startogamu.zikobot.core.utils.AppPrefs;
import com.startogamu.zikobot.databinding.ActivityWakeUpBinding;
import com.startogamu.zikobot.module.component.Injector;
import com.startogamu.zikobot.module.mock.Mock;
import com.startogamu.zikobot.module.music.manager.PlayerMusicManager;
import com.startogamu.zikobot.module.zikobot.model.Alarm;
import com.startogamu.zikobot.view.activity.ActivityWakeUp;
import com.startogamu.zikobot.viewmodel.base.AlarmVM;
import com.startogamu.zikobot.viewmodel.base.TrackVM;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.UnsupportedEncodingException;

import me.tatarka.bindingcollectionadapter.ItemView;

/**
 * Created by josh on 31/05/16.
 */
public class ActivityWakeUpVM extends ActivityBaseVM<ActivityWakeUp, ActivityWakeUpBinding> {


    private static final int ROTATION = 2;
    private static final int DELAY = 10;//MS
    @InjectExtra
    Alarm alarm;
    AudioManager am;


    int rotation = ROTATION;
    public AlarmVM alarmVM;
    public TrackVM trackVM;

    PlayerMusicManager playerMusicManager;

    /***
     * @param activity
     * @param binding
     */
    public ActivityWakeUpVM(ActivityWakeUp activity, ActivityWakeUpBinding binding) {
        super(activity, binding);

    }

    @Override
    public void init() {
        SpotifyPlayerManager.updateToken(AppPrefs.getSpotifyAccessToken());

        Injector.INSTANCE.spotifyAuth().inject(this);
        Injector.INSTANCE.playerComponent().inject(this);
        EventBus.getDefault().register(this);

        am = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);

//For Normal mode
        am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        Dart.inject(this, activity);
        activity.setSupportActionBar(binding.toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(listener -> onBackPressed());
        activity.setTitle(alarm.getName());
        alarmVM = new AlarmVM(activity, alarm) {
            @Override
            public ItemView itemView() {
                return ItemView.of(BR.trackVM, R.layout.item_alarm_track);

            }
        };
        binding.setAlarmVM(alarmVM);
        if (!alarm.getTracks().isEmpty()) {
            trackVM = new TrackVM(activity, Mock.track(activity));
            binding.setAlarmTrackVM(trackVM);
        }
        am.setStreamVolume(AudioManager.STREAM_MUSIC, alarm.getVolume(), alarm.getVolume());

        startAlarm(alarm);
    }

    @Subscribe
    public void onReceived(TrackChangeEvent trackChangeEvent) {
        trackVM.updateTrack(trackChangeEvent.getTrack());
    }


    /**
     * @param alarm
     */
    private void startAlarm(Alarm alarm) {
        playerMusicManager = Injector.INSTANCE.playerComponent().manager();
        playerMusicManager.startAlarm(alarm);
        rotateCD();
    }

    /**
     *
     */
    private void rotateCD() {
        binding.rlPlayer.animate().rotationBy(rotation).setDuration(50).setListener(new AnimationEndListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                rotateCD();
            }
        });
    }

    /***
     * @param view
     */
    public void stop(View view) {
        playerMusicManager.stop();
        PlayerNotification.cancel();
        activity.finish();
    }


    @Override
    protected boolean onBackPressed() {
        stop(null);
        return super.onBackPressed();

    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void destroy() {
    }
}
