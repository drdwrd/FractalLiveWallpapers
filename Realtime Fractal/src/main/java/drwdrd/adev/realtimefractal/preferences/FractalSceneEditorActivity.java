package drwdrd.adev.realtimefractal.preferences;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import drwdrd.adev.engine.LogSystem;
import drwdrd.adev.realtimefractal.RealtimeFractalRenderer;

public class FractalSceneEditorActivity extends Activity {

    private GLSurfaceView mSurfaceView=null;
    private RealtimeFractalRenderer mRenderer=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogSystem.debug("FractalSceneEditorActivity", "onCreate()");
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
/*        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        mSurfaceView = new GLSurfaceView(this);
        mSurfaceView.setEGLContextClientVersion(2);
        mSurfaceView.setPreserveEGLContextOnPause(true);
        mRenderer=new RealtimeFractalRenderer(this,true);
        mSurfaceView.setRenderer(mRenderer);
        mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        setContentView(mSurfaceView);

    }

    public Rect getWindowRect() {
        Rect windowRect = new Rect();
        mSurfaceView.getWindowVisibleDisplayFrame(windowRect);
        LogSystem.debug("FractalSceneEditorActivity", "Frame = (" + windowRect.left + ", " + windowRect.top + ", " + windowRect.right + ", " + windowRect.bottom + ")");
        return windowRect;
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
         * The activity must call the GL surface view's
         * onResume() on activity onResume().
         */
        if (mSurfaceView != null) {
            mSurfaceView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        /*
         * The activity must call the GL surface view's
         * onPause() on activity onPause().
         */
        if (mSurfaceView != null) {
            mSurfaceView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mRenderer!=null) {
            mRenderer.onDestroy();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mRenderer.onTouchEvent(event) == true) {
            return true;
        }
        return super.onTouchEvent(event);
    }
}
