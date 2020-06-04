package drwdrd.adev.realtimefractal.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;

import drwdrd.adev.realtimefractal.R;
import drwdrd.adev.ui.SeekBarPreference;

public class TimerSettingsFragment extends SettingsFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.timer_settings);
        updateAllPreferences(getPreferenceScreen());
        updateTimerSettingsPreferences();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        super.onSharedPreferenceChanged(sharedPreferences,key);
        if(key.equals("scaleTimeWithZoom")) {
            updateTimerSettingsPreferences();
        }
    }

    private void updateTimerSettingsPreferences() {
        CheckBoxPreference slowTimerPref=(CheckBoxPreference)findPreference("scaleTimeWithZoom");
        SeekBarPreference slowTimerFactorPref=(SeekBarPreference)findPreference("timeScalingFactor");
        if(slowTimerPref.isChecked()) {
            slowTimerFactorPref.setEnabled(true);
        }
        else {
            slowTimerFactorPref.setEnabled(false);
        }
    }
}
