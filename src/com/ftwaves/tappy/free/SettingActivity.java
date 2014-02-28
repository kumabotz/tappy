package com.ftwaves.tappy.free;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

public class SettingActivity extends PreferenceActivity {
    private static final String PREF_NAME = "com.ftwaves.tappy.free";
    private static final int DEFAULT_VIBRATION = 10;
    private SharedPreferences.Editor editor;
    private Preference prefVibration;
    private Vibrator vibrator;
    private int vibration;
    private int savedVibration;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        addPreferencesFromResource(R.xml.preferences);

        setUpActionBar();

        final SharedPreferences prefs = getSharedPreferences(PREF_NAME,
                Context.MODE_WORLD_READABLE);
        editor = prefs.edit();

        prefVibration = findPreference(MyPreferences.VIBRATION);
        if (prefs.contains(MyPreferences.VIBRATION)) {
            vibration = prefs
                    .getInt(MyPreferences.VIBRATION, DEFAULT_VIBRATION);
        } else {
            editor.putInt(MyPreferences.VIBRATION, DEFAULT_VIBRATION);
            vibration = DEFAULT_VIBRATION;
        }

        savedVibration = vibration;
        prefVibration
                .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(
                            final Preference preference, final Object o) {
                        vibration = (Integer) o;
                        if (savedVibration != vibration) {
                            vibrator.vibrate(vibration);
                            savedVibration = -1;
                        }
                        editor.putInt(MyPreferences.VIBRATION, vibration);
                        return true;
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            startActivity(new Intent(this, TappyFree.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        editor.commit();
    }

    private void setUpActionBar() {
        // make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final ActionBar actionBar = getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
