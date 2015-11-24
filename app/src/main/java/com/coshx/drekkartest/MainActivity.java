package com.coshx.drekkartest;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        findViewById(R.id.basic_triggering_trigger).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(
                        new Intent(getApplication(), BasicTriggeringActivity.class)
                    );
                }
            }
        );

        findViewById(R.id.benchmark_trigger).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(
                        new Intent(getApplication(), BenchmarkActivity.class)
                    );
                }
            }
        );

        findViewById(R.id.event_data_trigger).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(
                        new Intent(getApplication(), EventDataActivity.class)
                    );
                }
            }
        );

        findViewById(R.id.event_name_trigger).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(
                        new Intent(getApplication(), EventNameActivity.class)
                    );
                }
            }
        );

        findViewById(R.id.initialization_trigger).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(
                        new Intent(getApplication(), InitializationActivity.class)
                    );
                }
            }
        );

        findViewById(R.id.threading_trigger).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(
                        new Intent(getApplication(), ThreadingActivity.class)
                    );
                }
            }
        );
    }
}
