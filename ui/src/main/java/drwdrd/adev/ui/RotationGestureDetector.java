package drwdrd.adev.ui;


//http://stackoverflow.com/questions/10682019/android-two-finger-rotation thx StackOverflow ;)

import android.view.MotionEvent;

public class RotationGestureDetector {
    private static final int INVALID_POINTER_ID = -1;
    private float fX, fY, sX, sY;
    private int pointerId1 = INVALID_POINTER_ID;
    private int pointerId2 = INVALID_POINTER_ID;
    private float rotationAngle = 0f;
    private float deltaRotationAngle = 0f;
    private boolean inProgress=false;

    private OnRotationGestureListener listener;

    public float getAngle() {
        return rotationAngle;
    }

    public float getDeltaRotationAngle() {
        return deltaRotationAngle;
    }

    public boolean isInProgress() { return inProgress; }

    public RotationGestureDetector(OnRotationGestureListener listener){
        this.listener = listener;
    }

    public boolean onTouchEvent(MotionEvent event){
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                pointerId1 = event.getPointerId(event.getActionIndex());
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                inProgress = true;
                pointerId2 = event.getPointerId(event.getActionIndex());
                sX = event.getX(event.findPointerIndex(pointerId1));
                sY = event.getY(event.findPointerIndex(pointerId1));
                fX = event.getX(event.findPointerIndex(pointerId2));
                fY = event.getY(event.findPointerIndex(pointerId2));
                break;
            case MotionEvent.ACTION_MOVE:
                if(pointerId1 != INVALID_POINTER_ID && pointerId2 != INVALID_POINTER_ID){
                    float nfX, nfY, nsX, nsY;
                    nsX = event.getX(event.findPointerIndex(pointerId1));
                    nsY = event.getY(event.findPointerIndex(pointerId1));
                    nfX = event.getX(event.findPointerIndex(pointerId2));
                    nfY = event.getY(event.findPointerIndex(pointerId2));

                    float currentAngle =  angleBetweenLines(fX, fY, sX, sY, nfX, nfY, nsX, nsY);

                    deltaRotationAngle = currentAngle -rotationAngle;

                    rotationAngle = currentAngle;

                    if (listener != null) {
                        listener.OnRotation(this);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                inProgress = false;
                pointerId1 = INVALID_POINTER_ID;
                rotationAngle = 0f;
                deltaRotationAngle = 0f;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                inProgress = false;
                pointerId2 = INVALID_POINTER_ID;
                rotationAngle = 0f;
                deltaRotationAngle = 0f;
                break;
        }
        return true;
    }

    private float angleBetweenLines (float fX, float fY, float sX, float sY, float nfX, float nfY, float nsX, float nsY)
    {
        float angle1 = (float) Math.atan2( (fY - sY), (fX - sX) );
        float angle2 = (float) Math.atan2( (nfY - nsY), (nfX - nsX) );

        float angle = ((float)Math.toDegrees(angle1 - angle2)) % 360;
        if (angle < -180.f) angle += 360.0f;
        if (angle > 180.f) angle -= 360.0f;
        return angle/180.0f*(float)Math.PI;
    }

    public static interface OnRotationGestureListener {
        public void OnRotation(RotationGestureDetector rotationDetector);
    }
}