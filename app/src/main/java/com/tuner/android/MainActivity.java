package com.tuner.android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView mPitchMain;
    private TextView mPitchHalf;
    private TextView mPitchOctave;

    private TextView mPitchIsLower;
    private TextView mPitchIsHigher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPitchMain = findViewById(R.id.pitch_main);
        mPitchHalf = findViewById(R.id.pitch_half);
        mPitchOctave = findViewById(R.id.pitch_octave);
        mPitchIsLower = findViewById(R.id.pitch_is_lower);
        mPitchIsHigher = findViewById(R.id.pitch_is_higher);
    }


}