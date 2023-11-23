package com.example.statki;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class StartActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
    }

    protected void onStart(){
        super.onStart();

        Button start = findViewById(R.id.button);

        start.setOnClickListener(view -> Start());
    }

    /**
     * Zmiana startowego menu na okno gry
     */
    private void Start(){
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
