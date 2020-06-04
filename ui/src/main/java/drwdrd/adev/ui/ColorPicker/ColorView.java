package drwdrd.adev.ui.ColorPicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ColorView extends View {

    private float BORDER_WIDTH = 1.5f;
    private int previewColor = Color.BLACK;
    private Paint borderPaint=null;
    private Paint colorPaint=null;
    private RectF borderRect=null;
    private RectF clientRect=null;

    public ColorView(Context context) {
        super(context);
        init();
    }

    public ColorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setPreviewColor(int color) {
        previewColor=color;
        colorPaint.setColor(previewColor);
        invalidate();
    }

    private void init() {

        float displayDensity=getContext().getResources().getDisplayMetrics().density;

        BORDER_WIDTH *= displayDensity;

        borderPaint=new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setAntiAlias(true);
        borderPaint.setStrokeWidth(BORDER_WIDTH);

        colorPaint=new Paint();
        colorPaint.setStyle(Paint.Style.FILL);
        colorPaint.setColor(previewColor);

    }

    protected RectF getClientRect() {
        return clientRect;
    }

    protected void drawBorders(Canvas canvas) {
        borderPaint.setColor(Color.WHITE);
        canvas.drawRect(borderRect.left + BORDER_WIDTH, borderRect.top + BORDER_WIDTH, borderRect.right - BORDER_WIDTH, borderRect.bottom - BORDER_WIDTH, borderPaint);
        borderPaint.setColor(Color.BLACK);
        canvas.drawRect(borderRect, borderPaint);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(clientRect,colorPaint);
        drawBorders(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(width,height);
    }

    @Override
    protected  void onSizeChanged(int w, int h, int oldw, int oldh) {
        borderRect=new RectF(0,0,w,h);
        clientRect=new RectF(BORDER_WIDTH,BORDER_WIDTH,w-BORDER_WIDTH,h-BORDER_WIDTH);
    }

}
