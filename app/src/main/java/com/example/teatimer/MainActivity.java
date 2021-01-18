package com.example.teatimer;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private int teaTime = 270;

    // Seconds keep track of how long the timer has been running
    private int seconds = teaTime;

    // Keeps track of if a users tea is brewing
    private boolean brewing;

    private boolean wasBrewing;

    private boolean finished;

    private boolean useAlert = false;

    private boolean playing = false;

    private Uri alert;

    private Ringtone alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            seconds = savedInstanceState.getInt("seconds");
            brewing = savedInstanceState.getBoolean("brewing");
            wasBrewing = savedInstanceState.getBoolean("wasBrewing");
            finished = savedInstanceState.getBoolean("finished");
        }
        //button.setVisibility(View.INVISIBLE);
        runTimer();
    }

    // Save the state of the tea timer
    @Override
    public void onSaveInstanceState(
            Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("seconds", seconds);
        savedInstanceState.putBoolean("brewing", brewing);
        savedInstanceState.putBoolean("wasBrewing", wasBrewing);
        savedInstanceState.putBoolean("finsihed", finished);
    }

    private void alertUsage() {
        if (alert == null) {
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alert == null) {
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                if (alert == null) {
                    alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                }
            }
        }

        alarm = RingtoneManager.getRingtone(getApplicationContext(), alert);

        if (useAlert && !playing) {
            this.alarm.play();
            playing = true;
        }
    }

    // If activity is paused, stop the timer
    @Override
    protected void onPause() {
        super.onPause();
        wasBrewing = brewing;
        brewing = false;
    }

    // If activity is resumed, start the timer again if it was running before.
    @Override
    protected void onResume() {
        super.onResume();
        if(wasBrewing) {
            brewing = true;
        }
    }

    // Start the timer when the brew button is pressed.
    public void onClickStart(View view) {
        brewing = true;
    }

    // Stop the timer from running.
    public void onClickStop(View view) {
        useAlert = false;
        onClickReset(view);
    }

    // Reset the timer
    public void onClickReset(View view) {
        brewing = false;
        seconds = teaTime;
        finished = false;
        if (playing) {
            this.alarm.stop();
            playing = false;
        }
    }

    // sets the number of seconds on te timer
    private void runTimer() {
        // Get the text view
        final TextView timeView = findViewById(R.id.time_view);

        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                String time = String.format(Locale.getDefault(), "%02d:%02d", minutes, secs);

                // Set the text view text.
                timeView.setText(time);

                if (brewing && seconds <= 0) {
                    finished = true;
                }

                if (!playing && finished) {
                    useAlert = true;
                    //button.setVisibility(View.INVISIBLE);
                    alertUsage();
                }

                // If brewing is true, increment the seconds variable.
                if (brewing && !finished) {
                    seconds--;
                }

                // post the code again with a delay of 1 second.
                handler.postDelayed(this, 1000);
            }
        });
    }
}
