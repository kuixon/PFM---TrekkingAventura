package es.deusto.trekkingaventura.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import es.deusto.trekkingaventura.R;

/**
 * Created by salgu on 22/02/2017.
 */

public class FilterSettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if (sharedPreferences.getString("distance","").isEmpty()) {
            this.findPreference("distance").setSummary("Introduce la distancia máxima");
        } else {
            this.findPreference("distance").setSummary(sharedPreferences.getString("distance", ""));
        }

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("distance")) {
            if (sharedPreferences.getString(key,"").equals("")) {
                this.findPreference(key).setSummary("Introduce la distancia máxima");
            } else {
                this.findPreference(key).setSummary(sharedPreferences.getString(key, "Introduce la distancia máxima"));
            }
        }
    }
}
