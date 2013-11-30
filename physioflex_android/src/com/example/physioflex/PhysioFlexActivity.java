package com.example.physioflex;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import com.example.physioflex.Excercises.ArmCurl;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class PhysioFlexActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    Button btnStart;
    Button btnEnd;
    Chronometer timer;
    TextView dataView;
    TextView resultView;

    Timer tickTimer;
    TimerTask printTask;
    TimerTask stopTask;

    Handler handler;

    PhysioFlexTherapist pt;
    ArmCurl armCurl;
    boolean finished;

    public void getStopTask() {
        stopTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (finished){
                                printTask.cancel();
                                timer.stop();
                            }
                        } catch (Exception e) {
                            dataView.setText(e.getMessage());
                        }
                    }
                });
            }
        };
    }

    public void getPrintTask() {
        printTask =  new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {

                            float minX = -90.0f;
                            float maxX = 90.0f;
                            Random rand = new Random();
                            float finalX = rand.nextFloat() * (maxX - minX) + minX;

                            String currentDataPoint = Float.toString(finalX);
                            dataView.append(currentDataPoint + "\n");

                            pt.readNext(currentDataPoint);
                            PhysioFlexTherapist.TherapistThought observation = pt.considerData(armCurl);

                            switch (observation) {
                                case BAD:
                                    resultView.append("You're doing it wrong!\n");
                                    break;
                                case OK:
                                    break;
                                case END_OF_REP:
                                    resultView.append(String.format("Finished %d reps\n", armCurl.getNumDone()));
                                    break;
                                case END_OF_EXERCISE:
                                    finished = true;
                                    resultView.append(String.format("Done %d reps\n", armCurl.getNumDone()));
                                    break;
                            }
                        } catch (Exception e) {
                            dataView.setText(e.getMessage());
                        }
                    }
                });
            }
        };
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        btnStart = (Button) findViewById(R.id.btn_start);
        btnEnd = (Button) findViewById(R.id.btn_end);
        timer = (Chronometer) findViewById(R.id.timer);
        dataView = (TextView) findViewById(R.id.dataView);
        resultView = (TextView) findViewById(R.id.resultView);
        tickTimer = new Timer();
        handler = new Handler();
        finished = false;

        pt = new PhysioFlexTherapist();
        armCurl = new ArmCurl(3);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              timer.setBase(SystemClock.elapsedRealtime());
              timer.start();
              dataView.setText("");
              resultView.setText("");
              getPrintTask();
              getStopTask();

              tickTimer.scheduleAtFixedRate(printTask, 1000,1000);
              tickTimer.scheduleAtFixedRate(stopTask, 1100, 1000);
            }
        });

        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.stop();
                printTask.cancel();
            }
        });
    }
}
