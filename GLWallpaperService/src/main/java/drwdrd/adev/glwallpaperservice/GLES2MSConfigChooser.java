package drwdrd.adev.glwallpaperservice;


import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;


public class GLES2MSConfigChooser extends GLES2ConfigChooser {

    public GLES2MSConfigChooser(int r, int g, int b, int a, int depth, int stencil)
    {
        super(r,g,b,a,depth,stencil);
    }

    public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display)
    {
        boolean success=false;
        int[] num_config = new int[1];

        //first try to find multisampled configs
        int[] configAttribs = {
                EGL10.EGL_RED_SIZE, 5,
                EGL10.EGL_GREEN_SIZE, 6,
                EGL10.EGL_BLUE_SIZE, 5,
                EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
                EGL10.EGL_SAMPLE_BUFFERS, 1,
                EGL10.EGL_SAMPLES, 2,
                EGL10.EGL_NONE
        };


        egl.eglChooseConfig(display, configAttribs, null, 0, num_config);

        int numConfigs = num_config[0];

        if(numConfigs>0)
        {
            success=true;
        }

        if(!success) {
            // No normal multisampling config was found. Try to create a
            // converage multisampling configuration, for the nVidia Tegra2.

            final int EGL_COVERAGE_BUFFERS_NV = 0x30E0;
            final int EGL_COVERAGE_SAMPLES_NV = 0x30E1;

            configAttribs = new int[]{
                    EGL10.EGL_RED_SIZE, 5,
                    EGL10.EGL_GREEN_SIZE, 6,
                    EGL10.EGL_BLUE_SIZE, 5,
                    EGL10.EGL_DEPTH_SIZE, 16,
                    EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
                    EGL_COVERAGE_BUFFERS_NV, 1 /* true */,
                    EGL_COVERAGE_SAMPLES_NV, 2,  // always 5 in practice on tegra 2
                    EGL10.EGL_NONE
            };

            egl.eglChooseConfig(display, configAttribs, null, 0, num_config);

            numConfigs = num_config[0];

            if(numConfigs>0)
            {
                success=true;
            }

        }

        //try without multisampling
        if(!success) {

            configAttribs = new int[]{
                    EGL10.EGL_RED_SIZE, 5,
                    EGL10.EGL_GREEN_SIZE, 6,
                    EGL10.EGL_BLUE_SIZE, 5,
                    EGL10.EGL_DEPTH_SIZE, 16,
                    EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
                    EGL10.EGL_NONE
            };

            egl.eglChooseConfig(display, configAttribs, null, 0, num_config);

            numConfigs = num_config[0];

            if(numConfigs>0)
            {
                success=true;
            }

        }

        if(!success) {
            throw new IllegalArgumentException("No configs match configSpec");
        }

		/*
		 * Allocate then read the array of minimally matching EGL configs
		 */
        EGLConfig[] configs = new EGLConfig[numConfigs];
        egl.eglChooseConfig(display, configAttribs, configs, numConfigs, num_config);
/*
        if (EngineUtils.DEBUG())
        {
            printConfigs(egl, display, configs);
        }
*/
        return chooseConfig(egl, display, configs);
    }

    public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display, EGLConfig[] configs)
    {
        for (EGLConfig config : configs)
        {
            int d = findConfigAttrib(egl, display, config, EGL10.EGL_DEPTH_SIZE, 0);
            int s = findConfigAttrib(egl, display, config, EGL10.EGL_STENCIL_SIZE, 0);

            // We need at least mDepthSize and mStencilSize bits
            if (d < mDepthSize || s < mStencilSize)
            {
                continue;
            }

            // We want an *exact* match for red/green/blue/alpha
            int r = findConfigAttrib(egl, display, config, EGL10.EGL_RED_SIZE, 0);
            int g = findConfigAttrib(egl, display, config, EGL10.EGL_GREEN_SIZE, 0);
            int b = findConfigAttrib(egl, display, config, EGL10.EGL_BLUE_SIZE, 0);
            int a = findConfigAttrib(egl, display, config, EGL10.EGL_ALPHA_SIZE, 0);

            if (r >= mRedSize && g >= mGreenSize && b >= mBlueSize && a >= mAlphaSize)
            {
                printConfig(egl,display,config);
                return config;
            }
        }
        return null;
    }
}
