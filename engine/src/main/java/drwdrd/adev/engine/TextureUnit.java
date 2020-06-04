package drwdrd.adev.engine;

import android.opengl.GLES20;


public class TextureUnit {

    public TextureUnit(GLTexture texture, TextureSampler textureSampler) {
        this.texture = texture;
        this.textureSampler = textureSampler;
        this.activeUnit = -1;
    }

    public void delete() {
        texture.delete();
        textureSampler.delete();
    }

    public void bind(int unit) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + unit);
        texture.bind();
        textureSampler.bind();
        activeUnit = unit;
    }

    public void release() {
        if (activeUnit > -1) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + activeUnit);
            texture.release();
        }
    }

    private GLTexture texture;
    private TextureSampler textureSampler;
    int activeUnit;
}

