package drwdrd.adev.engine;


import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.ArrayList;

import drwdrd.adev.engine.TextureImage.ImagePixelDepth;
import drwdrd.adev.engine.TextureImage.ImagePixelFormat;


public class ImfLoader {
    private ImfLoader() {

    }

    public static boolean LoadFromAssets(Context context, ArrayList<TextureImage> dstImages, String fileName) {
        InputStream stream = null;
        InputStreamSerializer serializer = null;
        dstImages.clear();
        try {
            stream = context.getAssets().open(fileName);
            serializer = new InputStreamSerializer(stream, ByteOrder.LITTLE_ENDIAN, 4096);
            //read signature
            int imf2Signature = serializer.readInt();
            if (imf2Signature == ('I' << 24 | 'M' << 16 | 'F' << 8 | '2')) {
                //read header
                int version = serializer.readUnsignedShort();
                //imf2 supported
                if (version == 2) {
                    int pixelFormat = serializer.readInt();
                    int pixelDepth = serializer.readInt();
                    int width = serializer.readInt();
                    int height = serializer.readInt();
                    int layers = serializer.readUnsignedShort();
                    int attributes = serializer.readInt();
                    if (width < 0 || height < 0 || layers < 1) {
                        LogSystem.error(EngineUtils.tag, "Error while reading IMF image: invalid image size.");
                        return false;
                    }
                    if ((attributes & IMG_COMPRESSED) == 0) {
                        ImagePixelDepth pd;
                        if (TextureImage.ImagePixelDepth.IPD_32F.isEqual(pixelDepth)) {
                            pd = ImagePixelDepth.IPD_32F;
                        } else if (TextureImage.ImagePixelDepth.IPD_16U.isEqual(pixelDepth)) {
                            pd = ImagePixelDepth.IPD_16U;
                        } else if (TextureImage.ImagePixelDepth.IPD_8U.isEqual(pixelDepth)) {
                            pd = ImagePixelDepth.IPD_8U;
                        } else {
                            LogSystem.error(EngineUtils.tag, "Error while reading IMF image: unsupported pixel depth.");
                            return false;
                        }
                        ImagePixelFormat pf;
                        if (ImagePixelFormat.IPF_I.isEqual(pixelFormat)) {
                            pf = ImagePixelFormat.IPF_I;
                        } else if (ImagePixelFormat.IPF_RGB.isEqual(pixelFormat)) {
                            pf = ImagePixelFormat.IPF_RGB;
                        } else if (ImagePixelFormat.IPF_RGBA.isEqual(pixelFormat)) {
                            pf = ImagePixelFormat.IPF_RGBA;
                        } else {
                            LogSystem.error(EngineUtils.tag, "Error while reading IMF image: unsupported pixel format.");
                            return false;
                        }
                        int channels = TextureImage.getChannels(pf);
                        for (int i = 0; i < layers; i++) {
                            TextureImage image = new TextureImage(pf, pd, width, height);
                            dstImages.add(image);
                            switch (pd) {
                                case IPD_8U:
                                    serializer.read(image.getData().array(), 0, channels * width * height);
                                    break;
                                case IPD_16U:
                                    serializer.readUnsignedShorts(image.getData().array(), 0, channels * width * height);
                                    break;
                                case IPD_32F:
                                    serializer.readFloats(image.getData().array(), 0, channels * width * height);
                                    break;
                            }
                        }
                    } else {
                        LogSystem.error(EngineUtils.tag, "Error while reading IMF image: compressed formats unsupported.");
                        return false;
                    }
                }
            } else {
                LogSystem.error(EngineUtils.tag, "Invalid IMF header!");
                return false;
            }
            return true;
        } catch (IOException io) {
            String err = io.getMessage();
            if (err != null) {
                LogSystem.error(EngineUtils.tag, err);
            }
            io.printStackTrace();
            return false;
        } finally {
            try {
                if (serializer != null) {
                    serializer.close();
                }

            } catch (IOException io) {
                io.printStackTrace();
            }
        }
    }

    public static boolean LoadFromAssets(Context context, TextureImage dstImage, String fileName, int layer) {
        InputStream stream = null;
        InputStreamSerializer serializer = null;
        try {
            stream = context.getAssets().open(fileName);
            serializer = new InputStreamSerializer(stream, ByteOrder.LITTLE_ENDIAN, 4096);
            //read signature
            int imf2Signature = serializer.readInt();
            if (imf2Signature == ('I' << 24 | 'M' << 16 | 'F' << 8 | '2')) {
                //read header
                int version = serializer.readUnsignedShort();
                //imf2 supported
                if (version == 2) {
                    int pixelFormat = serializer.readInt();
                    int pixelDepth = serializer.readInt();
                    int width = serializer.readInt();
                    int height = serializer.readInt();
                    int layers = serializer.readUnsignedShort();
                    int attributes = serializer.readInt();
                    if (width < 0 || height < 0 || layers < 1) {
                        LogSystem.error(EngineUtils.tag, "Error while reading IMF image: invalid image size.");
                        return false;
                    }
                    if (layers <= layer) {
                        LogSystem.error(EngineUtils.tag, "Error while reading IMF image: invalid layer.");
                        return false;
                    }
                    if ((attributes & IMG_COMPRESSED) == 0) {
                        ImagePixelDepth pd;
                        if (TextureImage.ImagePixelDepth.IPD_32F.isEqual(pixelDepth)) {
                            pd = ImagePixelDepth.IPD_32F;
                        } else if (TextureImage.ImagePixelDepth.IPD_16U.isEqual(pixelDepth)) {
                            pd = ImagePixelDepth.IPD_16U;
                        } else if (TextureImage.ImagePixelDepth.IPD_8U.isEqual(pixelDepth)) {
                            pd = ImagePixelDepth.IPD_8U;
                        } else {
                            LogSystem.error(EngineUtils.tag, "Error while reading IMF image: unsupported pixel depth.");
                            return false;
                        }
                        ImagePixelFormat pf;
                        if (ImagePixelFormat.IPF_I.isEqual(pixelFormat)) {
                            pf = ImagePixelFormat.IPF_I;
                        } else if (ImagePixelFormat.IPF_RGB.isEqual(pixelFormat)) {
                            pf = ImagePixelFormat.IPF_RGB;
                        } else if (ImagePixelFormat.IPF_RGBA.isEqual(pixelFormat)) {
                            pf = ImagePixelFormat.IPF_RGBA;
                        } else {
                            LogSystem.error(EngineUtils.tag, "Error while reading IMF image: unsupported pixel format.");
                            return false;
                        }
                        int offset = TextureImage.getPixelSize(pf, pd) * width * height * layer;
                        if (serializer.skip(offset) != offset) {
                            LogSystem.error(EngineUtils.tag, "Error while reading IMF image: cannot read layer.");
                            return false;
                        }
                        int channels = TextureImage.getChannels(pf);
                        dstImage.Alloc(pf, pd, width, height);
                        switch (pd) {
                            case IPD_8U:
                                serializer.read(dstImage.getData().array(), 0, channels * width * height);
                                break;
                            case IPD_16U:
                                serializer.readUnsignedShorts(dstImage.getData().array(), 0, channels * width * height);
                                break;
                            case IPD_32F:
                                serializer.readFloats(dstImage.getData().array(), 0, channels * width * height);
                                break;
                        }
                    } else {
                        LogSystem.error(EngineUtils.tag, "Error while reading IMF image: compressed formats unsupported.");
                        return false;
                    }
                }
            } else {
                LogSystem.error(EngineUtils.tag, "Invalid IMF header!");
                return false;
            }
            return true;
        } catch (IOException io) {
            String err = io.getMessage();
            if (err != null) {
                LogSystem.error(EngineUtils.tag, err);
            }
            io.printStackTrace();
            return false;
        } finally {
            try {
                if (serializer != null) {
                    serializer.close();
                }

            } catch (IOException io) {
                io.printStackTrace();
            }
        }
    }

    private static final int IMG_COMPRESSED = 1;
}