package drwdrd.adev.engine;


public class matrix3f {
    public matrix3f() {

    }

    public matrix3f(float e0, float e1, float e2, float e3, float e4, float e5, float e6, float e7, float e8) {
        this.e0 = e0;
        this.e3 = e3;
        this.e6 = e6;

        this.e1 = e1;
        this.e4 = e4;
        this.e7 = e7;

        this.e2 = e2;
        this.e5 = e5;
        this.e8 = e8;
    }

    public matrix3f(matrix3f m) {
        e0 = m.e0;
        e3 = m.e3;
        e6 = m.e6;

        e1 = m.e1;
        e4 = m.e4;
        e7 = m.e7;

        e2 = m.e2;
        e5 = m.e5;
        e8 = m.e8;
    }

    public matrix3f(float[] e) {
        e0 = e[0];
        e3 = e[3];
        e6 = e[6];

        e1 = e[1];
        e4 = e[4];
        e7 = e[7];

        e2 = e[2];
        e5 = e[5];
        e8 = e[8];
    }

    public void loadIdentity() {
        e0 = 1.0f;
        e3 = 0.0f;
        e6 = 0.0f;

        e1 = 0.0f;
        e4 = 1.0f;
        e7 = 0.0f;

        e2 = 0.0f;
        e5 = 0.0f;
        e8 = 1.0f;
    }

    public void loadZero() {
        e0 = 0.0f;
        e3 = 0.0f;
        e6 = 0.0f;

        e1 = 0.0f;
        e4 = 0.0f;
        e7 = 0.0f;

        e2 = 0.0f;
        e5 = 0.0f;
        e8 = 0.0f;
    }

    public boolean equals(matrix3f m) {
        return ((m != null) &&
                (e0 == m.e0) && (e3 == m.e3) && (e6 == m.e6) &&
                (e1 == m.e1) && (e4 == m.e4) && (e7 == m.e7) &&
                (e2 == m.e2) && (e5 == m.e5) && (e8 == m.e8));
    }

    public int hashCode() {
        return (Float.floatToIntBits(e0) ^ Float.floatToIntBits(e3) ^ Float.floatToIntBits(e8) ^
                Float.floatToIntBits(e1) ^ Float.floatToIntBits(e4) ^ Float.floatToIntBits(e7) ^
                Float.floatToIntBits(e2) ^ Float.floatToIntBits(e5) ^ Float.floatToIntBits(e8));
    }

    public void get(float[] m) {
        m[0] = e0;
        m[3] = e3;
        m[6] = e6;

        m[1] = e1;
        m[4] = e4;
        m[7] = e7;

        m[2] = e2;
        m[5] = e5;
        m[8] = e8;
    }

    public void add(matrix3f m) {
        e0 += m.e0;
        e3 += m.e3;
        e6 += m.e6;

        e1 += m.e1;
        e4 += m.e4;
        e7 += m.e7;

        e2 += m.e2;
        e5 += m.e5;
        e8 += m.e8;
    }

    static public matrix3f add(matrix3f m1, matrix3f m2) {
        float e0 = m1.e0 + m2.e0;
        float e3 = m1.e3 + m2.e3;
        float e6 = m1.e6 + m2.e6;

        float e1 = m1.e1 + m2.e1;
        float e4 = m1.e4 + m2.e4;
        float e7 = m1.e7 + m2.e7;

        float e2 = m1.e2 + m2.e2;
        float e5 = m1.e5 + m2.e5;
        float e8 = m1.e8 + m2.e8;

        return new matrix3f(e0,e1,e2,e3,e4,e5,e6,e7,e8);
    }

    public void sub(matrix3f m) {
        e0 -= m.e0;
        e3 -= m.e3;
        e6 -= m.e6;

        e1 -= m.e1;
        e4 -= m.e4;
        e7 -= m.e7;

        e2 -= m.e2;
        e5 -= m.e5;
        e8 -= m.e8;
    }

    static public matrix3f sub(matrix3f m1, matrix3f m2) {
        float e0 = m1.e0 - m2.e0;
        float e3 = m1.e3 - m2.e3;
        float e6 = m1.e6 - m2.e6;

        float e1 = m1.e1 - m2.e1;
        float e4 = m1.e4 - m2.e4;
        float e7 = m1.e7 - m2.e7;

        float e2 = m1.e2 - m2.e2;
        float e5 = m1.e5 - m2.e5;
        float e8 = m1.e8 - m2.e8;

        return new matrix3f(e0,e1,e2,e3,e4,e5,e6,e7,e8);
    }

    public void mul(matrix3f m) {
        float ne0 = e0 * m.e0 + e3 * m.e1 + e6 * m.e2, ne3 = e0 * m.e3 + e3 * m.e4 + e6 * m.e5, ne6 = e0 * m.e6 + e3 * m.e7 + e6 * m.e8;

        float ne1 = e1 * m.e0 + e4 * m.e1 + e7 * m.e2, ne4 = e1 * m.e3 + e4 * m.e4 + e7 * m.e5, ne7 = e1 * m.e6 + e4 * m.e7 + e7 * m.e8;

        float ne2 = e2 * m.e0 + e5 * m.e1 + e8 * m.e2, ne5 = e2 * m.e3 + e5 * m.e4 + e8 * m.e5, ne8 = e2 * m.e6 + e5 * m.e7 + e8 * m.e8;

        e0 = ne0;
        e3 = ne3;
        e6 = ne6;
        e1 = ne1;
        e4 = ne4;
        e7 = ne7;
        e2 = ne2;
        e5 = ne5;
        e8 = ne8;
    }

    static public matrix3f mul(matrix3f m1, matrix3f m2) {
        float e0 = m1.e0 * m2.e0 + m1.e3 * m2.e1 + m1.e6 * m2.e2;
        float e3 = m1.e0 * m2.e3 + m1.e3 * m2.e4 + m1.e6 * m2.e5;
        float e6 = m1.e0 * m2.e6 + m1.e3 * m2.e7 + m1.e6 * m2.e8;

        float e1 = m1.e1 * m2.e0 + m1.e4 * m2.e1 + m1.e7 * m2.e2;
        float e4 = m1.e1 * m2.e3 + m1.e4 * m2.e4 + m1.e7 * m2.e5;
        float e7 = m1.e1 * m2.e6 + m1.e4 * m2.e7 + m1.e7 * m2.e8;

        float e2 = m1.e2 * m2.e0 + m1.e5 * m2.e1 + m1.e8 * m2.e2;
        float e5 = m1.e2 * m2.e3 + m1.e5 * m2.e4 + m1.e8 * m2.e5;
        float e8 = m1.e2 * m2.e6 + m1.e5 * m2.e7 + m1.e8 * m2.e8;

        return new matrix3f(e0,e1,e2,e3,e4,e5,e6,e7,e8);
    }

    public void mul(float s) {
        e0 *= s;
        e3 *= s;
        e6 *= s;

        e1 *= s;
        e4 *= s;
        e7 *= s;

        e2 *= s;
        e5 *= s;
        e8 *= s;
    }

    static public matrix3f mul(float s, matrix3f m) {
        float e0 = s * m.e0;
        float e3 = s * m.e3;
        float e6 = s * m.e6;

        float e1 = s * m.e1;
        float e4 = s * m.e4;
        float e7 = s * m.e7;

        float e2 = s * m.e2;
        float e5 = s * m.e5;
        float e8 = s * m.e8;

        return new matrix3f(e0,e1,e2,e3,e4,e5,e6,e7,e8);
    }

    public void negate() {
        e0 = -e0;
        e3 = -e3;
        e6 = -e6;

        e1 = -e1;
        e4 = -e4;
        e7 = -e7;

        e2 = -e2;
        e5 = -e5;
        e8 = -e8;
    }

    static public matrix3f negate(matrix3f m) {
        float e0 = -m.e0;
        float e3 = -m.e3;
        float e6 = -m.e6;

        float e1 = -m.e1;
        float e4 = -m.e4;
        float e7 = -m.e7;

        float e2 = -m.e2;
        float e5 = -m.e5;
        float e8 = -m.e8;

        return new matrix3f(e0,e1,e2,e3,e4,e5,e6,e7,e8);
    }

    public void transpose() {
        float te1 = e1;
        e1 = e3;
        e3 = te1;
        float te2 = e2;
        e2 = e6;
        e6 = te2;
        float te5 = e5;
        e5 = e7;
        e7 = te5;
    }

    public float det() {
        return (e0 * (e4 * e8 - e7 * e5) -
                e1 * (e3 * e8 - e5 * e6) +
                e2 * (e3 * e7 - e4 * e6));
    }

    public void inverse() {
        float idet = 1.0f / det();
        float
                inv0 = idet * (e4 * e8 - e7 * e5), inv3 = idet * (e6 * e5 - e3 * e8), inv6 = idet * (e3 * e7 - e6 * e4),
                inv1 = idet * (e7 * e2 - e1 * e8), inv4 = idet * (e0 * e8 - e6 * e2), inv7 = idet * (e6 * e1 - e0 * e7),
                inv2 = idet * (e1 * e5 - e4 * e2), inv5 = idet * (e3 * e2 - e0 * e5), inv8 = idet * (e0 * e4 - e3 * e1);

        e0 = inv0;
        e3 = inv3;
        e6 = inv6;
        e1 = inv1;
        e4 = inv4;
        e7 = inv7;
        e2 = inv2;
        e5 = inv5;
        e8 = inv8;
    }

    public void setScale(float sx, float sy) {
        loadIdentity();
        e0 = sx;
        e4 = sy;
    }

    public void setScale(vector2f s) {
        loadIdentity();
        e0 = s.ex;
        e4 = s.ey;
    }

    public void setScale(float[] s) {
        loadIdentity();
        e0 = s[0];
        e4 = s[1];
    }

    public void setScalePart(float sx, float sy) {
        e0 = sx;
        e4 = sy;
    }

    public void setScalePart(vector2f s) {
        e0 = s.ex;
        e4 = s.ey;
    }

    public void setScalePart(float[] s) {
        e0 = s[0];
        e4 = s[1];
    }

    public void setTranslation(float tx, float ty) {
        loadIdentity();
        e6 = tx;
        e7 = ty;
    }

    public void setTranslation(vector2f t) {
        loadIdentity();
        e6 = t.ex;
        e7 = t.ey;
    }

    public void setTranslation(float[] t) {
        loadIdentity();
        e6 = t[0];
        e7 = t[1];
    }

    public void setTranslationPart(float tx, float ty) {
        e6 = tx;
        e7 = ty;
    }

    public void setTranslationPart(vector2f t) {
        e6 = t.ex;
        e7 = t.ey;
    }

    public void setTranslationPart(float[] t) {
        e6 = t[0];
        e7 = t[1];
    }

    public void setRotation(float angle) {
        loadIdentity();
        e0 = (float)Math.cos(angle);
        e1 = (float)Math.sin(angle);
        e3 = -(float)Math.sin(angle);
        e4 = (float)Math.cos(angle);
    }

    public void setRotationPart(float angle) {
        e0 = (float)Math.cos(angle);
        e1 = (float)Math.sin(angle);
        e3 = -(float)Math.sin(angle);
        e4 = (float)Math.cos(angle);
    }

    public float e0, e3, e6,
                 e1, e4, e7,
                 e2, e5, e8;
}
