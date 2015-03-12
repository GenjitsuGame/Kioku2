package com.fragile.kioku2.scrobbling;

import android.app.Service;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.fragile.kioku2.KiokuApplication;
import com.fragile.kioku2.LastFmOperations;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import fm.last.api.LastFmServer;

public class ScrobblerService extends Service {

    public static final String SERVICECMD = "com.android.music.musicservicecommand";

    private ScheduledExecutorService ses;

    private Context context = this;

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter iF = new IntentFilter();
        iF.addAction("com.android.music.metachanged");

        iF.addAction("com.htc.music.metachanged");

        iF.addAction("fm.last.android.metachanged");
        iF.addAction("com.sec.android.app.music.metachanged");
        iF.addAction("com.nullsoft.winamp.metachanged");
        iF.addAction("com.amazon.mp3.metachanged");
        iF.addAction("com.miui.player.metachanged");
        iF.addAction("com.real.IMP.metachanged");
        iF.addAction("com.sonyericsson.music.metachanged");
        iF.addAction("com.rdio.android.metachanged");
        iF.addAction("com.samsung.sec.android.MusicPlayer.metachanged");
        iF.addAction("com.andrew.apollo.metachanged");
        iF.addAction("com.android.music.playstatechanged");

        iF.addAction("com.htc.music.playstatechanged");

        iF.addAction("fm.last.android.playstatechanged");
        iF.addAction("com.sec.android.app.music.playstatechanged");
        iF.addAction("com.nullsoft.winamp.playstatechanged");
        iF.addAction("com.amazon.mp3.playstatechanged");
        iF.addAction("com.miui.player.playstatechanged");
        iF.addAction("com.real.IMP.playstatechanged");
        iF.addAction("com.sonyericsson.music.playstatechanged");
        iF.addAction("com.rdio.android.playstatechanged");
        iF.addAction("com.samsung.sec.android.MusicPlayer.playstatechanged");
        iF.addAction("com.andrew.apollo.playstatechanged");
        registerReceiver(mReceiver, iF);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        private Bundle savedTrack;
        private boolean playStateChanged = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (KiokuApplication.isSessionSet()) {
                if (savedTrack != null) {
                    if (haveNetworkConnection()) {
                        new LastFmOperations.Scrobble().execute(savedTrack);
                    } else {
                        ScrobbleData.saveObject(context, savedTrack);
                        attemptScrobblingSavedTracksLater();
                    }
                }

                String action = intent.getAction();
                if (action.contains("playstatechanged")) {
                    if (playStateChanged) {
                        playStateChanged = false;
                        return;
                    } else {
                        playStateChanged = true;
                    }
                }
                Log.v("tag ", action);
                Bundle scrobbleBundle = new Bundle();
                scrobbleBundle.putString("ARTIST", intent.getStringExtra("artist"));
                scrobbleBundle.putString("ALBUM", intent.getStringExtra("album"));
                scrobbleBundle.putString("TRACK", intent.getStringExtra("track"));
                scrobbleBundle.putLong("TIMESTAMP", System.currentTimeMillis() / 1000);
                savedTrack = scrobbleBundle;
                if (haveNetworkConnection()) {
                    new LastFmOperations.UpdateNowPlaying().execute(scrobbleBundle);

                    //attemptScrobblingSavedTracksLater();
                } else {
                    ScrobbleData.saveObject(context, scrobbleBundle);

                    attemptScrobblingSavedTracksLater();
                }
            }
        }
    };

    private void attemptScrobblingSavedTracksLater() {
        if (ses == null) {
        ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Bundle track;
                while (haveNetworkConnection() && (track = ScrobbleData.getObject(context)) != null) {
                    new LastFmOperations.Scrobble().execute(track);
                }
                ses = null;
            }
        }, 10, 10, TimeUnit.SECONDS);
        }
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

}