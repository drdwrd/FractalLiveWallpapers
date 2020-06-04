package drwdrd.adev.engine;

import android.content.Context;
import java.util.ArrayList;
import java.util.HashMap;


//TODO: resource manager for caching resources
public class MaterialManager {

    public class Material {

        //TODO: special material format class should manage samplers and texture bindings
        private class TextureSamplerUnit {

            private String textureName=null;
            private TextureUnit textureUnit=null;

            private String samplerName=null;
            private int samplerUnit=-1;

            public TextureSamplerUnit(int samplerUnit,String samplerName,String textureName) {
                this.textureName=textureName;
                this.samplerName=samplerName;
                this.samplerUnit=samplerUnit;
            }
        }

        private String programName;
        private ProgramObject programObject=null;

        private ArrayList<TextureSamplerUnit> textureSamplers;

        public Material(String programName) {
            this.programName=programName;
        }

        public void addTexture(int samplerUnit,String samplerName,String textureName) {
            textureSamplers.add(new TextureSamplerUnit(samplerUnit,samplerName,textureName));
        }
    }

    private Context context=null;
    private HashMap<String,ProgramObject> programObjectMap=new HashMap<String, ProgramObject>();
    private HashMap<String,TextureUnit> textureUnitMap=new HashMap<String, TextureUnit>();
    private ArrayList<Material> materials=new ArrayList<Material>();

    public MaterialManager(Context context) {
        this.context=context;
    }

    public void addMaterial(Material material) {
        materials.add(material);
    }

    public void init() {
        //TODO: vertex format should be read from shader program config file, for now its hardcoded here
        VertexFormat vertexFormat = new VertexFormat(VertexFormat.VertexArrayLayout.VertexInterleaved);
        vertexFormat.addVertexAttribute(VertexFormat.Attribute.VertexAttrib1, "position", 0, VertexFormat.Type.Float, 3, 0);
        vertexFormat.addVertexAttribute(VertexFormat.Attribute.VertexAttrib2, "texCoord", 1, VertexFormat.Type.Float, 2, 0);


        for(Material m:materials) {
            //initialize shader programs
            if(programObjectMap.containsKey(m.programName)) {
                m.programObject = programObjectMap.get(m.programName);
            }
            else {
                String vertexProgramSource = TextFileReader.readFromAssets(context,m.programName + ".vert");
                ShaderObject vertexProgram = new ShaderObject("vertexProgram", ShaderObject.ShaderType.VertexShader);
                vertexProgram.compile(vertexProgramSource);

                String fragmentProgramSource = TextFileReader.readFromAssets(context,m.programName + ".frag");
                ShaderObject fragmentProgram = new ShaderObject("fragmentProgram", ShaderObject.ShaderType.FragmentShader);
                fragmentProgram.compile(fragmentProgramSource);

                ProgramObject program = new ProgramObject(m.programName);
                program.attachShader(vertexProgram);
                program.attachShader(fragmentProgram);
                program.setVertexFormat(vertexFormat);
                if(!program.link()) {
                    LogSystem.debug(EngineUtils.tag,"Error loading shader program: " + m.programName);
                }
                vertexProgram.delete();
                fragmentProgram.delete();

                programObjectMap.put(m.programName,program);
                m.programObject=program;
            }
            //initialize textures
            for(Material.TextureSamplerUnit s: m.textureSamplers) {
                if(textureUnitMap.containsKey(s.textureName)) {
                    s.textureUnit=textureUnitMap.get(s.textureName);
                }
                else {
                    GLTexture texture = new GLTexture();
                    texture.createTexture2DFromAssets(context, s.textureName, true);

                    //TODO: this should be read from material config file (along with material format)
                    TextureSampler sampler = new TextureSampler(GLTexture.Target.Texture2D);
                    sampler.setMinFilter(TextureSampler.FilterFunc.LinearMipmapLinear);

                    TextureUnit textureUnit=new TextureUnit(texture, sampler);
                    textureUnitMap.put(s.textureName,textureUnit);
                    s.textureUnit=textureUnit;
                }
            }
        }
    }

    public void release() {
        for(ProgramObject p: programObjectMap.values()) {
            p.delete();
        }
        programObjectMap.clear();

        for(TextureUnit t: textureUnitMap.values()) {
            t.delete();
        }
        textureUnitMap.clear();
    }

}
