package drwdrd.adev.livefractalwallpaper;


import android.view.SurfaceHolder;
import drwdrd.adev.engine.LogSystem;
import drwdrd.adev.glwallpaperservice.GLES2ContextFactory;
import drwdrd.adev.glwallpaperservice.GLES2ConfigChooser;
import drwdrd.adev.glwallpaperservice.GLWallpaperService;


public class LiveFractalWallpaperService extends GLWallpaperService
{
	
	public static final String tag="LiveFractalWallpaperService";

	public LiveFractalWallpaperService()
    {
		super();
//		EngineUtils.setDebug(true);
        LogSystem.debug(LiveFractalWallpaperService.tag,"starting service...");
    }

    public Engine onCreateEngine()
    {
    	LiveFractalWallpaperEngine engine=new LiveFractalWallpaperEngine(this);
        return engine;
    }

    class LiveFractalWallpaperEngine extends GLEngine
    {
         public LiveFractalWallpaperEngine(GLWallpaperService context)
         {
            super();
            
        	setEGLContextFactory(new GLES2ContextFactory());

        	setEGLConfigChooser(new GLES2ConfigChooser(5,6,5,0,16,0));

            // handle prefs, other initialization
            setRenderer(new ColorCyclingFractalRenderer(context));
            setRenderMode(RENDERMODE_CONTINUOUSLY);
         }
     }
}