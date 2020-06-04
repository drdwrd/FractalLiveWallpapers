package drwdrd.adev.realtimefractal;

import android.content.Context;

public interface RenderPath {

    public boolean isInitialized();
    public void onCreate(Context context);
    public void onDestroy();
    public void onInitialize();
    public void onRelease();
    public void onRender(FractalScene.SceneNode scene,float time);
    public void setViewport(int width,int height);
}
