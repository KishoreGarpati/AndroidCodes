package com.example.kishore.beacon.constants;

/**
 * Created by Kishore Garapati on 3/19/2017.
 */

public class Constants {

    public interface BLE{
        String JSON_FILE_NAME = "beacons_list.json";
    }

    public interface ACTION {
        String REGIONS_MISSING = "com.example.kishore.beacon.foregroundservice.noregions";
    }

    public interface SERVICE {
        String CONNECTION = "https://";
        String HOST_URL = "apsrtc.herokuapp.com";
        String MAIN_URL = CONNECTION + HOST_URL;
        String LOG_BEACON = MAIN_URL + "/logBeaconEvent";
        String CHECK_FOR_REGION_UPDATE = MAIN_URL + "/checkForBeaconsUpdate";
        String GET_BEACON_LIST = MAIN_URL + "/getBeaconsList";
    }

    public interface Response {
        int E404 = 404;
        int E401 = 401;
        int E400 = 400;
        int E500 = 500;
        int S200 = 200;
        String INVALID_TOKEN = "invalid_token";
        String UNAUTHORIZED = "unauthorized";
    }

}
