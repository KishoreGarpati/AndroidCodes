package com.example.kishore.beacon.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.estimote.sdk.SystemRequirementsChecker;
import com.example.kishore.beacon.R;
import com.example.kishore.beacon.bluetooth.BeaconScanService;
import com.example.kishore.beacon.bluetooth.Regions;
import com.example.kishore.beacon.constants.Constants;
import com.example.kishore.beacon.webservice.WebserviceManager;

public class MainActivity extends AppCompatActivity {

    private FetchBLEDetails fetchBLEDetails;


    private BroadcastReceiver noRegionsFoundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("BroadcastReceiver","region missing broadcast received");
            if (fetchBLEDetails != null)
                fetchBLEDetails = new FetchBLEDetails();
            if(fetchBLEDetails.getStatus() != AsyncTask.Status.PENDING || fetchBLEDetails
                    .getStatus() != AsyncTask.Status.RUNNING)
                fetchBLEDetails.execute();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fetchBLEDetails = new FetchBLEDetails();
        fetchBLEDetails.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        registerReceiver(noRegionsFoundReceiver, new IntentFilter(Constants.ACTION
                .REGIONS_MISSING));
        Log.d("MAIN", "registered broadcast receiver");
    }

    @Override
    protected void onPause() {
        unregisterReceiver(noRegionsFoundReceiver);
        super.onPause();
    }

    private class FetchBLEDetails extends AsyncTask<Void, Integer, Regions> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Regions doInBackground(Void... voids) {
            WebserviceManager webserviceManager = new WebserviceManager(MainActivity.this,
                    null);
            Log.d("AsyncTask", "Executing async task");
            return webserviceManager.getBeaconList();
        }


        @Override
        protected void onPostExecute(Regions regions) {
            super.onPostExecute(regions);

            if (regions != null) {
                stopService(new Intent(MainActivity.this.getApplicationContext(),
                        BeaconScanService.class));
                startService(new Intent(MainActivity.this.getApplicationContext(),
                        BeaconScanService.class));
            }
        }
    }
}
