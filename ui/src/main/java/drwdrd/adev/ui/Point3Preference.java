package drwdrd.adev.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.renderscript.Float2;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class Point3Preference extends DialogPreferenceWithButton {

    private View dialogView=null;
    // Namespaces to read attributes
    private static final String PREFERENCE_NS="http://schemas.android.com/apk/res-auto";

    // Attribute names
    private static final String ATTR_DEFAULT_VALUE_X="defaultValueX";
    private static final String ATTR_DEFAULT_VALUE_Y="defaultValueY";
    private static final String ATTR_DEFAULT_VALUE_Z="defaultValueZ";

    private static final String ATTR_LABEL_X="labelX";
    private static final String ATTR_LABEL_Y="labelY";
    private static final String ATTR_LABEL_Z="labelZ";

    private static final float DEFAULT_VALUE_X = 0.0f;
    private static final float DEFAULT_VALUE_Y = 0.0f;
    private static final float DEFAULT_VALUE_Z = 0.0f;

    private static final String DEFAULT_LABEL_X = "X";
    private static final String DEFAULT_LABEL_Y = "Y";
    private static final String DEFAULT_LABEL_Z = "Z";

    private float[] currentValue=new float[3];
    private float[] defaultValue=new float[]{DEFAULT_VALUE_X,DEFAULT_VALUE_Y,DEFAULT_VALUE_Z};
    private String labelX;
    private String labelY;
    private String labelZ;

    public Point3Preference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setDialogLayoutResource(R.layout.point3_preference_dialog);

        // Read parameters from attributes
        String val=attributeSet.getAttributeValue(PREFERENCE_NS,ATTR_DEFAULT_VALUE_X);
        float px = (val==null) ? DEFAULT_VALUE_X : Float.parseFloat(val);
        val=attributeSet.getAttributeValue(PREFERENCE_NS,ATTR_DEFAULT_VALUE_Y);
        float py = (val==null) ? DEFAULT_VALUE_Y : Float.parseFloat(val);
        val=attributeSet.getAttributeValue(PREFERENCE_NS,ATTR_DEFAULT_VALUE_Z);
        float pz = (val==null) ? DEFAULT_VALUE_Z : Float.parseFloat(val);

        defaultValue=new float[]{px,py,pz};
        currentValue=defaultValue;

        val=attributeSet.getAttributeValue(PREFERENCE_NS,ATTR_LABEL_X);
        labelX =  (val==null) ? DEFAULT_LABEL_X : val;

        val=attributeSet.getAttributeValue(PREFERENCE_NS,ATTR_LABEL_Y);
        labelY =  (val==null) ? DEFAULT_LABEL_Y : val;

        val=attributeSet.getAttributeValue(PREFERENCE_NS,ATTR_LABEL_Z);
        labelZ =  (val==null) ? DEFAULT_LABEL_Z : val;
    }

    @Override
    public void onBindView(View view) {
        setSummary(currentValue[0], currentValue[1], currentValue[2]);
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
        ((TextView)view.findViewById(R.id.labelZ)).setText(labelZ + " = ");
        ((EditText)view.findViewById(R.id.pointX)).setText(Float.toString(currentValue[0]));
        ((EditText)view.findViewById(R.id.pointY)).setText(Float.toString(currentValue[1]));
        ((EditText)view.findViewById(R.id.pointZ)).setText(Float.toString(currentValue[2]));
        super.onBindDialogView(view);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if(positiveResult) {
            float px=Float.parseFloat(((EditText)dialogView.findViewById(R.id.pointX)).getText().toString());
            float py=Float.parseFloat(((EditText)dialogView.findViewById(R.id.pointY)).getText().toString());
            float pz=Float.parseFloat(((EditText)dialogView.findViewById(R.id.pointZ)).getText().toString());
            currentValue=new float[]{px,py,pz};
            setSummary(px, py, pz);
            persistValue(currentValue);
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
            currentValue=getPersistedValue(defaultValue);
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
            persistValue(currentValue);
        }
    }

    private void setSummary(float x,float y,float z) {
        setSummary("[" + labelX + " = " + x + ", " + labelY + " = " + y + "," + labelZ + "=" + z +"]");
    }

    private void persistValue(float[] v) {
        if(shouldPersist()) {
            String value = stringify(v[0],v[1],v[2]);
            persistString(value);
        }
    }

    private float[] getPersistedValue(float[] defaultValue) {
        String value = getPersistedString("");
        if (value.isEmpty()) {
            return defaultValue;
        }
        //parse string
        Log.d("getPersistedValue=",value);
        return parse(value);
    }

    public static String stringify(float[] v) {
        return stringify(v[0],v[1],v[2]);
    }

    public static String stringify(float x,float y,float z) {
        return Float.toString(x) + " " + Float.toString(y) + " " + Float.toString(z);
    }

    public static float[] parse(String str) {
        String[] sv = str.split(" ");
        return new float[] {Float.parseFloat(sv[0]), Float.parseFloat(sv[1]), Float.parseFloat(sv[2])};
    }
}
