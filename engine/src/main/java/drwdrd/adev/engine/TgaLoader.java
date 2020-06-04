package drwdrd.adev.engine;


import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;


public class TgaLoader {
    private TgaLoader() {

    }


    public static boolean LoadFromAssets(Context context, TextureImage dstImage, String fileName) {
        InputStream stream = null;
        InputStreamSerializer serializer = null;
        try {
            stream = context.getAssets().open(fileName);
            serializer = new InputStreamSerializer(stream, ByteOrder.LITTLE_ENDIAN, 4096);

            int idLength = serializer.readUnsignedByte();
            int colormapType = serializer.readUnsignedByte();
            int imageType = serializer.readUnsignedByte();
            int colormapIndex = serializer.readUnsignedShort();
            int colormapLength = serializer.readUnsignedShort();
            int colormapSize = serializer.readUnsignedByte();
            int xOrigin = serializer.readUnsignedShort();
            int yOrigin = serializer.readUnsignedShort();
            int width = serializer.readUnsignedShort();
            int height = serializer.readUnsignedShort();
            int pixelSize = serializer.readUnsignedByte();
            int attributes = serializer.readUnsignedByte();
            boolean flipY = false;
            if ((attributes & 0x20) == 0) {
                flipY = true;
            }
            switch (imageType) {
                case 9: // 8 bit compressed TGA image
                case 1: // 8 bit uncompressed TGA image
                case 3: // 8 bit monochrome uncompressed TGA image
                case 10: // 24/32 bit compressed TGA image
                    LogSystem.error(EngineUtils.tag, "Error while reading TGA image: unsupported format.");
                    return false;
                case 2: // 24/32 bit uncompressed TGA image
                    if (colormapLength != 0) {
                        LogSystem.error(EngineUtils.tag, "Error while reading TGA image: 24/32 tga doesn't support colormap.");
                        return false;
                    }
                    if ((pixelSize == 32) || (pixelSize == 24)) {
                        int bytesPerPixel = pixelSize >> 3;
                        byte[] rowBuffer = new byte[bytesPerPixel * width];
                        byte[] data;
                        switch (pixelSize) {
                            case 24:
                                dstImage.Alloc(TextureImage.ImagePixelFormat.IPF_RGB, TextureImage.ImagePixelDepth.IPD_8U, width, height);
                                data = dstImage.getData().array();
                                if (flipY == true) {
                                    int offset = (height - 1) * 3 * width;
                                    for (int row = height - 1; row >= 0; --row) {
                                        serializer.read(rowBuffer, 0, 3 * width);
                                        //convert from bgr to rgb
                                        for (int i = 0; i < width; i++) {
                                            data[offset + 3 * i] = rowBuffer[3 * i + 2];
                                            data[offset + 3 * i + 1] = rowBuffer[3 * i + 1];
                                            data[offset + 3 * i + 2] = rowBuffer[3 * i];
                                        }
                                        offset -= 3 * width;
                                    }
                                } else {
                                    int offset = 0;
                                    for (int row = 0; row < height; ++row) {
                                        serializer.read(rowBuffer, 0, 3 * width);
                                        //convert from bgr to rgb
                                        for (int i = 0; i < width; i++) {
                                            data[offset + 3 * i] = rowBuffer[3 * i + 2];
                                            data[offset + 3 * i + 1] = rowBuffer[3 * i + 1];
                                            data[offset + 3 * i + 2] = rowBuffer[3 * i];
                                        }
                                        offset += 3 * width;
                                    }
                                }
                                break;
                            case 32:
                                dstImage.Alloc(TextureImage.ImagePixelFormat.IPF_RGBA, TextureImage.ImagePixelDepth.IPD_8U, width, height);
                                data = dstImage.getData().array();
                                if (flipY == true) {
                                    int offset = (height - 1) * 4 * width;
                                    for (int row = height - 1; row >= 0; --row) {
                                        serializer.read(rowBuffer, 0, 4 * width);
                                        //convert from bgra to rgba
                                        for (int i = 0; i < width; i++) {
                                            data[offset + 4 * i] = rowBuffer[4 * i + 2];
                                            data[offset + 4 * i + 1] = rowBuffer[4 * i + 1];
                                            data[offset + 4 * i + 2] = rowBuffer[4 * i];
                                            data[offset + 4 * i + 3] = rowBuffer[4 * i + 3];
                                        }
                                        offset -= 4 * width;
                                    }
                                } else {
                                    int offset = 0;
                                    for (int row = 0; row < height; ++row) {
                                        serializer.read(rowBuffer, 0, 4 * width);
                                        //convert from bgra to rgba
                                        for (int i = 0; i < width; i++) {
                                            data[offset + 4 * i] = rowBuffer[4 * i + 2];
                                            data[offset + 4 * i + 1] = rowBuffer[4 * i + 1];
                                            data[offset + 4 * i + 2] = rowBuffer[4 * i];
                                            data[offset + 4 * i + 3] = rowBuffer[4 * i + 3];
                                        }
                                        offset += 4 * width;
                                    }
                                }
                                break;
                        }
                        return true;
                    } else {
                        LogSystem.error(EngineUtils.tag, "Error while reading TGA image: unsupported format.");
                        return false;
                    }
            }
            LogSystem.error(EngineUtils.tag, "Error while reading TGA image: unsupported format.");
            return false;
        } catch (IOException io) {
            String err = io.getMessage();
            if (err != null) {
                LogSystem.error(EngineUtils.tag, err);
            }
            io.printStackTrace();
            return false;
        } finally {
            try {
                if (stream != null) {
                    serializer.close();
                }

            } catch (IOException io) {
                io.printStackTrace();
            }
        }
    }
}