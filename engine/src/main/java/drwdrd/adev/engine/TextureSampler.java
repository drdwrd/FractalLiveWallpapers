package drwdrd.adev.engine;

import android.opengl.GLES20;

import java.lang.ref.WeakReference;


public class TextureSampler {

    public enum WrapFunc {
        ClampToEdge(GLES20.GL_CLAMP_TO_EDGE),
        Repeat(GLES20.GL_REPEAT),
        MirroredRepeat(GLES20.GL_MIRRORED_REPEAT);


        private WrapFunc(int wrapFunc) {
            this.wrapFunc = wrapFunc;
        }

        private int wrapFunc;
    }

    public enum FilterFunc {
        Nearest(GLES20.GL_NEAREST),
        Linear(GLES20.GL_LINEAR),
        NearestMipmapNearest(GLES20.GL_NEAREST_MIPMAP_NEAREST),
        NearestMipmapLinear(GLES20.GL_NEAREST_MIPMAP_LINEAR),
        LinearMipmapNearest(GLES20.GL_LINEAR_MIPMAP_NEAREST),
        LinearMipmapLinear(GLES20.GL_LINEAR_MIPMAP_LINEAR);

        private FilterFunc(int filterFunc) {
            this.filterFunc = filterFunc;
        }

        private int filterFunc;
    }


    public TextureSampler(GLTexture.Target target) {
        this.target=target;
    }

    public void delete() {

    }

    public void bind() {
        GLES20.glTexParameteri(target.toInt(), GLES20.GL_TEXTURE_WRAP_S, this.wrapS.wrapFunc);
        GLES20.glTexParameteri(target.toInt(), GLES20.GL_TEXTURE_WRAP_T, this.wrapT.wrapFunc);
        GLES20.glTexParameteri(target.toInt(), GLES20.GL_TEXTURE_MIN_FILTER, this.minFilter.filterFunc);
        GLES20.glTexParameteri(target.toInt(), GLES20.GL_TEXTURE_MAG_FILTER, this.magFilter.filterFunc);
    }

    public void setWrapS(WrapFunc func) {
        this.wrapS = func;
    }

    public void setWrapT(WrapFunc func) {
        this.wrapT = func;
    }

    public void setMinFilter(FilterFunc func) {
        this.minFilter = func;
    }

    public void setMagFilter(FilterFunc func) {
        this.magFilter = func;
    }

    private GLTexture.Target target;
    private WrapFunc wrapS = WrapFunc.ClampToEdge;
    private WrapFunc wrapT = WrapFunc.ClampToEdge;
    private FilterFunc minFilter = FilterFunc.Linear;
    private FilterFunc magFilter = FilterFunc.Linear;

}