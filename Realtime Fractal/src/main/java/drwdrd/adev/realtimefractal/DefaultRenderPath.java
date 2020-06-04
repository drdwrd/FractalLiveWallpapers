package drwdrd.adev.realtimefractal;

import android.content.Context;
import android.opengl.GLES20;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import drwdrd.adev.engine.GLBufferObject;
import drwdrd.adev.engine.GLTexture;
import drwdrd.adev.engine.LogSystem;
import drwdrd.adev.engine.Mesh;
import drwdrd.adev.engine.ProgramObject;
import drwdrd.adev.engine.ShaderObject;
import drwdrd.adev.engine.TextFileReader;
import drwdrd.adev.engine.TextureSampler;
import drwdrd.adev.engine.TextureUnit;
import drwdrd.adev.engine.VertexArray;
import drwdrd.adev.engine.matrix3f;
import drwdrd.adev.engine.matrix4f;
import drwdrd.adev.engine.vector2f;
import drwdrd.adev.engine.vector3f;
import drwdrd.adev.engine.vector4f;

public class DefaultRenderPath implements RenderPath, FractalSettings.OnFractalSettingsChangedListener {

    protected Context context=null;

    protected Mesh meshData=null;

    protected String renderFractalVertexProgramSource=null;
    protected String renderFractalFragmentProgramSource=null;
    protected String renderFractalTransferFuncProgramSource=null;
    protected String renderFractalDistanceFuncSource=null;
    protected String renderFractalColorFuncSource=null;

    protected ProgramObject renderFractalProgram=null;
    protected VertexArray vertexArray=null;
    protected TextureUnit palette=null;

    protected int screenWidth=1;
    protected int screenHeight=1;

    protected vector2f aspect=new vector2f(1.0f, 1.0f);
    protected matrix3f texMatrix=new matrix3f();

    protected int wallpaperMode=2;				/*1-center,2-fit,3-stretch*/
    protected boolean initialized=false;


    public DefaultRenderPath() {

    }

    public void onCreate(Context context) {
        LogSystem.debug(RealtimeFractalService.tag,"DefaultRenderPath.onCreate()");

        this.context=context;

        FractalSettings fractalSettings=RealtimeFractalService.getFractalSettings();

        renderFractalFragmentProgramSource=loadFragmentProgram(fractalSettings);
        renderFractalDistanceFuncSource=loadDistanceFuncFragmentProgram(fractalSettings);
        renderFractalColorFuncSource=loadColorFuncFragmentProgram(fractalSettings);
        renderFractalTransferFuncProgramSource=loadTransferFuncFragmentProgram(fractalSettings);

        fractalSettings.registerFractalSettingsChangedListener(this);

        meshData=Mesh.MeshGenerator.createSimplePlane();

        renderFractalVertexProgramSource=TextFileReader.readFromAssets(context, "shaders/fractal.vert");

        texMatrix.loadIdentity();
/*        texMatrix.setScalePart(-2.0f,-2.0f);
        texMatrix.setTranslationPart(1.0f,1.0f);*/
    }

    public void onDestroy() {
        LogSystem.debug(RealtimeFractalService.tag,"DefaultRenderPath.onDestroy()");
        RealtimeFractalService.getFractalSettings().unregisterFractalSettingsChangedListener(this);
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void onRelease() {
        initialized=false;
        doRelease();
    }

    public void onInitialize() {
        doInit();
        initialized=true;
    }

    public void onRender(FractalScene.SceneNode scene,float time) {

        GLES20.glViewport(0,0,screenWidth,screenHeight);

        renderFractal(scene);
    }

    public void setViewport(int width,int height) {
        if(height==0)
        {
            height=1;
        }
        screenWidth=width;
        screenHeight=height;
        switch(wallpaperMode)
        {
            case 1:	   //center
                if(height>width)
                {
                    aspect = new vector2f(1.0f,(float)width/(float)height);
                }
                else
                {
                    aspect = new vector2f((float)height/(float)width,1.0f);
                }
                break;
            case 2:		//fit
                if(height>width)
                {
                    aspect = new vector2f((float)height/(float)width,1.0f);
                }
                else
                {
                    aspect = new vector2f(1.0f,(float)width/(float)height);
                }
                break;
            case 3:		//stretch
                aspect = new vector2f(1.0f,1.0f);
                break;
        }
    }

    protected void doInit() {

        FractalSettings fractalSettings=RealtimeFractalService.getFractalSettings();

        vertexArray=new VertexArray(meshData.createVertexBuffer(GLBufferObject.Usage.StaticDraw),meshData.createIndexBuffer(GLBufferObject.Usage.StaticDraw));

        renderFractalProgram=new ProgramObject("fractal.prog");

        ShaderObject vertexProgram=new ShaderObject("vertexProgram", ShaderObject.ShaderType.VertexShader);
        vertexProgram.compile(renderFractalVertexProgramSource);
        renderFractalProgram.attachShader(vertexProgram);

        ShaderObject fragmentProgram=new ShaderObject("fragmentProgram", ShaderObject.ShaderType.FragmentShader);

        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(renderFractalFragmentProgramSource);

        if((fractalSettings.colorFunc==FractalSettings.ColorFunc.OrbitTraps)||(fractalSettings.colorFunc==FractalSettings.ColorFunc.DebugDistanceField)) {
            stringBuilder.append(renderFractalDistanceFuncSource);
        }

        stringBuilder.append(renderFractalTransferFuncProgramSource);
        stringBuilder.append(renderFractalColorFuncSource);

        fragmentProgram.compile(stringBuilder.toString());
        renderFractalProgram.attachShader(fragmentProgram);


        renderFractalProgram.bindAttribLocation(0,"position");
        renderFractalProgram.link();

        vertexProgram.delete();
        fragmentProgram.delete();

        GLTexture paletteTexture=new GLTexture();
        paletteTexture.createTexture2DFromBitmap(fractalSettings.paletteInfo.buildPaletteBitmap(256,1,true),false);

        TextureSampler paletteSampler=new TextureSampler(GLTexture.Target.Texture2D);
        paletteSampler.setWrapS(TextureSampler.WrapFunc.ClampToEdge);
        paletteSampler.setWrapT(TextureSampler.WrapFunc.ClampToEdge);


        palette=new TextureUnit(paletteTexture,paletteSampler);



        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // Black Background
        GLES20.glDisable(GL10.GL_DEPTH_TEST); // Enables Depth Testing

    }

    protected void doRelease() {
        if(renderFractalProgram!=null)
        {
            renderFractalProgram.delete();
            renderFractalProgram=null;
        }
        if(vertexArray!=null)
        {
            vertexArray.delete();
            vertexArray=null;
        }
        if(palette!=null) {
            palette.delete();
            palette=null;
        }
    }

    protected void renderFractal(FractalScene.SceneNode scene) {

        FractalSettings fractalSettings=RealtimeFractalService.getFractalSettings();


        renderFractalProgram.bind();

        renderFractalProgram.setUniformValue("aspect",aspect);


        matrix3f fractalMatrix;

        matrix3f translationMatrix=new matrix3f();
        translationMatrix.setTranslation(scene.centerPoint);

        matrix3f scaleMatrix=new matrix3f();
        scaleMatrix.setScale(scene.scale, scene.scale);

        matrix3f rotationMatrix=new matrix3f();
        rotationMatrix.setRotation(scene.rotation);


        fractalMatrix=matrix3f.mul(translationMatrix,rotationMatrix);
        fractalMatrix.mul(scaleMatrix);

        fractalMatrix.mul(texMatrix);

        renderFractalProgram.setUniformValue("texMatrix", fractalMatrix);

        if(fractalSettings.colorFunc!=FractalSettings.ColorFunc.DebugDistanceField) {

            renderFractalProgram.setUniformValue("paramC", scene.paramC);
            renderFractalProgram.setUniformValue("bailout", fractalSettings.bailout);
            renderFractalProgram.setUniformValue("bailin", fractalSettings.bailin);
            renderFractalProgram.setUniformValue("colorGradient", fractalSettings.colorGradient);
            renderFractalProgram.setUniformValue("maxIterations", (float) fractalSettings.maxIterations);
            renderFractalProgram.setUniformValue("warmupIterations", (float) fractalSettings.warmupIterations);

            renderFractalProgram.setUniformValue("gamma", new vector4f(1.0f / fractalSettings.gamma, 1.0f / fractalSettings.gamma, 1.0f / fractalSettings.gamma, 1.0f));
            palette.bind(0);
            renderFractalProgram.setSampler("palette",0);
        }

        if((fractalSettings.colorFunc==FractalSettings.ColorFunc.OrbitTraps)||(fractalSettings.colorFunc==FractalSettings.ColorFunc.DebugDistanceField)) {

            int count=0;
            for(FractalSettings.OrbitTrap trap :fractalSettings.getOrbitTraps()) {
                switch (trap.getDistanceFunc()) {
                    case Point:
                        renderFractalProgram.setUniformValue("pointPos" + count, trap.getParam2f());
                        break;
                    case Line:
                        renderFractalProgram.setUniformValue("lineParams" + count, trap.getParam2f());
                        break;
                    case Circle:
                        renderFractalProgram.setUniformValue("circleParams" + count, trap.getParam3f());
                        break;
                    case Sin:
                        vector2f param=trap.getParam2f();
                        renderFractalProgram.setUniformValue("sinParams" + count,new vector2f(param.ex,(float) (2.0 * Math.PI * param.ey)));
                        break;
                }
                count++;
            }

        }

        vertexArray.bind();
        vertexArray.draw();

        vertexArray.release();

        palette.release();

        renderFractalProgram.release();
    }

    private String loadFragmentProgram(FractalSettings fractalSettings) {
        String fileName=null;
        if(fractalSettings.fractalType == FractalSettings.FractalType.Julia) {
            switch (fractalSettings.colorFunc) {
                case IterationCount:
                    fileName = "shaders/julia_iteration_count.frag";
                    break;
                case Smooth:
                    fileName = "shaders/julia_smooth.frag";
                    break;
                case DistanceEstimator:
                    fileName = "shaders/julia_distance_estimator.frag";
                    break;
                case OrbitTraps:
                    fileName = "shaders/julia_orbit_traps.frag";
                    break;
                case DebugDistanceField:
                    fileName = "shaders/distance_field_debug.frag";
                    break;
            }
        } else if(fractalSettings.fractalType == FractalSettings.FractalType.Mandelbrot) {
            switch (fractalSettings.colorFunc) {
                case IterationCount:
                    fileName = "shaders/mandelbrot_iteration_count.frag";
                    break;
                case Smooth:
                    fileName = "shaders/mandelbrot_smooth.frag";
                    break;
                case DistanceEstimator:
                    fileName = "shaders/mandelbrot_distance_estimator.frag";
                    break;
                case OrbitTraps:
                    fileName = "shaders/mandelbrot_orbit_traps.frag";
                    break;
                case DebugDistanceField:
                    fileName = "shaders/distance_field_debug.frag";
                    break;
            }
        }
        LogSystem.debug(RealtimeFractalService.tag,"Loading fractal fragment program: "+fileName);
        return TextFileReader.readFromAssets(context,fileName);
    }

    private String loadDistanceFuncFragmentProgram(FractalSettings fractalSettings) {
        ArrayList<FractalSettings.OrbitTrap> orbitTraps=fractalSettings.getOrbitTraps();
        String distanceFunc;
        if(orbitTraps.size()>0) {
            String uniformsString=null;
            String funcString="float distanceFunc(vec2 p) {\n";
            switch (orbitTraps.get(0).getDistanceFunc()) {
                case Point:
                    uniformsString = "uniform vec2 pointPos0;\n";
                    funcString += "float d=distanceFuncPoint(p,pointPos0);\n";
                    break;
                case Line:
                    uniformsString = "uniform vec2 lineParams0;\n";
                    funcString += "float d=distanceFuncLine(p,lineParams0);\n";
                    break;
                case Circle:
                    uniformsString = "uniform vec3 circleParams0;\n";
                    funcString += "float d=distanceFuncCircle(p,circleParams0);\n";
                    break;
                case Sin:
                    uniformsString = "uniform vec2 sinParams0;\n";
                    funcString += "float d=distanceFuncSin(p,sinParams0);\n";
                    break;
            }
            for (int i = 1; i < orbitTraps.size(); i++) {
                FractalSettings.OrbitTrap trap = orbitTraps.get(i);
                String uniformName;
                switch (trap.getDistanceFunc()) {
                    case Point:
                        uniformName = "pointPos" + i;
                        uniformsString += "uniform vec2 " + uniformName + ";\n";
                        funcString += "d=min(d,distanceFuncPoint(p," + uniformName + "));\n";
                        break;
                    case Line:
                        uniformName = "lineParams" + i;
                        uniformsString += "uniform vec2 " + uniformName + ";\n";
                        funcString += "d=min(d,distanceFuncLine(p," + uniformName + "));\n";
                        break;
                    case Circle:
                        uniformName = "circleParams" + i;
                        uniformsString += "uniform vec3 " + uniformName + ";\n";
                        funcString += "d=min(d,distanceFuncCircle(p," + uniformName + "));\n";
                        break;
                    case Sin:
                        uniformName = "sinParams" + i;
                        uniformsString += "uniform vec2 " + uniformName + ";\n";
                        funcString += "d=min(d,distanceFuncSin(p," + uniformName + "));\n";
                        break;
                }
            }
            funcString += "return d;\n}";
            distanceFunc = TextFileReader.readFromAssets(context, "shaders/distance_functions.frag") + uniformsString + funcString;
        } else {
            distanceFunc="float distanceFunc(vec2 p) {\n return dot(p,p);\n}";
        }
        LogSystem.debug(RealtimeFractalService.tag,"Loading orbit traps distance function:\n"+distanceFunc+"\n");
        return distanceFunc;
    }

    private String loadColorFuncFragmentProgram(FractalSettings fractalSettings) {
        String fileName="shaders/color_func_palette.frag";
        LogSystem.debug(RealtimeFractalService.tag,"Loading color function program: "+fileName);
        return TextFileReader.readFromAssets(context,fileName);
    }

    private String loadTransferFuncFragmentProgram(FractalSettings fractalSettings) {
        String fileName=null;
        switch (fractalSettings.transferFunc) {
            case Power:
                fileName= "shaders/transfer_func_pow.frag";
                break;
            case Power2:
                fileName= "shaders/transfer_func_pow2.frag";
                break;
            case Exponential:
                fileName= "shaders/transfer_func_exp.frag";
                break;
            case Exponential2:
                fileName= "shaders/transfer_func_exp2.frag";
                break;
            case Sine:
                fileName= "shaders/transfer_func_sin.frag";
                break;
            case Cosine:
                fileName= "shaders/transfer_func_cos.frag";
                break;
        }
        LogSystem.debug(RealtimeFractalService.tag,"Loading transfer function program: "+fileName);
        return TextFileReader.readFromAssets(context,fileName);
    }

    public void onFractalSettingsChanged(FractalSettings fractalSettings,String key) {
//        LogSystem.debug(RealtimeFractalService.tag,"DefaultRenderPath.onFractalSettingsChanged()");
        if(key.equals("fractalType") || key.equals("fractalColorFunc")) {
            renderFractalFragmentProgramSource=loadFragmentProgram(fractalSettings);
        } else if(key.contains("orbitTrap")) {
            renderFractalDistanceFuncSource=loadDistanceFuncFragmentProgram(fractalSettings);
        } else if(key.equals("coloringMode")) {
            renderFractalColorFuncSource = loadColorFuncFragmentProgram(fractalSettings);
        } else if(key.equals("transferFunc")) {
            renderFractalTransferFuncProgramSource = loadTransferFuncFragmentProgram(fractalSettings);
        }
    }
}
