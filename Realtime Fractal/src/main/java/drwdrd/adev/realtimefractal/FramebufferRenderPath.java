package drwdrd.adev.realtimefractal;


import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLES20;
import android.preference.PreferenceManager;

import javax.microedition.khronos.opengles.GL10;

import drwdrd.adev.engine.GLFrameBuffer;
import drwdrd.adev.engine.GLTexture;
import drwdrd.adev.engine.ProgramObject;
import drwdrd.adev.engine.ShaderObject;
import drwdrd.adev.engine.TextFileReader;
import drwdrd.adev.engine.TextureSampler;
import drwdrd.adev.engine.TextureUnit;

class FramebufferRenderPath extends DefaultRenderPath {

    protected String renderTextureVertexProgramSource=null;
    protected String renderTextureFragmentProgramSource=null;

    protected ProgramObject renderTextureProgram=null;
    protected GLFrameBuffer frameBuffer=null;
    protected TextureUnit textureUnit=null;


    protected int renderTextureWidth=256;
    protected int renderTextureHeight=256;


    public FramebufferRenderPath()
    {
        super();
    }

    public void onCreate(Context context) {
        super.onCreate(context);

        renderTextureVertexProgramSource=TextFileReader.readFromAssets(context, "shaders/simple.vert");
        renderTextureFragmentProgramSource=TextFileReader.readFromAssets(context, "shaders/simple.frag");

        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
        int framebufferSize=Integer.valueOf(prefs.getString("framebufferSize","1024"));
        renderTextureWidth=framebufferSize;
        renderTextureHeight=framebufferSize;
    }

    public void onRelease()
    {
        initialized=false;
        super.doRelease();
        if(frameBuffer!=null)
        {
            frameBuffer.delete();
            frameBuffer=null;
        }
        if(textureUnit!=null)
        {
            textureUnit.delete();
            textureUnit=null;
        }
        if(renderTextureProgram!=null)
        {
            renderTextureProgram.delete();
            renderTextureProgram=null;
        }
    }


    public void onInitialize()
    {
        super.doInit();

        ShaderObject vertexProgram=new ShaderObject("vertexProgram", ShaderObject.ShaderType.VertexShader);
        vertexProgram.compile(renderTextureVertexProgramSource);

        ShaderObject fragmentProgram=new ShaderObject("fragmentProgram", ShaderObject.ShaderType.FragmentShader);
        fragmentProgram.compile(renderTextureFragmentProgramSource);

        renderTextureProgram=new ProgramObject("texture.prog");
        renderTextureProgram.attachShader(vertexProgram);
        renderTextureProgram.attachShader(fragmentProgram);
        renderTextureProgram.bindAttribLocation(0,"position");
        renderTextureProgram.link();

        vertexProgram.delete();
        fragmentProgram.delete();

        frameBuffer=new GLFrameBuffer(renderTextureWidth,renderTextureHeight);
        frameBuffer.bind();
        GLTexture colorRenderTexture=frameBuffer.createRenderTexture(GLFrameBuffer.RenderBufferType.ColorRGB565,true);
        frameBuffer.release();

        TextureSampler sampler=new TextureSampler(GLTexture.Target.Texture2D);
        sampler.setMinFilter(TextureSampler.FilterFunc.Linear);
        sampler.setMagFilter(TextureSampler.FilterFunc.Linear);
        textureUnit=new TextureUnit(colorRenderTexture,sampler);

        initialized=true;
    }

    public void onRender(FractalScene.SceneNode scene,float time)
    {

        frameBuffer.bind();
        GLES20.glViewport(0,0,renderTextureWidth,renderTextureHeight);
        GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT);

        renderFractal(scene);

        frameBuffer.release();

        GLES20.glViewport(0,0,screenWidth,screenHeight);
        GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT);


        renderTextureProgram.bind();

        textureUnit.bind(0);
        renderTextureProgram.setSampler("renderTexture",0);


        vertexArray.bind();
        vertexArray.draw();

        vertexArray.release();
        renderTextureProgram.release();
    }
}