package drwdrd.adev.engine;

import android.opengl.GLES20;

import java.nio.Buffer;


public class IndexBuffer extends GLBufferObject {

    public enum IndicesType {
        UByte(GLES20.GL_UNSIGNED_BYTE),
        UShort(GLES20.GL_UNSIGNED_SHORT),
        UInt(GLES20.GL_UNSIGNED_INT);

        private int type;

        private IndicesType(int type) {
            this.type = type;
        }
    }

    public enum PrimitivesMode {
        Points(GLES20.GL_POINTS),
        LineStrips(GLES20.GL_LINE_STRIP),
        LineLoops(GLES20.GL_LINE_LOOP),
        Lines(GLES20.GL_LINES),
        TriangleStrips(GLES20.GL_TRIANGLE_STRIP),
        TriangleFan(GLES20.GL_TRIANGLE_FAN),
        Triangles(GLES20.GL_TRIANGLES);

        private int mode;

        private PrimitivesMode(int mode) {
            this.mode = mode;
        }
    }

    public IndexBuffer(IndicesType type, PrimitivesMode mode, GLBufferObject.Usage usage) {
        super(GLBufferObject.Type.IndexBuffer, usage);
        indicesCount = 0;
        indicesType = type;
        primitivesMode = mode;
    }

    public void alloc(int indicesCount) {
        this.indicesCount = indicesCount;
        super.alloc(indicesCount * IndexBuffer.getIndicesTypeSize(indicesType));
    }

    public int getIndicesCount() {
        return indicesCount;
    }

    public int getIndicesBufferSize() {
        return indicesCount * IndexBuffer.getIndicesTypeSize(indicesType);
    }

    public void draw() {
        GLES20.glDrawElements(primitivesMode.mode, indicesCount, indicesType.type, 0);
    }

    public void draw(int start,int count) {
        GLES20.glDrawElements(primitivesMode.mode,count,indicesType.type,start*getIndicesTypeSize(indicesType));
    }

    public static int getIndicesTypeSize(IndicesType type) {
        switch (type) {
            case UByte:
                return 1;
            case UShort:
                return 2;
            case UInt:
                return 4;
            default:
                return 0;
        }
    }

    public void setData(int indicesCount, Buffer data) {
        this.indicesCount = indicesCount;
        super.setData(indicesCount * IndexBuffer.getIndicesTypeSize(indicesType), data);
    }

    private int indicesCount;
    private IndicesType indicesType;
    private PrimitivesMode primitivesMode;
}


