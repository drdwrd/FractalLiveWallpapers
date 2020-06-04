package drwdrd.adev.engine;


import android.opengl.GLES20;

public class EngineUtils {
    public static final String tag = "ENGINE";
    private static boolean debugState = false;

    public final static boolean DEBUG() {
        return debugState;
    }

    public final static void setDebug(boolean state) {
        debugState = state;
    }

    public final static void Assert(boolean exp,String msg) {
        if(BuildConfig.DEBUG&&!exp)
            throw new AssertionError(msg);
    }

    public final static void checkGlError(String op)
    {
        int error;
        while((error = GLES20.glGetError())!=GLES20.GL_NO_ERROR)
        {
            LogSystem.error(EngineUtils.tag,String.format("%s GL error: %d",op,error));
        }
    }

}