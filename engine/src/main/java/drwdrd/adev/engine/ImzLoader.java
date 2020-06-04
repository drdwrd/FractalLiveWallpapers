package drwdrd.adev.engine;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;

import drwdrd.adev.engine.TextureImage.ImagePixelDepth;
import drwdrd.adev.engine.TextureImage.ImagePixelFormat;


public class ImzLoader {
    public static boolean LoadFromAssets(Context context, TextureImage dstImage, String fileName) {
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
                        LogSystem.error(EngineUtils.tag, "Error while reading IMZ image: invalid image size.");
                        return false;
                    }
                    if (layers > 1) {
                        LogSystem.error(EngineUtils.tag, "Error while reading IMZ image: invalid layer.");
                        return false;
                    }
                    if (TextureImage.ImagePixelDepth.IPD_16U.isEqual(pixelDepth) && TextureImage.ImagePixelFormat.IPF_I.isEqual(pixelFormat)) {
                        if ((attributes & IMG_COMPRESSED) == 0) {
                            dstImage.Alloc(ImagePixelFormat.IPF_RGBA.toInt(), ImagePixelDepth.IPD_16U.toInt(), 2, width, height);
                            byte[] dst = dstImage.getData().array();
//							serializer.read(dst,0,2*width*height);
                            for (int i = 0; i < height; i++) {
                                serializer.read(dst, 2 * i * width, 2 * width);
                            }
/*							dstImage.Alloc(ImagePixelFormat.IPF_RGB,ImagePixelDepth.IPD_8U,width,height);
                            int offset=0;
							byte[] dst=dstImage.getData().array();
							for(int row=0;row<height;row++)
							{
								for(int i=0;i<width;i++)
								{
									int px=serializer.readUnsignedShort();
									if((px&0x00008000)!=0)
									{
										int cl=px&0x00007fff;
										float fpx=cl/(float)0x00007fff;
										int ipx=(int)(fpx*65535.0f);
										dst[offset+3*i]=(byte)255;
										dst[offset+3*i+1]=(byte)((ipx&0x0000ff00)>>8);
										dst[offset+3*i+2]=(byte)(ipx&0x000000ff);
									}
									else
									{
										dst[offset+3*i]=0;
										dst[offset+3*i+1]=0;
										dst[offset+3*i+2]=0;
									}
								}
								offset+=3*width;
							}*/
                            return true;
                        } else {
/*							long compressedDataSize=serializer.readLong();
							int compressionType=serializer.readInt();
							int deflateHeader=serializer.readInt();
							InflaterInputStream inflater=new InflaterInputStream(stream);
							dstImage.Alloc(ImagePixelFormat.IPF_RGB,ImagePixelDepth.IPD_8U,width,height);
							int offset=0;
							byte[] dst=dstImage.getData().array();
//							byte[] rowBuffer=new byte[2*width];
//							BitReader bitReader=new DataSerializer(ByteOrder.LITTLE_ENDIAN);
							InputStreamSerializer compressedStreamSerializer=new InputStreamSerializer(inflater,ByteOrder.LITTLE_ENDIAN,4096);
							for(int row=0;row<height;row++)
							{
//								if(inflater.read(rowBuffer,0,2*width)!=2*width)
//								{
//									inflater.close();
//									LogSystem.error("Error while reading IMZ image: cannot read from stream.");
//									return false;
//								}
								for(int i=0;i<width;i++)
								{
//									int px=(inflater.read()&0xff)|((inflater.read()&0xff)<<8);
//									int px=(rowBuffer[2*i+1]&0xff)|((rowBuffer[2*i]&0xff)<<8);
									int px=compressedStreamSerializer.readUnsignedShort();
									if((px&0x00008000)!=0)
									{
										int cl=px&0x00007fff;
										float fpx=cl/(float)0x00007fff;
										int ipx=(int)(fpx*65535.0f);
										dst[offset+3*i]=(byte)255;
										dst[offset+3*i+1]=(byte)((ipx&0x0000ff00)>>8);
										dst[offset+3*i+2]=(byte)(ipx&0x000000ff);
									}
									else
									{
										dst[offset+3*i]=0;
										dst[offset+3*i+1]=0;
										dst[offset+3*i+2]=0;
									}
								}
								offset+=3*width;
							}
							inflater.close();
							return true;*/
                            LogSystem.error(EngineUtils.tag, "Error while reading IMZ image: unsupported format.");
                            return false;
                        }
                    } else {
                        LogSystem.error(EngineUtils.tag, "Error while reading IMZ image: unsupported format.");
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