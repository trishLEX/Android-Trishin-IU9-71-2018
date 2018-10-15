package ru.bmstu.trishlex.cocktailapp.Settings;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import ru.bmstu.trishlex.cocktailapp.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_fragment);
    }
}
