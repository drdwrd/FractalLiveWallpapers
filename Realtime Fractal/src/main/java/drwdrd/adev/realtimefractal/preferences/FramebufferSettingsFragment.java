package drwdrd.adev.realtimefractal.preferences;

import android.os.Bundle;
import drwdrd.adev.realtimefractal.R;

public class FramebufferSettingsFragment extends SettingsFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.framebuffer_settings);
        updateAllPreferences(getPreferenceScreen());
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

}
