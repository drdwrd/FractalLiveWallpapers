package drwdrd.adev.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.preference.Preference;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public final class SeekBarPreference extends Preference implements OnSeekBarChangeListener
{

    // Namespaces to read attributes
    private static final String PREFERENCE_NS="http://schemas.android.com/apk/res-auto";
    private static final String ANDROID_NS="http://schemas.android.com/apk/res/android";

    // Attribute names
    private static final String ATTR_DEFAULT_VALUE="defaultValue";
    private static final String ATTR_MIN_VALUE="minValue";
    private static final String ATTR_MAX_VALUE="maxValue";
    private static final String ATTR_STEP_SIZE="stepSize";
    private static final String ATTR_PRECISION="precision";

    // Default values for defaults
    private static final float DEFAULT_CURRENT_VALUE=50f;
    private static final float DEFAULT_MIN_VALUE=0f;
    private static final float DEFAULT_MAX_VALUE=100f;
    private static final float DEFAULT_STEP_SIZE=1f;
    private static final int DEFAULT_PRECISION=2;

    // Real defaults
    private final float defaultValue;
    private final float maxValue;
    private final float minValue;
    private final float stepSize;
    
    // Current value
    private float currentValue;
    
    // View elements
    private SeekBar seekBar;
    private TextView valueText;
    private DecimalFormat decimalFormat;

    public SeekBarPreference(Context context,AttributeSet attrs)
    {
    	super(context,attrs);

        decimalFormat=new DecimalFormat();

        String val=attrs.getAttributeValue(PREFERENCE_NS,ATTR_PRECISION);
        int digits=(val==null) ? DEFAULT_PRECISION : Integer.parseInt(val);
        decimalFormat.setMinimumFractionDigits(digits);
        decimalFormat.setMaximumFractionDigits(digits);
        DecimalFormatSymbols decimalFormatSymbols=decimalFormat.getDecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
        decimalFormat.setGroupingUsed(false);

    	// Read parameters from attributes
        val=attrs.getAttributeValue(ANDROID_NS,ATTR_DEFAULT_VALUE);
        defaultValue = (val==null) ? DEFAULT_CURRENT_VALUE : Float.parseFloat(val);
        currentValue=defaultValue;

        val=attrs.getAttributeValue(PREFERENCE_NS,ATTR_MIN_VALUE);
        minValue = (val==null) ? DEFAULT_MIN_VALUE : Float.parseFloat(val);

        val=attrs.getAttributeValue(PREFERENCE_NS,ATTR_MAX_VALUE);
        maxValue = (val==null) ? DEFAULT_MAX_VALUE : Float.parseFloat(val);

        val=attrs.getAttributeValue(PREFERENCE_NS,ATTR_STEP_SIZE);
        stepSize = (val==null) ? DEFAULT_STEP_SIZE : Float.parseFloat(val);
    }
    
    
    @Override
    protected View onCreateView(ViewGroup parent)
    {
    	// Inflate layout
    	LayoutInflater inflater=(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	View view=inflater.inflate(R.layout.seekbar_preference,null);

    	// Setup minimum and maximum text labels
    	((TextView)view.findViewById(R.id.min_value)).setText(decimalFormat.format(minValue));
    	((TextView)view.findViewById(R.id.max_value)).setText(decimalFormat.format(maxValue));

    	// Setup SeekBar
    	seekBar=(SeekBar)view.findViewById(R.id.seek_bar);
        int max=(int)(maxValue/stepSize);
        int min=(int)(minValue/stepSize);
        int val=(int)(currentValue/stepSize);
    	seekBar.setMax(max-min);
    	seekBar.setProgress(val-min);
    	seekBar.setOnSeekBarChangeListener(this);

    	// Setup text label for current value
    	valueText=(TextView)view.findViewById(R.id.current_value);
    	valueText.setText(decimalFormat.format(currentValue));

    	return view;
    }
    
    @Override
    public void onBindView(View view)
    {
       super.onBindView(view);
       updateView(view);
    }
     
    /**
     * Update a SeekBarPreference view with our current state
     * @param view
     */
    protected void updateView(View view)
    {
    	// Setup minimum and maximum text labels
    	((TextView)view.findViewById(R.id.min_value)).setText(decimalFormat.format(minValue));
    	((TextView)view.findViewById(R.id.max_value)).setText(decimalFormat.format(maxValue));

    	// Setup SeekBar
    	seekBar=(SeekBar)view.findViewById(R.id.seek_bar);
        int max=(int)(maxValue/stepSize);
        int min=(int)(minValue/stepSize);
        int val=(int)(currentValue/stepSize);
    	seekBar.setMax(max-min);
    	seekBar.setProgress(val-min);
    	seekBar.setOnSeekBarChangeListener(this);

    	// Setup text label for current value
    	valueText=(TextView)view.findViewById(R.id.current_value);
    	valueText.setText(decimalFormat.format(currentValue));
    }

    @Override
    protected Object onGetDefaultValue(TypedArray ta, int index)
    {
        return ta.getFloat(index, DEFAULT_CURRENT_VALUE);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue,Object defaultValueObj)
    {
        if(restorePersistedValue)
        {
            // Restore existing state
            currentValue=getPersistedFloat(defaultValue);
        }
        else
        {
            // Set default state from the XML attribute
            currentValue=(Float)defaultValueObj;
            persistFloat(currentValue);
        }
    }

    public void onProgressChanged(SeekBar seek,int value,boolean fromTouch)
    {
        Log.d("SeekBarPreference","onProgressChanged()");
        // Update current value
    	float newValue=value*stepSize+minValue;
        // change rejected, revert to the previous value
        if(!callChangeListener(newValue))
        {
            int min=(int)(minValue/stepSize);
            int val=(int)(currentValue/stepSize);
           seekBar.setProgress(val-min);
           return; 
        }

        // change accepted, store it
        currentValue=newValue;

        // Update label with current value
    	valueText.setText(decimalFormat.format(currentValue));
        if(shouldPersist()) {
            persistFloat(currentValue);
        }
    }
    
    public void setProgress(int newValue)
    {
    	currentValue=newValue*stepSize;
        int min=(int)(minValue/stepSize);
        int val=(int)(currentValue/stepSize);
    	seekBar.setProgress(val-min);
        // Update label with current value
    	valueText.setText(decimalFormat.format(currentValue));
        if(shouldPersist()) {
            persistFloat(currentValue);
        }
        notifyChanged();
    }

    public void onStartTrackingTouch(SeekBar seek)
    {
    	// Not used
        //Log.d("SeekBarPreference","onStartTrackingTouch()");
    }

    public void onStopTrackingTouch(SeekBar seek)
    {
//        Log.d("SeekBarPreference", "onStopTrackingTouch");
        notifyChanged();
    }
}