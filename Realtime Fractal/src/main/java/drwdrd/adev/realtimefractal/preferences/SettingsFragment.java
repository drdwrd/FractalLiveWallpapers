package drwdrd.adev.realtimefractal.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.util.AttributeSet;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import drwdrd.adev.ui.LoadFileDialogFragment;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updatePreference(findPreference(key));
    }

    protected void updateAllPreferences(PreferenceGroup preferenceGroup) {
        for(int i=0;i<preferenceGroup.getPreferenceCount();i++) {
            Preference preference=preferenceGroup.getPreference(i);
            if(preference instanceof PreferenceGroup) {
                updateAllPreferences((PreferenceGroup)preference);
            } else {
                updatePreference(preference);
            }
        }
    }

    protected void updatePreference(Preference preference) {
        if(preference!=null&&preference.isPersistent()) {
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                listPreference.setSummary(listPreference.getEntry().toString());
            } else if (preference instanceof EditTextPreference) {
                EditTextPreference editTextPreference = (EditTextPreference) preference;
                editTextPreference.setSummary(editTextPreference.getText());
            }
        }
    }

    public static AttributeSet readAttributeSetFromXml(Context context,int id,String tag) {
        XmlResourceParser parser = context.getResources().getXml(id);
        int state = 0;
        do {
            try {
                state = parser.next();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (state == XmlPullParser.START_TAG) {
                if (parser.getName().equals(tag)) {
                    return Xml.asAttributeSet(parser);
                }
            }
        } while(state != XmlPullParser.END_DOCUMENT);
        return null;
    }
}
