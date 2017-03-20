package com.example.kishore.beacon;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.estimote.sdk.BeaconManager;
import com.example.kishore.beacon.activity.MainActivity;
import com.example.kishore.beacon.bluetooth.BeaconScanService;

/**
 * Created by Kishore Garapati on 3/12/2017.
 */

public class MyApplication extends Application {

    private BeaconManager beaconManager;

    public BeaconManager getBeaconManager() {
        return beaconManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("APP", "starting application");
        beaconManager = new BeaconManager(getApplicationContext());
        startService(new Intent(this, BeaconScanService.class));
    }

}
