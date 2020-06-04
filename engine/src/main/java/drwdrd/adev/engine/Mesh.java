package drwdrd.adev.engine;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import drwdrd.adev.engine.VertexFormat.Attribute;
import drwdrd.adev.engine.VertexFormat.Type;
import drwdrd.adev.engine.VertexFormat.VertexArrayLayout;


public class Mesh {
    private VertexFormat vertexFormat;
    private ByteBuffer vertexData;
    private int vertexCount;
    private IndexBuffer.IndicesType indicesType;
    private IndexBuffer.PrimitivesMode primitivesMode;
    private ByteBuffer indexData;
    private int indicesCount;

    public Mesh(VertexFormat vertexFormat, ByteBuffer vertexData, int vertexCount, IndexBuffer.IndicesType indicesType, IndexBuffer.PrimitivesMode primitivesMode, ByteBuffer indexData, int indicesCount) {
        this.vertexFormat = vertexFormat;
        this.vertexData = vertexData;
        this.vertexCount = vertexCount;
        this.indicesType=indicesType;
        this.primitivesMode=primitivesMode;
        this.indexData = indexData;
        this.indicesCount = indicesCount;
    }

    public VertexBuffer createVertexBuffer(GLBufferObject.Usage usage) {
        VertexBuffer vb=new VertexBuffer(vertexFormat, usage);
        vb.bind();
        vb.setData(vertexCount,vertexData);
        vb.release();
        return vb;
    }

    public IndexBuffer createIndexBuffer(GLBufferObject.Usage usage) {
        IndexBuffer ib=new IndexBuffer(indicesType, primitivesMode, usage);
        ib.bind();
        ib.setData(indicesCount,indexData);
        ib.release();
        return ib;
    }

    public static class MeshGenerator {

        public static Mesh createSimplePlane() {
            float[] plane = {
                    -1.0f, -1.0f,
                     1.0f, -1.0f,
                    -1.0f,  1.0f,
                     1.0f,  1.0f
            };

            short[] indices = {
                    0, 1, 2, 2, 1, 3
            };

            VertexFormat vertexFormat = new VertexFormat(VertexArrayLayout.VertexInterleaved);
            vertexFormat.addVertexAttribute(Attribute.VertexAttrib1, "position", 0, Type.Float, 2, 0);

            ByteBuffer vertexBuffer = ByteBuffer.allocateDirect(4 * vertexFormat.getVertexSize()).order(ByteOrder.nativeOrder());
            FloatBuffer vertexData = vertexBuffer.asFloatBuffer();
            vertexData.put(plane);

            ByteBuffer indexBuffer = ByteBuffer.allocateDirect(6 * IndexBuffer.getIndicesTypeSize(IndexBuffer.IndicesType.UShort)).order(ByteOrder.nativeOrder());
            ShortBuffer indexData = indexBuffer.asShortBuffer();
            indexData.put(indices);

            return new Mesh(vertexFormat, vertexBuffer, 4, IndexBuffer.IndicesType.UShort, IndexBuffer.PrimitivesMode.Triangles, indexBuffer, 6);

        }


        public static Mesh createPlane(int xres, int yres) {
            VertexFormat vertexFormat = new VertexFormat(VertexArrayLayout.VertexInterleaved);
            vertexFormat.addVertexAttribute(Attribute.VertexAttrib1, "position", 0, Type.Float, 3, 0);
            vertexFormat.addVertexAttribute(Attribute.VertexAttrib2, "texCoord", 1, Type.Float, 2, 0);

            ByteBuffer vertexBuffer = ByteBuffer.allocateDirect(vertexFormat.getVertexSize() * xres * yres).order(ByteOrder.nativeOrder());
            FloatBuffer vertexData = vertexBuffer.asFloatBuffer();
            vertexData.position(0);


            float sd = 1.0f / (float) (xres - 1);
            float td = 1.0f / (float) (yres - 1);
            float x, y, z;
            for (int i = 0; i < yres; i++) {
                for (int j = 0; j < xres; j++) {
                    x = 1.0f - 2.0f * i * td;
                    y = 1.0f - 2.0f * j * sd;
                    z = 1.0f;

                    vertexData.put(x);
                    vertexData.put(y);
                    vertexData.put(z);

                    vertexData.put((float) i / (float) (xres - 1));
                    vertexData.put((float) j / (float) (yres - 1));
                }
            }

            ByteBuffer indexBuffer = ByteBuffer.allocateDirect(6 * IndexBuffer.getIndicesTypeSize(IndexBuffer.IndicesType.UShort) * (xres - 1) * (yres - 1)).order(ByteOrder.nativeOrder());
            ShortBuffer indexData = indexBuffer.asShortBuffer();
            indexBuffer.position(0);

            for (int i = 0; i < yres - 1; i++) {
                for (int j = 0; j < xres - 1; j++) {
                    indexData.put((short) (i * xres + j + 1));
                    indexData.put((short) ((i + 1) * xres + j));
                    indexData.put((short) (i * xres + j));
                }
                for (int j = 0; j < xres - 1; j++) {
                    indexData.put((short) (i * xres + j + 1));
                    indexData.put((short) ((i + 1) * xres + j + 1));
                    indexData.put((short) ((i + 1) * xres + j));
                }
            }
            return new Mesh(vertexFormat, vertexBuffer, xres * yres, IndexBuffer.IndicesType.UShort, IndexBuffer.PrimitivesMode.Triangles ,indexBuffer, 6 * (xres - 1) * (yres - 1));
        }

        public static Mesh createSphere(int res) {
            VertexFormat vertexFormat = new VertexFormat(VertexArrayLayout.VertexInterleaved);
            vertexFormat.addVertexAttribute(Attribute.VertexAttrib1, "position", 0, Type.Float, 3, 0);
            vertexFormat.addVertexAttribute(Attribute.VertexAttrib2, "texCoord", 1, Type.Float, 2, 0);

            ByteBuffer vertexBuffer = ByteBuffer.allocateDirect(6 * res * res * vertexFormat.getVertexSize()).order(ByteOrder.nativeOrder());
            FloatBuffer vertexData = vertexBuffer.asFloatBuffer();
            vertexData.position(0);

            float d = 1.0f / (float) (res - 1);
            float q, p, r;
            //px
            matrix3f pxFaceTransform = new matrix3f(0, 0, 1,
                    0, 1, 0,
                    1, 0, 0);
            for (int j = 0; j < res; j++) {
                for (int i = 0; i < res; i++) {
                    q = 1.0f - 2.0f * i * d;
                    p = 1.0f - 2.0f * j * d;
                    r = 1.0f;

                    vector3f pos = new vector3f(q, p, r);
                    pos.mul(pxFaceTransform);
                    pos.normalize();

                    vertexData.put(pos.ex);
                    vertexData.put(pos.ey);
                    vertexData.put(pos.ez);

                    vertexData.put((float) i / (float) (res - 1));
                    vertexData.put((float) j / (float) (res - 1));
                }
            }
            //nx
            matrix3f nxFaceTransform = new matrix3f(0, 0, -1,
                    0, 1, 0,
                    -1, 0, 0);
            for (int j = 0; j < res; j++) {
                for (int i = 0; i < res; i++) {
                    q = 1.0f - 2.0f * i * d;
                    p = 1.0f - 2.0f * j * d;
                    r = 1.0f;

                    vector3f pos = new vector3f(q, p, r);
                    pos.mul(nxFaceTransform);
                    pos.normalize();

                    vertexData.put(pos.ex);
                    vertexData.put(pos.ey);
                    vertexData.put(pos.ez);

                    vertexData.put((float) i / (float) (res - 1));
                    vertexData.put((float) j / (float) (res - 1));
                }
            }
            //py
            matrix3f pyFaceTransform = new matrix3f(-1, 0, 0,
                    0, 0, -1,
                    0, 1, 0);
            for (int j = 0; j < res; j++) {
                for (int i = 0; i < res; i++) {
                    q = 1.0f - 2.0f * i * d;
                    p = 1.0f - 2.0f * j * d;
                    r = 1.0f;

                    vector3f pos = new vector3f(q, p, r);
                    pos.mul(pyFaceTransform);
                    pos.normalize();

                    vertexData.put(pos.ex);
                    vertexData.put(pos.ey);
                    vertexData.put(pos.ez);

                    vertexData.put((float) i / (float) (res - 1));
                    vertexData.put((float) j / (float) (res - 1));
                }
            }
            //ny
            matrix3f nyFaceTransform = new matrix3f(-1, 0, 0,
                    0, 0, 1,
                    0, -1, 0);
            for (int j = 0; j < res; j++) {
                for (int i = 0; i < res; i++) {
                    q = 1.0f - 2.0f * i * d;
                    p = 1.0f - 2.0f * j * d;
                    r = 1.0f;

                    vector3f pos = new vector3f(q, p, r);
                    pos.mul(nyFaceTransform);
                    pos.normalize();

                    vertexData.put(pos.ex);
                    vertexData.put(pos.ey);
                    vertexData.put(pos.ez);

                    vertexData.put((float) i / (float) (res - 1));
                    vertexData.put((float) j / (float) (res - 1));
                }
            }
            //pz
            matrix3f pzFaceTransform = new matrix3f(-1, 0, 0,
                    0, 1, 0,
                    0, 0, 1);
            for (int j = 0; j < res; j++) {
                for (int i = 0; i < res; i++) {
                    q = 1.0f - 2.0f * i * d;
                    p = 1.0f - 2.0f * j * d;
                    r = 1.0f;

                    vector3f pos = new vector3f(q, p, r);
                    pos.mul(pzFaceTransform);
                    pos.normalize();

                    vertexData.put(pos.ex);
                    vertexData.put(pos.ey);
                    vertexData.put(pos.ez);

                    vertexData.put((float) i / (float) (res - 1));
                    vertexData.put((float) j / (float) (res - 1));
                }
            }
            //nz
            matrix3f nzFaceTransform = new matrix3f(1, 0, 0,
                    0, 1, 0,
                    0, 0, -1);
            for (int j = 0; j < res; j++) {
                for (int i = 0; i < res; i++) {
                    q = 1.0f - 2.0f * i * d;
                    p = 1.0f - 2.0f * j * d;
                    r = 1.0f;

                    vector3f pos = new vector3f(q, p, r);
                    pos.mul(nzFaceTransform);
                    pos.normalize();

                    vertexData.put(pos.ex);
                    vertexData.put(pos.ey);
                    vertexData.put(pos.ez);

                    vertexData.put((float) i / (float) (res - 1));
                    vertexData.put((float) j / (float) (res - 1));
                }
            }

            ByteBuffer indexBuffer = ByteBuffer.allocateDirect(36 * IndexBuffer.getIndicesTypeSize(IndexBuffer.IndicesType.UShort) * (res - 1) * (res - 1)).order(ByteOrder.nativeOrder());
            ShortBuffer indexData = indexBuffer.asShortBuffer();
            indexBuffer.position(0);

            for (int k = 0; k < 6; k++) {
                int index = k * res * res;
                for (int j = 0; j < res - 1; j++) {
                    for (int i = 0; i < res - 1; i++) {
                        indexData.put((short) index);
                        indexData.put((short) (index + 1));
                        indexData.put((short) (index + res));

                        indexData.put((short) (index + res));
                        indexData.put((short) (index + 1));
                        indexData.put((short) (index + res + 1));

                        index++;
                    }
                    index++;
                }
            }

            return new Mesh(vertexFormat, vertexBuffer, 6 * res * res, IndexBuffer.IndicesType.UShort, IndexBuffer.PrimitivesMode.Triangles, indexBuffer, 36 * (res - 1) * (res - 1));
        }
    }

}


