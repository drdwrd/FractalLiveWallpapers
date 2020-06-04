package drwdrd.adev.engine;


public class vector2f {

    public vector2f() {

    }

    public vector2f(vector2f v) {
        ex = v.ex;
        ey = v.ey;
    }

    public vector2f(float x, float y) {
        ex = x;
        ey = y;
    }

    public vector2f(float[] v) {
        ex = v[0];
        ey = v[1];
    }


    public boolean equals(vector2f v) {
        return (v != null) && (ex == v.ex) && (ey == v.ey);
    }

    public int hashCode() {
        int xbits = Float.floatToIntBits(ex);
        int ybits = Float.floatToIntBits(ey);
        return xbits ^ ybits;
    }

    public void get(float[] v) {
        v[0] = ex;
        v[1] = ey;
    }

    public void add(vector2f v) {
        ex += v.ex;
        ey += v.ey;
    }

    static public vector2f add(vector2f v1, vector2f v2) {
        return new vector2f(v1.ex + v2.ex,v1.ey + v2.ey);
    }

    public void sub(vector2f v) {
        ex -= v.ex;
        ey -= v.ey;
    }

    static public vector2f sub(vector2f v1, vector2f v2) {
        return  new vector2f(v1.ex - v2.ex,v1.ey - v2.ey);
    }

    public void mul(vector2f v) {
        ex *= v.ex;
        ey *= v.ey;
    }

    static public vector2f mul(vector2f v1, vector2f v2) {
        return new vector2f(v1.ex * v2.ex,v1.ey * v2.ey);
    }

    public void mul(float s) {
        ex *= s;
        ey *= s;
    }

    static public vector2f mul(float s, vector2f v) {
        return new vector2f(s * v.ex,s * v.ey);
    }

    public void mul(matrix2f m) {
        float x = m.e0 * ex + m.e2 * ey;
        float y = m.e1 * ex + m.e3 * ey;
        ex = x;
        ey = y;
    }

    static public vector2f mul(vector2f v, matrix2f m) {
        float ex = m.e0 * v.ex + m.e2 * v.ey;
        float ey = m.e1 * v.ex + m.e3 * v.ey;
        return new vector2f(ex,ey);
    }

    static public vector2f mix(vector2f a,vector2f b,float s) {
        return new vector2f(s*a.ex+(1.0f-s)*b.ex,s*a.ey+(1.0f-s)*b.ey);
    }

    public void negate() {
        ex = -ex;
        ey = -ey;
    }

    static public vector2f negate(vector2f v) {
        return new vector2f(-v.ex,-v.ey);
    }

    public float abs() {
        return (ex * ex + ey * ey);
    }

    public float dotProduct(vector2f v) {
        return (ex * v.ex + ey * v.ey);
    }

    public void normalize() {
        float length = (float) Math.sqrt(ex * ex + ey * ey);
        ex /= length;
        ey /= length;
    }

    public float length() {
        return (float) Math.sqrt(ex * ex + ey * ey);
    }

    public float squaredLength() {
        return ex * ex + ey * ey;
    }

    public void set(float x, float y) {
        ex = x;
        ey = y;
    }

    public void transformed(matrix3f m) {
        float nex = m.e0 * ex + m.e3 * ey + m.e6;
        float ney = m.e1 * ex + m.e4 * ey + m.e7;
        ex = nex;
        ey = ney;
    }

    static public vector2f transformed(vector2f v, matrix3f m) {
        float ex = m.e0 * v.ex + m.e3 * v.ey + m.e6;
        float ey = m.e1 * v.ex + m.e4 * v.ey + m.e7;
        return new vector2f(ex,ey);
    }

    public void rotated(matrix3f m) {
        float nex = m.e0 * ex + m.e3 * ey;
        float ney = m.e1 * ex + m.e4 * ey;
        ex = nex;
        ey = ney;
    }

    static public vector2f rotated(vector2f v, matrix3f m) {
        float ex = m.e0 * v.ex + m.e3 * v.ey;
        float ey = m.e1 * v.ex + m.e4 * v.ey;
        return new vector2f(ex,ey);
    }

    public void inverseRotated(matrix3f m) {
        float nex = m.e0 * ex + m.e1 * ey;
        float ney = m.e3 * ex + m.e4 * ey;
        ex = nex;
        ey = ney;
    }

    static public vector2f inverseRotated(vector2f v, matrix3f m) {
        float ex = m.e0 * v.ex + m.e1 * v.ey;
        float ey = m.e3 * v.ex + m.e4 * v.ey;
        return new vector2f(ex,ey);
    }

    public void translated(matrix3f m) {
        ex += m.e6;
        ey += m.e7;
    }

    static public vector2f translated(vector2f v, matrix3f m) {
        float ex = v.ex + m.e6;
        float ey = v.ey + m.e7;
        return new vector2f(ex,ey);
    }

    public void inverseTranslated(matrix3f m) {
        ex -= m.e6;
        ey -= m.e7;
    }

    static public vector2f inverseTranslated(vector2f v, matrix3f m) {
        float ex = v.ex - m.e6;
        float ey = v.ey - m.e7;
        return new vector2f(ex,ey);
    }

    public void scaled(matrix3f m) {
        ex *= m.e0;
        ey *= m.e4;
    }

    static public vector2f scaled(vector2f v, matrix3f m) {
        float ex = v.ex * m.e0;
        float ey = v.ey * m.e4;
        return new vector2f(ex,ey);
    }

    public float ex;
    public float ey;
}
