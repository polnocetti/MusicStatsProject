package uy.com.polnocetti.musicstats;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import uy.com.polnocetti.musicstats.database.MusicStatsDatabase;

/**
 * Created by pol on 6/10/13.
 */
public class MusicStatsService extends Service {

    private final IBinder mBinder = new LocalBinder();

    private MusicStatsDatabase mDataAccess;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String cmd = intent.getStringExtra("command");

            String artist = intent.getStringExtra("artist");
            String album = intent.getStringExtra("album");
            String track = intent.getStringExtra("track");

            if (artist != null && album != null && track != null){
                mDataAccess.newSong(artist, album, track);

                Toast.makeText(MusicStatsService.this, artist + ":" + album + ":" + track, Toast.LENGTH_SHORT).show();
            }

        }

    };

    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {

        IntentFilter iF = new IntentFilter();
        iF.addAction("com.android.music.metachanged");
        //iF.addAction("com.android.music.playstatechanged");
        //iF.addAction("com.android.music.playbackcomplete");
        //iF.addAction("com.android.music.queuechanged");

        registerReceiver(mReceiver, iF);

        mDataAccess = new MusicStatsDatabase(getApplicationContext());
        mDataAccess.getWritableDatabase();
        mDataAccess.initializeDatabase();
        mDataAccess.close();

    }

    public class LocalBinder extends Binder {
        MusicStatsService getService() {
            return MusicStatsService.this;
        }
    }

}
