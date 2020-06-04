package drwdrd.adev.engine;

import android.content.Context;
import android.opengl.GLES20;

import java.util.HashMap;
import java.util.HashSet;


public class ProgramObject {

    public interface ProgramProvider {
        public String getVertexShader();
        public String getFragmentShader();
    }

    public static class AssetsProgramProvider implements ProgramProvider {

        private String vertexProgram;
        private String fragmentProgram;
        private Context context;

        public AssetsProgramProvider(Context context,String vertexProgram,String fragmentProgram) {
            this.context=context;
            this.vertexProgram=vertexProgram;
            this.fragmentProgram=fragmentProgram;
        }

        public String getVertexShader() {
            return TextFileReader.readFromAssets(context,vertexProgram);
        }

        public String getFragmentShader() {
            return TextFileReader.readFromAssets(context,fragmentProgram);
        }

    }

    public static class Attribute {
        private int location;
        private String name;

        public Attribute(int location,String name) {
            this.location=location;
            this.name=name;
        }

        public int getLocation() { return location; }
        public String getName() { return name; }
    }

    public ProgramObject(String name) {
        this.name = name;
        glProgramId = GLES20.glCreateProgram();
    }

    public void delete() {
        GLES20.glDeleteProgram(glProgramId);
    }

    public String getName() {
        return name;
    }

    public boolean isValid() {
        return (glProgramId != 0);
    }

    public void bind() {
        GLES20.glUseProgram(glProgramId);
    }

    public void release() {
        GLES20.glUseProgram(0);
    }

    public void setProgramProvider(ProgramProvider programProvider) {
        this.programProvider=programProvider;
    }

    public void setAttributes(Attribute[] attributes) {
        this.attributes=attributes;
    }

    public void attachShader(ShaderObject shader) {
        GLES20.glAttachShader(glProgramId, shader.getId());
    }

    public void detachShader(ShaderObject shader) {
        GLES20.glDetachShader(glProgramId, shader.getId());
    }

    public boolean link() {
        ShaderObject vertexProgram=null;
        ShaderObject fragmentProgram=null;
        if(programProvider!=null) {
            vertexProgram=new ShaderObject("vertexProgram", ShaderObject.ShaderType.VertexShader);
            if(!vertexProgram.compile(programProvider.getVertexShader())) {
                return false;
            }
            attachShader(vertexProgram);

            fragmentProgram=new ShaderObject("fragmentProgram", ShaderObject.ShaderType.FragmentShader);
            if(!fragmentProgram.compile(programProvider.getFragmentShader())) {
                return false;
            }
            attachShader(fragmentProgram);
        }
        if(attributes!=null) {
            for(Attribute attribute :attributes) {
                bindAttribLocation(attribute.getLocation(),attribute.getName());
            }
        }
        GLES20.glLinkProgram(glProgramId);
        int[] status = new int[1];
        GLES20.glGetProgramiv(glProgramId, GLES20.GL_LINK_STATUS, status, 0);
        if (status[0] == GLES20.GL_FALSE) {
            String infoLog = GLES20.glGetProgramInfoLog(glProgramId);
            LogSystem.error(EngineUtils.tag, String.format("Cannot link program: %s", infoLog));
            return false;
        }
        if(vertexProgram!=null) {
            vertexProgram.delete();
        }
        if(fragmentProgram!=null) {
            fragmentProgram.delete();
        }
        return true;

    }

    public int getAttribLocation(String name) {
        return GLES20.glGetAttribLocation(glProgramId, name);
    }

    public void bindAttribLocation(int index, String name) {
        GLES20.glBindAttribLocation(glProgramId, index, name);
    }

    public void setVertexFormat(VertexFormat vertexFormat) {
        vertexFormat.bindAttribLocations(this);
    }

    public int getUniformLocation(String name) {
        Integer loc = uniformCache.get(name);
        if(loc != null) {
            return loc.intValue();
        } else {
            int location = GLES20.glGetUniformLocation(glProgramId, name);
            if (location == -1) {
                LogSystem.error(EngineUtils.tag, String.format("Uniform %s not found in program %s\n", name, this.name));
            } else {
                uniformCache.put(name,location);
            }
            return location;
        }
    }

    public void setSampler(int location, int val) {
        GLES20.glUniform1i(location, val);
    }

    public void setSampler(String name, int val) {
        int location = getUniformLocation(name);
        if (location == -1) {
            LogSystem.error(EngineUtils.tag, String.format("Uniform %s not found in program %s\n", name, this.name));
        }
        GLES20.glUniform1i(location, val);
    }

    public void setUniformValue(int location, int val) {
        GLES20.glUniform1i(location, val);
    }

    public void setUniformValue(int location, float val) {
        GLES20.glUniform1f(location, val);
    }

    public void setUniformValue(String name, float val) {
        int location = getUniformLocation(name);
        if (location == -1) {
            LogSystem.error(EngineUtils.tag, String.format("Uniform %s not found in program %s\n", name, this.name));
        }
        GLES20.glUniform1f(location, val);
    }

    public void setUniformValue(int location, vector2f val) {
        val.get(buf);
        GLES20.glUniform2fv(location, 1, buf, 0);
    }

    public void setUniformValue(String name, vector2f val) {
        val.get(buf);
        int location = getUniformLocation(name);
        if (location == -1) {
            LogSystem.error(EngineUtils.tag, String.format("Uniform %s not found in program %s\n", name, this.name));
        }
        GLES20.glUniform2fv(location, 1, buf, 0);
    }

    public void setUniformValue(int location, vector3f val) {
        val.get(buf);
        GLES20.glUniform3fv(location, 1, buf, 0);
    }

    public void setUniformValue(String name, vector3f val) {
        val.get(buf);
        int location = getUniformLocation(name);
        if (location == -1) {
            LogSystem.error(EngineUtils.tag, String.format("Uniform %s not found in program %s\n", name, this.name));
        }
        GLES20.glUniform3fv(location, 1, buf, 0);
    }

    public void setUniformValue(int location, vector4f val) {
        val.get(buf);
        GLES20.glUniform4fv(location, 1, buf, 0);
    }

    public void setUniformValue(String name, vector4f val) {
        val.get(buf);
        int location = getUniformLocation(name);
        if (location == -1) {
            LogSystem.error(EngineUtils.tag, String.format("Uniform %s not found in program %s\n", name, this.name));
        }
        GLES20.glUniform4fv(location, 1, buf, 0);
    }

    public void setUniformValue(int location, matrix2f val) {
        val.get(buf);
        GLES20.glUniformMatrix2fv(location, 1, false, buf, 0);
    }

    public void setUniformValue(String name, matrix2f val) {
        val.get(buf);
        int location = getUniformLocation(name);
        if (location == -1) {
            LogSystem.error(EngineUtils.tag, String.format("Uniform %s not found in program %s\n", name, this.name));
        }
        GLES20.glUniformMatrix2fv(location, 1, false, buf, 0);
    }

    public void setUniformValue(int location, matrix3f val) {
        val.get(buf);
        GLES20.glUniformMatrix3fv(location, 1, false, buf, 0);
    }

    public void setUniformValue(String name, matrix3f val) {
        val.get(buf);
        int location = getUniformLocation(name);
        if (location == -1) {
            LogSystem.error(EngineUtils.tag, String.format("Uniform %s not found in program %s\n", name, this.name));
        }
        GLES20.glUniformMatrix3fv(location, 1, false, buf, 0);
    }

    public void setUniformValue(int location, matrix4f val) {
        val.get(buf);
        GLES20.glUniformMatrix4fv(location, 1, false, buf, 0);
    }

    public void setUniformValue(String name, matrix4f val) {
        val.get(buf);
        GLES20.glUniformMatrix4fv(getUniformLocation(name), 1, false, buf, 0);
    }

    private static float[] buf = new float[16];
    private String name;
    private int glProgramId;
    private ProgramProvider programProvider = null;
    private Attribute[] attributes = null;
    private HashMap<String,Integer> uniformCache = new HashMap<>();
}