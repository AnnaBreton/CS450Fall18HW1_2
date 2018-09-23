package com.example.anna.stopwatch;


import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    // Define variable for our views
    private TextView tv_count = null;
    private Button bt_start = null;
    private Button bt_stop =null;
    //private Button bt_resume =null;
    private Button bt_reset =null;
    private Timer t = null;
    private Counter ctr = null;  // TimerTask

    public AudioAttributes  aa = null;
    private SoundPool soundPool = null;
    private int bloopSound = 0;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize views
        this.tv_count = findViewById(R.id.tv_count);
        this.bt_start = findViewById(R.id.bt_start);
        this.bt_stop = findViewById(R.id.bt_stop);
        //this.bt_resume = findViewById(R.id.bt_resume);
        this.bt_reset = findViewById(R.id.bt_reset);

        bt_stop.setEnabled(false);


        this.bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt_start.setEnabled(false);
                bt_reset.setEnabled(false);
                bt_stop.setEnabled(true);
                t = new Timer();
                ctr = new Counter();
                t.scheduleAtFixedRate(ctr, 0, 100);
            }
        });



        this.bt_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bt_start.setEnabled(true);
                bt_reset.setEnabled(true);
                bt_stop.setEnabled(false);

                getPreferences(MODE_PRIVATE)
                        .edit()
                        .putInt("Count", ctr.count)
                        .apply();

                t.cancel();
                ctr.cancel();

            }
        });

        this.bt_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt_start.setEnabled(true);
                bt_reset.setEnabled(false);
                bt_stop.setEnabled(false);
                ctr.count=0;
                getPreferences(MODE_PRIVATE)
                        .edit()
                        .putInt("Count", 0)
                        .apply();
                tv_count.setText("0:00:0");
                t.cancel();
                ctr.cancel();
            }
        });

        /*
        this.bt_resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt_resume.setEnabled(false);
                t.scheduleAtFixedRate(ctr, 0, 100);
            }
        });
*/
        this.aa = new AudioAttributes
                .Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build();

        this.soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(aa)
                .build();
        this.bloopSound = this.soundPool.load(
                this, R.raw.bloop, 1);

        this.tv_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soundPool.play(bloopSound, 1f,
                        1f, 1, 0, 1f);
                Animator anim = AnimatorInflater
                        .loadAnimator(MainActivity.this,
                                R.animator.counter);
                anim.setTarget(tv_count);
                anim.start();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        ctr = new Counter();
        t = new Timer();

        // releoad the count from a previous
        // run, if first time running, start at 0.
        /// preferences to share state
        int count = getPreferences(MODE_PRIVATE)
                .getInt("Count", 0);
        this.tv_count.setText( Integer.toString(count/600)+" : "+Integer.toString((count/10)%60)+" : "+Integer.toString(count%10));




        // factory method - design pattern
        Toast.makeText(this, "Stopwatch is started",
                Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferences(MODE_PRIVATE)
                .edit()
                .putInt("Count", ctr.count)
                .apply();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getPreferences(MODE_PRIVATE)
                .edit()
                .putInt("Count", ctr.count)
                .apply();
    }

    class Counter extends TimerTask {
        private int count = getPreferences(MODE_PRIVATE)
                .getInt("Count", 0) ;

        @Override
        public void run() {
            MainActivity.this.runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.tv_count.setText(
                                    Integer.toString(count/600)+" : "+Integer.toString((count/10)%60)+" : "+Integer.toString(count%10));
                            count++;
                        }
                    }
            );
        }

    }
}
