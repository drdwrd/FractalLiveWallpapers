package drwdrd.adev.ui.ColorPicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class HSMapView extends ColorComponentView {

    private float TRACKER_STROKE_WIDTH = 2.0f;
    private float HS_TRACKER_RADIUS = 3.0f;

    private Bitmap hsMapBitmap=null;
    private RectF hsMapRect=null;
    private Paint trackerPaint=null;

    private float hue=1.0f;
    private float saturation=0.0f;
    private float lightness=0.5f;

    private boolean orientationLandscape=false;

    public HSMapView(Context context) {
        super(context);
        init();
    }

    public HSMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HSMapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context,attrs,defStyleAttr);
        init();
    }

    private void init() {

        float displayDensity=getContext().getResources().getDisplayMetrics().density;

        TRACKER_STROKE_WIDTH *= displayDensity;
        HS_TRACKER_RADIUS *= displayDensity;

        trackerPaint=new Paint();
        trackerPaint.setStyle(Paint.Style.STROKE);
        trackerPaint.setAntiAlias(true);
        trackerPaint.setColor(0xff000000);
        trackerPaint.setStrokeWidth(TRACKER_STROKE_WIDTH);
    }

    @Override
    public void setColor(ColorInfo color) {
        hue=color.getHue();
        saturation=color.getSaturation();
        lightness=color.getLightness();
    }

    @Override
    public ColorInfo getColor() {
        return new ColorInfo(hue, saturation, lightness);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(hsMapBitmap, null, hsMapRect, null);
        drawHSTracker(canvas);
        drawBorders(canvas);
    }

    @Override
    protected  void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w,h,oldw,oldh);
        orientationLandscape = (w > h);
        hsMapBitmap=buildHSMapBitmap(256,360,orientationLandscape);
        hsMapRect=getClientRect();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x=event.getX();
        float y=event.getY();
        boolean update=false;

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if(hsMapRect.contains(x,y)) {
                    if (orientationLandscape) {
                        saturation = 1.0f - (y - hsMapRect.top) / hsMapRect.height();
                        hue = (x - hsMapRect.left) / hsMapRect.width();
                    } else {
                        hue = (y - hsMapRect.top) / hsMapRect.height();
                        saturation = 1.0f - (x - hsMapRect.left) / hsMapRect.width();
                    }
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

    private void drawHSTracker(Canvas canvas) {
        float x,y;
        if(orientationLandscape) {
            x = hsMapRect.left + hue * hsMapRect.width();
            y = hsMapRect.top + hsMapRect.height() - saturation * hsMapRect.height();
        } else {
            x = hsMapRect.left + hsMapRect.width() - saturation * hsMapRect.width();
            y = hsMapRect.top + hue * hsMapRect.height();
        }
        canvas.drawCircle(x,y,HS_TRACKER_RADIUS,trackerPaint);
    }

    private Bitmap buildHSMapBitmap(int width,int height,boolean orientationLandscape) {
        if(orientationLandscape) {
            Bitmap bitmap=Bitmap.createBitmap(height,width,Bitmap.Config.ARGB_8888);
            for (int i = 0; i < bitmap.getWidth(); i++) {
                for (int j = 0; j < bitmap.getHeight(); j++) {
                    float s = 1.0f - (float) j / (float) bitmap.getHeight();
                    float h = (float) i / (float) bitmap.getWidth();
                    bitmap.setPixel(i, j, ColorInfo.HSLToColor(h, s, 0.5f));
                }
            }
            return bitmap;
        } else {
            Bitmap bitmap=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
            for (int i = 0; i < bitmap.getWidth(); i++) {
                for (int j = 0; j < bitmap.getHeight(); j++) {
                    float s = 1.0f - (float) i / (float) bitmap.getWidth();
                    float h = (float) j / (float) bitmap.getHeight();
                    bitmap.setPixel(i, j, ColorInfo.HSLToColor(h, s, 0.5f));
                }
            }
            return bitmap;
        }
    }
}
