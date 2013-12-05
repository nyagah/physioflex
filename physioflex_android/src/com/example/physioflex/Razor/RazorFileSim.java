package com.example.physioflex.Razor;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class RazorFileSim {
    private static String TAG = "RAZOR_FILE_SIM";

    // IDs passed to internal message handler
    private static final int MSG_ID__YPR_DATA = 0;
    private static final int MSG_ID__END = 1;

    // Razor Listener
    private RazorListener razorListener;

    // Android context
    Context myContext = null;

    FileReaderThread frt;
    Scanner dataScanner;
    InputStream inStream;

    boolean startedRead = false;
    boolean shouldRead = true;

    public RazorFileSim(Context c, RazorListener razorListener) throws IOException {
        myContext = c;
        frt = new FileReaderThread();
        this.razorListener = razorListener;
    }


    // Object pools
    private ObjectPool<float[]> float3Pool = new ObjectPool<float[]>(new ObjectPool.ObjectFactory<float[]>() {
        @Override
        public float[] newObject() {
            return new float[3];
        }
    });

    private void sendToParentThread(int msgId, Object o) {
        parentThreadHandler.obtainMessage(msgId, o).sendToTarget();
    }


    private class FileReaderThread extends Thread {
        @Override
        public void run () {

            try {
                while(shouldRead && dataScanner.hasNextLine()) {

                        float yaw = dataScanner.nextFloat();
                        float pitch = dataScanner.nextFloat();
                        float roll = dataScanner.nextFloat();

                        float[] ypr = float3Pool.get();
                        ypr[0] = yaw;
                        ypr[1] = pitch;
                        ypr[2] = roll;

                        sendToParentThread(MSG_ID__YPR_DATA, ypr);
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            Log.e(TAG, e.getMessage());
                        }

                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    public void initDataScanner() throws IOException {

        if(startedRead) {
            dataScanner.close();
        }

        AssetManager am = myContext.getAssets();
        inStream = am.open("armCurl.data");
        dataScanner = new Scanner(inStream);
    }
    public void startReading() throws IOException {
        initDataScanner();
        if (!startedRead) {
            startedRead = true;
            frt.start();
        }
    }

    /**
     * Handler that forwards messages to the RazorListener callbacks. This handler runs in the
     * thread this RazorAHRS object was created in and receives data from the Bluetooth I/O thread.
     */
    private Handler parentThreadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ID__YPR_DATA:	// Yaw, pitch and roll data
                    float[] ypr = (float[]) msg.obj;
                    razorListener.onAnglesUpdate(ypr[0], ypr[1], ypr[2]);
                    float3Pool.put(ypr);
                    break;
                case MSG_ID__END:
                    razorListener.onFinished();
                    break;
            }
        }
    };
}
