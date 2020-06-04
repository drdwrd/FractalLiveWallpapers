package drwdrd.adev.realtimefractal;



import android.content.Context;
import android.net.Uri;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import drwdrd.adev.engine.EngineUtils;
import drwdrd.adev.engine.LogSystem;
import drwdrd.adev.glwallpaperservice.GLES2ContextFactory;
import drwdrd.adev.glwallpaperservice.GLES2MSConfigChooser;
import drwdrd.adev.glwallpaperservice.GLWallpaperService;


public class RealtimeFractalService extends GLWallpaperService
{

    public interface OnFractalSceneChangedListener {

        public void onFractalSceneChanged();
    }

    private static ArrayList<OnFractalSceneChangedListener> onFractalSceneChangedListeners = new ArrayList<OnFractalSceneChangedListener>();

    public static final String tag="RealtimeFractalService";
    private static FractalSettings fractalSettings = null;


    public RealtimeFractalService()
    {
        super();
		EngineUtils.setDebug(false);
//        android.os.Debug.waitForDebugger();
        LogSystem.debug(RealtimeFractalService.tag,"starting service...");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(fractalSettings==null) {
            loadCurrentFractalSettings(this);
        }
    }

    @Override
    public void onDestroy() {
        fractalSettings.saveToXml(this, "current.xml");
        super.onDestroy();
    }

    @Override
    public Engine onCreateEngine()
    {
        return new RealtimeFractalWallpaperEngine(this);
    }

    public static FractalSettings getFractalSettings() { return fractalSettings; }

    public static void loadDefaultFractalSettings(Context context) {
        fractalSettings = new FractalSettings(context);
    }

    public static void loadCurrentFractalSettings(Context context) {
        fractalSettings = new FractalSettings(context, "current.xml");
        for(OnFractalSceneChangedListener listener: onFractalSceneChangedListeners) {
            listener.onFractalSceneChanged();
        }
    }

    public static boolean loadFractalSettings(Context context, Uri uri) {
        InputStream stream = null;
        try {
            stream = context.getContentResolver().openInputStream(uri);
            fractalSettings = new FractalSettings(context, stream);
            for(OnFractalSceneChangedListener listener: onFractalSceneChangedListeners) {
                listener.onFractalSceneChanged();
            }
            return true;
        } catch (IOException io) {
            String err = io.getMessage();
            if (err != null) {
                LogSystem.error(RealtimeFractalService.tag, "Cannot parse xml: " + err);
            }
            io.printStackTrace();
        }
        return false;
    }

    public static boolean loadFractalSettings(Context context, String xml) {
        InputStream stream = new ByteArrayInputStream(xml.getBytes());
        fractalSettings = new FractalSettings(context, stream);
        for(OnFractalSceneChangedListener listener: onFractalSceneChangedListeners) {
            listener.onFractalSceneChanged();
        }
        return true;
    }

    class RealtimeFractalWallpaperEngine extends GLEngine
    {
        private RealtimeFractalRenderer renderer=null;

        public RealtimeFractalWallpaperEngine(GLWallpaperService context)
        {
            super();

            setEGLContextFactory(new GLES2ContextFactory());
            setEGLConfigChooser(new GLES2MSConfigChooser(8,8,8,0,24,0));

            // handle prefs, other initialization
            renderer=new RealtimeFractalRenderer(context,false);
            onFractalSceneChangedListeners.add(renderer);
            setRenderer(renderer);
            setRenderMode(RENDERMODE_CONTINUOUSLY);
        }

        @Override
        public void onDestroy()
        {
            if(isPreview()) {
                fractalSettings.saveToXml(RealtimeFractalService.this, "current.xml");
            }
            onFractalSceneChangedListeners.remove(renderer);
            renderer.onDestroy();
            renderer=null;
            super.onDestroy();
        }
    }
}