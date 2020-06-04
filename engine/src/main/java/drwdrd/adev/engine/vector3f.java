package drwdrd.adev.engine;


import android.graphics.Color;

public class vector3f {

    public vector3f() {

    }

    public vector3f(vector3f v) {
        ex = v.ex;
        ey = v.ey;
        ez = v.ez;
    }

    public vector3f(float x, float y, float z) {
        ex = x;
        ey = y;
        ez = z;
    }

    public vector3f(float[] v) {
        ex = v[0];
        ey = v[1];
        ez = v[2];
    }

    private vector3f(int color) {
        ex = Color.red(color)/255f;
        ey = Color.green(color)/255f;
        ez = Color.blue(color)/255f;
    }

    public static vector3f fromColor(int color) {
        return new vector3f(color);
    }

    public int toColor() {
        return Color.rgb(Math.round(255f*ex),Math.round(255f*ey),Math.round(255f*ez));
    }

    public boolean equals(vector3f v) {
        return (v != null) && (ex == v.ex) && (ey == v.ey) && (ez == v.ez);
    }

    public int hashCode() {
        int xbits = Float.floatToIntBits(ex);
        int ybits = Float.floatToIntBits(ey);
        int zbits = Float.floatToIntBits(ez);
        return xbits ^ ybits ^ zbits;
    }

    public void get(float[] v) {
        v[0] = ex;
        v[1] = ey;
        v[2] = ez;
    }

    public void add(vector3f v) {
        ex += v.ex;
        ey += v.ey;
        ez += v.ez;
    }

    static public vector3f add(vector3f v1, vector3f v2) {
        float ex = v1.ex + v2.ex;
        float ey = v1.ey + v2.ey;
        float ez = v1.ez + v2.ez;
        return new vector3f(ex,ey,ez);
    }

    public void sub(vector3f v) {
        ex -= v.ex;
        ey -= v.ey;
        ez -= v.ez;
    }

    static public vector3f sub(vector3f v1, vector3f v2) {
        float ex = v1.ex - v2.ex;
        float ey = v1.ey - v2.ey;
        float ez = v1.ez - v2.ez;
        return new vector3f(ex,ey,ez);
    }

    public void mul(vector3f v) {
        ex *= v.ex;
        ey *= v.ey;
        ez *= v.ez;
    }

    static public vector3f mul(vector3f v1, vector3f v2) {
        float ex = v1.ex * v2.ex;
        float ey = v1.ey * v2.ey;
        float ez = v1.ez * v2.ez;
        return new vector3f(ex,ey,ez);
    }

    public void mul(float s) {
        ex *= s;
        ey *= s;
        ez *= s;
    }

    static public vector3f mul(float s, vector3f v) {
        float ex = s * v.ex;
        float ey = s * v.ey;
        float ez = s * v.ez;
        return new vector3f(ex,ey,ez);
    }

    public void mul(matrix3f m) {
        float nex = m.e0 * ex + m.e3 * ey + m.e6 * ez;
        float ney = m.e1 * ex + m.e4 * ey + m.e7 * ez;
        float nez = m.e2 * ex + m.e5 * ey + m.e8 * ez;
        ex = nex;
        ey = ney;
        ez = nez;
    }

    static public vector3f mul(vector3f v, matrix3f m) {
        float ex = m.e0 * v.ex + m.e3 * v.ey + m.e6 * v.ez;
        float ey = m.e1 * v.ex + m.e4 * v.ey + m.e7 * v.ez;
        float ez = m.e2 * v.ex + m.e5 * v.ey + m.e8 * v.ez;
        return new vector3f(ex,ey,ez);
    }

    public void negate() {
        ex = -ex;
        ey = -ey;
        ez = -ez;
    }

    static public vector3f negate(vector3f v) {
        return new vector3f(-v.ex,-v.ey,-v.ez);
    }

    public float abs() {
        return (ex * ex + ey * ey + ez * ez);
    }

    public float dotProduct(vector3f v) {
        return (ex * v.ex + ey * v.ey + ez * v.ez);
    }

    static  public vector3f crossProduct(vector3f v1, vector3f v2) {
        float ex = v1.ey * v2.ez - v1.ez * v2.ey;
        float ey = v1.ez * v2.ex - v1.ex * v2.ez;
        float ez = v1.ex * v2.ey - v1.ey * v2.ex;
        return new vector3f(ex,ey,ez);
    }

    public void normalize() {
        float length = (float) Math.sqrt(ex * ex + ey * ey + ez * ez);
        ex /= length;
        ey /= length;
        ez /= length;
    }

    public float length() {
        return (float) Math.sqrt(ex * ex + ey * ey + ez * ez);
    }

    public float squaredLength() {
        return ex * ex + ey * ey + ez * ez;
    }

    void set(float x, float y, float z) {
        ex = x;
        ey = y;
        ez = z;
    }

    public void transformed(matrix4f m) {
        float nex = m.e0 * ex + m.e4 * ey + m.e8 * ez + m.e12;
        float ney = m.e1 * ex + m.e5 * ey + m.e9 * ez + m.e13;
        float nez = m.e2 * ex + m.e6 * ey + m.e10 * ez + m.e14;

        ex = nex;
        ey = ney;
        ez = nez;
    }

    static public vector3f transformed(vector3f v, matrix4f m) {
        float ex = m.e0 * v.ex + m.e4 * v.ey + m.e8 * v.ez + m.e12;
        float ey = m.e1 * v.ex + m.e5 * v.ey + m.e9 * v.ez + m.e13;
        float ez = m.e2 * v.ex + m.e6 * v.ey + m.e10 * v.ez + m.e14;
        return new vector3f(ex,ey,ez);
    }

    public void rotated(matrix4f m) {
        float nex = m.e0 * ex + m.e4 * ey + m.e8 * ez;
        float ney = m.e1 * ex + m.e5 * ey + m.e9 * ez;
        float nez = m.e2 * ex + m.e6 * ey + m.e10 * ez;

        ex = nex;
        ey = ney;
        ez = nez;
    }

    static public vector3f rotated(vector3f v, matrix4f m) {
        float ex = m.e0 * v.ex + m.e4 * v.ey + m.e8 * v.ez;
        float ey = m.e1 * v.ex + m.e5 * v.ey + m.e9 * v.ez;
        float ez = m.e2 * v.ex + m.e6 * v.ey + m.e10 * v.ez;
        return new vector3f(ex,ey,ez);
    }

    public void inverseRotated(matrix4f m) {
        float nex = m.e0 * ex + m.e1 * ey + m.e2 * ez;
        float ney = m.e4 * ex + m.e5 * ey + m.e6 * ez;
        float nez = m.e8 * ex + m.e9 * ey + m.e10 * ez;

        ex = nex;
        ey = ney;
        ez = nez;
    }

    static public vector3f inverseRotated(vector3f v, matrix4f m) {
        float ex = m.e0 * v.ex + m.e1 * v.ey + m.e2 * v.ez;
        float ey = m.e4 * v.ex + m.e5 * v.ey + m.e6 * v.ez;
        float ez = m.e8 * v.ex + m.e9 * v.ey + m.e10 * v.ez;
        return new vector3f(ex,ey,ez);
    }

    public void translated(matrix4f m) {
        ex += m.e12;
        ey += m.e13;
        ez += m.e14;
    }

    static public vector3f translated(vector3f v, matrix4f m) {
        float ex = v.ex + m.e12;
        float ey = v.ey + m.e13;
        float ez = v.ez + m.e14;
        return new vector3f(ex,ey,ez);
    }

    public void inverseTranslated(matrix4f m) {
        ex -= m.e12;
        ey -= m.e13;
        ez -= m.e14;
    }

    static public vector3f inverseTranslated(vector3f v, matrix4f m) {
        float ex = v.ex - m.e12;
        float ey = v.ey - m.e13;
        float ez = v.ez - m.e14;
        return new vector3f(ex,ey,ez);
    }

    public void scaled(matrix4f m) {
        ex *= m.e0;
        ey *= m.e5;
        ez *= m.e10;
    }

    static public vector3f scaled(vector3f v, matrix4f m) {
        float ex = v.ex * m.e0;
        float ey = v.ey * m.e5;
        float ez = v.ez * m.e10;
        return new vector3f(ex,ey,ez);
    }

    public float ex;
    public float ey;
    public float ez;
}
