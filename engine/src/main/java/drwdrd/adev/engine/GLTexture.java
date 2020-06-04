package drwdrd.adev.engine;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class GLTexture {

    public enum Format {
        RGBA8(GLES20.GL_RGBA,GLES20.GL_UNSIGNED_BYTE),
        RGB8(GLES20.GL_RGB,GLES20.GL_UNSIGNED_BYTE),
        RGBA4(GLES20.GL_RGBA,GLES20.GL_UNSIGNED_SHORT_4_4_4_4),
        RGB5A1(GLES20.GL_RGBA,GLES20.GL_UNSIGNED_SHORT_5_5_5_1),
        RGB565(GLES20.GL_RGB,GLES20.GL_UNSIGNED_SHORT_5_6_5),
        LUMINANCE_ALPHA(GLES20.GL_LUMINANCE_ALPHA,GLES20.GL_UNSIGNED_BYTE),
        LUMINANCE(GLES20.GL_LUMINANCE,GLES20.GL_UNSIGNED_BYTE),
        ALPHA(GLES20.GL_ALPHA,GLES20.GL_UNSIGNED_BYTE);

        private Format(int format,int type) {
            this.format=format;
            this.type=type;
        }

        private int format;
        private int type;
    }

    public enum Target {
        Texture2D(GLES20.GL_TEXTURE_2D),
        TextureCubemap(GLES20.GL_TEXTURE_CUBE_MAP);

        public int toInt() {
            return target;
        }

        private Target(int target) {
            this.target = target;
        }

        private int target;
    }


    public GLTexture() {
        glTextureId = new int[1];
        GLES20.glGenTextures(1, glTextureId, 0);
    }

    public int getId() { return glTextureId[0]; }

    public void delete() {
        GLES20.glDeleteTextures(1, glTextureId, 0);
    }

    public boolean isValid() {
        return (glTextureId[0] != 0);
    }

    public void bind() {
        GLES20.glBindTexture(target.target, glTextureId[0]);
    }

    public void release() {
        GLES20.glBindTexture(target.target, 0);
    }

    public void createCubemapTexture(ArrayList<TextureImage> srcImages) {
        if (srcImages.size() != 6) {
            LogSystem.error(EngineUtils.tag, "Cubemap texture requires 6 layers.");
            return;
        }
        target = Target.TextureCubemap;
        int format = 0;
        int type = 0;
        if (TextureImage.ImagePixelFormat.IPF_I.isEqual(srcImages.get(0).getPixelFormat()) && TextureImage.ImagePixelDepth.IPD_8U.isEqual(srcImages.get(0).getPixelDepth())) {
            format = GLES20.GL_LUMINANCE;
            type = GLES20.GL_UNSIGNED_BYTE;
        } else if (TextureImage.ImagePixelFormat.IPF_RGB.isEqual(srcImages.get(0).getPixelFormat()) && TextureImage.ImagePixelDepth.IPD_8U.isEqual(srcImages.get(0).getPixelDepth())) {
            format = GLES20.GL_RGB;
            type = GLES20.GL_UNSIGNED_BYTE;
        } else if (TextureImage.ImagePixelFormat.IPF_RGBA.isEqual(srcImages.get(0).getPixelFormat()) && TextureImage.ImagePixelDepth.IPD_8U.isEqual(srcImages.get(0).getPixelDepth())) {
            format = GLES20.GL_RGBA;
            type = GLES20.GL_UNSIGNED_BYTE;
        } else if (TextureImage.ImagePixelFormat.IPF_RGBA.isEqual(srcImages.get(0).getPixelFormat()) && TextureImage.ImagePixelDepth.IPD_16U.isEqual(srcImages.get(0).getPixelDepth())) {
            format = GLES20.GL_RGBA;
            type = GLES20.GL_UNSIGNED_SHORT_5_5_5_1;
        } else {
            LogSystem.error(EngineUtils.tag, "Unsupported pixel format.");
            return;
        }
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(target.target, glTextureId[0]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, format, srcImages.get(0).getWidth(), srcImages.get(0).getHeight(), 0, format, type, srcImages.get(0).getData());
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, format, srcImages.get(1).getWidth(), srcImages.get(1).getHeight(), 0, format, type, srcImages.get(1).getData());
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, format, srcImages.get(2).getWidth(), srcImages.get(2).getHeight(), 0, format, type, srcImages.get(2).getData());
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, format, srcImages.get(3).getWidth(), srcImages.get(3).getHeight(), 0, format, type, srcImages.get(3).getData());
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, format, srcImages.get(4).getWidth(), srcImages.get(4).getHeight(), 0, format, type, srcImages.get(4).getData());
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, format, srcImages.get(5).getWidth(), srcImages.get(5).getHeight(), 0, format, type, srcImages.get(5).getData());
        GLES20.glGenerateMipmap(target.target);
        GLES20.glBindTexture(target.target, 0);
    }

    public void createTexture2D(TextureImage image) {
        target = Target.Texture2D;
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(target.target, glTextureId[0]);
        int format = 0;
        int type = 0;
        if (TextureImage.ImagePixelFormat.IPF_I.isEqual(image.getPixelFormat()) && TextureImage.ImagePixelDepth.IPD_8U.isEqual(image.getPixelDepth())) {
            format = GLES20.GL_LUMINANCE;
            type = GLES20.GL_UNSIGNED_BYTE;
        } else if (TextureImage.ImagePixelFormat.IPF_RGB.isEqual(image.getPixelFormat()) && TextureImage.ImagePixelDepth.IPD_8U.isEqual(image.getPixelDepth())) {
            format = GLES20.GL_RGB;
            type = GLES20.GL_UNSIGNED_BYTE;
        } else if (TextureImage.ImagePixelFormat.IPF_RGBA.isEqual(image.getPixelFormat()) && TextureImage.ImagePixelDepth.IPD_8U.isEqual(image.getPixelDepth())) {
            format = GLES20.GL_RGBA;
            type = GLES20.GL_UNSIGNED_BYTE;
        } else if (TextureImage.ImagePixelFormat.IPF_RGBA.isEqual(image.getPixelFormat()) && TextureImage.ImagePixelDepth.IPD_16U.isEqual(image.getPixelDepth())) {
            format = GLES20.GL_RGBA;
            type = GLES20.GL_UNSIGNED_SHORT_5_5_5_1;
        } else {
            LogSystem.error(EngineUtils.tag, "Unsupported pixel format,use IPD_8U.");
            return;
        }
        GLES20.glTexImage2D(target.target, 0, format, image.getWidth(), image.getHeight(), 0, format, type, image.getData());
        GLES20.glGenerateMipmap(target.target);
        GLES20.glBindTexture(target.target, 0);
    }

    public void createTexture2DFromAssets(Context context,String fileName,boolean createMipmaps) {

        InputStream stream=null;
        Bitmap bitmap=null;
        try {
            stream=context.getAssets().open(fileName);
            bitmap=BitmapFactory.decodeStream(stream);
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException io) {
                io.printStackTrace();
            }
        }

        createTexture2DFromBitmap(bitmap,createMipmaps);
    }

    public void createTexture2DFromBitmap(Bitmap bitmap,boolean createMipmaps) {
        target = Target.Texture2D;
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(target.target, glTextureId[0]);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,bitmap,0);

        if(createMipmaps==true) {
            GLES20.glGenerateMipmap(target.target);
        }
        GLES20.glBindTexture(target.target, 0);
    }


    public void createTexture2D(Format format,int width,int height) {
        target = Target.Texture2D;
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(target.target, glTextureId[0]);
        GLES20.glTexImage2D(target.target, 0, format.format, width, height, 0, format.format, format.type, null);
        GLES20.glBindTexture(target.target, 0);
    }

    private Target target;
    private int[] glTextureId;
}
