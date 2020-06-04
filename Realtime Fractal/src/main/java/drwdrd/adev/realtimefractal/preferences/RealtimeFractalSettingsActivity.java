package drwdrd.adev.realtimefractal.preferences;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.preference.PreferenceActivity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import drwdrd.adev.engine.LogSystem;
import drwdrd.adev.realtimefractal.R;
import drwdrd.adev.realtimefractal.RealtimeFractalService;
import drwdrd.adev.ui.AppCompatPreferenceActivity;
import drwdrd.adev.ui.LoadFileDialogFragment;
import drwdrd.adev.ui.SaveFileDialogFragment;

public class RealtimeFractalSettingsActivity extends AppCompatPreferenceActivity {

    public static final String tag="RealtimeFractalSettingsActivity";
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 101;
    private Fragment mainFragment=null;
    private SaveFileDialogFragment saveFileDialogFragment = new SaveFileDialogFragment();
    private LoadFileDialogFragment loadFileDialogFragment = new LoadFileDialogFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        LogSystem.debug("RealtimeFractalSettingsActivity", "onCreate()");
        super.onCreate(savedInstanceState);
        if(!usePreferenceHeaders()&&mainFragment==null) {
            mainFragment=new MainSettingsFragment();
            getFragmentManager().beginTransaction().replace(android.R.id.content,mainFragment).commit();
        }
        saveFileDialogFragment.setTitle("Save to file");
        saveFileDialogFragment.setDefaultFileName("NewFractal");
        saveFileDialogFragment.setFileExtension(".xml");
        saveFileDialogFragment.setOnSaveListener(new SaveFileDialogFragment.OnSaveListener() {
            @Override
            public void onSave(String fileName) {
                if(RealtimeFractalService.getFractalSettings().saveToXml(RealtimeFractalSettingsActivity.this, fileName) == true) {
                    Toast msg = Toast.makeText(RealtimeFractalSettingsActivity.this, fileName + " saved...", Toast.LENGTH_SHORT);
                    msg.show();
                } else {
                    Toast msg = Toast.makeText(RealtimeFractalSettingsActivity.this, "Error while writing to file " + fileName + "!", Toast.LENGTH_SHORT);
                    msg.show();
                }
            }
        });
        loadFileDialogFragment.setTitle("Open file");
        loadFileDialogFragment.setFileExtension(".xml");
        loadFileDialogFragment.setOnLoadListener(new LoadFileDialogFragment.OnLoadListener() {
            @Override
            public void onLoad(String fileName) {
                if(RealtimeFractalService.getFractalSettings().loadFromXml(RealtimeFractalSettingsActivity.this, fileName) == true) {
                    Toast msg = Toast.makeText(RealtimeFractalSettingsActivity.this, fileName + " loaded...", Toast.LENGTH_SHORT);
                    msg.show();
                    finish();
                } else {
                    Toast msg = Toast.makeText(RealtimeFractalSettingsActivity.this, "Error while reading from file " + fileName + "!", Toast.LENGTH_SHORT);
                    msg.show();
                }
//                startActivity(getIntent());
            }
        });

        Intent intent = getIntent();

        if(intent != null) {

            String action = intent.getAction();
            String type = intent.getType();

            String displayMsg = null;

            LogSystem.debug("RealtimeFractalSettingsActivity", "Activity started with Intent(action = " + action + ", type = " + type + ")");

            if (Intent.ACTION_SEND.equals(action)) {

                displayMsg = "Fractal loaded...";

                Bundle bundle = intent.getExtras();
                for (String key : bundle.keySet()) {
                    Object value = bundle.get(key);
                    if(value != null) {
                        LogSystem.debug("RealtimeFractalSettingsActivity", "Intent.getExtras() = " + String.format("%s %s (%s)", key, value.toString(), value.getClass().getName()));
                    }
                }

                if (type.equals("text/xml")) {
                    String xmlString = intent.getStringExtra(Intent.EXTRA_TEXT);
                    if (xmlString == null || RealtimeFractalService.loadFractalSettings(this, xmlString) == false) {
                        RealtimeFractalService.loadDefaultFractalSettings(this);
                        displayMsg = "Error! Falling back to default settings.";
                    }
                } else if (type.equals("text/plain")) {
                    String xmlString = (String) intent.getExtras().get(Intent.EXTRA_TEXT);
                    if(xmlString == null || RealtimeFractalService.loadFractalSettings(this, xmlString) == false) {
                        Uri xmlUri = (Uri) intent.getExtras().get(Intent.EXTRA_STREAM);
                        if (xmlUri == null || RealtimeFractalService.loadFractalSettings(this, xmlUri) == false) {
/*                            if(xmlUri.getScheme().equals("file")) {
                                if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    LogSystem.debug(RealtimeFractalSettingsActivity.tag, "Requesting READ_EXTERNAL_STORAGE permission.");
                                    if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                                        LogSystem.debug(RealtimeFractalSettingsActivity.tag, "Showing dialog...");
                                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                                        alertBuilder.setCancelable(true);
                                        alertBuilder.setMessage("Read external storage permission is necessary to read file.");
                                        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                                            public void onClick(DialogInterface dialog, int which) {
                                                ActivityCompat.requestPermissions(RealtimeFractalSettingsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                                            }
                                        });
                                        alertBuilder.create().show();
                                    } else {
                                        LogSystem.debug(RealtimeFractalSettingsActivity.tag, "Requesting permission...");
                                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                                    }
                                }
                            }*/
                            RealtimeFractalService.loadDefaultFractalSettings(this);
                            displayMsg = "Error! Falling back to default settings.";
                        }
                    }
                } else {
                    RealtimeFractalService.loadCurrentFractalSettings(this);
                }
            } else if(NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

                Tag ndefTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                String[] techList = ndefTag.getTechList();
                String searchedTech = Ndef.class.getName();

                for (String tech : techList) {
                    if(searchedTech.equals(tech)) {
                        String xmlString = NdefReader(ndefTag);
                        LogSystem.debug(tag, "NDEF = " + xmlString);
                        if(xmlString == null || RealtimeFractalService.loadFractalSettings(this, xmlString) == false) {
                            RealtimeFractalService.loadDefaultFractalSettings(this);
                            displayMsg = "Error! Falling back to default settings.";
                        }
                        break;
                    }
                }
            }

            if(displayMsg != null) {
                Toast msg = Toast.makeText(RealtimeFractalSettingsActivity.this, displayMsg, Toast.LENGTH_SHORT);
                msg.show();
            }
        }
    }

    private String NdefReader(Tag ndefTag) {

        Ndef ndef = Ndef.get(ndefTag);
        if (ndef == null) {
            // NDEF is not supported by this Tag.
            return null;
        }

        NdefMessage ndefMessage = ndef.getCachedNdefMessage();

        NdefRecord[] records = ndefMessage.getRecords();
        for (NdefRecord ndefRecord : records) {
            if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                try {
                    return readText(ndefRecord);
                } catch (UnsupportedEncodingException e) {
                    LogSystem.debug(tag, "Unsupported Encoding" + e.toString());
                }
            }
        }
        return null;
    }

    private String readText(NdefRecord record) throws UnsupportedEncodingException {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

        byte[] payload = record.getPayload();

        // Get the Text Encoding
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

        // Get the Language Code
        int languageCodeLength = payload[0] & 0063;

        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
        // e.g. "en"

        // Get the Text
        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
    }
/*
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LogSystem.debug(RealtimeFractalSettingsActivity.tag, "PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE granted!");
                    //granted
                } else {
                    LogSystem.debug(RealtimeFractalSettingsActivity.tag, "PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE not granted!");
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }*/

    @Override
    public void onBuildHeaders(List<Header> target) {
        super.onBuildHeaders(target);
        if(usePreferenceHeaders()) {
            loadHeadersFromResource(R.xml.preference_headers, target);
        }
    }

    //Check for fragment injection security risk http://securityintelligence.com/new-vulnerability-android-framework-fragment-injection/#.U2u8_KI2mOw
    @Override
    protected boolean isValidFragment (String fragmentName) {
        if(fragmentName.equals("drwdrd.adev.realtimefractal.preferences.OrbitTrapsSettingsFragment"))
            return true;
        else if(fragmentName.equals("drwdrd.adev.realtimefractal.preferences.FractalSettingsFragment"))
            return true;
        else if(fragmentName.equals("drwdrd.adev.realtimefractal.preferences.TimerSettingsFragment"))
            return true;
        else if(fragmentName.equals("drwdrd.adev.realtimefractal.preferences.FramebufferSettingsFragment"))
            return true;
        else if(fragmentName.equals("drwdrd.adev.realtimefractal.preferences.ColoringSettingsFragment"))
            return true;
        return false;
    }

    protected boolean usePreferenceHeaders() {
        return (!onIsHidingHeaders() && onIsMultiPane());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menuSave:
                saveFileDialogFragment.show(getFragmentManager(),"SaveFileDialogFragment");
                return true;
            case R.id.menuLoad:
                loadFileDialogFragment.show(getFragmentManager(), "OpenFileDialogFragment");
                return true;
            case R.id.menuShare:
                Intent sendIntent = new Intent();
                DateFormat df = new SimpleDateFormat("_yyyyMMdd_HHmmss");
                String name = getResources().getString(R.string.share_name) + df.format(Calendar.getInstance().getTime()) + ".xml";
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TITLE, name);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, name);
                sendIntent.putExtra(Intent.EXTRA_TEXT, RealtimeFractalService.getFractalSettings().saveToXmlString());
                sendIntent.setType("text/xml");
                if(sendIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(Intent.createChooser(sendIntent, "Share fractal"));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
