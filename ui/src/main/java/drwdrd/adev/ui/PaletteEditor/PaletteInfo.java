package drwdrd.adev.ui.PaletteEditor;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import drwdrd.adev.ui.ColorPicker.ColorInfo;

public class PaletteInfo {


    public class KeyColor {
        public int color;
        public float offset;
        public boolean enabled;

        public KeyColor(int color,float offset,boolean enabled) {
            this.color=color;
            this.offset=offset;
            this.enabled=enabled;
        }
    }

    private ArrayList<KeyColor> keyColors;

    public PaletteInfo(int size) {
        keyColors= new ArrayList<>();
        for(int i=0;i<size;i++) {
            float d=(float)i/(float)(size-1);
            int c=(int)(255.0f*d);
            keyColors.add(new KeyColor(Color.rgb(c,c,c),d,true));
        }
    }

    public PaletteInfo(int[] palette) {
        keyColors= new ArrayList<>(palette.length);
        for(int idx=0;idx<palette.length;idx++) {
            keyColors.add(new KeyColor(palette[idx],(float)idx/(float)palette.length,true));
        }
    }

    public PaletteInfo(PaletteInfo paletteInfo) {
        keyColors= new ArrayList<>(paletteInfo.keyColors.size());
        for(KeyColor keyColor : paletteInfo.keyColors) {
            keyColors.add(new KeyColor(keyColor.color,keyColor.offset,keyColor.enabled));
        }
    }

    private PaletteInfo() {
        keyColors= new ArrayList<>();
    }


    public ArrayList<KeyColor> getKeyColors() {
        return keyColors;
    }

    public  KeyColor getKeyColor(int index) {
        return keyColors.get(index);
    }

    public void setColor(int index,int color) {
        keyColors.get(index).color=color;
        keyColors.get(index).offset=(float)index/(float)(keyColors.size()-1);
    }

    public void enableColor(int index,boolean enabled) {
        keyColors.get(index).enabled=enabled;
    }

    public void addColor(int color,float offset,boolean enabled) {
        keyColors.add(new KeyColor(color,offset,enabled));
    }

    public Bitmap buildPaletteBitmap(int width,int height,boolean horizontal) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        KeyColor startColor=null;
        KeyColor endColor=new KeyColor(Color.BLACK,0.0f,true);
        int startEntry=0;
        int endEntry=0;
        float dh;
        if(horizontal) {
            int k = 0;
            while (k < keyColors.size()) {
                if (keyColors.get(k).enabled == true) {
                    endColor = keyColors.get(k);
                    endEntry = (int) (endColor.offset * width);
                    if (startColor == null) {
                        startColor = endColor;
                    }
                    for (int i = startEntry; i < endEntry; i++) {
                        for (int j = 0; j < height; j++) {
                            dh = (float) (i - startEntry) / (float) (endEntry - startEntry);
                            int c = ColorInfo.mix(startColor.color, endColor.color, dh);
                            bitmap.setPixel(i, j, c);
                        }
                    }
                    startColor = endColor;
                    startEntry = endEntry;
                }
                k++;
            }
            for (int i = endEntry; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    bitmap.setPixel(i, j, endColor.color);
                }
            }
        } else {
            int k = 0;
            while (k < keyColors.size()) {
                if (keyColors.get(k).enabled == true) {
                    endColor = keyColors.get(k);
                    endEntry = (int) (endColor.offset * height);
                    if (startColor == null) {
                        startColor = endColor;
                    }
                    for (int i = startEntry; i < endEntry; i++) {
                        for (int j = 0; j < width; j++) {
                            dh = (float) (i - startEntry) / (float) (endEntry - startEntry);
                            int c = ColorInfo.mix(startColor.color, endColor.color, dh);
                            bitmap.setPixel(j, i, c);
                        }
                    }
                    startColor = endColor;
                    startEntry = endEntry;
                }
                k++;
            }
            for (int i = endEntry; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    bitmap.setPixel(j, i, endColor.color);
                }
            }
        }
        return bitmap;
    }

    public static String toString(PaletteInfo paletteInfo) {
        StringWriter stringWriter = new StringWriter();

        XmlSerializer serializer = Xml.newSerializer();
        try {
            serializer.setOutput(stringWriter);

            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "palette");
            for(KeyColor keyColor:paletteInfo.keyColors) {
                serializer.startTag("","keyColor");
                serializer.attribute("", "color", Integer.toString(keyColor.color));
                serializer.attribute("","offset",Float.toString(keyColor.offset));
                serializer.attribute("","enabled",Boolean.toString(keyColor.enabled));
                serializer.endTag("","keyColor");
            }
            serializer.endTag("","palette");
            serializer.endDocument();
            serializer.flush();

        } catch (IOException io) {
            io.printStackTrace();
        }
        return  stringWriter.toString();
    }

    public static PaletteInfo parsePalette(String str) {
        PaletteInfo paletteInfo=new PaletteInfo();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document dom = builder.parse(new InputSource(new StringReader(str)));
            Node paletteNode = dom.getElementsByTagName("palette").item(0);
            for (int i = 0; i < paletteNode.getChildNodes().getLength(); i++) {
                if (paletteNode.getChildNodes().item(i).getNodeName().equals("keyColor")) {
                    Node keyColorNode = paletteNode.getChildNodes().item(i);
                    int color=Integer.parseInt(keyColorNode.getAttributes().getNamedItem("color").getNodeValue());
                    float offset=Float.parseFloat(keyColorNode.getAttributes().getNamedItem("offset").getNodeValue());
                    boolean enabled=Boolean.parseBoolean(keyColorNode.getAttributes().getNamedItem("enabled").getNodeValue());
                    paletteInfo.addColor(color,offset,enabled);
                }
            }

        } catch(ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException io) {
            io.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return paletteInfo;
    }
}
