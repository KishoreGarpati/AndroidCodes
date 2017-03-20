package com.example.kishore.beacon.bluetooth;
import android.content.Context;
import android.util.Log;

import com.estimote.sdk.Region;
import com.estimote.sdk.repackaged.gson_v2_3_1.com.google.gson.Gson;
import com.estimote.sdk.repackaged.gson_v2_3_1.com.google.gson.JsonParseException;
import com.estimote.sdk.repackaged.gson_v2_3_1.com.google.gson.annotations.Expose;
import com.estimote.sdk.repackaged.gson_v2_3_1.com.google.gson.annotations.SerializedName;
import com.example.kishore.beacon.constants.Constants;
import com.example.kishore.beacon.webservice.WebserviceManager;

import java.util.ArrayList;
import java.util.UUID;


/**
 * Bean class for beacon
 */
public class BLE {

    @Expose
    @SerializedName("uuid")
    public String uuid;
    @Expose
    @SerializedName("major")
    public String major;
    @Expose
    @SerializedName("minor")
    public String minor;
    @Expose
    @SerializedName("identifier")
    public String identifier;
    @Expose
    @SerializedName("detectionTime")
    public long detectionTime;
    public long outTime;
    @Expose
    @SerializedName("detectionType")
    public String detectionType;

    /**
     * Create region with available parameters
     */
    public static Region createRegion(String uuid, String major, String minor, String identifier)
            throws NullPointerException {
        int majorValue, minorValue;

        if (uuid == null)
            throw new NullPointerException();

        UUID mUUID = UUID.fromString(uuid);

        if (major == null && minor == null) {
            Region region = new Region(identifier, mUUID, null, null);
            return region;
        } else if (minor == null) {
            majorValue = Integer.parseInt(major);
            Region region = new Region(identifier, mUUID, majorValue, null);
            return region;
        } else {
            majorValue = Integer.parseInt(major);
            minorValue = Integer.parseInt(minor);
            Region region = new Region(identifier, mUUID, majorValue, minorValue);
            return region;
        }

    }

    /**
     * Parse JSON string and return region list
     */
    public static ArrayList<Region> getRegions(String jsonString) throws JsonParseException {
        Gson gson = new Gson();
        Log.d("GET_REGIONS",jsonString);
        Regions regions = gson.fromJson(jsonString, Regions.class);
        Log.d("REGIONS", regions.toString());
        ArrayList<Region> regionList = new ArrayList<Region>();
        for (BLE obj : regions.regionList) {
            Region region = BLE.createRegion(obj.uuid, obj.major, obj.minor, obj.identifier);
            regionList.add(region);
        }
        return regionList;
    }

    public static void saveRegionInServer(final Context context, Region region, String event) {

        final WebserviceManager webserviceManager = new WebserviceManager(context, new
                WebserviceManager.Executor() {

                    @Override
                    public void onError(int errorCode, String error) {
                        Log.d("BLE", "Eri " + errorCode);
                    }

                    @Override
                    public void onError(String error, String errorDescription) {
                        Log.d("BLE", "Ers " + error);

                    }
                });
        webserviceManager.logBeaconEvent(region, event);
        Log.d("BLE", event + " " + region.getIdentifier());
    }




    public interface EVENT {
        public String ENTRY = "entry";
        public String EXIT = "exit";
    }
}
