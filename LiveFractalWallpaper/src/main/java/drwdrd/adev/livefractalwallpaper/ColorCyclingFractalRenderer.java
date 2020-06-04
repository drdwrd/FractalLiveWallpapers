package drwdrd.adev.livefractalwallpaper;


//TODO: jesli chcialbym submitowac nowa wersje do sklepu musze zadbac o kompatybilnosc starych ustawien SeekBarPreferences (byly integer) z nowymi (sa float)

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.preference.PreferenceManager;

import drwdrd.adev.engine.EngineUtils;
import drwdrd.adev.engine.Mesh;
import drwdrd.adev.engine.ImzLoader;
import drwdrd.adev.engine.FrameCounter;
import drwdrd.adev.engine.TextFileReader;
import drwdrd.adev.engine.TimeCounter;
import drwdrd.adev.engine.GLBufferObject.Usage;
import drwdrd.adev.engine.GLTexture;
import drwdrd.adev.engine.LogSystem;
import drwdrd.adev.engine.ProgramObject;
import drwdrd.adev.engine.ShaderObject;
import drwdrd.adev.engine.ShaderObject.ShaderType;
import drwdrd.adev.engine.TextureImage;
import drwdrd.adev.engine.TextureSampler;
import drwdrd.adev.engine.TextureSampler.WrapFunc;
import drwdrd.adev.engine.GLCaps;
import drwdrd.adev.engine.TextureUnit;
import drwdrd.adev.engine.TgaLoader;
import drwdrd.adev.engine.VertexArray;
import drwdrd.adev.engine.matrix4f;
import drwdrd.adev.engine.vector3f;

public class ColorCyclingFractalRenderer implements GLSurfaceView.Renderer
{
	
	public final static String fractalDefaultValue="wlp03fa.imz";
	public final static String colormodeDefaultValue="ColorLoop2.frag";
	public final static String wavemodeDefaultValue="ColorWaves.frag";
	public final static String wallpapermodeDefaultValue="1";
	public final static String colorpaletteDefaultValue="palette01.xml";
	public final static int brightnessDefaultValue=10;
	public final static float brightnessMultiplier=0.1f;
	public final static int timescaleDefaultValue=10;
	public final static float timescaleMultiplier=0.01f;
	public final static int colorfrequencyDefaultValue=10;
	public final static float colorfrequencyMultiplier=0.1f;
	
   
   private Context context=null;
   private SettingsUpdater settingsUpdater=null;
   private Mesh meshData=null;
   private TextureImage textureImage=new TextureImage();
   private TextureImage paletteImage=new TextureImage();
   private String vertexProgramSource=null;
   private String fragmentProgramSource=null;

   private ProgramObject program=null;
   private VertexArray vertexArray=null;
   private TextureUnit colorMap=null;
   private TextureUnit   palette=null;
   private matrix4f projectionMatrix=null;
   private matrix4f viewMatrix=null;
   private matrix4f projectionViewMatrix=null;
   private TimeCounter timer=new TimeCounter(timescaleMultiplier*timescaleDefaultValue,1000.0);
   private FrameCounter frameCounter=new FrameCounter(10000.0);
   private float colorFrequency=1.0f;
   private boolean waveMode=false;
   private int wallpaperMode=1;				/*1-center,2-fit,3-stretch*/
   private float brightness=1.0f;
   private int paletteSize=512;
   
   public ColorCyclingFractalRenderer(Context context)
   {
//	   android.os.Debug.waitForDebugger();
       this.context=context;
       meshData=Mesh.MeshGenerator.createPlane(2,2);
	   vertexProgramSource=TextFileReader.readFromAssets(context,"simple.vert");
	   SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
	   setFractal(prefs);
	   setColorPalette(prefs);
	   setWallpaperMode(prefs);
	   chooseColorMode(prefs);
	   setBrightness(prefs);
	   setTimeScale(prefs);
	   setColorFrequency(prefs);
	   paletteSize=prefs.getInt("paletteSize",512);
	   settingsUpdater=new SettingsUpdater();
	   prefs.registerOnSharedPreferenceChangeListener(settingsUpdater);
	   
	   projectionMatrix=new matrix4f();

	   viewMatrix=new matrix4f();
	   viewMatrix.setLookAt(new vector3f(0.0f,0.0f,-10.0f),new vector3f(0.0f,0.0f,1.0f),new vector3f(0.0f,1.0f,0.0f));

	   projectionViewMatrix=new matrix4f();

       LogSystem.debug(LiveFractalWallpaperService.tag,"ColorCyclingFractalRenderer.ColorCyclingFractalRenderer()...");
   }
   
  
   public void release()
   {
	   if(program!=null)
	   {
		   program.delete();
		   program=null;
	   }
	   if(vertexArray!=null)
	   {
		   vertexArray.delete();
		   vertexArray=null;
	   }
	   if(colorMap!=null)
	   {
		   colorMap.delete();
		   colorMap=null;
	   }
	   if(palette!=null)
	   {
		   palette.delete();
		   palette=null;
	   }
	   LogSystem.debug(LiveFractalWallpaperService.tag,"ColorCyclingFractalRenderer.release()...");
   }


   public void onSurfaceCreated(GL10 gl,EGLConfig config)
   {
	   release();
	   
	   if(EngineUtils.DEBUG())
	   {
		   LogSystem.debug(LiveFractalWallpaperService.tag,GLCaps.getInstance().toString());
	   }
	   
	   int paletteMinSize=Math.min(GLCaps.getInstance().getMaxTextureSize(),2048);
	   
	   if(paletteSize!=paletteMinSize)
	   {
		   paletteSize=paletteMinSize;
		   SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(context);
		   setColorPalette(prefs);
		   SharedPreferences.Editor editor=prefs.edit();
		   editor.putInt("paletteSize",paletteSize);
		   editor.apply();
		   LogSystem.debug(LiveFractalWallpaperService.tag,String.format("Setting up palette size to %d...",paletteSize));
	   }

	   vertexArray=new VertexArray(meshData.createVertexBuffer(Usage.StaticDraw),meshData.createIndexBuffer(Usage.StaticDraw));
	   

	   ShaderObject vertexProgram=new ShaderObject("vertexProgram",ShaderType.VertexShader);
	   vertexProgram.compile(vertexProgramSource);
        
	   ShaderObject fragmentProgram=new ShaderObject("fragmentProgram",ShaderType.FragmentShader);
	   fragmentProgram.compile(fragmentProgramSource);
         
	   program=new ProgramObject("simple.prog");
	   program.attachShader(vertexProgram);
	   program.attachShader(fragmentProgram);
	   program.bindAttribLocation(0,"position");
	   program.bindAttribLocation(1,"tex");
	   program.link();
	   
	   vertexProgram.delete();
	   fragmentProgram.delete();
	   
	   
	   GLES20.glEnable(GLES20.GL_CULL_FACE);
	   GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); // Black Background
	   GLES20.glClearDepthf(1.0f); // Depth Buffer Setup
	   GLES20.glEnable(GL10.GL_DEPTH_TEST); // Enables Depth Testing
	   GLES20.glDepthFunc(GL10.GL_LEQUAL); // The Type Of Depth Testing To Do

       
	   GLTexture texture=new GLTexture();
	   texture.createTexture2D(textureImage);
	   
	   TextureSampler sampler=new TextureSampler(GLTexture.Target.Texture2D);
	   
	   colorMap=new TextureUnit(texture,sampler);

	    
	   GLTexture ptexture=new GLTexture();
	   ptexture.createTexture2D(paletteImage);

	   TextureSampler psampler=new TextureSampler(GLTexture.Target.Texture2D);
	   psampler.setWrapS(WrapFunc.Repeat);
	   psampler.setWrapT(WrapFunc.Repeat);

         
	   palette=new TextureUnit(ptexture,psampler);

	   checkGlError("ColorCyclingFractalRenderer.onSurfaceCreated()");
	   
	   timer.start();
	   frameCounter.start();
	   
	   LogSystem.debug(LiveFractalWallpaperService.tag,"ColorCyclingFractalRenderer.onSurfaceCreated()...");	   
   }

   public void onDrawFrame(GL10 gl)
   {
	   projectionViewMatrix=matrix4f.mul(projectionMatrix,viewMatrix);
	   GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
	   program.bind();
	   program.setUniformValue("modelViewProjectionMatrix",projectionViewMatrix);
	   program.setUniformValue("brightness",brightness);
	   if(waveMode==true)
	   {
		   program.setUniformValue("colorFrequency",(float)(2.0*colorFrequency*Math.PI));
		   program.setUniformValue("time",(float)(0.002*Math.PI*timer.tick()));
	   }
	   else
	   {
		   program.setUniformValue("colorFrequency",colorFrequency);
		   program.setUniformValue("time",(float)(0.001*timer.tick()));
	   }
         
	   colorMap.bind(0);
	   program.setSampler("colormap",0);
	   palette.bind(1);
	   program.setSampler("palette",1);

	   vertexArray.bind();
	   vertexArray.draw();
	   
	   vertexArray.release();
	   colorMap.release();
	   palette.release();
	   program.release();
       	
	   checkGlError("ColorCyclingFractalRenderer.onDrawFrame()");
	   frameCounter.tick();
   	}

   public void onSurfaceChanged(GL10 gl,int width,int height)
   {
	   if(height==0)
	   {
		   height=1;
	   }
	   GLES20.glViewport(0,0,width,height);
	   float aspect;
	   switch(wallpaperMode)
	   {
	   case 1:	   //center
		   if(height>width)
		   {
			   aspect=(float)height/(float)width;
			   projectionMatrix.setOrthoProjection(-1.0f,1.0f,-aspect,aspect,0.01f,1000.0f);
		   }
		   else
		   {
			   aspect=(float)width/(float)height;
			   projectionMatrix.setOrthoProjection(-aspect,aspect,-1.0f,1.0f,0.01f,1000.0f);
		   }
		   break;
	   case 2:		//fit
		   if(height>width)
		   {
		   aspect=(float)width/(float)height;
		   projectionMatrix.setOrthoProjection(-aspect,aspect,-1.0f,1.0f,0.01f,1000.0f);
		   }
		   else
		   {
			   aspect=(float)height/(float)width;
			   projectionMatrix.setOrthoProjection(-1.0f,1.0f,-aspect,aspect,0.01f,1000.0f);
		   }
		   break;
	   case 3:		//stretch
		   projectionMatrix.setOrthoProjection(-1.0f,1.0f,-1.0f,1.0f,0.01f,1000.0f);
		   break;
	   }
	   checkGlError("ColorCyclingFractalRenderer.onSurfaceChanged()");
	   LogSystem.debug(LiveFractalWallpaperService.tag,"ColorCyclingFractalRenderer.onSurfaceChanged()...");	   
   }

   private void checkGlError(String op)
   {
	   int error;
	   while((error = GLES20.glGetError())!=GLES20.GL_NO_ERROR)
	   {
		   LogSystem.error(LiveFractalWallpaperService.tag,String.format("%s GL error: %d",op,error));
	   }
   	}
   
   private void setColorPalette(SharedPreferences prefs)
   {
	   ColorPalette colorPalette=ColorPalette.loadColorPaletteFromAssets(context,prefs.getString("palette",colorpaletteDefaultValue));
	   if(colorPalette != null) {
		   colorPalette.buildTextureImage(paletteImage, paletteSize);
	   }
   }
   
   private void setFractal(SharedPreferences prefs)
   {
       StringBuilder path=new StringBuilder();
       if(prefs.getBoolean("highres",false)==true)
       {
    	   path.append("highres/");
       }
       else
       {
    	   path.append("lowres/");
       }
       String filename=prefs.getString("fractal",fractalDefaultValue);
       path.append(filename);
       if(filename.endsWith(".imz"))
       {
    	   ImzLoader.LoadFromAssets(context,textureImage,path.toString());
       }
       else if(filename.endsWith(".tga"))
       {
    	   TgaLoader.LoadFromAssets(context,textureImage,path.toString());
       }
   }
   
   private void chooseColorMode(SharedPreferences prefs)
   {
	   waveMode=false;
	   if(prefs.getString("colormode",colormodeDefaultValue).equals("ColorWaves")==true)
	   {
			fragmentProgramSource=TextFileReader.readFromAssets(context,prefs.getString("wavemode",wavemodeDefaultValue));
			waveMode=true;
	   }
	   else
	   {
			fragmentProgramSource=TextFileReader.readFromAssets(context,prefs.getString("colormode",colormodeDefaultValue));
	   }
   }
   
   private void setBrightness(SharedPreferences prefs)
   {
       brightness = brightnessMultiplier*prefs.getFloat("brightness", brightnessDefaultValue);
   }
   
   private void setTimeScale(SharedPreferences prefs)
   {
		timer.setTimeScale(timescaleMultiplier*prefs.getFloat("timescale", timescaleDefaultValue));
   }

   private void setColorFrequency(SharedPreferences prefs)
   {
		colorFrequency=colorfrequencyMultiplier*prefs.getFloat("colorfrequency", colorfrequencyDefaultValue);
   }
   
   private void setWallpaperMode(SharedPreferences prefs)
   {
	   wallpaperMode=Integer.valueOf(prefs.getString("wallpapermode",wallpapermodeDefaultValue));
   }
   
   private class SettingsUpdater implements SharedPreferences.OnSharedPreferenceChangeListener
   {

	public void onSharedPreferenceChanged(SharedPreferences prefs,String key)
	{
		if(key.equals("fractal")==true)
		{
			setFractal(prefs);
		}
		else if(key.equals("palette")==true)
		{
			   setColorPalette(prefs);
		}
		else if(key.equals("wallpapermode")==true)
		{
			   setWallpaperMode(prefs);
		}
		else if((key.equals("colormode")==true)||(key.equals("wavemode")==true))
		{
			chooseColorMode(prefs);
		}
		else if(key.equals("timescale")==true)
		{
			setTimeScale(prefs);
		}
		else if(key.equals("colorfrequency")==true)
		{
			setColorFrequency(prefs);
		}
		else if(key.equals("highres")==true)
		{
			setFractal(prefs);
		}
		else if(key.equals("brightness")==true)
		{
			setBrightness(prefs);
		}
	}
	   
   }

}