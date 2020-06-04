package drwdrd.adev.realtimefractal;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.preference.PreferenceManager;
import android.view.MotionEvent;

import drwdrd.adev.engine.EngineUtils;
import drwdrd.adev.engine.FrameCounter;
import drwdrd.adev.engine.LogSystem;
import drwdrd.adev.engine.GLCaps;
import drwdrd.adev.engine.TimeCounter;
import drwdrd.adev.glwallpaperservice.GLWallpaperService;

public class RealtimeFractalRenderer implements GLSurfaceView.Renderer, SharedPreferences.OnSharedPreferenceChangeListener, RealtimeFractalService.OnFractalSceneChangedListener
{

    private Context context=null;
    private RenderPath renderPath=null;
    private RenderPath oldRenderPath=null;
    private UIRenderer uiRenderer=null;

    protected FractalScene currentScene=null;

    protected TimeCounter timer=null;
    protected FrameCounter frameCounter=new FrameCounter(1000.0);


    public RealtimeFractalRenderer(Context context,boolean editorMode)
    {
        this.context=context;

        currentScene = RealtimeFractalService.getFractalSettings().fractalScene;

        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(context);
        prefs.registerOnSharedPreferenceChangeListener(this);

        renderPath=createRenderPath(prefs);
        if(editorMode==true) {
            uiRenderer = new UIRenderer();
            uiRenderer.onCreate(this, context);
        }

        timer=new TimeCounter(RealtimeFractalService.getFractalSettings().timeScale,2.0*Math.PI);

        LogSystem.debug(RealtimeFractalService.tag,"RealtimeFractalRenderer.RealtimeFractalRenderer()...");
    }


    public void onRelease()
    {
        //free all resources
        if(oldRenderPath!=null) {
            oldRenderPath.onRelease();
            oldRenderPath.onDestroy();
            oldRenderPath=null;
        }
        renderPath.onRelease();
        if(uiRenderer!=null) {
            uiRenderer.onRelease();
        }
        LogSystem.debug(RealtimeFractalService.tag,"RealtimeFractalRenderer.onRelease()...");
    }

    public void onDestroy() {
        if(oldRenderPath!=null) {
            oldRenderPath.onDestroy();
        }
        renderPath.onDestroy();
        if(uiRenderer!=null) {
            uiRenderer.onDestroy();
        }
    }

    public void onSurfaceCreated(GL10 gl,EGLConfig config)
    {
        if(EngineUtils.DEBUG())
        {
            LogSystem.debug(RealtimeFractalService.tag,GLCaps.getInstance().toString());
        }

        if(GLCaps.getInstance().isExtensionSuported("GL_OES_FRAGMENT_PRECISION_HIGH")) {
            LogSystem.debug(RealtimeFractalService.tag,"High precision shaders avalaible!");
        }

        onRelease();

        renderPath.onInitialize();
        if(uiRenderer!=null) {
            uiRenderer.onInitialize();
        }

        EngineUtils.checkGlError("RealtimeFractalRenderer.onSurfaceCreated()");
        LogSystem.debug(RealtimeFractalService.tag, "RealtimeFractalRenderer.onSurfaceCreated()...");

        timer.setTimeScale(RealtimeFractalService.getFractalSettings().timeScale);
        timer.start();
        frameCounter.start();
    }

    public void onDrawFrame(GL10 gl)
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        if(renderPath.isInitialized()==false) {
            LogSystem.debug(RealtimeFractalService.tag, "Renderer not initialized!");
            return;
        }

        FractalSettings fractalSettings=RealtimeFractalService.getFractalSettings();

//        float currentTime=1.0f-(float)Math.abs(timer.tick()-1.0);
        float currentTime=0.5f*(1.0f-(float)Math.cos(timer.tick()));

        FractalScene.SceneNode scene = currentScene.playScene(currentTime);

        if(fractalSettings.scaleTimeWithZoom) {
            timer.setTimeScale(fractalSettings.timeScale*Math.pow(scene.scale, fractalSettings.timerScalingFactor));
        }

        renderPath.onRender(scene,currentTime);

        if(uiRenderer!=null) {
            uiRenderer.onRender(currentTime);
        }

        EngineUtils.checkGlError("RealtimeFractalRenderer.onDrawFrame()");

        frameCounter.tick();
    }

    public void startScene(FractalScene scene) {
        timer.reset();
        currentScene = scene;
    }

    public void onSurfaceChanged(GL10 gl,int width,int height)
    {
        renderPath.setViewport(width,height);
        if(uiRenderer!=null) {
            uiRenderer.setViewport(width, height);
        }
        LogSystem.debug(RealtimeFractalService.tag,"RealtimeFractalRenderer.onSurfaceChanged()...");
    }

    protected RenderPath createRenderPath(SharedPreferences prefs) {
        RenderPath renderPath;
        if(prefs.getBoolean("isFramebufferOn",false)==true) {
            renderPath=new FramebufferRenderPath();
        }
        else {
            renderPath=new DefaultRenderPath();
        }
        renderPath.onCreate(context);
        return  renderPath;
    }


    public void onSharedPreferenceChanged(SharedPreferences prefs,String key)
    {
        if(key.equals("isFramebufferOn")==true) {
            oldRenderPath=renderPath;
            renderPath=createRenderPath(prefs);
        }
        else if(key.equals("framebufferSize")) {
            oldRenderPath=renderPath;
            if(prefs.getBoolean("isFramebufferOn",false)==true) {
                renderPath=new FramebufferRenderPath();
                renderPath.onCreate(context);
            }
        }
    }

    public void onFractalSceneChanged() {
        LogSystem.debug(RealtimeFractalService.tag, "onFractalSceneChanged()");
        startScene(RealtimeFractalService.getFractalSettings().fractalScene);
    }

    public FrameCounter getFrameCounter() {
        return frameCounter;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if(uiRenderer!=null) {
            return uiRenderer.onTouchEvent(event);
        }
        return false;
    }
}