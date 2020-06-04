package drwdrd.adev.ui.ColorPicker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ColorPickerPreference extends Preference implements Preference.OnPreferenceClickListener {

//    private ColorPickerDialog dialog=null;
    private int colorValue=Color.BLACK;
    private float displayDensity=1f;
    private View view;

    public ColorPickerPreference(Context context) {
        super(context);
        init();
    }

    public ColorPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColorPickerPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getColor(index,Color.BLACK);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        if(restoreValue)
            setColor(getPersistedInt(Color.BLACK));
        else
            setColor((Integer)defaultValue);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        this.view=view;
        setPreviewColor();
    }

    private void setPreviewColor() {
        if(view == null) return;
        ImageView imageView = new ImageView(getContext());
        LinearLayout widgetFrameView = ((LinearLayout)view.findViewById(android.R.id.widget_frame));
        if(widgetFrameView == null) return;
        widgetFrameView.setVisibility(View.VISIBLE);
        widgetFrameView.setPadding(widgetFrameView.getPaddingLeft(),widgetFrameView.getPaddingTop(),(int)(displayDensity*8f),widgetFrameView.getPaddingBottom());
        // remove already create preview image
        int count = widgetFrameView.getChildCount();
        if (count > 0) {
            widgetFrameView.removeViews(0, count);
        }
        widgetFrameView.addView(imageView);
        widgetFrameView.setMinimumWidth(0);
        imageView.setImageBitmap(getPreviewBitmap());
        notifyChanged();
    }

    private Bitmap getPreviewBitmap() {
        int d = (int) (displayDensity* 31); //30dip
        int color = colorValue;
        Bitmap bm = Bitmap.createBitmap(d, d, Bitmap.Config.ARGB_8888);
        int w = bm.getWidth();
        int h = bm.getHeight();
        int c = color;
        for (int i = 0; i < w; i++) {
            for (int j = i; j < h; j++) {
                c = (i <= 1 || j <= 1 || i >= w-2 || j >= h-2) ? Color.GRAY : color;
                bm.setPixel(i, j, c);
                if (i != j) {
                    bm.setPixel(j, i, c);
                }
            }
        }
        return bm;
    }

    private void init() {
       displayDensity=getContext().getResources().getDisplayMetrics().density;
       setOnPreferenceClickListener(this);
    }

    public boolean onPreferenceClick(Preference preference) {
        ColorPickerDialog dialog=new ColorPickerDialog(getContext(),colorValue);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                ColorPickerDialog colorPickerDialog=(ColorPickerDialog)dialogInterface;
                setColor(colorPickerDialog.getColor());
            }
        });
        dialog.show();
        return true;
    }

    public void setColor(int color) {
        if(shouldPersist()) {
            persistInt(color);
        }
        colorValue=color;
        setPreviewColor();
    }
}
