package com.startogamu.zikobot.viewmodel.activity;

import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;

import com.joxad.easydatabinding.activity.ActivityBaseVM;
import com.joxad.easydatabinding.activity.INewIntent;
import com.joxad.easydatabinding.activity.IPermission;
import com.joxad.easydatabinding.activity.IResult;
import com.orhanobut.logger.Logger;
import com.startogamu.zikobot.R;
import com.startogamu.zikobot.core.event.EventFabClicked;
import com.startogamu.zikobot.core.event.EventShowMessage;
import com.startogamu.zikobot.core.event.dialog.EventShowDialogAlarm;
import com.startogamu.zikobot.core.event.player.EventPlayListClicked;
import com.startogamu.zikobot.core.fragmentmanager.DrawerManager;
import com.startogamu.zikobot.core.fragmentmanager.NavigationManager;
import com.startogamu.zikobot.core.utils.AppPrefs;
import com.startogamu.zikobot.databinding.ActivityMainBinding;
import com.startogamu.zikobot.module.component.Injector;
import com.startogamu.zikobot.view.Henson;
import com.startogamu.zikobot.view.activity.ActivityMain;
import com.startogamu.zikobot.view.fragment.alarm.DialogFragmentAlarms;
import com.startogamu.zikobot.viewmodel.custom.PlayerVM;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.UnsupportedEncodingException;

/**
 * Created by josh on 08/06/16.
 */
public class ActivityMainVM extends ActivityBaseVM<ActivityMain, ActivityMainBinding> implements IResult, IPermission, INewIntent {
    public ActionBarDrawerToggle actionBarDrawerToggle;

    public NavigationManager navigationManager;
    public DrawerManager drawerManager;
    public PlayerVM playerVM;
    public ObservableBoolean fabVisible, tabLayoutVisible;
    private AlertDialog alertDialog;

    /***
     * @param activity
     * @param binding
     */
    public ActivityMainVM(ActivityMain activity, ActivityMainBinding binding) {
        super(activity, binding);
        Injector.INSTANCE.spotifyAuth().inject(this);
        Injector.INSTANCE.playerComponent().inject(this);
    }

    @Override
    public void init() {
        if (AppPrefs.isFirstStart()) {
            activity.startActivity(Henson.with(activity).gotoActivityFirstStart().build());
        }
        fabVisible = new ObservableBoolean(false);
        tabLayoutVisible = new ObservableBoolean(true);
        initSpotify();
        initNavigationManager();
        initToolbar();
        initDrawer();
        initPlayerVM();
    }

    private void initSpotify() {
        if (AppPrefs.spotifyUser()==null)
            return;
        try {
            Injector.INSTANCE.spotifyAuth().manager().refreshToken(activity, () -> {
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void initNavigationManager() {
        navigationManager = new NavigationManager(this, activity, binding, activity.getSupportFragmentManager());
        navigationManager.init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            navigationManager.subscribe();
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
        drawerManager.onResume();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        navigationManager.unsubscribe();
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    private void initToolbar() {
        activity.setSupportActionBar(binding.toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeButtonEnabled(true);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(true);
    }


    /***
     * Init the menu drawer layout that will contains the other way to play music
     */
    private void initDrawer() {
        drawerManager = new DrawerManager(activity, this, binding);
        drawerManager.init();
    }


    @Subscribe
    public void onEvent(EventShowMessage event) {
        if (alertDialog != null && alertDialog.isShowing())
            return;
        alertDialog = new AlertDialog.Builder(activity)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(event.getTitle())
                .setMessage(event.getString())
                .create();
        alertDialog.show();
    }


    @Subscribe
    public void onEvent(EventShowDialogAlarm event) {
        DialogFragmentAlarms dialogFragmentAlarms = DialogFragmentAlarms.newInstance(event.getModel());
        dialogFragmentAlarms.show(activity.getSupportFragmentManager(), DialogFragmentAlarms.TAG);

    }
    /***
     *
     */
    private void initPlayerVM() {
        playerVM = new PlayerVM(activity,binding.viewPlayer);
    }

    @Override
    public void destroy() {
        EventBus.getDefault().unregister(this);
    }

    /***
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    /***
     * @param intent
     */
    @Override
    public void onNewIntent(Intent intent) {
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        navigationManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void onPostCreate() {
        actionBarDrawerToggle.syncState();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return false;
    }

    @Override
    protected boolean onBackPressed() {

        if (drawerManager.isDrawerOpen()) {
            drawerManager.closeDrawer();
            return false;
        }
        return super.onBackPressed();
    }


    /***
     * Called when the play button is called in above a list of tracks
     *
     * @param view
     */
    public void onPlayListClicked(View view) {
        EventBus.getDefault().post(new EventPlayListClicked());
    }

    /***
     * @param view
     */
    public void fabClicked(View view) {
        EventBus.getDefault().post(new EventFabClicked());
    }
}
