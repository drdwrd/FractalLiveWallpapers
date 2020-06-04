package drwdrd.adev.engine;

import android.content.Context;
import android.opengl.GLES20;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class ShaderObject {

    public enum ShaderType {
        VertexShader(GLES20.GL_VERTEX_SHADER),
        FragmentShader(GLES20.GL_FRAGMENT_SHADER);

        private ShaderType(int type) {
            this.type = type;
        }

        private int type;
    }


    public ShaderObject(String shaderName, ShaderType type) {
        this.name = shaderName;
        this.type = type;
        this.glShaderId = GLES20.glCreateShader(type.type);
    }

    public void delete() {
        GLES20.glDeleteShader(glShaderId);
    }


    public String getShaderName() {
        return name;
    }

    public boolean isValid() {
        return (glShaderId != 0);
    }

    public int getId() {
        return glShaderId;
    }

    public boolean compileFromAssets(Context context, String fileName) {
        String source = TextFileReader.readFromAssets(context, fileName);
        if (source != null) {
            return compile(source);
        }
        return false;
    }

    public boolean compile(String source) {
        GLES20.glShaderSource(glShaderId, source);
        GLES20.glCompileShader(glShaderId);
        int[] status = new int[1];
        GLES20.glGetShaderiv(glShaderId, GLES20.GL_COMPILE_STATUS, status, 0);
        if (status[0] == GLES20.GL_FALSE) {
            String infoLog = GLES20.glGetShaderInfoLog(glShaderId);
            LogSystem.error(EngineUtils.tag, String.format("Cannot compile shader: %s", infoLog));
            return false;
        }
        return true;
    }

    public boolean compileFromFile(String filename) {
        File file = new File(filename);
        StringBuilder text = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;

            while ((line = reader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            return compile(text.toString());
        } catch (IOException io) {
            io.printStackTrace();
            return false;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }

            } catch (IOException io) {
                io.printStackTrace();
            }
        }
    }


    private ShaderType type;
    private String name;
    private int glShaderId;
}