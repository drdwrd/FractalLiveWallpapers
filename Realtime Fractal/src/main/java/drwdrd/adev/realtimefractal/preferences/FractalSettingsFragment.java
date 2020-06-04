package drwdrd.adev.realtimefractal.preferences;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;

import drwdrd.adev.realtimefractal.R;

public class FractalSettingsFragment extends SettingsFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.fractal_settings);

        PreferenceScreen orbitTrapSettings=(PreferenceScreen)findPreference("orbitTrapSettingsScreen");
        orbitTrapSettings.setIntent(new Intent(getActivity(),OrbitTrapsSettingsDialogActivity.class));

        PreferenceScreen fractalEditor=(PreferenceScreen)findPreference("fractalSceneEditor");
        fractalEditor.setIntent(new Intent(getActivity(),FractalSceneEditorActivity.class));

        updateAllPreferences(getPreferenceScreen());
        updateOrbitTrapsPreferences();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        super.onSharedPreferenceChanged(sharedPreferences,key);
        if(key.equals("fractalColorFunc")||key.equals("fractalOrbitTrapFunc")) {
            updateOrbitTrapsPreferences();
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
}
