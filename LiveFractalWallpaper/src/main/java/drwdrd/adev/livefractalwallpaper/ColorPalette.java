package drwdrd.adev.livefractalwallpaper;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;

import drwdrd.adev.engine.LogSystem;
import drwdrd.adev.engine.TextureImage;
import drwdrd.adev.engine.TextureImage.ImagePixelDepth;
import drwdrd.adev.engine.TextureImage.ImagePixelFormat;



public class ColorPalette
{
	
	public static ColorPalette loadColorPaletteFromAssets(Context context,String fileName)
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		InputStream stream=null;
		try
		{
		    stream=context.getAssets().open(fileName);
			DocumentBuilder builder=factory.newDocumentBuilder();
	        Document dom=builder.parse(stream);
	        Node paletteNode=dom.getElementsByTagName("palette").item(0);
			if(paletteNode!=null)
			{
				int keyColorsCount=Integer.valueOf(paletteNode.getAttributes().getNamedItem("keyColorsCount").getNodeValue());
				ColorPalette palette=new ColorPalette(keyColorsCount);
				NodeList items=paletteNode.getChildNodes();
		        for(int i=0;i<items.getLength();i++)
		        {
		        	Node item=items.item(i);
		        	if(item.getNodeName().equalsIgnoreCase("keyColor"))
		        	{
	        			short red=Short.valueOf(item.getAttributes().getNamedItem("red").getNodeValue());
	        			short green=Short.valueOf(item.getAttributes().getNamedItem("green").getNodeValue());
	        			short blue=Short.valueOf(item.getAttributes().getNamedItem("blue").getNodeValue());
	        			short alpha=Short.valueOf(item.getAttributes().getNamedItem("alpha").getNodeValue());
	        			float distance=Float.valueOf(item.getAttributes().getNamedItem("distance").getNodeValue());
	        			palette.keyColors.add(new KeyColor(red,green,blue,alpha,distance));
		            }
		        }
		        return palette;
			}
			return null;
	    }
	    catch(IOException io)
	    {
            String err=io.getMessage();
            if(err!=null) {
                LogSystem.error(LiveFractalWallpaperService.tag, "Cannot parse file: " + err);
            }
			io.printStackTrace();
			return null;
	    }
		catch(SAXException e)
	    {
            String err=e.getMessage();
            if(err!=null) {
                LogSystem.error(LiveFractalWallpaperService.tag, "Cannot parse file: " + err);
            }
			e.printStackTrace();
			return null;
		}
		catch(ParserConfigurationException e)
		{
            String err=e.getMessage();
            if(err!=null) {
                LogSystem.error(LiveFractalWallpaperService.tag, "Cannot parse file: " + err);
            }
			e.printStackTrace();
			return null;
		}
		catch(NumberFormatException e)
		{
            String err=e.getMessage();
            if(err!=null) {
                LogSystem.error(LiveFractalWallpaperService.tag, "Cannot parse file: " + err);
            }
			e.printStackTrace();
			return null;
		}
		finally
		{
			try
			{
				if(stream!=null)
                    stream.close();

			}
			catch(IOException io)
			{
				io.printStackTrace();
			}
		}
	}
	
	public boolean buildTextureImage(TextureImage dstImage,int paletteSize)
	{
		KeyColor c1,c2;
		dstImage.Alloc(ImagePixelFormat.IPF_RGBA,ImagePixelDepth.IPD_8U,paletteSize,1);
		byte[] data=dstImage.getData().array();
		for(int i=0;i<keyColors.size()-1;i++)
		{
			c1=keyColors.get(i);
			c2=keyColors.get(i+1);
			int paletteEntry1=(int)(c1.distance*paletteSize);
			int paletteEntry2=(int)(c2.distance*paletteSize);
			for(int k=paletteEntry1;k<paletteEntry2;k++)
			{
				float dh=(float)(k-paletteEntry1)/(float)(paletteEntry2-paletteEntry1);
				data[4*k]=(byte)((1.0f-dh)*c1.red+dh*c2.red);
				data[4*k+1]=(byte)((1.0f-dh)*c1.green+dh*c2.green);
				data[4*k+2]=(byte)((1.0f-dh)*c1.blue+dh*c2.blue);
				data[4*k+3]=(byte)((1.0f-dh)*c1.alpha+dh*c2.alpha);
			}
		}
		return true;
	}
	
	private ColorPalette(int keyColorsCount)
	{
		keyColors=new ArrayList<KeyColor>(keyColorsCount);
	}

	public static class KeyColor
	{
		public KeyColor(short red,short green,short blue,short alpha,float distance)
		{
			this.red=red;
			this.green=green;
			this.blue=blue;
			this.alpha=alpha;
			this.distance=distance;
		}
		
		public short red;
		public short green;
		public short blue;
		public short alpha;
		public float distance;
	}
	
	private List<KeyColor> keyColors;
}