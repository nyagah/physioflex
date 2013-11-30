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

    // Razor Listener
    private RazorListener razorListener;

    // Android context
    Context myContext = null;

    public RazorFileSim(Context c) {
        myContext = c;
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
            AssetManager am = myContext.getAssets();
            try {
                InputStream is = am.open("armCurl.data");
                final Scanner dataScanner = new Scanner(is);
                while (dataScanner.hasNextLine()) {
                    sendToParentThread(MSG_ID__YPR_DATA, null);
                }
            } catch (IOException e) {
                Log.d(TAG, "IOException in Bluetooth thread: " + e.getMessage());e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public void startReading() {
        FileReaderThread frt = new FileReaderThread();
        frt.run();
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
            }
        }
    };
}
