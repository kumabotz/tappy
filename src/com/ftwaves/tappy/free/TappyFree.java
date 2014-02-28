package com.ftwaves.tappy.free;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class TappyFree extends Activity {
    private static final String MY_APPS = "https://play.google.com/store/apps/developer?id=43waves";
    private static final String PREF_NAME = "com.ftwaves.tappy.free";
    private static final int DEFAULT_VIBRATION = 10;
    private SharedPreferences.Editor editor;
    private Button counter;
    private Vibrator vibrator;
    private SharedPreferences prefs;
    private boolean canUndo;
    private int count;
    private int prevCount;
    private int vibration;

    private float width;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        try {
            prefs = getSharedPreferences(PREF_NAME, Context.MODE_WORLD_READABLE);
            editor = prefs.edit();
        } catch (final Exception e) {
            e.printStackTrace();
        }

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        counter = (Button) findViewById(R.id.counter);

        counter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                return true;
            }
        });
        counter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                haptic();
                increment();
                updateView();
            }
        });

        final Button undo = (Button) findViewById(R.id.undo);
        undo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                return true;
            }
        });
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                haptic();
                undo();
                updateView();
            }
        });

        final Button reset = (Button) findViewById(R.id.reset);
        reset.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                return true;
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                haptic();
                reset();
                updateView();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
        case R.id.settings:
            // save current count in pref or else once navigate back from
            // setting, the count will be lost
            save();
            startActivity(new Intent(this, SettingActivity.class));
            break;
        case R.id.more_apps:
            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri
                    .parse(MY_APPS)));
            break;
        }
        return true;
    }

    private void save() {
        editor.putFloat(MyPreferences.WIDTH, width);
        editor.putInt(MyPreferences.COUNT, count);
        editor.putInt(MyPreferences.PREV_COUNT, prevCount);
        editor.putBoolean(MyPreferences.CAN_UNDO, canUndo);
        editor.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (prefs != null) {
            vibration = prefs
                    .getInt(MyPreferences.VIBRATION, DEFAULT_VIBRATION);
            width = prefs.getFloat(MyPreferences.WIDTH, getWindowManager()
                    .getDefaultDisplay().getWidth());
            count = prefs.getInt(MyPreferences.COUNT, 0);
            prevCount = prefs.getInt(MyPreferences.PREV_COUNT, 0);
            canUndo = prefs.getBoolean(MyPreferences.CAN_UNDO, false);
            counter.setText(String.valueOf(count));
            updateCounterTextSize();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateCounterTextSize();
        save();
    }

    private void savePrevious() {
        prevCount = count;
        canUndo = true;
    }

    private void increment() {
        savePrevious();
        count++;

        if (count > 9999999) {
            Toast.makeText(this, "Maximum count reach!", Toast.LENGTH_SHORT)
                    .show();
            count = 9999999;
        }
    }

    private void decrement() {
        if (count > 1) {
            savePrevious();
            count--;
        } else {
            canUndo = false;
            count = 0;
        }
    }

    private void updateCounterTextSize() {
        if (count < 100) {
            counter.setTextSize(TypedValue.COMPLEX_UNIT_DIP,
                    (float) (0.38 * width));
        } else if (count < 1000) {
            counter.setTextSize(TypedValue.COMPLEX_UNIT_DIP,
                    (float) (0.25 * width));
        } else if (count < 10000) {
            counter.setTextSize(TypedValue.COMPLEX_UNIT_DIP,
                    (float) (0.19 * width));
        } else if (count < 100000) {
            counter.setTextSize(TypedValue.COMPLEX_UNIT_DIP,
                    (float) (0.15 * width));
        } else if (count < 1000000) {
            counter.setTextSize(TypedValue.COMPLEX_UNIT_DIP,
                    (float) (0.12 * width));
        } else {
            counter.setTextSize(TypedValue.COMPLEX_UNIT_DIP,
                    (float) (0.1 * width));
        }
    }

    private void reset() {
        if (count != 0) {
            savePrevious();
            count = 0;
        }
    }

    private void undo() {
        if (!canUndo) {
            return;
        }
        if (count < 1) {
            count = prevCount;
        } else {
            decrement();
        }
    }

    private void updateView() {
        updateCounterTextSize();
        counter.setText(String.valueOf(count));
    }

    private void haptic() {
        vibrator.vibrate(vibration);
    }
}
