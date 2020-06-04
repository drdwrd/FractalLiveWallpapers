package drwdrd.adev.engine;

import java.nio.Buffer;


public class VertexBuffer extends GLBufferObject {

    public VertexBuffer(GLBufferObject.Usage usage) {
        super(GLBufferObject.Type.VertexBuffer, usage);
        vertexCount = 0;
    }

    public VertexBuffer(VertexFormat vertexFormat, GLBufferObject.Usage usage) {
        super(GLBufferObject.Type.VertexBuffer, usage);
        this.vertexFormat = new VertexFormat(vertexFormat);
        vertexCount = 0;

    }

    public void alloc(int vertexCount) {
        this.vertexCount = vertexCount;
        super.alloc(vertexCount * vertexFormat.getVertexSize());
    }

    public void setVertexFormat(VertexFormat vertexFormat) {
        this.vertexFormat = new VertexFormat(vertexFormat);
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getVertexBufferSize() {
        return vertexCount * vertexFormat.getVertexSize();
    }

    public VertexFormat getVertexFormat() {
        return vertexFormat;
    }

    public void enableVertexArray() {
        vertexFormat.enableVertexArray(vertexCount);
    }

    public void disableVertexArray() {
        vertexFormat.disableVertexArray();
    }

    public void setData(int vertexCount, Buffer data) {
        this.vertexCount = vertexCount;
        super.setData(vertexCount * vertexFormat.getVertexSize(), data);
    }

    private VertexFormat vertexFormat;
    private int vertexCount;
}

