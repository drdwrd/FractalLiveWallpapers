package drwdrd.adev.engine;

import android.opengl.GLES20;

import java.nio.ByteBuffer;


public class VertexFormat {

    public enum Attribute {
        //generic attributes
        VertexAttrib1(0),
        VertexAttrib2(1),
        VertexAttrib3(2),
        VertexAttrib4(3),
        VertexAttrib5(4),
        VertexAttrib6(5),
        VertexAttrib7(6),
        VertexAttrib8(7),
        VertexAttrib9(8),
        VertexAttrib10(9),
        VertexAttrib11(10),
        VertexAttrib12(11),
        VertexAttrib13(12),
        VertexAttrib14(13),
        VertexAttrib15(14),
        VertexAttrib16(15),

        MaxVertexAttribute(16),

        //special definitions
        VertexPosition(VertexAttrib1),
        VertexColor(VertexAttrib2),
        VertexNormal(VertexAttrib3),
        Tex1Coord(VertexAttrib4);

        private Attribute(int attrib) {
            this.attribute = attrib;
        }

        private Attribute(Attribute attrib) {
            this.attribute = attrib.attribute;
        }

        private int attribute;
    }

    public enum Type {
        Byte(GLES20.GL_BYTE),
        UByte(GLES20.GL_UNSIGNED_BYTE),
        Short(GLES20.GL_SHORT),
        UShort(GLES20.GL_UNSIGNED_SHORT),
        Int(GLES20.GL_INT),
        UInt(GLES20.GL_UNSIGNED_INT),
        Float(GLES20.GL_FLOAT);

        private int type;

        private Type(int type) {
            this.type = type;
        }
    }

    public enum Flag {
        Normalized(1),            //attribute should be normalized
        Ignored(2);            //attribute should be ignored by all gl commands

        private int flag;

        private Flag(int flag) {
            this.flag = flag;
        }
    }

    public enum VertexArrayLayout {
        VertexInterleaved(1),            //each vertex as single component
        VertexAttrStream(2);            //every attr single stream

        private int layout;

        private VertexArrayLayout(int layout) {
            this.layout = layout;
        }
    }

    private class VertexAttribInfo {
        private int index;
        private Type type;
        private int size;
        private int flags;
        private int offset;
        private String attribName;

        private VertexAttribInfo() {
            this.index = 0;
            this.type = Type.Float;
            this.size = 0;
            this.flags = 0;
            this.offset = 0;
            this.attribName=null;
        }
    }

    public VertexFormat() {
        vertexSize = 0;
        vertexArrayLayout = VertexArrayLayout.VertexInterleaved;
        vertexFormat = new VertexAttribInfo[Attribute.MaxVertexAttribute.attribute];
        for (int i = 0; i < Attribute.MaxVertexAttribute.attribute; i++) {
            vertexFormat[i] = new VertexAttribInfo();
        }
    }

    public VertexFormat(VertexArrayLayout layout) {
        vertexSize = 0;
        vertexArrayLayout = layout;
        vertexFormat = new VertexAttribInfo[Attribute.MaxVertexAttribute.attribute];
        for (int i = 0; i < Attribute.MaxVertexAttribute.attribute; i++) {
            vertexFormat[i] = new VertexAttribInfo();
        }
    }

    public VertexFormat(VertexFormat format) {
        vertexSize = format.vertexSize;
        vertexArrayLayout = format.vertexArrayLayout;
        vertexFormat = new VertexAttribInfo[Attribute.MaxVertexAttribute.attribute];
        for (int i = 0; i < Attribute.MaxVertexAttribute.attribute; i++) {
            vertexFormat[i] = new VertexAttribInfo();
            vertexFormat[i].index = format.vertexFormat[i].index;
            vertexFormat[i].type = format.vertexFormat[i].type;
            vertexFormat[i].size = format.vertexFormat[i].size;
            vertexFormat[i].flags = format.vertexFormat[i].flags;
            vertexFormat[i].offset = format.vertexFormat[i].offset;
            vertexFormat[i].attribName = format.vertexFormat[i].attribName;
        }
    }

    public int getAttrIndex(Attribute attr) {
        return vertexFormat[attr.attribute].index;
    }

    public Type getAttrType(Attribute attr) {
        return vertexFormat[attr.attribute].type;
    }

    public int getAttrSize(Attribute attr) {
        return vertexFormat[attr.attribute].size;
    }

    public boolean isAttr(Attribute attr, Flag flag) {
        return ((vertexFormat[attr.attribute].flags & flag.flag) != 0);
    }

    public int getAttrOffset(Attribute attr) {
        return vertexFormat[attr.attribute].offset;
    }

    public String getAttrName(Attribute attr) { return vertexFormat[attr.attribute].attribName; }

    public int getVertexSize() {
        return vertexSize;
    }

    public VertexArrayLayout getLayout() {
        return vertexArrayLayout;
    }

    public void setVertexArrayLayout(VertexArrayLayout layout) {
        vertexArrayLayout = layout;
    }

    public void addVertexAttribute(Attribute attr, String name, int index, Type type, int size, int flags) {
        vertexFormat[attr.attribute].index = index;
        vertexFormat[attr.attribute].type = type;
        vertexFormat[attr.attribute].size = size;
        vertexFormat[attr.attribute].flags = flags;
        vertexFormat[attr.attribute].offset = 0;
        vertexFormat[attr.attribute].attribName=name;
        calculateVertexSize();
        calculateOffsets();
    }

    public void bindAttribLocations(ProgramObject programObject) {
        for (int i = 0; i < Attribute.MaxVertexAttribute.attribute; i++) {
            if ((vertexFormat[i].size > 0) && ((vertexFormat[i].flags & Flag.Ignored.flag) == 0)) {
                programObject.bindAttribLocation(vertexFormat[i].index,vertexFormat[i].attribName);
            }
        }
    }

    public void enableVertexArray(ByteBuffer vertexData, int vertexCount) {
        int countMul = 1;
        if (vertexArrayLayout == VertexArrayLayout.VertexAttrStream) {
            countMul = vertexCount;
        }
        for (int i = 0; i < Attribute.MaxVertexAttribute.attribute; i++) {
            if ((vertexFormat[i].size > 0) && ((vertexFormat[i].flags & Flag.Ignored.flag) == 0)) {
                vertexData.position(countMul * vertexFormat[i].offset);
                GLES20.glVertexAttribPointer(vertexFormat[i].index, vertexFormat[i].size, vertexFormat[i].type.type, (vertexFormat[i].flags & Flag.Normalized.flag) != 0, vertexSize, vertexData);
                GLES20.glEnableVertexAttribArray(vertexFormat[i].index);
            }
        }
    }

    public void enableVertexArray(int vertexCount) {
        int countMul = 1;
        if (vertexArrayLayout == VertexArrayLayout.VertexAttrStream) {
            countMul = vertexCount;
        }
        for (int i = 0; i < Attribute.MaxVertexAttribute.attribute; i++) {
            if ((vertexFormat[i].size > 0) && ((vertexFormat[i].flags & Flag.Ignored.flag) == 0)) {
                GLES20.glVertexAttribPointer(vertexFormat[i].index, vertexFormat[i].size, vertexFormat[i].type.type, ((vertexFormat[i].flags & Flag.Normalized.flag) != 0), vertexSize, countMul * vertexFormat[i].offset);
                GLES20.glEnableVertexAttribArray(vertexFormat[i].index);
            }
        }
    }

    public void disableVertexArray() {
        for (int i = 0; i < Attribute.MaxVertexAttribute.attribute; i++) {
            if ((vertexFormat[i].size > 0) && ((vertexFormat[i].flags & Flag.Ignored.flag) == 0)) {
                GLES20.glDisableVertexAttribArray(vertexFormat[i].index);
            }
        }
    }


    private void calculateVertexSize() {
        vertexSize = 0;
        for (int i = 0; i < Attribute.MaxVertexAttribute.attribute; i++) {
            vertexSize += vertexFormat[i].size * VertexFormat.getTypeSize(vertexFormat[i].type);
        }
    }

    private void calculateOffsets() {
        for (int i = 0; i < Attribute.MaxVertexAttribute.attribute; i++) {
            if (vertexFormat[i].size > 0) {
                int offset = 0;
                for (int j = 0; j < Attribute.MaxVertexAttribute.attribute; j++) {
                    if (vertexFormat[j].index < vertexFormat[i].index) {
                        offset += vertexFormat[j].size * VertexFormat.getTypeSize(vertexFormat[j].type);
                    }
                }
                vertexFormat[i].offset = offset;
            }
        }
    }

    private static int getTypeSize(Type type) {
        switch (type) {
            case Byte:
            case UByte:
                return 1;
            case Short:
            case UShort:
                return 2;
            case Int:
            case UInt:
            case Float:
                return 4;
            default:
                return 0;
        }
    }

    private VertexAttribInfo[] vertexFormat;
    int vertexSize;
    VertexArrayLayout vertexArrayLayout;
}
