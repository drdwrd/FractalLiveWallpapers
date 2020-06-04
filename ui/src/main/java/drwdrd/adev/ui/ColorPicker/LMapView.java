package drwdrd.adev.ui.ColorPicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class LMapView extends ColorComponentView {

    private float TRACKER_STROKE_WIDTH = 2.0f;
    private float L_TRACKER_WIDTH = 2.0f;

    private RectF lMapRect;
    private Paint lMapPaint=null;
    private Shader lMapShader=null;

    private Paint trackerPaint=null;

    private float hue =1.0f;
    private float saturation =0.0f;
    private float lightness =0.5f;


    public LMapView(Context context) {
        super(context);
        init();
    }

    public LMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LMapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context,attrs,defStyleAttr);
        init();
    }

    @Override
    public void setColor(ColorInfo color) {
        hue=color.getHue();
        saturation=color.getSaturation();
        lightness=color.getLightness();
        lMapShader=null;
        invalidate();
    }

    @Override
    public ColorInfo getColor() {
        return new ColorInfo(hue, saturation, lightness);
    }

    private void init() {

        float displayDensity=getContext().getResources().getDisplayMetrics().density;

        TRACKER_STROKE_WIDTH *= displayDensity;
        L_TRACKER_WIDTH *= displayDensity;

        lMapPaint=new Paint();

        trackerPaint=new Paint();
        trackerPaint.setStyle(Paint.Style.STROKE);
        trackerPaint.setAntiAlias(true);
        trackerPaint.setStrokeWidth(0.5f * TRACKER_STROKE_WIDTH);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if(lMapShader==null) {
            lMapShader=new LinearGradient(lMapRect.left,lMapRect.top,lMapRect.left,lMapRect.bottom,buildLMapGradient(),null,Shader.TileMode.CLAMP);
            lMapPaint.setShader(lMapShader);
        }

        canvas.drawRect(lMapRect,lMapPaint);
        drawBorders(canvas);
        drawLTracker(canvas);
    }

    @Override
    protected  void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w,h,oldw,oldh);
        lMapRect=getClientRect();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x=event.getX();
        float y=event.getY();
        boolean update=false;
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if(lMapRect.contains(x,y)) {
                    lightness = 1.0f - (y - lMapRect.top) / lMapRect.height();
                    update = true;
                }
                break;
        }
        if(update) {
            if(listener!=null) {
                listener.onColorChanged(new ColorInfo(hue, saturation, lightness));
            }
            invalidate();
            return true;
        }
        return super.onTouchEvent(event);
    }

    private void drawLTracker(Canvas canvas) {
        float y=lMapRect.height()+lMapRect.top-lightness*lMapRect.height();
        trackerPaint.setColor(0xff000000);
        canvas.drawRect(lMapRect.left,y-L_TRACKER_WIDTH,lMapRect.right,y+L_TRACKER_WIDTH,trackerPaint);
        trackerPaint.setColor(0xffffffff);
        canvas.drawRect(lMapRect.left,y-0.5f*L_TRACKER_WIDTH,lMapRect.right,y+0.5f*L_TRACKER_WIDTH,trackerPaint);
    }

    private int[] buildLMapGradient() {
        int[] gradient=new int[256];
        for(int i=0;i<256;i++) {
            gradient[i]=ColorInfo.HSLToColor(hue, saturation, 1.0f - (float) i / 256.0f);
        }
        return gradient;
    }
}
