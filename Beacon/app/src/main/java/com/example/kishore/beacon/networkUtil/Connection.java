package com.example.kishore.beacon.networkUtil;


import android.content.Context;
import android.util.Log;

import com.estimote.sdk.repackaged.gson_v2_3_1.com.google.gson.Gson;
import com.example.kishore.beacon.constants.Constants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

/**
 * Common class for Post and Get requests
 */
public class Connection {

    private static final String POST = "POST";
    private static final String GET = "GET";
    private static final String TAG = "Connection";
    private static OnConnection mOnConnection;


    /***
     * Get method
     */
    public static String get(Context context, String urlString, OnConnection onConnection) throws
            IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException,
            KeyManagementException {
        return get(context, urlString, onConnection, null);
    }

    public static String get(Context context, final String urlString, OnConnection onConnection,
                             HashMap<String,
                                     String> requestHeader) throws
            IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException,
            KeyManagementException {

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());

        Log.d(TAG, "G ---> " + c.getTime());

        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpsURLConnection) url.openConnection();

        connection.setRequestMethod(GET);
        connection.setRequestProperty("Content-Type", "application/json");

        if (requestHeader != null) {
            Iterator iterator = requestHeader.entrySet().iterator();
            while (iterator.hasNext()) {
                HashMap.Entry entry = (HashMap.Entry) iterator.next();
                connection.setRequestProperty(entry.getKey() + "", entry.getValue()
                        + "");
                Log.d(TAG, urlString + " header: " + entry.getKey() + ":" + entry.getValue());
                iterator.remove();
            }
        }
        mOnConnection = onConnection;

        int responseCode = connection.getResponseCode();

        Log.d(TAG,Integer.toString(responseCode));

        if (responseCode == Constants.Response.S200) {


            InputStream is = connection.getInputStream();
            StringBuilder sb = new StringBuilder();

            if (is != null) {
                String line;
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is));
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
            }
            is.close();

            Log.d(TAG, sb.toString() + " --> " + System.currentTimeMillis());

            return sb.toString();
        } else {

            InputStream es = connection.getErrorStream();
            StringBuilder sb = new StringBuilder();

            if (es != null) {
                String line;
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(es));
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
            }
            es.close();
            Log.d(TAG, sb.toString());

            if (responseCode == Constants.Response.E400 || responseCode == Constants.Response
                    .E401) {

                Gson gson = new Gson();
            } else {
                mOnConnection.onError(responseCode, connection.getResponseMessage());
            }

            return responseCode + "";
        }

    }


    /***
     * Post method
     */
    public static String post(Context context, final String urlString, String params,
                              OnConnection onConnection) throws
            IOException, CertificateException, NoSuchAlgorithmException, KeyStoreException,
            KeyManagementException {

        mOnConnection = onConnection;
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());

        Log.d(TAG, "P ---> " + c.getTime());

        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpsURLConnection) url.openConnection();

        connection.setRequestMethod(POST);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");

        connection.connect();

        /* json data */
        if (params != null) {
            Log.d(TAG, urlString + " params: " + params);
            Writer writer = new BufferedWriter(new OutputStreamWriter(connection
                    .getOutputStream(), "UTF-8"));
            writer.write(params);
            writer.close();
        }


        int responseCode = connection.getResponseCode();

        Log.d(TAG,Integer.toString(responseCode));

        if (responseCode == Constants.Response.S200) {


            InputStream is = connection.getInputStream();
            StringBuilder sb = new StringBuilder();

            if (is != null) {
                String line;
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is));
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
            }
            is.close();

            Log.d(TAG, sb.toString() + " --> " + System.currentTimeMillis());

            return sb.toString();
        } else {

            InputStream es = connection.getErrorStream();
            StringBuilder sb = new StringBuilder();

            if (es != null) {
                String line;
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(es));
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
            }
            es.close();
            Log.d(TAG, sb.toString());

            if (responseCode == Constants.Response.E400 || responseCode == Constants.Response
                    .E401) {
                Gson gson = new Gson();
            } else {
                mOnConnection.onError(responseCode, connection.getResponseMessage());
            }

            return responseCode + "";
        }

    }

    public interface OnConnection {

        void onError(int errorCode, String errorMessage);

        void onError(String error, String errorDescription);
    }


}
