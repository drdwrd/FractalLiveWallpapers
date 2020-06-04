package drwdrd.adev.glwallpaperservice;

import drwdrd.adev.engine.LogSystem;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import android.opengl.GLSurfaceView.EGLContextFactory;





public class GLES2ContextFactory implements EGLContextFactory
{
	private static int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

	public EGLContext createContext(EGL10 egl,EGLDisplay display,EGLConfig eglConfig)
	{
		LogSystem.debug(GLWallpaperService.tag,"creating OpenGL ES 2.0 context");
		GLWallpaperService.checkEglError("Before eglCreateContext", egl);
		int[] attrib_list = { EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE };
		EGLContext context = egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, attrib_list);
		GLWallpaperService.checkEglError("After eglCreateContext", egl);
		return context;
	}

	public void destroyContext(EGL10 egl,EGLDisplay display,EGLContext context)
	{
		egl.eglDestroyContext(display, context);
	}
}

