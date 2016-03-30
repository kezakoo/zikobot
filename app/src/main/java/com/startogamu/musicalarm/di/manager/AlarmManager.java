package com.startogamu.musicalarm.di.manager;

import com.startogamu.musicalarm.model.Alarm;
import com.startogamu.musicalarm.model.AlarmTrack;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.realm.Realm;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by josh on 28/03/16.
 */
@Singleton
public class AlarmManager {

    private Realm realm;

    @Inject
    public AlarmManager(Realm realm) {
        this.realm = realm;
    }

    public void loadAlarms(Subscriber<List<Alarm>> subscriber) {
        realm.where(Alarm.class).findAllAsync().asObservable()
                .observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
    }


    public int getNextKey() {


        try {
            return realm.where(Alarm.class).max("id").intValue() + 1;
        } catch (NullPointerException ne) {
            return 0;
        }
    }

    /***
     * @param alarm
     * @param alarmTrackList
     */
    public void saveAlarm(Alarm alarm, List<AlarmTrack> alarmTrackList) {
        realm.beginTransaction();
        if (alarm.getId() == -1)
            alarm.setId(getNextKey());
        if (alarmTrackList != null) {
            for (AlarmTrack alarmTrack : alarmTrackList) {
                alarmTrack = realm.copyToRealm(alarmTrack);
                alarm.getAlarmTracks().add(alarmTrack);
            }
        }
        realm.copyToRealmOrUpdate(alarm);
        realm.commitTransaction();

    }


}
