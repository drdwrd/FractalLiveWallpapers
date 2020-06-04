package drwdrd.adev.realtimefractal.preferences;

import android.app.WallpaperManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;

import drwdrd.adev.engine.LogSystem;
import drwdrd.adev.realtimefractal.R;
import drwdrd.adev.ui.SeekBarPreference;

public class MainSettingsFragment extends SettingsFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.fractal_settings);
        addPreferencesFromResource(R.xml.coloring_settings);
        addPreferencesFromResource(R.xml.timer_settings);
        addPreferencesFromResource(R.xml.framebuffer_settings);
        updateAllPreferences(getPreferenceScreen());
        updateOrbitTrapsPreferences();
        updateTimerSettingsPreferences();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference.getKey().equals("orbitTrapSettingsScreen")) {
            Intent intent=new Intent(getActivity(),OrbitTrapsSettingsActivity.class);
            getActivity().startActivity(intent);
        }
        if(preference.getKey().equals("fractalSceneEditor")) {
            Intent intent=new Intent(getActivity(),FractalSceneEditorActivity.class);
            getActivity().startActivity(intent);
        }
        return super.onPreferenceTreeClick(preferenceScreen,preference);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        LogSystem.debug(RealtimeFractalSettingsActivity.tag,"MainSettingsFragment.onSharedPreferenceChanged()");
        super.onSharedPreferenceChanged(sharedPreferences,key);
        if(key.equals("fractalColorFunc")) {
            updateOrbitTrapsPreferences();
        } else if(key.equals("scaleTimeWithZoom")) {
            updateTimerSettingsPreferences();
        }
    }

    private void updateOrbitTrapsPreferences() {
        ListPreference colorFuncList=(ListPreference)findPreference("fractalColorFunc");
        PreferenceScreen orbitTrapSettings=(PreferenceScreen)findPreference("orbitTrapSettingsScreen");
        if(colorFuncList.getValue().equals("orbitTraps")||colorFuncList.getValue().equals("debugDistanceField")) {
            orbitTrapSettings.setEnabled(true);
        }
        else {
            orbitTrapSettings.setEnabled(false);
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
