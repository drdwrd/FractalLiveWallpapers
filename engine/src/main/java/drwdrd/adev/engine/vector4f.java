package drwdrd.adev.engine;


import android.graphics.Color;

public class vector4f {

    public vector4f() {

    }

    public vector4f(vector4f v) {
        ex = v.ex;
        ey = v.ey;
        ez = v.ez;
        ew = v.ew;
    }

    public vector4f(float x, float y, float z, float w) {
        ex = x;
        ey = y;
        ez = z;
        ew = w;
    }

    public vector4f(float[] v) {
        ex = v[0];
        ey = v[1];
        ez = v[2];
        ew = v[3];
    }

    private vector4f(int color) {
        ex = Color.red(color)/255f;
        ey = Color.green(color)/255f;
        ez = Color.blue(color)/255f;
        ew = Color.alpha(color)/255f;
    }

    public static vector4f fromColor(int color) {
        return new vector4f(color);
    }

    public int toColor() {
        return Color.argb(Math.round(255f*ew),Math.round(255f*ex),Math.round(255f*ey),Math.round(255f*ez));
    }

    public boolean equals(vector4f v) {
        return (v != null) && (ex == v.ex) && (ey == v.ey) && (ez == v.ez) && (ew == v.ew);
    }

    public int hashCode() {
        int xbits = Float.floatToIntBits(ex);
        int ybits = Float.floatToIntBits(ey);
        int zbits = Float.floatToIntBits(ez);
        int wbits = Float.floatToIntBits(ew);
        return xbits ^ ybits ^ zbits ^ wbits;
    }

    public void get(float[] v) {
        v[0] = ex;
        v[1] = ey;
        v[2] = ez;
        v[3] = ew;
    }

    public void add(vector4f v) {
        ex += v.ex;
        ey += v.ey;
        ez += v.ez;
        ew += v.ew;
    }

    static public vector4f add(vector4f v1, vector4f v2) {
        float ex = v1.ex + v2.ex;
        float ey = v1.ey + v2.ey;
        float ez = v1.ez + v2.ez;
        float ew = v1.ew + v2.ew;
        return new vector4f(ex,ey,ez,ew);
    }

    public void sub(vector4f v) {
        ex -= v.ex;
        ey -= v.ey;
        ez -= v.ez;
        ew -= v.ew;
    }

    static public vector4f sub(vector4f v1, vector4f v2) {
        float ex = v1.ex - v2.ex;
        float ey = v1.ey - v2.ey;
        float ez = v1.ez - v2.ez;
        float ew = v1.ew - v2.ew;
        return new vector4f(ex,ey,ez,ew);
    }

    public void mul(vector4f v) {
        ex *= v.ex;
        ey *= v.ey;
        ez *= v.ez;
        ew *= v.ew;
    }

    static public vector4f mul(vector4f v1, vector4f v2) {
        float ex = v1.ex * v2.ex;
        float ey = v1.ey * v2.ey;
        float ez = v1.ez * v2.ez;
        float ew = v1.ew * v2.ew;
        return new vector4f(ex,ey,ez,ew);
    }

    public void mul(float s) {
        ex *= s;
        ey *= s;
        ez *= s;
        ew *= s;
    }

    static public vector4f mul(float s, vector4f v) {
        float ex = s * v.ex;
        float ey = s * v.ey;
        float ez = s * v.ez;
        float ew = s * v.ew;
        return new vector4f(ex,ey,ez,ew);
    }

    public void mul(matrix4f m) {
        float nx = m.e0 * ex + m.e4 * ey + m.e8 * ez + m.e12 * ew;
        float ny = m.e1 * ex + m.e5 * ey + m.e9 * ez + m.e13 * ew;
        float nz = m.e2 * ex + m.e6 * ey + m.e10 * ez + m.e14 * ew;
        float nw = m.e3 * ex + m.e7 * ey + m.e11 * ez + m.e15 * ew;

        ex = nx;
        ey = ny;
        ez = nz;
        ew = nw;
    }

    static public vector4f mul(vector4f v, matrix4f m) {
        float ex = m.e0 * v.ex + m.e4 * v.ey + m.e8 * v.ez + m.e12 * v.ew;
        float ey = m.e1 * v.ex + m.e5 * v.ey + m.e9 * v.ez + m.e13 * v.ew;
        float ez = m.e2 * v.ex + m.e6 * v.ey + m.e10 * v.ez + m.e14 * v.ew;
        float ew = m.e3 * v.ex + m.e7 * v.ey + m.e11 * v.ez + m.e15 * v.ew;
        return new vector4f(ex,ey,ez,ew);
    }

    public void negate() {
        ex = -ex;
        ey = -ey;
        ez = -ez;
        ew = -ew;
    }

    static public vector4f negate(vector4f v) {
        float ex = -v.ex;
        float ey = -v.ey;
        float ez = -v.ez;
        float ew = -v.ew;
        return new vector4f(ex,ey,ez,ew);
    }

    public float abs() {
        return (ex * ex + ey * ey + ez * ez + ew * ew);
    }

    public float dot(vector4f v) {
        return (ex * v.ex + ey * v.ey + ez * v.ez + ew * v.ew);
    }

    public void normalize() {
        float length = (float) Math.sqrt(ex * ex + ey * ey + ez * ez + ew * ew);
        ex /= length;
        ey /= length;
        ez /= length;
        ew /= length;
    }

    public void gammaCorrection(float gamma)
    {
        ex=(float)Math.pow(ex,gamma);
        ey=(float)Math.pow(ey,gamma);
        ez=(float)Math.pow(ez,gamma);
    }

    public float length() {
        return (float) Math.sqrt(ex * ex + ey * ey + ez * ez + ew * ew);
    }

    public float squaredLength() {
        return ex * ex + ey * ey + ez * ez + ew * ew;
    }

    void set(float x, float y, float z, float w) {
        ex = x;
        ey = y;
        ez = z;
        ew = w;
    }


    public float ex;
    public float ey;
    public float ez;
    public float ew;
}
