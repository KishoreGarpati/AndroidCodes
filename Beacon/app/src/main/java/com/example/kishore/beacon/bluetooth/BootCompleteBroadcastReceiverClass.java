package com.example.kishore.beacon.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Kishore Garapati on 3/19/2017.
 */

public class BootCompleteBroadcastReceiverClass extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent scanService = new Intent(context, BeaconScanService.class);
        context.startService(scanService);
    }
}
