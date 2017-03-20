package com.example.kishore.beacon.webservice;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.estimote.sdk.Region;
import com.estimote.sdk.repackaged.gson_v2_3_1.com.google.gson.Gson;
import com.estimote.sdk.repackaged.gson_v2_3_1.com.google.gson.GsonBuilder;
import com.example.kishore.beacon.bluetooth.BLE;
import com.example.kishore.beacon.bluetooth.Regions;
import com.example.kishore.beacon.constants.Constants;
import com.example.kishore.beacon.networkUtil.Connection;
import com.example.kishore.beacon.util.FileOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Common class to handle webservices and parsing JSON responses
 */
public class WebserviceManager {

    private Context mContext;
    private Executor mExecutor;

    public WebserviceManager(Context context, Executor executor) {
        mContext = context;
        mExecutor = executor;
    }


    /**
     * Encode url - append params
     */
    private String urlEncoding(String url, HashMap<String, String> params) {

        if (url == null && params == null)
            return null;
        if (params.size() > 0) {
            Iterator iterator = params.entrySet().iterator();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(url);
            stringBuilder.append("?");
            while (iterator.hasNext()) {
                HashMap.Entry entry = (HashMap.Entry) iterator.next();
                stringBuilder.append(entry.getKey());
                stringBuilder.append("=");
                stringBuilder.append(entry.getValue());
                iterator.remove();
                if (iterator.hasNext()) {
                    stringBuilder.append("&");
                }

            }
            return stringBuilder.toString();
        }
        return null;
    }

    /**
     * Webservice for region list
     */
    public Regions getBeaconList() {

        try {

            String response = Connection.post(mContext, Constants.SERVICE.GET_BEACON_LIST,null,
                    new Connection.OnConnection() {
                        @Override
                        public void onError(int errorCode, String errorMessage) {
                            if (mExecutor != null)
                                mExecutor.onError(errorCode, errorMessage);
                        }

                        @Override
                        public void onError(String error, String errorDescription) {
                            if (mExecutor != null)
                                mExecutor.onError(error, errorDescription);
                        }
                    });

            Log.d("RESPONSE", response);
            FileOperation fileOperation = new FileOperation(mContext);
            fileOperation.writeToFile(Constants.BLE.JSON_FILE_NAME, response);

            Regions regions = new Gson().fromJson(response, Regions.class);
            return regions;

        } catch (Exception e) {

            return null;
        }
    }

    /**
     * webservice for log region detection in server
     */
    public void logBeaconEvent(Region region, String bleEvent) {

        try {
            BLE ble = new BLE();
            ble.identifier = region.getIdentifier();
            ble.major = region.getMajor().toString();
            ble.uuid = region.getProximityUUID().toString().toUpperCase();
            ble.detectionTime = System.currentTimeMillis();
            ble.detectionType = bleEvent;

            Regions regions = new Regions();
            ArrayList<BLE> regionList = new ArrayList<>();
            regionList.add(ble);
            regions.regionLog = regionList;

            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();

            String params = gson.toJson(regions, Regions.class);

            Connection.post(mContext, Constants.SERVICE.LOG_BEACON, params, new
                    Connection
                            .OnConnection() {
                        @Override
                        public void onError(int errorCode, String errorMessage) {
                            if (mExecutor != null)
                                mExecutor.onError(errorCode, errorMessage);
                        }

                        @Override
                        public void onError(String error, String errorDescription) {
                            if (mExecutor != null)
                                mExecutor.onError(error, errorDescription);
                            if (error.equals(Constants.Response.INVALID_TOKEN) || error.equals
                                    (Constants.Response.UNAUTHORIZED)) {
                            }
                        }
                    });


        } catch (Exception e) {

        }

    }

    /**
     * Interface to handle error
     */
    public interface Executor {
        public void onError(int errorCode, String error);

        public void onError(String error, String errorDescription);
    }

}
