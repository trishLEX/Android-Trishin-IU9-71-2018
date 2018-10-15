package ru.bmstu.trishlex.cocktailapp.Settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.util.Log;

import ru.bmstu.trishlex.cocktailapp.R;

/**
 * Загрузчик настроек
 */
public class PreferencesActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_fragment);

        PreferenceManager
                .getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        PreferenceScreen preferenceScreen = getPreferenceScreen();
        SharedPreferences sharedPreferences = preferenceScreen.getSharedPreferences();

        for (int i = 0; i < preferenceScreen.getPreferenceCount(); i++) {
            Preference p = preferenceScreen.getPreference(i);
            if (p.getKey().equals(getString(R.string.listKey))) {
                ListPreference listPreference = (ListPreference) p;
                String value = listPreference.getValue();
                int prefIndex = listPreference.findIndexOfValue(value);
                Log.d("debugLog", "sum value: " + value + " index " + prefIndex);
                if (prefIndex >= 0) {
                    p.setSummary(listPreference.getEntries()[prefIndex] + "px");
                }
            } else if (p.getKey().equals(getString(R.string.textSizeIngredientsKey))
                    || p.getKey().equals(getString(R.string.textSizeSimpleKey))) {
                String value = Integer.toString(sharedPreferences.getInt(p.getKey(), getResources().getInteger(R.integer.textSizeDefValue)));

                p.setSummary(value);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.listKey))) {
            String value = sharedPreferences.getString(key, null);
            Log.d("debugLog", "value: " + value);

            Preference preference = findPreference(key);
            preference.setSummary(value + "px");
        } else if (key.equals(getString(R.string.textSizeIngredientsKey))
                || key.equals(getString(R.string.textSizeSimpleKey))) {
            String value = Integer.toString(sharedPreferences.getInt(key, getResources().getInteger(R.integer.textSizeDefValue)));

            Preference preference = findPreference(key);
            preference.setSummary(value);
        }
    }

    @Override
    protected void onDestroy() {
        PreferenceManager
                .getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }
}
