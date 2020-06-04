package drwdrd.adev.engine;


import java.nio.ByteBuffer;


public class TextureImage {

    public enum ImagePixelFormat {
        IPF_I(TextureImage.makePixelFormat(-1, -1, -1, -1, 1)),
        IPF_RGB(TextureImage.makePixelFormat(0, 1, 2, -1, 3)),
        IPF_RGBA(TextureImage.makePixelFormat(0, 1, 2, 3, 4));

        public boolean isEqual(int value) {
            return this.pixelFormat == value;
        }

        private ImagePixelFormat(int pixelFormat) {
            this.pixelFormat = pixelFormat;
        }

        public int toInt() {
            return pixelFormat;
        }

        private int pixelFormat;
    }

    public enum ImagePixelDepth {
        IPD_8U(1 << 3),
        IPD_16U(2 << 3),
        IPD_32F(4 << 3);

        public boolean isEqual(int value) {
            return this.pixelDepth == value;
        }

        public int toInt() {
            return pixelDepth;
        }

        private ImagePixelDepth(int pixelDepth) {
            this.pixelDepth = pixelDepth;
        }

        private int pixelDepth;
    }

    public TextureImage() {
        this.pixelFormat = 0;
        this.pixelDepth = 0;
        this.width = 0;
        this.height = 0;
        imageData = null;
    }

    public TextureImage(ImagePixelFormat pixelFormat, ImagePixelDepth pixelDepth, int width, int height) {
        this.pixelFormat = pixelFormat.pixelFormat;
        this.pixelDepth = pixelDepth.pixelDepth;
        this.pixelSize = getPixelSize(pixelFormat, pixelDepth);
        this.width = width;
        this.height = height;
        imageData = ByteBuffer.allocateDirect(pixelSize * width * height);
    }

    public TextureImage(int pixelFormat, int pixelDepth, int pixelSize, int width, int height) {
        this.pixelFormat = pixelFormat;
        this.pixelDepth = pixelDepth;
        this.pixelSize = pixelSize;
        this.width = width;
        this.height = height;
        imageData = ByteBuffer.allocateDirect(pixelSize * width * height);
    }

    public void Alloc(ImagePixelFormat pixelFormat, ImagePixelDepth pixelDepth, int width, int height) {
        this.pixelFormat = pixelFormat.pixelFormat;
        this.pixelDepth = pixelDepth.pixelDepth;
        this.pixelSize = getPixelSize(pixelFormat, pixelDepth);
        this.width = width;
        this.height = height;
        int dataSize = pixelSize * width * height;
        if ((imageData == null) || (imageData.capacity() < dataSize)) {
            imageData = ByteBuffer.allocateDirect(dataSize);
        }
    }

    public void Alloc(int pixelFormat, int pixelDepth, int pixelSize, int width, int height) {
        this.pixelFormat = pixelFormat;
        this.pixelDepth = pixelDepth;
        this.pixelSize = pixelSize;
        this.width = width;
        this.height = height;
        int dataSize = pixelSize * width * height;
        if ((imageData == null) || (imageData.capacity() < dataSize)) {
            imageData = ByteBuffer.allocateDirect(dataSize);
        }
    }

    public int getPixelFormat() {
        return pixelFormat;
    }

    public int getPixelDepth() {
        return pixelDepth;
    }

    public int getPixelSize() {
        return pixelSize;
    }

    int getWidth() {
        return width;
    }

    int getHeight() {
        return height;
    }

    public ByteBuffer getData() {
        return imageData;
    }

    public static int getPixelSize(ImagePixelFormat pixelFormat, ImagePixelDepth pixelDepth) {
        return (((pixelDepth.pixelDepth) >> 3) * ((pixelFormat.pixelFormat) & 7));
    }

    public static int getChannels(ImagePixelFormat pixelFormat) {
        return ((pixelFormat.pixelFormat) & 7);
    }

    public static boolean hasAlpha(ImagePixelFormat pixelFormat) {
        return ((((pixelFormat.pixelFormat) >> 3) & 7) != 0);
    }

    private static int makePixelFormat(int offsetR, int offsetG, int offsetB, int offsetA, int channels) {
        return (((((offsetR) + 1) << 12) | (((offsetG) + 1) << 9) | (((offsetB) + 1) << 6) | ((offsetA) + 1) << 3) | (channels));
    }

    private int pixelFormat;
    private int pixelDepth;
    private int pixelSize;
    private int width;
    private int height;
    private ByteBuffer imageData;
}