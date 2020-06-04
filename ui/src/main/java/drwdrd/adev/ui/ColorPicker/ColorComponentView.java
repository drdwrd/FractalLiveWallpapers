package drwdrd.adev.ui.ColorPicker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public abstract class ColorComponentView extends ColorView {

    public interface OnColorChangedListener {
        public void onColorChanged(ColorInfo color);
    }

    protected OnColorChangedListener listener=null;

    public ColorComponentView(Context context) {
        super(context);
    }

    public ColorComponentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorComponentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setListener(OnColorChangedListener listener) {
        this.listener=listener;
    }

    public abstract void setColor(ColorInfo color);

    public abstract ColorInfo getColor();
}
