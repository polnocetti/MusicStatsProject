package uy.com.polnocetti.musicstats;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MusicStatsActivity extends Activity {

    private Intent mIntentServicio;
    Switch mSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_stats);

        mIntentServicio = new Intent(this.getApplicationContext(), MusicStatsService.class);

        mSwitch = (Switch) findViewById(R.id.switchServicio);

        mSwitch.setChecked(servicioCorriendo());

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (mSwitch.isChecked()) {
                    iniciarServicio();
                } else {
                    pararServicio();
                }

            }
        });

    }

    private boolean servicioCorriendo() {


        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MusicStatsService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.music_stats, menu);
        return true;
    }

    private void iniciarServicio() {
        MusicStatsService servicio = new MusicStatsService();
        startService(mIntentServicio);
    }

    private void pararServicio() {
        stopService(mIntentServicio);
    }
}
