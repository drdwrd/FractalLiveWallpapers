package drwdrd.adev.ui.PaletteEditor;


import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.preference.Preference;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONArray;

public class PalettePreference extends Preference implements Preference.OnPreferenceClickListener {

    private float displayDensity=1f;
    private View view;
    private PaletteInfo currentPalette=new PaletteInfo(6);
    private Bitmap paletteBitmap = null;

    public PalettePreference(Context context) {
        super(context);
        init();
    }

    public PalettePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PalettePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        if(restoreValue)
            setPalette(PaletteInfo.parsePalette(getPersistedString("")));
        else
            setPalette(PaletteInfo.parsePalette((String)defaultValue));
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        this.view=view;
        setPalettePreview();
    }

    private void init() {
        displayDensity=getContext().getResources().getDisplayMetrics().density;
        setOnPreferenceClickListener(this);
    }

    public boolean onPreferenceClick(Preference preference) {
        PaletteEditor paletteEditor=new PaletteEditor(getContext(),currentPalette);
        paletteEditor.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                PaletteEditor paletteEditor = (PaletteEditor) dialogInterface;
                setPalette(paletteEditor.getPalette());
            }
        });
        paletteEditor.show();
        return true;
    }

    private void setPalettePreview() {
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
        imageView.setImageBitmap(paletteBitmap);
        notifyChanged();
    }

    public void setPalette(PaletteInfo paletteInfo) {
        if(shouldPersist()) {
            persistString(PaletteInfo.toString(paletteInfo));
        }
        currentPalette=paletteInfo;
        paletteBitmap = currentPalette.buildPaletteBitmap((int) (displayDensity * 200f), (int) (displayDensity * 40f), true);
        setPalettePreview();
    }
}
