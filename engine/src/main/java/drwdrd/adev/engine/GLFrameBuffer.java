package drwdrd.adev.engine;

import android.opengl.GLES20;

public class GLFrameBuffer {

    public enum RenderBufferType {
        ColorRGBA4(GLES20.GL_COLOR_ATTACHMENT0,GLES20.GL_RGBA4),
        ColorRGB565(GLES20.GL_COLOR_ATTACHMENT0,GLES20.GL_RGB565),
        ColorRGB5A1(GLES20.GL_COLOR_ATTACHMENT0,GLES20.GL_RGB5_A1),
        Depth16(GLES20.GL_DEPTH_ATTACHMENT,GLES20.GL_DEPTH_COMPONENT16),
        Stencil8(GLES20.GL_STENCIL_ATTACHMENT,GLES20.GL_STENCIL_INDEX8);

        private RenderBufferType(int type,int format) {
            this.type=type;
            this.format=format;
        }

        private int type;
        private int format;
    }

    public class GLRenderBuffer {

        private GLRenderBuffer(RenderBufferType type) {
            this.type=type;
            glBufferId = new int[1];
            GLES20.glGenRenderbuffers(1, glBufferId, 0);
        }

        public void delete() {
            GLES20.glDeleteRenderbuffers(1, glBufferId, 0);
            glBufferId[0] = 0;
        }

        public boolean isValid() {
            return (glBufferId[0] != 0);
        }

        private void alloc(int width,int height) { GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, type.format, width, height); }

        public void bind() {
            GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, glBufferId[0]);
        }

        public void release() { GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0); }

        private int[] glBufferId;
        private RenderBufferType type;
    }


    public GLFrameBuffer(int width,int height) {
        this.width=width;
        this.height=height;
        this.glBufferId = new int[1];
        GLES20.glGenFramebuffers(1, this.glBufferId, 0);
    }

    public void delete() {
        GLES20.glDeleteFramebuffers(1, glBufferId, 0);
        glBufferId[0] = 0;
    }

    public boolean isValid() { return (glBufferId[0] != 0); }

    public boolean isComplete() {
        int status=GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (status==GLES20.GL_FRAMEBUFFER_COMPLETE)
        {
            return true;
        }
        return false;
    }

    public void bind() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, glBufferId[0]);
    }

    public void release() { GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0); }

    public void attachRenderBuffer(GLRenderBuffer renderBuffer) {
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER,renderBuffer.type.type,GLES20.GL_RENDERBUFFER,renderBuffer.glBufferId[0]);
    }

    public void detachRenderBuffer(RenderBufferType type) {
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER,type.type,GLES20.GL_RENDERBUFFER,0);
    }

    public void attachRenderTexture(GLTexture renderTexture) {
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,GLES20.GL_COLOR_ATTACHMENT0,GLES20.GL_TEXTURE_2D,renderTexture.getId(),0);
    }

    public void detachRenderTexture() {
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,GLES20.GL_COLOR_ATTACHMENT0,GLES20.GL_TEXTURE_2D,0,0);
    }

    public GLRenderBuffer createRenderBuffer(RenderBufferType type,boolean attached) {
        GLRenderBuffer renderBuffer=new GLRenderBuffer(type);
        renderBuffer.bind();
        renderBuffer.alloc(width,height);
        if (attached) {
            attachRenderBuffer(renderBuffer);
        }
        renderBuffer.release();
        return renderBuffer;
    }

    public  GLTexture createRenderTexture(RenderBufferType type,boolean attached) {
        GLTexture renderTexture=new GLTexture();
        switch (type) {
            case ColorRGB565:
                renderTexture.createTexture2D(GLTexture.Format.RGB565,width,height);
                break;
            case ColorRGB5A1:
                renderTexture.createTexture2D(GLTexture.Format.RGB5A1,width,height);
                break;
            case ColorRGBA4:
                renderTexture.createTexture2D(GLTexture.Format.RGBA4,width,height);
                break;
            default:
                return null;
        }
        if (attached) {
            attachRenderTexture(renderTexture);
        }
        return renderTexture;
    }

    private int[] glBufferId;
    private int width;
    private int height;
}
