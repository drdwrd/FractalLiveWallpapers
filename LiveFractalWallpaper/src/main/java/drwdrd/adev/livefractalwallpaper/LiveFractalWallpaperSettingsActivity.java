package drwdrd.adev.livefractalwallpaper;

import drwdrd.adev.engine.GLCaps;
import drwdrd.adev.engine.LogSystem;
import drwdrd.adev.livefractalwallpaper.R;
import drwdrd.adev.ui.SeekBarPreference;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class LiveFractalWallpaperSettingsActivity extends PreferenceActivity
{


	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		
	    if(GLCaps.getInstance().getMaxTextureSize()<2048)
	    {
	    	Preference highresTexturesPrefenrence=getPreferenceScreen().findPreference("highres");
	    	highresTexturesPrefenrence.setEnabled(false);
	 	   LogSystem.debug(LiveFractalWallpaperService.tag,"High Resoultion Textures unavalaible...");
	    }
	    
	    Preference button=(Preference)getPreferenceScreen().findPreference("resetsettings");      
	    if(button!=null) 
	    {
			button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
			{
				@Override
				public boolean onPreferenceClick(Preference arg0)
				{
					resetSettings(getPreferenceScreen().getSharedPreferences());
					return true;
				}
			});
	     }
	}
	
	private void resetSettings(SharedPreferences prefs)
	{
		SeekBarPreference brightness=(SeekBarPreference)getPreferenceScreen().findPreference("brightness");
		brightness.setProgress(ColorCyclingFractalRenderer.brightnessDefaultValue);
		SeekBarPreference timescale=(SeekBarPreference)getPreferenceScreen().findPreference("timescale");
		timescale.setProgress(ColorCyclingFractalRenderer.timescaleDefaultValue);
		SeekBarPreference colorfrequency=(SeekBarPreference)getPreferenceScreen().findPreference("colorfrequency");
		colorfrequency.setProgress(ColorCyclingFractalRenderer.colorfrequencyDefaultValue);
	}

} 