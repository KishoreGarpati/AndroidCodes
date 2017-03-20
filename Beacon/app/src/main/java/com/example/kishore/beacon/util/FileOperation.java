package com.example.kishore.beacon.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * File operation class
 */
public class FileOperation {

    private static int BUFFER = 1000;
    private final String TAG = "FileOperation";
    private Context context;

    public FileOperation(Context context) {
        this.context = context;
    }

    /***
     * Zip file
     */
    public static void zip(String[] _files, String zipFileName) {
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte data[] = new byte[BUFFER];

            for (int i = 0; i < _files.length; i++) {

                Log.d("Compress", "Adding: " + _files[i]);

                FileInputStream fi = new FileInputStream(_files[i]);
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
        } catch (Exception e) {

        }
    }

    public void writeToFile(String filename, String data) throws FileNotFoundException,
            IOException {
        FileOutputStream outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
        outputStream.write(data.getBytes());
        Log.d(TAG, "data written to file...");
        outputStream.close();
    }

    public String readFromFile(String filename) throws FileNotFoundException, IOException {
        int c;
        String temp = "";
        FileInputStream inputStream = context.openFileInput(filename);

        while ((c = inputStream.read()) != -1) {
            temp = temp + Character.toString((char) c);
        }
        Log.d(TAG, "reading data from file...");
        inputStream.close();
        return temp;
    }

}
