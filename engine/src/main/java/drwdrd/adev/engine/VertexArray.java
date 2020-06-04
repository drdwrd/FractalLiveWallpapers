package drwdrd.adev.engine;


public class VertexArray {

    public VertexArray(VertexBuffer vertexBuffer, IndexBuffer indexBuffer) {
        this.vertexBuffer = vertexBuffer;
        this.indexBuffer = indexBuffer;
    }

    public void delete() {
        vertexBuffer.delete();
        indexBuffer.delete();
    }

    public void bind() {
        vertexBuffer.bind();
        indexBuffer.bind();
        vertexBuffer.enableVertexArray();
    }

    public void release() {
        vertexBuffer.release();
        indexBuffer.release();
    }

    public void draw() {
        indexBuffer.draw();
    }

    public void draw(int start,int count) {
        indexBuffer.draw(start,count);
    }

    private VertexBuffer vertexBuffer;
    private IndexBuffer indexBuffer;
}

