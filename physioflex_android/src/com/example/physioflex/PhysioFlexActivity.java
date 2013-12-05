package com.example.physioflex;

import android.app.Activity;
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

    Button btnReset;
    Chronometer timer;
    TextView resultView;
    TextView pitchVal;

    ImageView feedbackImg;
    Handler handler;


    PhysioFlexTherapist pt;
    ArmCurl armCurl;
    private RazorFileSim rfs;
    boolean finished;

    boolean started;
//    long calibStartTime;
//    boolean calibrating;
//    float calibOffset;
//    int numCalibratedData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

//        Log.d(TAG, "In onCreate()");
//        btAdapter = BluetoothAdapter.getDefaultAdapter();
//        checkBTState();

        btnReset = (Button) findViewById(R.id.btn_reset);
        timer = (Chronometer) findViewById(R.id.timer);
        resultView = (TextView) findViewById(R.id.resultView);
        pitchVal = (TextView) findViewById(R.id.pitch_val);
        feedbackImg = (ImageView) findViewById(R.id.feedBackImg);
        handler = new Handler();
        finished = false;
        started = false;


        // Calibration
//        calibrating = true;
//        calibStartTime = System.currentTimeMillis() % 1000;
//        calibOffset = 0;
//        numCalibratedData = 0;

        pt = new PhysioFlexTherapist();
        armCurl = new ArmCurl(4);
        try {

            rfs = new RazorFileSim(this, new RazorListener() {
                @Override
                public void onConnectAttempt(int attempt, int maxAttempts) {
                }

                @Override
                public void onConnectOk() {
                }

                public void onConnectFail(Exception e) {
                }

                @Override
                public void onAnglesUpdate(float yaw, float pitch, float roll) {


                   pt.getNextYawPitchRoll(yaw, pitch, roll);
                   pitchVal.setText(String.format("Pitch: %f", pitch));

                   PhysioFlexTherapist.TherapistThought observation = pt.considerData(armCurl);

                   switch (observation) {
                       case BAD:
                           feedbackImg.setBackgroundColor(getResources().getColor(R.color.bad_color)); // Color.RED);
                           break;
                       case OK:
                           feedbackImg.setBackgroundColor(getResources().getColor(R.color.good_color));
                           break;
                       case END_OF_REP:
                           resultView.setText(String.format("%d", armCurl.getNumDone())); // String.format("Finished %d reps\n", armCurl.getNumDone()));
                           break;
//                       case END_OF_EXERCISE:
//                           resultView.append(String.format("Done %d reps\n", armCurl.getNumDone()));
//                           feedbackImg.setBackgroundColor(Color.LTGRAY);
//                           this.onFinished();
//                           break;
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
//                    rfs.stopReading();
                }
            });



        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }


        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    rfs.startReading();
//                  Toast.makeText(PhysioFlexActivity.this, "Calibrating: Please hold your arm down", 5).show();
//                  Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                armCurl.reset();
                resultView.setText("0");
                timer.setBase(SystemClock.elapsedRealtime());
                timer.start();
            }
        });


    }
}
