package drwdrd.adev.ui.PaletteEditor;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;


import drwdrd.adev.ui.ColorPicker.ColorView;

public class PaletteView extends ColorView {

    private Bitmap paletteBitmap=null;
    private RectF paletteViewRect=null;
    private PaletteInfo paletteInfo=new PaletteInfo(6);

    private boolean orientationLandscape=false;

    public PaletteView(Context context) {
        super(context);
        init();
    }

    public PaletteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PaletteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context,attrs,defStyleAttr);
        init();
    }

    public void setPalette(PaletteInfo paletteInfo) {
        this.paletteInfo=new PaletteInfo(paletteInfo);
        paletteBitmap=paletteInfo.buildPaletteBitmap(128, 256, false);
        invalidate();
    }

    public void setColor(int index,int color) {
        paletteInfo.setColor(index,color);
        paletteBitmap=paletteInfo.buildPaletteBitmap(128, 256, false);
        invalidate();
    }

    public void enableColor(int index,boolean enabled) {
        paletteInfo.enableColor(index,enabled);
        paletteBitmap=paletteInfo.buildPaletteBitmap(128, 256, false);
        invalidate();
    }

    public PaletteInfo getCurrentPalette() { return paletteInfo; }

    private void init() {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(paletteBitmap, null, paletteViewRect, null);
        drawBorders(canvas);
    }


    @Override
    protected  void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w,h,oldw,oldh);
        orientationLandscape = (w > h);
        paletteBitmap=paletteInfo.buildPaletteBitmap(128, 256, false);
        paletteViewRect=getClientRect();
    }
}
