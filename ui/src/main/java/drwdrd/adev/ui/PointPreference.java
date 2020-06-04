package drwdrd.adev.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class PointPreference extends DialogPreferenceWithButton {

    private View dialogView=null;
    // Namespaces to read attributes
    private static final String PREFERENCE_NS="http://schemas.android.com/apk/res-auto";

    // Attribute names
    private static final String ATTR_DEFAULT_VALUE_X="defaultValueX";
    private static final String ATTR_DEFAULT_VALUE_Y="defaultValueY";
    private static final String ATTR_LABEL_X="labelX";
    private static final String ATTR_LABEL_Y="labelY";

    private static final float DEFAULT_VALUE_X = 0.0f;
    private static final float DEFAULT_VALUE_Y = 0.0f;
    private static final String DEFAULT_LABEL_X = "X";
    private static final String DEFAULT_LABEL_Y = "Y";

    private PointF currentValue;
    private PointF defaultValue=new PointF(DEFAULT_VALUE_X,DEFAULT_VALUE_Y);
    private String labelX;
    private String labelY;

    public PointPreference(Context context,AttributeSet attributeSet) {
        super(context,attributeSet);
        setDialogLayoutResource(R.layout.point_preference_dialog);

        // Read parameters from attributes
        String val=attributeSet.getAttributeValue(PREFERENCE_NS,ATTR_DEFAULT_VALUE_X);
        float px = (val==null) ? DEFAULT_VALUE_X : Float.parseFloat(val);
        val=attributeSet.getAttributeValue(PREFERENCE_NS,ATTR_DEFAULT_VALUE_Y);
        float py = (val==null) ? DEFAULT_VALUE_Y : Float.parseFloat(val);
        defaultValue=new PointF(px,py);
        currentValue=defaultValue;

        val=attributeSet.getAttributeValue(PREFERENCE_NS,ATTR_LABEL_X);
        labelX =  (val==null) ? DEFAULT_LABEL_X : val;

        val=attributeSet.getAttributeValue(PREFERENCE_NS,ATTR_LABEL_Y);
        labelY =  (val==null) ? DEFAULT_LABEL_Y : val;
    }

    @Override
    public void onBindView(View view) {
        setSummary(currentValue.x, currentValue.y);
        super.onBindView(view);
    }

    @Override
    public void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        builder.setTitle(getTitle());
        super.onPrepareDialogBuilder(builder);
    }

    @Override
    public void onBindDialogView(View view) {
        this.dialogView=view;
        ((TextView)view.findViewById(R.id.labelX)).setText(labelX + " = ");
        ((TextView)view.findViewById(R.id.labelY)).setText(labelY + " = ");
        ((EditText)view.findViewById(R.id.pointX)).setText(Float.toString(currentValue.x));
        ((EditText)view.findViewById(R.id.pointY)).setText(Float.toString(currentValue.y));
        super.onBindDialogView(view);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if(positiveResult) {
            float px=Float.parseFloat(((EditText)dialogView.findViewById(R.id.pointX)).getText().toString());
            float py=Float.parseFloat(((EditText)dialogView.findViewById(R.id.pointY)).getText().toString());
            currentValue=new PointF(px,py);
            setSummary(px, py);
            persistPoint(currentValue);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray ta, int index)
    {
        return ta.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue,Object defaultValueObj)
    {
        if(restorePersistedValue)
        {
            // Restore existing state
            currentValue=getPersistedPoint(defaultValue);
        }
        else
        {
            // Set default state from the XML attribute
            String str=(String)defaultValueObj;
            if(str.isEmpty()) {
                currentValue=defaultValue;
            } else {
                currentValue = parse(str);
            }
            persistPoint(currentValue);
        }
    }

    private void setSummary(float x,float y) {
        setSummary("[" + labelX + " = " + x + ", "+ labelY + " = " + y + "]");
    }

    private void persistPoint(PointF pt) {
        if(shouldPersist()) {
            String value = stringify(pt.x,pt.y);
            persistString(value);
        }
    }

    private PointF getPersistedPoint(PointF defaultValue) {
        String value = getPersistedString("");
        if (value.isEmpty()) {
            return defaultValue;
        }
        //parse string
        return parse(value);
    }

    public static String stringify(PointF pt) {
        return stringify(pt.x,pt.y);
    }

    public static String stringify(float x,float y) {
        return Float.toString(x) + " " + Float.toString(y);
    }

    public static PointF parse(String str) {
        String[] sv = str.split(" ");
        return new PointF(Float.parseFloat(sv[0]), Float.parseFloat(sv[1]));
    }
}
