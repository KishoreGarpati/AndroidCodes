package com.example.kishore.beacon.bluetooth;

import com.estimote.sdk.repackaged.gson_v2_3_1.com.google.gson.annotations.Expose;
import com.estimote.sdk.repackaged.gson_v2_3_1.com.google.gson.annotations.SerializedName;
import com.example.kishore.beacon.bluetooth.BLE;

import java.util.ArrayList;

/**
 * Bean class for region list
 */
public class Regions {
    @Expose
    @SerializedName("data")
    public ArrayList<BLE> regionList;
    @Expose
    @SerializedName("beaconLog")
    public ArrayList<BLE> regionLog;

}
