package drwdrd.adev.realtimefractal.preferences;

import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.view.View;

import drwdrd.adev.engine.LogSystem;
import drwdrd.adev.engine.vector2f;
import drwdrd.adev.engine.vector3f;
import drwdrd.adev.realtimefractal.FractalSettings;
import drwdrd.adev.realtimefractal.R;
import drwdrd.adev.realtimefractal.RealtimeFractalService;
import drwdrd.adev.ui.DialogPreferenceWithButton;
import drwdrd.adev.ui.ListViewDialogFragment;
import drwdrd.adev.ui.Point3Preference;
import drwdrd.adev.ui.PointPreference;

public class OrbitTrapsSettingsFragment extends SettingsFragment implements ListViewDialogFragment.OnItemClickListener {

    private int preferenceCount = 0;
    private ListViewDialogFragment addOrbitTrapDialog=new ListViewDialogFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.orbit_traps_settings);
        updateAllPreferences(getPreferenceScreen());
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        preferenceCount = 0;

        addOrbitTrapDialog.setTitle("Add New Orbit Trap");
        String items[]={"Point","Line","Circle","Sine"};
        addOrbitTrapDialog.setItemList(items);
        addOrbitTrapDialog.setOnItemClickListener(this);

        for (FractalSettings.OrbitTrap trap : RealtimeFractalService.getFractalSettings().getOrbitTraps()) {
            switch (trap.getDistanceFunc()) {
                case Point:
                    final PointPreference pointPreference = new PointPreference(getActivity(), readAttributeSetFromXml(getActivity(), R.xml.orbit_traps_point_settings, "drwdrd.adev.ui.PointPreference"));
                    pointPreference.setKey(trap.getKey());
                    pointPreference.setTitle("Point" + preferenceCount);
                    pointPreference.setOrder(preferenceCount++);
                    pointPreference.enableButton(true);
                    pointPreference.setButtonIcon(R.drawable.ic_menu_close_clear_cancel);
                    pointPreference.setButtonOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getPreferenceScreen().removePreference(pointPreference);
                            String key = pointPreference.getKey();
                            RealtimeFractalService.getFractalSettings().removeOrbitTrap(key);
                        }
                    });
                    getPreferenceScreen().addPreference(pointPreference);
                    updatePreference(pointPreference);
                    break;
                case Line:
                    final PointPreference linePreference = new PointPreference(getActivity(), readAttributeSetFromXml(getActivity(), R.xml.orbit_traps_line_settings, "drwdrd.adev.ui.PointPreference"));
                    linePreference.setKey(trap.getKey());
                    linePreference.setTitle("Line" + preferenceCount);
                    linePreference.setOrder(preferenceCount++);
                    linePreference.enableButton(true);
                    linePreference.setButtonIcon(R.drawable.ic_menu_close_clear_cancel);
                    linePreference.setButtonOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getPreferenceScreen().removePreference(linePreference);
                            String key = linePreference.getKey();
                            RealtimeFractalService.getFractalSettings().removeOrbitTrap(key);
                        }
                    });
                    getPreferenceScreen().addPreference(linePreference);
                    updatePreference(linePreference);
                    break;
                case Circle:
                    final Point3Preference circlePreference = new Point3Preference(getActivity(), readAttributeSetFromXml(getActivity(), R.xml.orbit_traps_circle_settings, "drwdrd.adev.ui.Point3Preference"));
                    circlePreference.setKey(trap.getKey());
                    circlePreference.setTitle("Circle" + preferenceCount);
                    circlePreference.setOrder(preferenceCount++);
                    circlePreference.enableButton(true);
                    circlePreference.setButtonIcon(R.drawable.ic_menu_close_clear_cancel);
                    circlePreference.setButtonOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getPreferenceScreen().removePreference(circlePreference);
                            String key = circlePreference.getKey();
                            RealtimeFractalService.getFractalSettings().removeOrbitTrap(key);
                        }
                    });
                    getPreferenceScreen().addPreference(circlePreference);
                    updatePreference(circlePreference);
                    break;
                case Sin:
                    final PointPreference sinePreference = new PointPreference(getActivity(), readAttributeSetFromXml(getActivity(), R.xml.orbit_traps_sin_settings, "drwdrd.adev.ui.PointPreference"));
                    sinePreference.setKey(trap.getKey());
                    sinePreference.setTitle("Sine" + preferenceCount);
                    sinePreference.setOrder(preferenceCount++);
                    sinePreference.enableButton(true);
                    sinePreference.setButtonIcon(R.drawable.ic_menu_close_clear_cancel);
                    sinePreference.setButtonOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getPreferenceScreen().removePreference(sinePreference);
                            String key = sinePreference.getKey();
                            RealtimeFractalService.getFractalSettings().removeOrbitTrap(key);
                        }
                    });
                    getPreferenceScreen().addPreference(sinePreference);
                    updatePreference(sinePreference);
                    break;
            }
        }
    }


    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference.getKey().equals("addNewOrbitTrap")) {
            addOrbitTrapDialog.show(getFragmentManager(),"ListViewDialogFragment");
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public void onItemClick(String item) {
        FractalSettings fractalSettings = RealtimeFractalService.getFractalSettings();
        if (item.equals("Point")) {
            String key="orbitTrapPoint" + preferenceCount;
            final PointPreference pointPreference = new PointPreference(getActivity(), readAttributeSetFromXml(getActivity(), R.xml.orbit_traps_point_settings, "drwdrd.adev.ui.PointPreference"));
            pointPreference.setKey(key);
            pointPreference.setTitle("Point" + preferenceCount);
            pointPreference.setOrder(preferenceCount++);
            pointPreference.enableButton(true);
            pointPreference.setButtonIcon(R.drawable.ic_menu_close_clear_cancel);
            pointPreference.setButtonOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getPreferenceScreen().removePreference(pointPreference);
                    String key = pointPreference.getKey();
                    RealtimeFractalService.getFractalSettings().removeOrbitTrap(key);
                }
            });
            fractalSettings.addNewOrbitTrap(FractalSettings.DistanceFunc.Point, key, new vector2f(0.0f, 0.0f));
            getPreferenceScreen().addPreference(pointPreference);
            updatePreference(pointPreference);
        } else if (item.equals("Circle")) {
            String key = "orbitTrapCircle" + preferenceCount;
            final Point3Preference circlePreference = new Point3Preference(getActivity(), readAttributeSetFromXml(getActivity(), R.xml.orbit_traps_circle_settings, "drwdrd.adev.ui.Point3Preference"));
            circlePreference.setKey(key);
            circlePreference.setTitle("Circle" + preferenceCount);
            circlePreference.setOrder(preferenceCount++);
            circlePreference.enableButton(true);
            circlePreference.setButtonIcon(R.drawable.ic_menu_close_clear_cancel);
            circlePreference.setButtonOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getPreferenceScreen().removePreference(circlePreference);
                    String key = circlePreference.getKey();
                    RealtimeFractalService.getFractalSettings().removeOrbitTrap(key);
                }
            });
            fractalSettings.addNewOrbitTrap(FractalSettings.DistanceFunc.Circle, key, new vector3f(0.0f,0.0f,1.0f));
            getPreferenceScreen().addPreference(circlePreference);
            updatePreference(circlePreference);
        } else if (item.equals("Line")) {
            String key = "orbitTrapLine" + preferenceCount;
            final PointPreference linePreference = new PointPreference(getActivity(), readAttributeSetFromXml(getActivity(), R.xml.orbit_traps_line_settings, "drwdrd.adev.ui.PointPreference"));
            linePreference.setKey(key);
            linePreference.setTitle("Line" + preferenceCount);
            linePreference.enableButton(true);
            linePreference.setOrder(preferenceCount++);
            linePreference.setButtonIcon(R.drawable.ic_menu_close_clear_cancel);
            linePreference.setButtonOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getPreferenceScreen().removePreference(linePreference);
                    String key = linePreference.getKey();
                    RealtimeFractalService.getFractalSettings().removeOrbitTrap(key);
                }
            });
            fractalSettings.addNewOrbitTrap(FractalSettings.DistanceFunc.Line, key, new vector2f(1.0f, 0.0f));
            getPreferenceScreen().addPreference(linePreference);
            updatePreference(linePreference);
        } else if (item.equals("Sine")) {
            String key = "orbitTrapSin" + preferenceCount;
            final PointPreference sinePreference = new PointPreference(getActivity(), readAttributeSetFromXml(getActivity(), R.xml.orbit_traps_sin_settings, "drwdrd.adev.ui.PointPreference"));
            sinePreference.setKey(key);
            sinePreference.setTitle("Sine" + preferenceCount);
            sinePreference.setOrder(preferenceCount++);
            sinePreference.enableButton(true);
            sinePreference.setButtonIcon(R.drawable.ic_menu_close_clear_cancel);
            sinePreference.setButtonOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getPreferenceScreen().removePreference(sinePreference);
                    String key = sinePreference.getKey();
                    RealtimeFractalService.getFractalSettings().removeOrbitTrap(key);
                }
            });
            fractalSettings.addNewOrbitTrap(FractalSettings.DistanceFunc.Sin, key, new vector2f(1.0f, 1.0f));
            getPreferenceScreen().addPreference(sinePreference);
            updatePreference(sinePreference);
        }
    }
}
