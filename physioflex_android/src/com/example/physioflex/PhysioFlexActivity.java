package com.example.physioflex;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.physioflex.Excercises.ArmCurl;
import com.example.physioflex.Razor.RazorFileSim;
import com.example.physioflex.Razor.RazorListener;

import java.io.IOException;

public class PhysioFlexActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    private static final String TAG = "PHYSIO_FLEX";

//    private static final int REQUEST_ENABLE_BT = 1;
//    private BluetoothAdapter btAdapter = null;
//    private BluetoothSocket btSocket = null;
//    private OutputStream outStream = null;
//    private InputStream inStream = null;

    // Well known SPP UUID
//    private static final UUID MY_UUID =
//            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Insert your server's MAC address
//    private static String address = "20:13:07:18:20:64";

    Button btnStart;
    Chronometer timer;
    TextView dataView;
    TextView resultView;

    TextView yawVal;
    TextView rollVal;
    TextView pitchVal;


    ImageView feedbackImg;
    Handler handler;


    PhysioFlexTherapist pt;
    ArmCurl armCurl;
    private RazorFileSim rfs;
    boolean finished;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Log.d(TAG, "In onCreate()");
//        btAdapter = BluetoothAdapter.getDefaultAdapter();
//        checkBTState();

        btnStart = (Button) findViewById(R.id.btn_start);
        timer = (Chronometer) findViewById(R.id.timer);
        resultView = (TextView) findViewById(R.id.resultView);
        yawVal = (TextView) findViewById(R.id.yaw_val);
        pitchVal = (TextView) findViewById(R.id.pitch_val);
        rollVal = (TextView) findViewById(R.id.roll_val);
        feedbackImg = (ImageView) findViewById(R.id.feedBackImg);
        handler = new Handler();
        finished = false;

        pt = new PhysioFlexTherapist();
        armCurl = new ArmCurl(4);

        try {
            rfs = new RazorFileSim(this, new RazorListener() {
                @Override
                public void onConnectAttempt(int attempt, int maxAttempts) {
    //                Toast.makeText(RazorExample.this, "Connect attempt " + attempt + " of " + maxAttempts + "...", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onConnectOk() {
    //                Toast.makeText(RazorExample.this, "Connected!", Toast.LENGTH_LONG).show();
    //                setButtonStateConnected();
                }

                public void onConnectFail(Exception e) {
    //                setButtonStateDisconnected();
    //                Toast.makeText(RazorExample.this, "Connecting failed: " + e.getMessage() + ".", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onAnglesUpdate(float yaw, float pitch, float roll) {
                   pt.getNextYawPitchRoll(yaw, pitch, roll);
                   rollVal.setText(String.format("Roll: %f", roll));
                   yawVal.setText(String.format("Yaw: %f", yaw));
                   pitchVal.setText(String.format("Pitch: %f", pitch));

                   PhysioFlexTherapist.TherapistThought observation = pt.considerData(armCurl);

                   switch (observation) {
                       case BAD:
                           feedbackImg.setBackgroundColor(Color.RED);
                           break;
                       case OK:
                           feedbackImg.setBackgroundColor(Color.GREEN);
                           break;
                       case END_OF_REP:
                           resultView.append(String.format("Finished %d reps\n", armCurl.getNumDone()));
                           break;
                       case END_OF_EXERCISE:
                           resultView.append(String.format("Done %d reps\n", armCurl.getNumDone()));
                           feedbackImg.setBackgroundColor(Color.LTGRAY);
                           this.onFinished();
                           break;
                   }
                }

                @Override
                public void onSensorsUpdate(float accX, float accY, float accZ,
                                            float magX, float magY, float magZ,
                                            float gyrX, float gyrY, float gyrZ) {}

                @Override
                public void onIOExceptionAndDisconnect(IOException e) {}

                @Override
                public void onFinished() {
                    timer.stop();
                    rfs.stopReading();
                }
            });
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              timer.setBase(SystemClock.elapsedRealtime());
              timer.start();
                try {
                    rfs.startReading();
                } catch (IOException e) {
                    Log.e(TAG,e.getMessage());
                }
            }
        });


    }

//    @Override
//    public void onResume() {
//        super.onResume();
//
//        Log.d(TAG, "...In onResume - Attempting client connect...");
//
//        // Set up a pointer to the remote node using it's address.
//        BluetoothDevice device = btAdapter.getRemoteDevice(address);
//
//        // Two things are needed to make a connection:
//        //   A MAC address, which we got above.
//        //   A Service ID or UUID.  In this case we are using the
//        //     UUID for SPP.
//        try {
//            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
//        } catch (IOException e) {
//            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
//        }
//
//        // Discovery is resource intensive.  Make sure it isn't going on
//        // when you attempt to connect and pass your message.
//        btAdapter.cancelDiscovery();
//
//        // Establish the connection.  This will block until it connects.
//        Log.d(TAG, "...Connecting to Remote...");
//        try {
//            btSocket.connect();
//            Log.d(TAG, "...Connection established and data link opened...");
//        } catch (IOException e) {
//            try {
//                btSocket.close();
//            } catch (IOException e2) {
//                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
//            }
//        }
//
//        // Create a data stream so we can talk to server.
//        Log.d(TAG, "...Creating Socket...");
//
//        try {
//            outStream = btSocket.getOutputStream();
//            inStream = btSocket.getInputStream();
//        } catch (IOException e) {
//            errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
//        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//
//        Log.d(TAG, "...In onPause()...");
//
//        if (outStream != null) {
//            try {
//                outStream.flush();
//            } catch (IOException e) {
//                errorExit("Fatal Error", "In onPause() and failed to flush output stream: " + e.getMessage() + ".");
//            }
//        }
//
//        try     {
//            btSocket.close();
//        } catch (IOException e2) {
//            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
//        }
//    }
//
//    private void checkBTState() {
//        // Check for Bluetooth support and then check to make sure it is turned on
//
//        // Emulator doesn't support Bluetooth and will return null
//        if(btAdapter==null) {
//            errorExit("Fatal Error", "Bluetooth Not supported. Aborting.");
//        } else {
//            if (btAdapter.isEnabled()) {
//                Log.d(TAG, "...Bluetooth is enabled...");
//            } else {
//                //Prompt user to turn on Bluetooth
//                Intent enableBtIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//            }
//        }
//    }
//
//    private void errorExit(String title, String message){
//        Toast msg = Toast.makeText(getBaseContext(),
//                title + " - " + message, Toast.LENGTH_SHORT);
//        msg.show();
//        finish();
//    }
//
//    private void sendData(String message) {
//        byte[] msgBuffer = message.getBytes();
//
//        Log.d(TAG, "...Sending data: " + message + "...");
//
//        try {
//            outStream.write(msgBuffer);
//        } catch (IOException e) {
//            String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
//            if (address.equals("00:00:00:00:00:00"))
//                msg = msg + ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 37 in the java code";
//            msg = msg +  ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";
//
//            errorExit("Fatal Error", msg);
//        }
//    }
//
//    private void readData(String message) {
//        byte[] msgBuffer = message.getBytes();
//
//        Log.d(TAG, "...Sending data: " + message + "...");
//
//        try {
//            int read = inStream.read(msgBuffer);
//        } catch (IOException e) {
//            String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
//            if (address.equals("00:00:00:00:00:00"))
//                msg = msg + ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 37 in the java code";
//            msg = msg +  ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";
//
//            errorExit("Fatal Error", msg);
//        }
//    }
}
