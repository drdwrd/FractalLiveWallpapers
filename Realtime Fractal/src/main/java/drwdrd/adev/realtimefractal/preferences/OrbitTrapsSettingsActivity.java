package drwdrd.adev.realtimefractal.preferences;

import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import drwdrd.adev.engine.LogSystem;
import drwdrd.adev.realtimefractal.R;
import drwdrd.adev.ui.AppCompatPreferenceActivity;
import drwdrd.adev.ui.ListViewDialogFragment;

public class OrbitTrapsSettingsActivity extends AppCompatPreferenceActivity {

    public static final String tag = "OrbitTrapsSettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OrbitTrapsSettingsFragment fragment=new OrbitTrapsSettingsFragment();
        getFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
        setTitle("Orbit Traps");
    }
}
