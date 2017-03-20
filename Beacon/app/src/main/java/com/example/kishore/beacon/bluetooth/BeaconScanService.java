package com.example.kishore.beacon.bluetooth;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.repackaged.gson_v2_3_1.com.google.gson.JsonSyntaxException;
import com.example.kishore.beacon.MyApplication;
import com.example.kishore.beacon.activity.MainActivity;
import com.example.kishore.beacon.constants.Constants;
import com.example.kishore.beacon.util.FileOperation;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Scan service - background
 */
public class BeaconScanService extends Service {


    final private static String TAG = "BeaconScanService";
    private final AtomicInteger c = new AtomicInteger(0);
    private BeaconManager mBeaconManager;
    private PendingIntent pendingIntent;
    private ArrayList<Region> bleRegionList;
    private NotificationManager mNotificationManager;
    private Notification.Builder mNotifyBuilder;

    /**
     * Connect all beacons
     */
    private static void connectBeacon(final BeaconManager mBeaconManager, final ArrayList<Region>
            regionList) {
        try {
            mBeaconManager.connect(new BeaconManager.ServiceReadyCallback() {

                @Override
                public void onServiceReady() {
                    for (Region region : regionList) {
                        mBeaconManager.startMonitoring(region);
                    }

                }
            });
        } catch (Exception e) {

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "Beacon service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "Beacon service started");
        try {
            FileOperation fileOperation = new FileOperation(this);

            String jsonString = fileOperation.readFromFile(Constants.BLE.JSON_FILE_NAME);
            if (jsonString == null) {
                Log.d(TAG,"regions missing");
                sendRegionMissingBroadcast();
                return START_NOT_STICKY;
            }
            Log.d("JSON_STRING",jsonString);
            bleRegionList = BLE.getRegions(jsonString);

            Log.d("SIZE",Integer.toString(bleRegionList.size()));
            /*bleRegionList = new ArrayList<Region>();
            bleRegionList.add(new Region(
                    "monitored region",
                    UUID.fromString("74278bda-b644-4520-8f0c-720eaf059935"),
                    65500, 65505));
*/
            startMonitoring(bleRegionList);
        }
        catch (FileNotFoundException e){
            Log.d("Exception", "FileNotFoundException");
            sendRegionMissingBroadcast();
            return START_NOT_STICKY;
        }
        catch (JsonSyntaxException e){
            Log.d("Exception", "JsonSyntaxException");
            sendRegionMissingBroadcast();
            return START_NOT_STICKY;
        }
        catch (Exception e){
            e.printStackTrace();
            return START_NOT_STICKY;
        }
        Log.d("SERVICE","Recreating service");
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "Beacon service killed");
    }

    /**
     * Listener for beacon
     **/
    private void startMonitoring(final ArrayList<Region> regionList) {

        mBeaconManager = ((MyApplication) getApplication()).getBeaconManager();

        connectBeacon(mBeaconManager, regionList);

        Log.d(TAG, "Starting Monitoring..");

        mBeaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(final Region region, List<Beacon> list) {

                String title = "Entering " + region.getIdentifier();
                String message = region.getMajor() + " ";

                Log.d(TAG, "Entry- " + region.getIdentifier());

                LogRegionDetection logRegionDetection = new LogRegionDetection(region, BLE.EVENT
                        .ENTRY);
                logRegionDetection.execute();

                showNotification(title, message);
            }

            @Override
            public void onExitedRegion(Region region) {

                String title = "Leaving " + region.getIdentifier();
                String message = region.getMajor() + " ";

                Log.d(TAG, "Exit- " + region.getIdentifier());

                LogRegionDetection logRegionDetection = new LogRegionDetection(region, BLE.EVENT.EXIT);
                Log.d(TAG, "En" + region.getIdentifier());
                logRegionDetection.execute();
                showNotification(title, message);
            }
        });


    }

    public void showNotification(String title, String message) {

        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[] { notifyIntent }, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
//        long[] vibrate = { 0, 100, 200, 300 };
//        notification.vibrate = vibrate;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    private void sendRegionMissingBroadcast() {

        Intent intent = new Intent(Constants.ACTION.REGIONS_MISSING);
        sendBroadcast(intent);
        Log.d("Broadcaster ", "Region file missing...");
    }

    private class LogRegionDetection extends AsyncTask<Void, Void, Void> {

        private Region region;
        private String bEvent;

        public LogRegionDetection(Region region, String event) {
            this.region = region;
            bEvent = event;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            BLE.saveRegionInServer(BeaconScanService.this, region, bEvent);
            return null;
        }

    }


}
