package drwdrd.adev.engine;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Font {

    private final static int CHAR_START = 32; // First Character (ASCII Code)
    private  final static int CHAR_END = 126; // Last Character (ASCII Code)
    private  final static int CHAR_CNT = ( ( ( CHAR_END - CHAR_START ) + 1 ) + 1 ); // Character Count (Including Character to use for Unknown)
    private  final static int CHAR_NONE = 32; // Character to Use for Unknown (ASCII Code)
    private  final static int CHAR_UNKNOWN = ( CHAR_CNT - 1 ); // Index of the Unknown Character

    //program used for font rendering
    private ProgramObject fontProgram = null;
    //distance field texture
    private TextureUnit fontTexture = null;
    //vertex/index buffer containing mesh with all chars
    private VertexArray fontArray = null;

    private Bitmap font = null;
    // Font Padding (Pixels; On Each Side, ie. Doubled on Both X+Y Axis)
    private int fontPadX = 0;
    private int fontPadY = 0;
    private float fontHeight = 0.0f; // Font Height (Actual; Pixels)
    private float fontAscent = 0.0f; // Font Ascent (Above Baseline; Pixels)
    private float fontDescent = 0.0f; // Font Descent (Below Baseline; Pixels)
    private float charWidthMax = 0.0f; // Character Width (Maximum; Pixels)
    private float charHeight = 0.0f; // Character Height (Maximum; Pixels)
    private final float[] charWidths = new float[CHAR_CNT]; // Width of Each Character (Actual; Pixels)
    // Character Cell Width/Height
    private int cellWidth = 0;
    private int cellHeight = 0;

    private RectF[] regions=new RectF[CHAR_CNT];

    public Font() {

    }

    public boolean loadFont(Context context,String name,boolean readFromAssets) {
        LogSystem.debug(EngineUtils.tag,"Loading " + name + " font ...");
        //load xml
        InputStream xmlInputStream = null;
        try {
            if(readFromAssets) {
                xmlInputStream=context.getAssets().open(name + ".xml");
            } else {
                xmlInputStream = context.openFileInput(name + ".xml");
            }
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document dom = builder.parse(xmlInputStream);
            Node fontNode=dom.getElementsByTagName("font").item(0);
            NamedNodeMap fontAttributes=fontNode.getAttributes();
            cellWidth=Integer.parseInt(fontAttributes.getNamedItem("cellWidth").getNodeValue());
            cellHeight=Integer.parseInt(fontAttributes.getNamedItem("cellHeight").getNodeValue());
            int charCnt=CHAR_START;
            for (int i = 0; i < fontNode.getChildNodes().getLength(); i++) {
                if (fontNode.getChildNodes().item(i).getNodeName().equals("region")) {
                    Node regionNode = fontNode.getChildNodes().item(i);
                    float left=Float.parseFloat(regionNode.getAttributes().getNamedItem("left").getNodeValue());
                    float top=Float.parseFloat(regionNode.getAttributes().getNamedItem("top").getNodeValue());
                    float right=Float.parseFloat(regionNode.getAttributes().getNamedItem("right").getNodeValue());
                    float bottom=Float.parseFloat(regionNode.getAttributes().getNamedItem("bottom").getNodeValue());
                    regions[charCnt-CHAR_START]=new RectF(left,top,right,bottom);
                    float charWidth=Float.parseFloat(regionNode.getAttributes().getNamedItem("charWidth").getNodeValue());
                    charWidths[charCnt-CHAR_START]=charWidth;
                    charCnt++;
                }
            }
        } catch (SAXException e) {
            String err = e.getMessage();
            if (err != null) {
                LogSystem.error(EngineUtils.tag, "Cannot parse file: " + err);
            }
            e.printStackTrace();
            return false;
        } catch (ParserConfigurationException e) {
            String err = e.getMessage();
            if (err != null) {
                LogSystem.error(EngineUtils.tag, "Cannot parse file: " + err);
            }
            e.printStackTrace();
            return false;
        } catch (NumberFormatException e) {
            String err = e.getMessage();
            if (err != null) {
                LogSystem.error(EngineUtils.tag, "Cannot parse file: " + err);
            }
            e.printStackTrace();
            return false;
        } catch (IOException io) {
            String err = io.getMessage();
            if (err != null) {
                LogSystem.error(EngineUtils.tag, "Cannot parse file: " + err);
            }
            io.printStackTrace();
            return false;
        } catch (NullPointerException e) {
            String err = e.getMessage();
            if (err != null) {
                LogSystem.error(EngineUtils.tag, "Cannot parse file: " + err);
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (xmlInputStream != null) xmlInputStream.close();
            } catch (IOException io) {
                String err=io.getMessage();
                if(err != null) {
                    LogSystem.error(EngineUtils.tag, "Cannot close file: " + err);
                }
                io.printStackTrace();
            }
        }
        //load bitmap
        InputStream pngInputStream = null;
        try {
            try{
                if(readFromAssets) {
                    pngInputStream = context.getAssets().open(name + ".png");
                } else {
                    pngInputStream = context.openFileInput(name + ".png");
                }
                font = BitmapFactory.decodeStream(pngInputStream);
            }
            finally {
                if(pngInputStream != null) {
                    pngInputStream.close();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean saveFont(Context context,String name) {
        LogSystem.debug(EngineUtils.tag,"Saving " + name + "font ...");
        //save xml
        XmlSerializer serializer = Xml.newSerializer();
        try {
            FileOutputStream xmlOutputStream=context.openFileOutput(name + ".xml",Context.MODE_PRIVATE);
            try {
                serializer.setOutput(xmlOutputStream, "UTF-8");
                serializer.startDocument("UTF-8", true);
                serializer.startTag("", "font");
                serializer.attribute("", "name", name);
                serializer.attribute("", "cellWidth", Integer.toString(cellWidth));
                serializer.attribute("", "cellHeight", Integer.toString(cellHeight));
                int cnt=0;
                for(RectF region: regions) {
                    serializer.startTag("","region");
                    serializer.attribute("", "left", Float.toString(region.left));
                    serializer.attribute("", "top", Float.toString(region.top));
                    serializer.attribute("", "right", Float.toString(region.right));
                    serializer.attribute("","bottom",Float.toString(region.bottom));
                    serializer.attribute("","charWidth",Float.toString(charWidths[cnt++]));
                    serializer.endTag("","region");

                }
                serializer.endTag("", "font");
                serializer.endDocument();
                serializer.flush();
            } finally {
                xmlOutputStream.close();
            }
        } catch (IOException io) {
            String err = io.getMessage();
            if (err != null) {
                LogSystem.error(EngineUtils.tag, err);
            }
            io.printStackTrace();
            return false;
        }
        //save bitmap
        FileOutputStream pngOutputStream = null;
        try {
            try {
                pngOutputStream = context.openFileOutput(name + ".png", Context.MODE_PRIVATE);
                font.compress(Bitmap.CompressFormat.PNG, 100, pngOutputStream);
                pngOutputStream.flush();
                return true;
            } finally {
                if(pngOutputStream != null) {
                    pngOutputStream.close();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void create() {

        //create mesh
        VertexFormat vertexFormat = new VertexFormat(VertexFormat.VertexArrayLayout.VertexInterleaved);
        vertexFormat.addVertexAttribute(VertexFormat.Attribute.VertexAttrib1, "position", 0, VertexFormat.Type.Float, 2, 0);
        vertexFormat.addVertexAttribute(VertexFormat.Attribute.VertexAttrib2, "tex", 1, VertexFormat.Type.Float, 2, 0);

        ByteBuffer vertexBuffer = ByteBuffer.allocateDirect(4 * CHAR_CNT * vertexFormat.getVertexSize()).order(ByteOrder.nativeOrder());
        FloatBuffer vertexData = vertexBuffer.asFloatBuffer();
        vertexData.position(0);

        ByteBuffer indexBuffer = ByteBuffer.allocateDirect(6 * CHAR_CNT * IndexBuffer.getIndicesTypeSize(IndexBuffer.IndicesType.UShort)).order(ByteOrder.nativeOrder());
        ShortBuffer indexData = indexBuffer.asShortBuffer();
        indexBuffer.position(0);

        for(int i=0;i<CHAR_CNT;i++) {
            float[] plane = {
                    -1.0f, -1.0f, regions[i].left, regions[i].bottom,
                    1.0f, -1.0f, regions[i].right, regions[i].bottom,
                    -1.0f, 1.0f, regions[i].left, regions[i].top,
                    1.0f, 1.0f, regions[i].right, regions[i].top
            };
            vertexData.put(plane);

            short[] indices = {
                    (short)(4*i + 0),(short)(4*i + 1),(short)(4*i + 2),(short)(4*i + 2),(short)(4*i + 1),(short)(4*i + 3)
            };

            indexData.put(indices);
        }

        Mesh mesh=new Mesh(vertexFormat,vertexBuffer,4*CHAR_CNT, IndexBuffer.IndicesType.UShort, IndexBuffer.PrimitivesMode.Triangles,indexBuffer,6*CHAR_CNT);

        fontArray = new VertexArray(mesh.createVertexBuffer(GLBufferObject.Usage.StaticDraw),mesh.createIndexBuffer(GLBufferObject.Usage.StaticDraw));

        //create texture
        GLTexture texture=new GLTexture();
        texture.createTexture2DFromBitmap(font,true);
        TextureSampler sampler=new TextureSampler(GLTexture.Target.Texture2D);
        sampler.setMinFilter(TextureSampler.FilterFunc.LinearMipmapLinear);
        sampler.setMagFilter(TextureSampler.FilterFunc.Linear);
        sampler.setWrapS(TextureSampler.WrapFunc.ClampToEdge);
        sampler.setWrapT(TextureSampler.WrapFunc.ClampToEdge);
        fontTexture=new TextureUnit(texture,sampler);

        //create program
        fontProgram=new ProgramObject("font.prog");
        ProgramObject.Attribute attributes[]= {
                new ProgramObject.Attribute(0,"position"),
                new ProgramObject.Attribute(1,"tex")
        };
        fontProgram.setAttributes(attributes);
        fontProgram.setProgramProvider(new FontProgramProvider());
        fontProgram.link();

    }

    public void bind(int unit) {
        fontProgram.bind();
        fontTexture.bind(unit);
        fontProgram.setSampler("fontTexture",unit);
        fontArray.bind();
    }

    public void release() {
        fontArray.release();
        fontTexture.release();
        fontProgram.release();
    }

    public void drawChar(char ch,matrix3f textMatrix) {
        fontProgram.setUniformValue("textMatrix", textMatrix);
        fontArray.draw(6*((int)ch-CHAR_START),6);
    }

    public void drawChar(char ch,float scale,float x,float y) {
        matrix3f textMatrix=new matrix3f();
        textMatrix.loadIdentity();
        textMatrix.setTranslationPart(x, y);
        textMatrix.setScalePart(scale, scale);
        fontProgram.setUniformValue("textMatrix", textMatrix);
        fontArray.draw(6 * ((int) ch - CHAR_START), 6);
    }

    public void drawText(String text,float scaleX,float scaleY,float x,float y) {
        matrix3f textMatrix=new matrix3f();
        float lx = x + scaleX;
        float ly = y - scaleY;
        for(int i=0;i<text.length();i++) {
            char ch=text.charAt(i);
            textMatrix.loadIdentity();
            textMatrix.setTranslationPart(lx, ly);
            textMatrix.setScalePart(scaleX, scaleY);
            fontProgram.setUniformValue("textMatrix", textMatrix);
            fontArray.draw(6 * ((int) ch - CHAR_START), 6);
            lx += 2.0f * charWidths[ch-CHAR_START] * scaleX;
        }
    }

    public void delete() {
        if(fontArray!=null) {
            fontArray.delete();
        }
        if(fontTexture !=null) {
            fontTexture.delete();
        }
        if(fontProgram != null) {
            fontProgram.delete();
        }
    }


    public static class DistanceFieldFontGenerator {

        public static Font generateDefaultFont(int style,int size,int scaling,int padX,int padY) {
            return generateFont(Typeface.defaultFromStyle(style),size,scaling,padX,padY);
        }

        public static Font generateFont(String fontFamily,int style,int size,int scaling,int padX,int padY) {
            return generateFont(Typeface.create(fontFamily,style),size,scaling,padX,padY);
        }

        public static Font generateFont(Typeface typeface,int size,int scaling,int padX,int padY) {

            Font font=new Font();

            font.fontPadX = padX;
            font.fontPadY = padY;

            // load the font and setup paint instance for drawing
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setTextSize(size);
            paint.setColor(0xffffffff);
            paint.setTypeface(typeface);

            // get font metrics
            Paint.FontMetrics fm = paint.getFontMetrics(); // Get Font Metrics
            font.fontHeight = (float)Math.ceil( Math.abs( fm.bottom ) + Math.abs( fm.top ) ); // Calculate Font Height
            font.fontAscent = (float)Math.ceil( Math.abs( fm.ascent ) ); // Save Font Ascent
            font.fontDescent = (float)Math.ceil( Math.abs( fm.descent ) ); // Save Font Descent

            // determine the width of each character (including unknown character)
            // also determine the maximum character width
            char[] s = new char[2];
            font.charWidthMax = 0;
            font.charHeight = 0;
            float[] w = new float[2];
            int cnt = 0;
            for(char c = CHAR_START; c <= CHAR_END; c++ ) {
                s[0] = c;
                paint.getTextWidths( s, 0, 1, w );
                font.charWidths[cnt] = w[0];
                if(font.charWidths[cnt] > font.charWidthMax)
                    font.charWidthMax = font.charWidths[cnt];
                cnt++;
            }
            s[0] = CHAR_NONE;
            paint.getTextWidths( s, 0, 1, w );
            font.charWidths[cnt] = w[0];
            if(font.charWidths[cnt] > font.charWidthMax)
                font.charWidthMax = font.charWidths[cnt];
//            cnt++;

            // set character height to font height
            font.charHeight = font.fontHeight;

            // find the maximum size, validate, and setup cell sizes
            font.cellWidth = (int)font.charWidthMax + ( 2 * font.fontPadX );
            font.cellHeight = (int)font.charHeight + ( 2 * font.fontPadY );

            int maxSize = font.cellWidth > font.cellHeight ? font.cellWidth : font.cellHeight; // Save Max Size (Width/Height)

            //determine texture size
            int defSize=maxSize/scaling*((int)Math.sqrt(CHAR_CNT) + 1);
            int textureSize;
            if(defSize<=128)
                textureSize = 128;
            else if(defSize<=256)
                textureSize = 256;
            else if(defSize<=512)
                textureSize = 512;
            else if(defSize<=1024)
                textureSize = 1024;
            else if(defSize<=2048)
                textureSize = 2048;
            else
                return null;

            // create an empty bitmap (alpha only)
            Bitmap fontBitmap= Bitmap.createBitmap(maxSize,maxSize,Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas( fontBitmap );
            fontBitmap.eraseColor(0x00000000);

            // create distance field texture
            font.font = Bitmap.createBitmap(textureSize,textureSize, Bitmap.Config.ARGB_8888);
            font.font.eraseColor(0x00000000);

            // render each of the characters to the canvas (ie. build the font map)
            float sx = font.fontPadX;
            float sy = (font.cellHeight - 1 ) - font.fontDescent - font.fontPadY;

            int x=0;
            int y=0;
            int cw=font.cellWidth/scaling;
            int ch=font.cellHeight/scaling;

            for (char c=CHAR_START;c<=CHAR_END;c++) {
                s[0] = c;
                fontBitmap.eraseColor(0x00000000);
                canvas.drawText(s, 0, 1, sx, sy, paint);
                generateDistanceField(fontBitmap, font.font, new Rect(x, y, x + cw, y + ch), 48, scaling);
                font.regions[c-CHAR_START]=new RectF((float)x/textureSize,(float)y/textureSize,(float)(x+cw)/textureSize,(float)(y+ch)/textureSize);
                font.charWidths[c-CHAR_START]/=(float)font.cellWidth;
//                LogSystem.debug("RectF of " + c + ":","("+font.regions[c-CHAR_START].left+","+font.regions[c-CHAR_START].top+","+font.regions[c-CHAR_START].right+","+font.regions[c-CHAR_START].bottom+")");
                x+=cw;
                if(x+cw>textureSize) {
                    x = 0 ;
                    y += ch;
                }

            }

            s[0] = CHAR_NONE;
            canvas.drawText( s, 0, 1, sx, sy, paint );
            generateDistanceField(fontBitmap, font.font, new Rect(x, y, x + cw, y + ch), 48, scaling);
            font.regions[CHAR_UNKNOWN]=new RectF(x/textureSize,y/textureSize,(x+cw)/textureSize,(y+ch)/textureSize);
            font.charWidths[CHAR_UNKNOWN]/=(float)font.cellWidth;

            return font; // Return Success
        }

        private static void generateDistanceField(Bitmap srcImage,Bitmap dstImage,Rect dstRect,int spread,int scaling) {

            int dstWidth=dstRect.width();
            int dstHeight=dstRect.height();

            for(int i=0;i<dstWidth;i++) {
                for(int j=0;j<dstHeight;j++) {

                    float d = findDistance(srcImage,i*scaling+scaling/2,j*scaling+scaling/2,spread);

                    float alpha = 0.5f - 0.5f*d;
                    alpha = Math.min(Math.max(0f, alpha), 1f);
//                    int alphaByte = (int) (alpha * 255);
                    int alphaByte = (int) (alpha * 65535f);
//                    dstImage.setPixel(i + dstRect.left, j + dstRect.top, ((alphaByte) << 24) | 0x00ffffff);
                    dstImage.setPixel(i + dstRect.left, j + dstRect.top, (alphaByte<<16)|0x0000ffff);
                }
            }
        }

        private static float findDistance(Bitmap srcImage,int x,int y,int spread) {
            boolean pixel=thresholdFunc(srcImage.getPixel(x, y));

            int sx=Math.max(0,x-spread);
            int ex=Math.min(x+spread,srcImage.getWidth());

            int sy=Math.max(0,y-spread);
            int ey=Math.min(y+spread,srcImage.getHeight());

            float minDistance=spread;

            for(int i=sx;i<ex;i++) {
                for(int j=sy;j<ey;j++) {
                    if(pixel!=thresholdFunc(srcImage.getPixel(i,j))) {
                        float d=distanceFunc(x, y, i, j);
                        if(d<=spread) {
                            minDistance = Math.min(minDistance, d);
                        }
                    }
                }
            }
            return (pixel ? -1f : 1f) * Math.min(minDistance/spread,1f);
        }

        private static float distanceFunc(int cx,int cy,int px,int py) {
            float dx=cx-px;
            float dy=cy-py;
            return (float)Math.sqrt(dx*dx+dy*dy);
        }

        public static boolean thresholdFunc(int color) {
            if(Color.red(color)>=128 ||
                    Color.green(color)>=128 ||
                    Color.blue(color)>=128 ||
                    Color.alpha(color)>=128) {
                return true;
            }
            return false;
        }
    }

    private class FontProgramProvider implements ProgramObject.ProgramProvider {

        private final static String vertexShader =
                "attribute vec2 position;\n" +
                "attribute vec2 tex;\n" +
                "\n" +
//                "uniform vec2 aspect;\n" +
                "uniform mat3 textMatrix;\n" +
                "\n" +
                "varying vec2 texCoord;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                    "\ttexCoord = tex;\n" +
                    "\t vec3 p = textMatrix * vec3(position, 1.0);" +
                    "\tgl_Position = vec4(p.xy, 0.0, 1.0);\n" +
                "}\n" +
                "\n";

        private final static String fragmentShader =
                "#ifdef GL_FRAGMENT_PRECISION_HIGH\n" +
                    "\tprecision highp float;\n" +
                "#else\n" +
                    "\tprecision mediump float;\n" +
                "#endif\n" +
                "uniform sampler2D fontTexture;\n" +
                "\n" +
                "varying vec2 texCoord;\n" +
                "const float smoothing = 1.0/16.0;\n" +
                "\n" +
                "void main()\n" +
                "{\n" +
                        "\tvec4 cl = texture2D(fontTexture,texCoord);\n" +
                        "\tfloat distance= cl.a+cl.r/255.0;\n" +
                        "\tfloat alpha = smoothstep(0.5-smoothing,0.5+smoothing,distance);\n" +
                        "\tgl_FragColor = vec4(1,1,1,alpha);\n" +
                "}\n";

        public String getVertexShader() {
            return vertexShader;
        }

        public String getFragmentShader() {
            return fragmentShader;
        }
    }
}
