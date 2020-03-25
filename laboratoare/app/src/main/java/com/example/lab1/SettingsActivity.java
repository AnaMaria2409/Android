package com.example.lab1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {

    String signature, reply;
    Boolean sync, attachment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        // valorile la prima apasare pe settings
        signature = sharedPref.getString("signature", "");
        reply = sharedPref.getString("reply", "reply");
        sync = sharedPref.getBoolean("sync", false);
        attachment = sharedPref.getBoolean("attachment", false);

        // store settings
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("signature", signature);
        editor.putString("reply", reply);
        editor.putBoolean("sync", sync);
        editor.putBoolean("attachment", attachment);
        editor.commit();

        // apel listener daca se modifica valorile
        sharedPref.registerOnSharedPreferenceChangeListener(listener);

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

    SharedPreferences.OnSharedPreferenceChangeListener listener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    if (key.equals("signature")) {
                        signature = sharedPreferences.getString("signature", "");
                        Log.i("Updated", "Preference value for signature was updated to: " + signature);
                    }
                    else if(key.equals("reply")){
                        reply = sharedPreferences.getString("reply", "reply");
                        Log.i("Updated", "Preference value for replay was updated to: " + reply);
                    }
                    else if(key.equals("sync")){
                        sync = sharedPreferences.getBoolean("sync", false);
                        Log.i("Updated", "Preference value for sync was updated to: " + sync);
                    }
                    else if(key.equals("attachment")){
                        attachment = sharedPreferences.getBoolean("attachment", false);
                        Log.i("Updated", "Preference value for attachment was updated to: " + attachment);
                    }
                }
            };


}