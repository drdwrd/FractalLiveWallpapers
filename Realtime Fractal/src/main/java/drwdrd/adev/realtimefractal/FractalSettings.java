package drwdrd.adev.realtimefractal;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.PointF;
import android.preference.PreferenceManager;
import android.util.Xml;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import drwdrd.adev.engine.LogSystem;
import drwdrd.adev.engine.TextFileReader;
import drwdrd.adev.engine.vector2f;
import drwdrd.adev.engine.vector3f;
import drwdrd.adev.realtimefractal.preferences.RealtimeFractalSettingsActivity;
import drwdrd.adev.ui.PaletteEditor.PaletteInfo;
import drwdrd.adev.ui.Point3Preference;
import drwdrd.adev.ui.PointPreference;

public class FractalSettings implements SharedPreferences.OnSharedPreferenceChangeListener {

    public interface OnFractalSettingsChangedListener {
        public void onFractalSettingsChanged(FractalSettings fractalSettings,String key);
    }

    private ArrayList<OnFractalSettingsChangedListener> fractalSettingsChangedListeners=new ArrayList<OnFractalSettingsChangedListener>();

    //timer settings
    public float timeScale=0.0001f;
    public float timerScalingFactor=0.5f;
    public boolean scaleTimeWithZoom=false;

    //general fractal settings
    public enum FractalType {
        Julia("julia"),Mandelbrot("mandelbrot");

        FractalType(String value) {
            this.value=value;
        }

        public static FractalType fromValue(String value) {
            if (value != null) {
                for (FractalType fractalType : values()) {
                    if (fractalType.value.equals(value)) {
                        return fractalType;
                    }
                }
            }
            throw new IllegalArgumentException("Invalid value: " + value);
        }

        public String toValue() {
            return value;
        }

        private final String value;
    }

    public enum ColorFunc {
        IterationCount("iterationCount"),Smooth("smooth"),DistanceEstimator("distanceEstimator"),OrbitTraps("orbitTraps"),DebugDistanceField("debugDistanceField");

        ColorFunc(String value) {
            this.value=value;
        }

        public static ColorFunc fromValue(String value) {
            if (value != null) {
                for (ColorFunc colorFunc : values()) {
                    if (colorFunc.value.equals(value)) {
                        return colorFunc;
                    }
                }
            }
            throw new IllegalArgumentException("Invalid value: " + value);
        }

        public String toValue() {
            return value;
        }

        private final String value;
    }

    public enum TransferFunc {
        Power("pow"),Power2("pow2"),Exponential("exp"),Exponential2("exp2"),Sine("sin"),Cosine("cos");

        TransferFunc(String value) {
            this.value=value;
        }

        public static TransferFunc fromValue(String value) {
            if (value != null) {
                for (TransferFunc transferFunc : values()) {
                    if (transferFunc.value.equals(value)) {
                        return transferFunc;
                    }
                }
            }
            throw new IllegalArgumentException("Invalid value: " + value);
        }

        public String toValue() {
            return value;
        }

        private final String value;
    }

    public float bailout=2.0f;
    public float bailin=0.001f;
    public float colorGradient=1.0f;
    public int maxIterations=200;
    public int warmupIterations=100;
    public FractalType fractalType=FractalType.Julia;
    public ColorFunc colorFunc=ColorFunc.Smooth;
    public TransferFunc transferFunc=TransferFunc.Power;

    //orbit traps specific
    public enum DistanceFunc {
        Point("point"),Line("line"),Circle("circle"),Sin("sinfunc");

        DistanceFunc(String value) {
            this.value=value;
        }

        public static DistanceFunc fromValue(String value) {
            if (value != null) {
                for (DistanceFunc distanceFunc : values()) {
                    if (distanceFunc.value.equals(value)) {
                        return distanceFunc;
                    }
                }
            }
            throw new IllegalArgumentException("Invalid value: " + value);
        }

        public String toValue() {
            return value;
        }

        private final String value;
    }

    public class OrbitTrap {
        private String key;
        private DistanceFunc distanceFunc;
        private vector3f param;

        public OrbitTrap(DistanceFunc distanceFunc,String key,vector3f param) {
            this.distanceFunc=distanceFunc;
            this.key=key;
            this.param=param;
        }

        public OrbitTrap(DistanceFunc distanceFunc,String key,vector2f param) {
            this.distanceFunc=distanceFunc;
            this.key=key;
            this.param=new vector3f(param.ex,param.ey,0.0f);
        }

        public OrbitTrap(DistanceFunc distanceFunc,String key,float param) {
            this.distanceFunc=distanceFunc;
            this.key=key;
            this.param=new vector3f(param,0.0f,0.0f);
        }

        public DistanceFunc getDistanceFunc() {
            return distanceFunc;
        }

        public String getKey() {
            return key;
        }

        public float getParam1f() {
            return param.ex;
        }

        public vector2f getParam2f() {
            return new vector2f(param.ex,param.ey);
        }

        public vector3f getParam3f() {
            return param;
        }
    }

    private ArrayList<OrbitTrap> orbitTraps=new ArrayList<OrbitTrap>();

    public float gamma=1.0f;
    public PaletteInfo paletteInfo=new PaletteInfo(6);

    //fractal scene
    public FractalScene fractalScene=new FractalScene();

    public FractalSettings(Context context) {
        LogSystem.debug(RealtimeFractalService.tag, "FractalSettings.FractalSettings()...");
        loadDefaultSettings(context);
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    public FractalSettings(Context context,String fileName) {
        LogSystem.debug(RealtimeFractalService.tag, "FractalSettings.FractalSettings()...");
        if(!loadFromXml(context,fileName)) {
            Toast msg = Toast.makeText(context, "Error while reading from file " + fileName + "!", Toast.LENGTH_SHORT);
            msg.show();
            loadDefaultSettings(context);
        }
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    public FractalSettings(Context context, InputStream stream) {
        LogSystem.debug(RealtimeFractalService.tag, "FractalSettings.FractalSettings()...");
        if(!loadFromXml(context,stream)) {
            loadDefaultSettings(context);
        }
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    public ArrayList<OrbitTrap> getOrbitTraps() {
        return orbitTraps;
    }

    public void addNewOrbitTrap(DistanceFunc distanceFunc,String key,vector3f param) {
        LogSystem.debug(RealtimeFractalService.tag,"FractalSettings.addNewOrbitTrap(), key = " + key);
        orbitTraps.add(new OrbitTrap(distanceFunc,key,param));
        for(OnFractalSettingsChangedListener listener: fractalSettingsChangedListeners) {
            listener.onFractalSettingsChanged(this,"orbitTrap");
        }
    }

    public void addNewOrbitTrap(DistanceFunc distanceFunc,String key,vector2f param) {
        LogSystem.debug(RealtimeFractalService.tag,"FractalSettings.addNewOrbitTrap(), key = " + key);
        orbitTraps.add(new OrbitTrap(distanceFunc,key,param));
        for(OnFractalSettingsChangedListener listener: fractalSettingsChangedListeners) {
            listener.onFractalSettingsChanged(this,"orbitTrap");
        }
    }

    public void addNewOrbitTrap(DistanceFunc distanceFunc,String key,float param) {
        LogSystem.debug(RealtimeFractalService.tag,"FractalSettings.addNewOrbitTrap(), key = " + key);
        orbitTraps.add(new OrbitTrap(distanceFunc,key,param));
        for(OnFractalSettingsChangedListener listener: fractalSettingsChangedListeners) {
            listener.onFractalSettingsChanged(this,"orbitTrap");
        }
    }

    public void removeOrbitTrap(String key) {
        LogSystem.debug(RealtimeFractalService.tag,"FractalSettings.removeOrbitTrap(), key = " + key);
        for(OrbitTrap trap :orbitTraps) {
            if(key.equals(trap.getKey())) {
                orbitTraps.remove(trap);
                for(OnFractalSettingsChangedListener listener: fractalSettingsChangedListeners) {
                    listener.onFractalSettingsChanged(this,"orbitTrap");
                }
                return;
            }
        }
    }

    public void loadDefaultSettings(Context context) {
        /*
            create presets
         */
        LogSystem.debug(RealtimeFractalService.tag, "Writing presets...");
        AssetManager mgr = context.getAssets();
        try {
            String presets[] = mgr.list("presets");
            for(String preset: presets) {
                String xml = TextFileReader.readFromAssets(context,"presets/" + preset);
                if(xml != null) {
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(preset, Context.MODE_PRIVATE));
                    outputStreamWriter.write(xml);
                    outputStreamWriter.close();
                }
            }
        } catch(IOException io) {
            String err = io.getMessage();
            if (err != null) {
                LogSystem.error(RealtimeFractalService.tag, err);
            }
            io.printStackTrace();
        }

        fractalScene.appendSceneNode(new vector2f(0.5f,0.25f), new vector2f(0.0f,0.0f), 2.0f, 0.0f);
        saveToSharedPreferences(context);
    }

    public void registerFractalSettingsChangedListener(OnFractalSettingsChangedListener listener) {
        fractalSettingsChangedListeners.add(listener);
    }

    public void unregisterFractalSettingsChangedListener(OnFractalSettingsChangedListener listener) {
        fractalSettingsChangedListeners.remove(listener);
    }

    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
//        LogSystem.debug(RealtimeFractalService.tag,"FractalSettings.onSharedPreferenceChanged()");
        if(key.equals("fractalType")) {
            fractalType = FractalType.fromValue(prefs.getString("fractalType", "julia"));
        } else if(key.equals("fractalColorFunc")) {
            colorFunc=ColorFunc.fromValue(prefs.getString("fractalColorFunc","smooth"));
        } else if(key.equals("transferFunc")) {
            transferFunc=TransferFunc.fromValue(prefs.getString("transferFunc","pow"));
        } else if(key.equals("bailout")) {
            bailout=Float.parseFloat(prefs.getString("bailout","2.0"));
        } else if(key.equals("bailin")) {
            bailin=Float.parseFloat(prefs.getString("bailin", "0.001"));
        } else if(key.equals("maxIterations")) {
            maxIterations=Integer.parseInt(prefs.getString("maxIterations", "200"));
        } else if(key.equals("colorGradient")) {
            colorGradient=Float.parseFloat(prefs.getString("colorGradient","1.0"));
        } else if(key.equals("timeScale")) {
            timeScale=0.00002f*prefs.getFloat("timeScale",1.0f);
        } else if(key.equals("timeScalingFactor")) {
            timerScalingFactor=1.0f+prefs.getFloat("timeScalingFactor",50f);
        } else if(key.equals("scaleTimeWithZoom")) {
            scaleTimeWithZoom=prefs.getBoolean("scaleTimeWithZoom",false);
        } else if(key.contains("orbitTrap")) {
            updateOrbitTrapsSettings(prefs);
        } else if(key.equals("gamma")||key.equals("palette")) {
            loadColoringModeSettings(prefs);
        }
        for(OnFractalSettingsChangedListener listener: fractalSettingsChangedListeners) {
            listener.onFractalSettingsChanged(this,key);
        }
    }


    private void loadColoringModeSettings(SharedPreferences prefs) {
        gamma = prefs.getFloat("gamma", 1.0f);
        paletteInfo=PaletteInfo.parsePalette(prefs.getString("palette",""));
    }


    private void updateOrbitTrapsSettings(SharedPreferences prefs) {
        for(OrbitTrap trap :orbitTraps) {
            String key=trap.getKey();
            switch (trap.distanceFunc) {
                case Point:
                    PointF pt = PointPreference.parse(prefs.getString(key, PointPreference.stringify(0.0f,0.0f)));
                    trap.param=new vector3f(pt.x,pt.y,0.0f);
                    break;
                case Line:
                    PointF l = PointPreference.parse(prefs.getString(key, PointPreference.stringify(1.0f,0.0f)));
                    trap.param=new vector3f(l.x,l.y,0.0f);
                    break;
                case Circle:
                    float[] c = Point3Preference.parse(prefs.getString(key, Point3Preference.stringify(0.0f,0.0f,1.0f)));
                    trap.param=new vector3f(c[0],c[1],c[2]);
                    break;
                case Sin:
                    PointF params = PointPreference.parse(prefs.getString(key,PointPreference.stringify(1.0f,1.0f)));
                    trap.param=new vector3f(params.x,params.y,0.0f);
                    break;
            }
        }
    }

    public boolean saveToXml(Context context,String fileName) {
        LogSystem.debug(RealtimeFractalService.tag,"Saving fractal to file: " + fileName);
        //TODO: hack: recalculate timers before saving
        fractalScene.recalculateNodeTimers();

        XmlSerializer serializer = Xml.newSerializer();
        try {
            FileOutputStream stream=context.openFileOutput(fileName,Context.MODE_PRIVATE);
            try {
                serializer.setOutput(stream, "UTF-8");
                return serializeToXml(serializer);
            } finally {
                stream.close();
            }
        } catch (IOException io) {
            String err = io.getMessage();
            if (err != null) {
                LogSystem.error(RealtimeFractalService.tag, err);
            }
            io.printStackTrace();
            return false;
        }
    }

    public String saveToXmlString() {
        //TODO: hack: recalculate timers before saving
        fractalScene.recalculateNodeTimers();

        XmlSerializer serializer = Xml.newSerializer();
        try {
            StringWriter writer = new StringWriter();
            serializer.setOutput(writer);
            serializeToXml(serializer);
            return writer.toString();
        } catch (IOException io) {
            String err = io.getMessage();
            if (err != null) {
                LogSystem.error(RealtimeFractalService.tag, err);
            }
            io.printStackTrace();
            return "";
        }
    }

    private boolean serializeToXml(XmlSerializer serializer) {
        try {
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "settings");
            serializer.startTag("", "fractal");
            serializer.attribute("", "fractalType", fractalType.toValue());
            serializer.attribute("", "colorFunc", colorFunc.toValue());
            serializer.attribute("", "transferFunc", transferFunc.toValue());
            serializer.attribute("", "bailout", Float.toString(bailout));
            serializer.attribute("", "bailin", Float.toString(bailin));
            serializer.attribute("", "maxIterations", Integer.toString(maxIterations));
            serializer.attribute("", "colorGradient", Float.toString(colorGradient));
            if (colorFunc == ColorFunc.OrbitTraps) {
                serializer.startTag("", "orbitTraps");
                for (OrbitTrap trap : orbitTraps) {
                    vector3f param = trap.getParam3f();
                    switch (trap.getDistanceFunc()) {
                        case Line:
                            serializer.startTag("", "line");
                            serializer.attribute("", "a", Float.toString(param.ex));
                            serializer.attribute("", "b", Float.toString(param.ey));
                            serializer.endTag("", "line");
                            break;
                        case Point:
                            serializer.startTag("", "point");
                            serializer.attribute("", "x", Float.toString(param.ex));
                            serializer.attribute("", "y", Float.toString(param.ey));
                            serializer.endTag("", "point");
                            break;
                        case Circle:
                            serializer.startTag("", "circle");
                            serializer.attribute("", "x", Float.toString(param.ex));
                            serializer.attribute("", "y", Float.toString(param.ey));
                            serializer.attribute("", "r", Float.toString(param.ez));
                            serializer.endTag("", "circle");
                            break;
                        case Sin:
                            serializer.startTag("", "sin");
                            serializer.attribute("", "amplitude", Float.toString(param.ex));
                            serializer.attribute("", "frequency", Float.toString(param.ey));
                            serializer.endTag("", "sin");
                            break;
                    }
                }
                serializer.endTag("", "orbitTraps");
            }
            serializer.endTag("", "fractal");

            serializer.startTag("", "coloring");
            serializer.attribute("", "gamma", Float.toString(gamma));
            serializer.startTag("", "palette");
            for (PaletteInfo.KeyColor keyColor : paletteInfo.getKeyColors()) {
                serializer.startTag("", "keyColor");
                serializer.attribute("", "color", Integer.toString(keyColor.color));
                serializer.attribute("", "offset", Float.toString(keyColor.offset));
                serializer.attribute("", "enabled", Boolean.toString(keyColor.enabled));
                serializer.endTag("", "keyColor");
            }
            serializer.endTag("", "palette");
            serializer.endTag("", "coloring");

            serializer.startTag("", "time");
            serializer.attribute("", "timeScale", Float.toString(timeScale));
            serializer.attribute("", "timeScalingFactor", Float.toString(timerScalingFactor));
            serializer.attribute("", "scaleTimeWithZoom", Boolean.toString(scaleTimeWithZoom));
            serializer.endTag("", "time");

            serializer.startTag("", "scene");
            for (int i = 0; i < fractalScene.getSceneNodesCount(); i++) {
                FractalScene.SceneNode node = fractalScene.getSceneNode(i);
                serializer.startTag("", "node");
                serializer.attribute("", "scale", Float.toString(node.scale));
                serializer.attribute("", "rotation", Float.toString(node.rotation));
                serializer.attribute("", "startTime", Float.toString(node.startTime));
                serializer.startTag("", "param");
                serializer.attribute("", "re", Float.toString(node.paramC.ex));
                serializer.attribute("", "im", Float.toString(node.paramC.ey));
                serializer.endTag("", "param");
                serializer.startTag("", "centerPoint");
                serializer.attribute("", "x", Float.toString(node.centerPoint.ex));
                serializer.attribute("", "y", Float.toString(node.centerPoint.ey));
                serializer.endTag("", "centerPoint");
                serializer.endTag("", "node");
            }
            serializer.endTag("", "scene");
            serializer.endTag("", "settings");
            serializer.endDocument();
            serializer.flush();
            return true;
        } catch (IOException io) {
            String err = io.getMessage();
            if (err != null) {
                LogSystem.error(RealtimeFractalService.tag, err);
            }
            io.printStackTrace();
            return false;
        }
    }

    public boolean loadFromXml(Context context,String fileName) {
        LogSystem.debug(RealtimeFractalService.tag,"Loading fractal from file: " + fileName);
        InputStream stream = null;
        try {
            stream = context.openFileInput(fileName);
            return loadFromXml(context, stream);
        } catch (IOException io) {
            String err = io.getMessage();
            if (err != null) {
                LogSystem.error(RealtimeFractalService.tag, "Cannot parse file: " + err);
            }
            io.printStackTrace();
            return false;
        } finally {
            try {
                if (stream != null) stream.close();
            } catch (IOException io) {
                String err=io.getMessage();
                if(err != null) {
                    LogSystem.error(RealtimeFractalService.tag, "Cannot close file: " + err);
                }
                io.printStackTrace();
            }
        }
    }

    protected boolean loadFromXml(Context context, InputStream stream) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document dom = builder.parse(stream);
            Node fractalNode=dom.getElementsByTagName("fractal").item(0);
            NamedNodeMap fractalAttributes=fractalNode.getAttributes();
            fractalType=FractalType.fromValue(fractalAttributes.getNamedItem("fractalType").getNodeValue());
            colorFunc=ColorFunc.fromValue(fractalAttributes.getNamedItem("colorFunc").getNodeValue());
            transferFunc=TransferFunc.fromValue(fractalAttributes.getNamedItem("transferFunc").getNodeValue());
            bailout=Float.parseFloat(fractalAttributes.getNamedItem("bailout").getNodeValue());
            bailin=Float.parseFloat(fractalAttributes.getNamedItem("bailin").getNodeValue());
            maxIterations=Integer.parseInt(fractalAttributes.getNamedItem("maxIterations").getNodeValue());
            colorGradient=Float.parseFloat(fractalAttributes.getNamedItem("colorGradient").getNodeValue());
            //try to find and parse orbitTraps definition
            Node orbitTrapsNode=null;
            for (int i = 0; i < fractalNode.getChildNodes().getLength(); i++) {
                if (fractalNode.getChildNodes().item(i).getNodeName().equals("orbitTraps")) {
                    orbitTrapsNode = fractalNode.getChildNodes().item(i);
                }
            }
            if(orbitTrapsNode!=null) {
                int count=0;
                orbitTraps.clear();
                for (int i = 0; i < orbitTrapsNode.getChildNodes().getLength(); i++) {
                    Node distanceFuncNode = orbitTrapsNode.getChildNodes().item(i);
                    String distanceFuncName = distanceFuncNode.getNodeName();
                    if (distanceFuncName.equals("line")) {
                        String key="orbitTrapLine" + count;
                        float a = Float.parseFloat(distanceFuncNode.getAttributes().getNamedItem("a").getNodeValue());
                        float b = Float.parseFloat(distanceFuncNode.getAttributes().getNamedItem("b").getNodeValue());
                        OrbitTrap trap=new OrbitTrap(DistanceFunc.Line,key,new vector2f(a,b));
                        orbitTraps.add(trap);
                        count++;
                    } else if (distanceFuncName.equals("point")) {
                        String key="orbitTrapPoint" + count;
                        float x = Float.parseFloat(distanceFuncNode.getAttributes().getNamedItem("x").getNodeValue());
                        float y = Float.parseFloat(distanceFuncNode.getAttributes().getNamedItem("y").getNodeValue());
                        OrbitTrap trap=new OrbitTrap(DistanceFunc.Point,key,new vector2f(x,y));
                        orbitTraps.add(trap);
                        count++;
                    } else if (distanceFuncName.equals("circle")) {
                        String key="orbitTrapCircle" + count;
                        float x = Float.parseFloat(distanceFuncNode.getAttributes().getNamedItem("x").getNodeValue());
                        float y = Float.parseFloat(distanceFuncNode.getAttributes().getNamedItem("y").getNodeValue());
                        float r = Float.parseFloat(distanceFuncNode.getAttributes().getNamedItem("r").getNodeValue());
                        OrbitTrap trap=new OrbitTrap(DistanceFunc.Circle,key,new vector3f(x,y,r));
                        orbitTraps.add(trap);
                        count++;
                    } else if (distanceFuncName.equals("sin")) {
                        String key="orbitTrapSin" + count;
                        float amplitude = Float.parseFloat(distanceFuncNode.getAttributes().getNamedItem("amplitude").getNodeValue());
                        float frequency = Float.parseFloat(distanceFuncNode.getAttributes().getNamedItem("frequency").getNodeValue());
                        OrbitTrap trap=new OrbitTrap(DistanceFunc.Sin,key,new vector2f(amplitude,frequency));
                        orbitTraps.add(trap);
                        count++;
                    }
                }
            }

            Node coloringNode=dom.getElementsByTagName("coloring").item(0);
            gamma=Float.parseFloat(coloringNode.getAttributes().getNamedItem("gamma").getNodeValue());
            Node paletteNode = dom.getElementsByTagName("palette").item(0);
            paletteInfo.getKeyColors().clear();
            for (int i = 0; i < paletteNode.getChildNodes().getLength(); i++) {
                if (paletteNode.getChildNodes().item(i).getNodeName().equals("keyColor")) {
                    Node keyColorNode = paletteNode.getChildNodes().item(i);
                    int color=Integer.parseInt(keyColorNode.getAttributes().getNamedItem("color").getNodeValue());
                    float offset=Float.parseFloat(keyColorNode.getAttributes().getNamedItem("offset").getNodeValue());
                    boolean enabled=Boolean.parseBoolean(keyColorNode.getAttributes().getNamedItem("enabled").getNodeValue());
                    paletteInfo.addColor(color, offset, enabled);
                }
            }

            Node timeNode=dom.getElementsByTagName("time").item(0);
            timeScale=Float.parseFloat(timeNode.getAttributes().getNamedItem("timeScale").getNodeValue());
            timerScalingFactor=Float.parseFloat(timeNode.getAttributes().getNamedItem("timeScalingFactor").getNodeValue());
            scaleTimeWithZoom=Boolean.parseBoolean(timeNode.getAttributes().getNamedItem("scaleTimeWithZoom").getNodeValue());

            fractalScene.clear();
            Node fractalSceneNode=dom.getElementsByTagName("scene").item(0);
            for (int i = 0; i < fractalSceneNode.getChildNodes().getLength(); i++) {
                Node sceneNode = fractalSceneNode.getChildNodes().item(i);
                if(sceneNode.getNodeName().equals("node")) {
                    float scale=Float.parseFloat(sceneNode.getAttributes().getNamedItem("scale").getNodeValue());
                    float rotation=Float.parseFloat(sceneNode.getAttributes().getNamedItem("rotation").getNodeValue());
                    float startTime=Float.parseFloat(sceneNode.getAttributes().getNamedItem("startTime").getNodeValue());
                    vector2f paramC=null;
                    vector2f centerPoint=null;
                    for(int k=0;k<sceneNode.getChildNodes().getLength();k++) {
                        if(sceneNode.getChildNodes().item(k).getNodeName().equals("param")) {
                            float re=Float.parseFloat(sceneNode.getChildNodes().item(k).getAttributes().getNamedItem("re").getNodeValue());
                            float im=Float.parseFloat(sceneNode.getChildNodes().item(k).getAttributes().getNamedItem("im").getNodeValue());
                            paramC=new vector2f(re,im);
                        } else if(sceneNode.getChildNodes().item(k).getNodeName().equals("centerPoint")) {
                            float x=Float.parseFloat(sceneNode.getChildNodes().item(k).getAttributes().getNamedItem("x").getNodeValue());
                            float y=Float.parseFloat(sceneNode.getChildNodes().item(k).getAttributes().getNamedItem("y").getNodeValue());
                            centerPoint=new vector2f(x,y);
                        }
                    }
                    fractalScene.addSceneNode(startTime,paramC,centerPoint,scale,rotation);
                }
            }
        } catch (SAXException e) {
            String err = e.getMessage();
            if (err != null) {
                LogSystem.error(RealtimeFractalService.tag, "Cannot parse xml: " + err);
            }
            e.printStackTrace();
            return false;
        } catch (ParserConfigurationException e) {
            String err = e.getMessage();
            if (err != null) {
                LogSystem.error(RealtimeFractalService.tag, "Cannot parse xml: " + err);
            }
            e.printStackTrace();
            return false;
        } catch (NumberFormatException e) {
            String err = e.getMessage();
            if (err != null) {
                LogSystem.error(RealtimeFractalService.tag, "Cannot parse xml: " + err);
            }
            e.printStackTrace();
            return false;
        } catch (NullPointerException e) {
            String err = e.getMessage();
            if (err != null) {
                LogSystem.error(RealtimeFractalService.tag, "Cannot parse xml: " + err);
            }
            e.printStackTrace();
            return false;
        } catch (IOException io) {
            String err = io.getMessage();
            if (err != null) {
                LogSystem.error(RealtimeFractalService.tag, "Cannot parse xml: " + err);
            }
            io.printStackTrace();
            return false;
        }
        saveToSharedPreferences(context);
        return true;
    }

    protected void  saveToSharedPreferences(Context context) {
        SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.clear();
        editor.putString("fractalType",fractalType.toValue());
        editor.putString("fractalColorFunc",colorFunc.toValue());
        editor.putString("transferFunc",transferFunc.toValue());
        editor.putString("bailout",Float.toString(bailout));
        editor.putString("bailin",Float.toString(bailin));
        editor.putString("maxIterations",Integer.toString(maxIterations));
        editor.putString("colorGradient",Float.toString(colorGradient));
        //try to find and parse orbitTraps definition
        for(OrbitTrap trap :orbitTraps) {
            String key=trap.getKey();
            vector3f param=trap.getParam3f();
            switch (trap.getDistanceFunc()) {
                case Point:
                    editor.putString(key,PointPreference.stringify(param.ex,param.ey));
                    break;
                case Line:
                    editor.putString(key,PointPreference.stringify(param.ex,param.ey));
                    break;
                case Circle:
                    editor.putString(key,Point3Preference.stringify(param.ex,param.ey,param.ez));
                    break;
                case Sin:
                    editor.putString(key,PointPreference.stringify(param.ex,param.ey));
                    break;
            }
        }

        editor.putFloat("gamma",gamma);
        editor.putString("palette", PaletteInfo.toString(paletteInfo));

        editor.putFloat("timeScale",50000.0f*timeScale);
        editor.putFloat("timerScalingFactor",timerScalingFactor-1.0f);
        editor.putBoolean("scaleTimeWithZoom",scaleTimeWithZoom);

        editor.apply();
    }
}
