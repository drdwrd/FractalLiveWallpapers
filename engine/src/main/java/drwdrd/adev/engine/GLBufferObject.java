package drwdrd.adev.engine;

import android.opengl.GLES20;
import java.nio.Buffer;


public class GLBufferObject {
    public enum Type {
        VertexBuffer(GLES20.GL_ARRAY_BUFFER),                            //Vertex attributes
        IndexBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER);                    //Vertex array indices

        private int type;

        private Type(int type) {
            this.type = type;
        }
    }

    public enum Usage {
        StreamDraw(GLES20.GL_STREAM_DRAW),
        StaticDraw(GLES20.GL_STATIC_DRAW),
        DynamicDraw(GLES20.GL_DYNAMIC_DRAW);

        private int usage;

        private Usage(int usage) {
            this.usage = usage;
        }
    }


    public GLBufferObject(GLBufferObject.Type type, GLBufferObject.Usage usage) {
        glTarget = type;
        glUsage = usage;
        glBufferId = new int[1];
        GLES20.glGenBuffers(1, glBufferId, 0);
    }

    public void delete() {
        GLES20.glDeleteBuffers(1, glBufferId, 0);
        glBufferId[0] = 0;
    }

    public boolean isValid() {
        return (glBufferId[0] != 0);
    }

    void alloc(int size) {
        GLES20.glBufferData(glTarget.type, size, null, glUsage.usage);
    }

    public void bind() {
        GLES20.glBindBuffer(glTarget.type, glBufferId[0]);
    }

    public void release() {
        GLES20.glBindBuffer(glTarget.type, 0);
    }

    public void setData(int size, Buffer data) {
        GLES20.glBufferData(glTarget.type, size, data, glUsage.usage);
    }

    public void setData(int offset, int size, Buffer data) {
        GLES20.glBufferSubData(glTarget.type, offset, size, data);
    }


    private int[] glBufferId;
    private GLBufferObject.Type glTarget;
    private GLBufferObject.Usage glUsage;
}

