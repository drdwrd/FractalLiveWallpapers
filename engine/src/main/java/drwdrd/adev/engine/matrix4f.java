package drwdrd.adev.engine;


public class matrix4f {
    public matrix4f() {

    }

    public matrix4f(float e0, float e1, float e2, float e3, float e4, float e5, float e6, float e7, float e8, float e9,
                    float e10, float e11, float e12, float e13, float e14, float e15) {
        this.e0 = e0;
        this.e4 = e4;
        this.e8 = e8;
        this.e12 = e12;

        this.e1 = e1;
        this.e5 = e5;
        this.e9 = e9;
        this.e13 = e13;

        this.e2 = e2;
        this.e6 = e6;
        this.e10 = e10;
        this.e14 = e14;

        this.e3 = e3;
        this.e7 = e7;
        this.e11 = e11;
        this.e15 = e15;
    }

    public matrix4f(matrix4f m) {
        e0 = m.e0;
        e4 = m.e4;
        e8 = m.e8;
        e12 = m.e12;

        e1 = m.e1;
        e5 = m.e5;
        e9 = m.e9;
        e13 = m.e13;

        e2 = m.e2;
        e6 = m.e6;
        e10 = m.e10;
        e14 = m.e14;

        e3 = m.e3;
        e7 = m.e7;
        e11 = m.e11;
        e15 = m.e15;
    }

    public matrix4f(float[] e) {
        e0 = e[0];
        e4 = e[4];
        e8 = e[8];
        e12 = e[12];

        e1 = e[1];
        e5 = e[5];
        e9 = e[9];
        e13 = e[13];

        e2 = e[2];
        e6 = e[6];
        e10 = e[10];
        e14 = e[14];

        e3 = e[3];
        e7 = e[7];
        e11 = e[11];
        e15 = e[15];
    }

    public void loadIdentity() {
        e0 = 1.0f;
        e4 = 0.0f;
        e8 = 0.0f;
        e12 = 0.0f;

        e1 = 0.0f;
        e5 = 1.0f;
        e9 = 0.0f;
        e13 = 0.0f;

        e2 = 0.0f;
        e6 = 0.0f;
        e10 = 1.0f;
        e14 = 0.0f;

        e3 = 0.0f;
        e7 = 0.0f;
        e11 = 0.0f;
        e15 = 1.0f;
    }

    public void loadZero() {
        e0 = 0.0f;
        e4 = 0.0f;
        e8 = 0.0f;
        e12 = 0.0f;

        e1 = 0.0f;
        e5 = 0.0f;
        e9 = 0.0f;
        e13 = 0.0f;

        e2 = 0.0f;
        e6 = 0.0f;
        e10 = 0.0f;
        e14 = 0.0f;

        e3 = 0.0f;
        e7 = 0.0f;
        e11 = 0.0f;
        e15 = 0.0f;
    }

    public boolean equals(matrix4f m) {
        return ((m != null) &&
                (e0 == m.e0) && (e4 == m.e4) && (e8 == m.e8) && (e12 == m.e12) &&
                (e1 == m.e1) && (e5 == m.e5) && (e9 == m.e9) && (e13 == m.e13) &&
                (e2 == m.e2) && (e6 == m.e6) && (e10 == m.e10) && (e14 == m.e14) &&
                (e3 == m.e3) && (e7 == m.e7) && (e11 == m.e11) && (e15 == m.e15));
    }

    public int hashCode() {
        return (Float.floatToIntBits(e0) ^ Float.floatToIntBits(e4) ^ Float.floatToIntBits(e8) ^ Float.floatToIntBits(e12) ^
                Float.floatToIntBits(e1) ^ Float.floatToIntBits(e5) ^ Float.floatToIntBits(e9) ^ Float.floatToIntBits(e13) ^
                Float.floatToIntBits(e2) ^ Float.floatToIntBits(e6) ^ Float.floatToIntBits(e10) ^ Float.floatToIntBits(e14) ^
                Float.floatToIntBits(e3) ^ Float.floatToIntBits(e7) ^ Float.floatToIntBits(e11) ^ Float.floatToIntBits(e15));
    }

    public void get(float[] m) {
        m[0] = e0;
        m[4] = e4;
        m[8] = e8;
        m[12] = e12;

        m[1] = e1;
        m[5] = e5;
        m[9] = e9;
        m[13] = e13;

        m[2] = e2;
        m[6] = e6;
        m[10] = e10;
        m[14] = e14;

        m[3] = e3;
        m[7] = e7;
        m[11] = e11;
        m[15] = e15;
    }

    public void add(matrix4f m) {
        e0 += m.e0;
        e4 += m.e4;
        e8 += m.e8;
        e12 += m.e12;

        e1 += m.e1;
        e5 += m.e5;
        e9 += m.e9;
        e13 += m.e13;

        e2 += m.e2;
        e6 += m.e6;
        e10 += m.e10;
        e14 += m.e14;

        e3 += m.e3;
        e7 += m.e7;
        e11 += m.e11;
        e15 += m.e15;
    }

    static public matrix4f add(matrix4f m1, matrix4f m2) {
        float e0 = m1.e0 + m2.e0;
        float e4 = m1.e4 + m2.e4;
        float e8 = m1.e8 + m2.e8;
        float e12 = m1.e12 + m2.e12;

        float e1 = m1.e1 + m2.e1;
        float e5 = m1.e5 + m2.e5;
        float e9 = m1.e9 + m2.e9;
        float e13 = m1.e13 + m2.e13;

        float e2 = m1.e2 + m2.e2;
        float e6 = m1.e6 + m2.e6;
        float e10 = m1.e10 + m2.e10;
        float e14 = m1.e14 + m2.e14;

        float e3 = m1.e3 + m2.e3;
        float e7 = m1.e7 + m2.e7;
        float e11 = m1.e11 + m2.e11;
        float e15 = m1.e15 + m2.e15;

        return new matrix4f(e0,e1,e2,e3,e4,e5,e6,e7,e8,e9,e10,e11,e12,e13,e14,e15);
    }

    public void sub(matrix4f m) {
        e0 -= m.e0;
        e4 -= m.e4;
        e8 -= m.e8;
        e12 -= m.e12;

        e1 -= m.e1;
        e5 -= m.e5;
        e9 -= m.e9;
        e13 -= m.e13;

        e2 -= m.e2;
        e6 -= m.e6;
        e10 -= m.e10;
        e14 -= m.e14;

        e3 -= m.e3;
        e7 -= m.e7;
        e11 -= m.e11;
        e15 -= m.e15;
    }

    static public matrix4f sub(matrix4f m1, matrix4f m2) {
        float e0 = m1.e0 - m2.e0;
        float e4 = m1.e4 - m2.e4;
        float e8 = m1.e8 - m2.e8;
        float e12 = m1.e12 - m2.e12;

        float e1 = m1.e1 - m2.e1;
        float e5 = m1.e5 - m2.e5;
        float e9 = m1.e9 - m2.e9;
        float e13 = m1.e13 - m2.e13;

        float e2 = m1.e2 - m2.e2;
        float e6 = m1.e6 - m2.e6;
        float e10 = m1.e10 - m2.e10;
        float e14 = m1.e14 - m2.e14;

        float e3 = m1.e3 - m2.e3;
        float e7 = m1.e7 - m2.e7;
        float e11 = m1.e11 - m2.e11;
        float e15 = m1.e15 - m2.e15;

        return new matrix4f(e0,e1,e2,e3,e4,e5,e6,e7,e8,e9,e10,e11,e12,e13,e14,e15);
    }

    public void mul(matrix4f m) {
        float ne0 = e0 * m.e0 + e4 * m.e1 + e8 * m.e2 + e12 * m.e3;
        float ne1 = e1 * m.e0 + e5 * m.e1 + e9 * m.e2 + e13 * m.e3;
        float ne2 = e2 * m.e0 + e6 * m.e1 + e10 * m.e2 + e14 * m.e3;
        float ne3 = e3 * m.e0 + e7 * m.e1 + e11 * m.e2 + e15 * m.e3;

        float ne4 = e0 * m.e4 + e4 * m.e5 + e8 * m.e6 + e12 * m.e7;
        float ne5 = e1 * m.e4 + e5 * m.e5 + e9 * m.e6 + e13 * m.e7;
        float ne6 = e2 * m.e4 + e6 * m.e5 + e10 * m.e6 + e14 * m.e7;
        float ne7 = e3 * m.e4 + e7 * m.e5 + e11 * m.e6 + e15 * m.e7;

        float ne8 = e0 * m.e8 + e4 * m.e9 + e8 * m.e10 + e12 * m.e11;
        float ne9 = e1 * m.e8 + e5 * m.e9 + e9 * m.e10 + e13 * m.e11;
        float ne10 = e2 * m.e8 + e6 * m.e9 + e10 * m.e10 + e14 * m.e11;
        float ne11 = e3 * m.e8 + e7 * m.e9 + e11 * m.e10 + e15 * m.e11;

        float ne12 = e0 * m.e12 + e4 * m.e13 + e8 * m.e14 + e12 * m.e15;
        float ne13 = e1 * m.e12 + e5 * m.e13 + e9 * m.e14 + e13 * m.e15;
        float ne14 = e2 * m.e12 + e6 * m.e13 + e10 * m.e14 + e14 * m.e15;
        float ne15 = e3 * m.e12 + e7 * m.e13 + e11 * m.e14 + e15 * m.e15;

        e0 = ne0;
        e4 = ne4;
        e8 = ne8;
        e12 = ne12;
        e1 = ne1;
        e5 = ne5;
        e9 = ne9;
        e13 = ne13;
        e2 = ne2;
        e6 = ne6;
        e10 = ne10;
        e14 = ne14;
        e3 = ne3;
        e7 = ne7;
        e11 = ne11;
        e15 = ne15;

    }

    static public matrix4f mul(matrix4f m1, matrix4f m2) {
        float e0 = m1.e0 * m2.e0 + m1.e4 * m2.e1 + m1.e8 * m2.e2 + m1.e12 * m2.e3;
        float e1 = m1.e1 * m2.e0 + m1.e5 * m2.e1 + m1.e9 * m2.e2 + m1.e13 * m2.e3;
        float e2 = m1.e2 * m2.e0 + m1.e6 * m2.e1 + m1.e10 * m2.e2 + m1.e14 * m2.e3;
        float e3 = m1.e3 * m2.e0 + m1.e7 * m2.e1 + m1.e11 * m2.e2 + m1.e15 * m2.e3;

        float e4 = m1.e0 * m2.e4 + m1.e4 * m2.e5 + m1.e8 * m2.e6 + m1.e12 * m2.e7;
        float e5 = m1.e1 * m2.e4 + m1.e5 * m2.e5 + m1.e9 * m2.e6 + m1.e13 * m2.e7;
        float e6 = m1.e2 * m2.e4 + m1.e6 * m2.e5 + m1.e10 * m2.e6 + m1.e14 * m2.e7;
        float e7 = m1.e3 * m2.e4 + m1.e7 * m2.e5 + m1.e11 * m2.e6 + m1.e15 * m2.e7;

        float e8 = m1.e0 * m2.e8 + m1.e4 * m2.e9 + m1.e8 * m2.e10 + m1.e12 * m2.e11;
        float e9 = m1.e1 * m2.e8 + m1.e5 * m2.e9 + m1.e9 * m2.e10 + m1.e13 * m2.e11;
        float e10 = m1.e2 * m2.e8 + m1.e6 * m2.e9 + m1.e10 * m2.e10 + m1.e14 * m2.e11;
        float e11 = m1.e3 * m2.e8 + m1.e7 * m2.e9 + m1.e11 * m2.e10 + m1.e15 * m2.e11;

        float e12 = m1.e0 * m2.e12 + m1.e4 * m2.e13 + m1.e8 * m2.e14 + m1.e12 * m2.e15;
        float e13 = m1.e1 * m2.e12 + m1.e5 * m2.e13 + m1.e9 * m2.e14 + m1.e13 * m2.e15;
        float e14 = m1.e2 * m2.e12 + m1.e6 * m2.e13 + m1.e10 * m2.e14 + m1.e14 * m2.e15;
        float e15 = m1.e3 * m2.e12 + m1.e7 * m2.e13 + m1.e11 * m2.e14 + m1.e15 * m2.e15;

        return new matrix4f(e0,e1,e2,e3,e4,e5,e6,e7,e8,e9,e10,e11,e12,e13,e14,e15);
    }

    public void mul(float s) {
        e0 *= s;
        e4 *= s;
        e8 *= s;
        e12 *= s;
        e1 *= s;
        e5 *= s;
        e9 *= s;
        e13 *= s;
        e2 *= s;
        e6 *= s;
        e10 *= s;
        e14 *= s;
        e3 *= s;
        e7 *= s;
        e11 *= s;
        e15 *= s;
    }

    static public matrix4f mul(float s, matrix4f m) {
        float e0 = m.e0 * s;
        float e4 = m.e4 * s;
        float e8 = m.e8 * s;
        float e12 = m.e12 * s;
        float e1 = m.e1 * s;
        float e5 = m.e5 * s;
        float e9 = m.e9 * s;
        float e13 = m.e13 * s;
        float e2 = m.e2 * s;
        float e6 = m.e6 * s;
        float e10 = m.e10 * s;
        float e14 = m.e14 * s;
        float e3 = m.e3 * s;
        float e7 = m.e7 * s;
        float e11 = m.e11 * s;
        float e15 = m.e15 * s;

        return new matrix4f(e0,e1,e2,e3,e4,e5,e6,e7,e8,e9,e10,e11,e12,e13,e14,e15);
    }

    public void negate() {
        e0 = -e0;
        e4 = -e4;
        e8 = -e8;
        e12 = -e12;
        e1 = -e1;
        e5 = -e5;
        e9 = -e9;
        e13 = -e13;
        e2 = -e2;
        e6 = -e6;
        e10 = -e10;
        e14 = -e14;
        e3 = -e3;
        e7 = -e7;
        e11 = -e11;
        e15 = -e15;
    }

    static public matrix4f negate(matrix4f m) {
        float e0 = -m.e0;
        float e4 = -m.e4;
        float e8 = -m.e8;
        float e12 = -m.e12;
        float e1 = -m.e1;
        float e5 = -m.e5;
        float e9 = -m.e9;
        float e13 = -m.e13;
        float e2 = -m.e2;
        float e6 = -m.e6;
        float e10 = -m.e10;
        float e14 = -m.e14;
        float e3 = -m.e3;
        float e7 = -m.e7;
        float e11 = -m.e11;
        float e15 = -m.e15;

        return new matrix4f(e0,e1,e2,e3,e4,e5,e6,e7,e8,e9,e10,e11,e12,e13,e14,e15);
    }

    public void transpose() {
        float te1 = e1;
        e1 = e4;
        float te2 = e2;
        e2 = e8;
        float te3 = e3;
        e3 = e12;

        e4 = te1;
        float te6 = e6;
        e6 = e8;
        float te7 = e7;
        e7 = e13;

        e8 = te2;
        e9 = te6;
        float te11 = e11;
        e11 = e14;

        e12 = te3;
        e13 = te7;
        e14 = te11;
    }

    public float det() {
        float vec0 =
                e5 * e10 * e15 -
                        e5 * e11 * e14 -
                        e9 * e6 * e15 +
                        e9 * e7 * e14 +
                        e13 * e6 * e11 -
                        e13 * e7 * e10;

        float vec1 =
                -e4 * e10 * e15 +
                        e4 * e11 * e14 +
                        e8 * e6 * e15 -
                        e8 * e7 * e14 -
                        e12 * e6 * e11 +
                        e12 * e7 * e10;

        float vec2 =
                e4 * e9 * e15 -
                        e4 * e11 * e13 -
                        e8 * e5 * e15 +
                        e8 * e7 * e13 +
                        e12 * e5 * e11 -
                        e12 * e7 * e9;

        float vec3 =
                -e4 * e9 * e14 +
                        e4 * e10 * e13 +
                        e8 * e5 * e14 -
                        e8 * e6 * e13 -
                        e12 * e5 * e10 +
                        e12 * e6 * e9;

        return (e0 * vec0 + e1 * vec1 + e2 * vec2 + e3 * vec3);
    }

    public void inverse() {
        float inv0=e5*e10*e15-
                e5*e11*e14-
                e9*e6*e15+
                e9*e7*e14+
                e13*e6*e11-
                e13*e7*e10;

        float inv4=-e4*e10*e15+
                e4*e11*e14+
                e8*e6*e15-
                e8*e7*e14-
                e12*e6*e11+
                e12*e7*e10;

        float inv8=e4*e9*e15-
                e4*e11*e13-
                e8*e5*e15+
                e8*e7*e13+
                e12*e5*e11-
                e12*e7*e9;

        float inv12=-e4*e9*e14+
                e4*e10*e13+
                e8*e5*e14-
                e8*e6*e13-
                e12*e5*e10+
                e12*e6*e9;

        float inv1=-e1*e10*e15+
                e1*e11*e14+
                e9*e2*e15-
                e9*e3*e14-
                e13*e2*e11+
                e13*e3*e10;

        float inv5=e0*e10*e15-
                e0*e11*e14-
                e8*e2*e15+
                e8*e3*e14+
                e12*e2*e11-
                e12*e3*e10;

        float inv9=-e0*e9*e15+
                e0*e11*e13+
                e8*e1*e15-
                e8*e3*e13-
                e12*e1*e11+
                e12*e3*e9;

        float inv13=e0*e9*e14-
                e0*e10*e13-
                e8*e1*e14+
                e8*e2*e13+
                e12*e1*e10-
                e12*e2*e9;

        float inv2=e1*e6*e15-
                e1*e7*e14-
                e5*e2*e15+
                e5*e3*e14+
                e13*e2*e7-
                e13*e3*e6;

        float inv6=-e0*e6*e15+
                e0*e7*e14+
                e4*e2*e15-
                e4*e3*e14-
                e12*e2*e7+
                e12*e3*e6;

        float inv10=e0*e5*e15-
                e0*e7*e13-
                e4*e1*e15+
                e4*e3*e13+
                e12*e1*e7-
                e12*e3*e5;

        float inv14=-e0*e5*e14+
                e0*e6*e13+
                e4*e1*e14-
                e4*e2*e13-
                e12*e1*e6+
                e12*e2*e5;

        float inv3=-e1*e6*e11+
                e1*e7*e10+
                e5*e2*e11-
                e5*e3*e10-
                e9*e2*e7+
                e9*e3*e6;

        float inv7=e0*e6*e11-
                e0*e7*e10-
                e4*e2*e11+
                e4*e3*e10+
                e8*e2*e7-
                e8*e3*e6;

        float inv11=-e0*e5*e11+
                e0*e7*e9+
                e4*e1*e11-
                e4*e3*e9-
                e8*e1*e7+
                e8*e3*e5;

        float inv15=e0*e5*e10-
                e0*e6*e9-
                e4*e1*e10+
                e4*e2*e9+
                e8*e1*e6-
                e8*e2*e5;

        float det=e0*inv0+e1*inv4+e2*inv8+e3*inv12;
        det=1.0f/det;

        e0=inv0*det;
        e1=inv1*det;
        e2=inv2*det;
        e3=inv3*det;
        e4=inv4*det;
        e5=inv5*det;
        e6=inv6*det;
        e7=inv7*det;
        e8=inv8*det;
        e9=inv9*det;
        e10=inv10*det;
        e11=inv11*det;
        e12=inv12*det;
        e13=inv13*det;
        e14=inv14*det;
        e15=inv15*det;
    }

    public void setScale(float sx, float sy, float sz) {
        loadIdentity();
        e0 = sx;
        e5 = sy;
        e10 = sz;
    }

    public void setScale(vector3f s) {
        loadIdentity();
        e0 = s.ex;
        e5 = s.ey;
        e10 = s.ez;
    }

    public void setScale(float[] s) {
        loadIdentity();
        e0 = s[0];
        e5 = s[1];
        e10 = s[2];
    }

    public void setScalePart(float sx, float sy, float sz) {
        e0 = sx;
        e5 = sy;
        e10 = sz;
    }

    public void setScalePart(vector3f s) {
        e0 = s.ex;
        e5 = s.ey;
        e10 = s.ez;
    }

    public void setScalePart(float[] s) {
        e0 = s[0];
        e5 = s[1];
        e10 = s[2];
    }

    public void setTranslation(float tx, float ty, float tz) {
        loadIdentity();
        e12 = tx;
        e13 = ty;
        e14 = tz;
    }

    public void setTranslation(vector3f t) {
        loadIdentity();
        e12 = t.ex;
        e13 = t.ey;
        e14 = t.ez;
    }

    public void setTranslation(float[] t) {
        loadIdentity();
        e12 = t[0];
        e13 = t[1];
        e14 = t[2];
    }

    public void setTranslationPart(float tx, float ty, float tz) {
        e12 = tx;
        e13 = ty;
        e14 = tz;
    }

    public void setTranslationPart(vector3f t) {
        e12 = t.ex;
        e13 = t.ey;
        e14 = t.ez;
    }

    public void setTranslationPart(float[] t) {
        e12 = t[0];
        e13 = t[1];
        e14 = t[2];
    }

    public void setAxisRotation(vector3f axis, float angle) {
        loadIdentity();
        setAxisRotationPart(axis, angle);
    }

    public void setAxisRotationPart(vector3f axis, float angle) {
        vector3f u = new vector3f(axis);
        u.normalize();

        float sinAngle = (float) Math.sin(angle);
        float cosAngle = (float) Math.cos(angle);
        float oneMinusCosAngle = 1.0f - cosAngle;

        e0 = u.ex * u.ex + cosAngle * (1 - u.ex * u.ex);
        e4 = oneMinusCosAngle * u.ex * u.ey - sinAngle * u.ez;
        e8 = oneMinusCosAngle * u.ex * u.ez + sinAngle * u.ey;

        e1 = oneMinusCosAngle * u.ex * u.ey + sinAngle * u.ez;
        e5 = u.ey * u.ey + cosAngle * (1 - u.ey * u.ey);
        e9 = oneMinusCosAngle * u.ey * u.ez - sinAngle * u.ex;

        e2 = oneMinusCosAngle * u.ex * u.ez - sinAngle * u.ey;
        e6 = oneMinusCosAngle * u.ey * u.ez + sinAngle * u.ex;
        e10 = u.ez * u.ez + cosAngle * (1 - u.ez * u.ez);
    }

    public void setRotationX(float angle) {
        loadIdentity();

        e5 = (float) Math.cos(angle);
        e6 = (float) Math.sin(angle);

        e9 = -e6;
        e10 = e5;
    }

    public void setRotationY(float angle) {
        loadIdentity();

        e0 = (float) Math.cos(angle);
        e2 = -(float) Math.sin(angle);

        e8 = -e2;
        e10 = e0;
    }

    public void setRotationZ(float angle) {
        loadIdentity();

        e0 = (float) Math.cos(angle);
        e1 = (float) Math.sin(angle);

        e4 = -e1;
        e5 = e0;
    }

    public void setEulerRotation(float xangle, float yangle, float zangle) {
        loadIdentity();
        setEulerRotationPart(xangle, yangle, zangle);
    }

    public void setEulerRotationPart(float xangle, float yangle, float zangle) {
        float cr = (float) Math.cos(xangle);
        float sr = (float) Math.sin(xangle);
        float cp = (float) Math.cos(yangle);
        float sp = (float) Math.sin(yangle);
        float cy = (float) Math.cos(zangle);
        float sy = (float) Math.sin(zangle);
        float srsp = sr * sp;
        float crsp = cr * sp;

        e0 = cp * cy;
        e1 = cp * sy;
        e2 = -sp;
        e4 = srsp * cy - cr * sy;
        e5 = srsp * sy + cr * cy;
        e6 = sr * cp;
        e8 = crsp * cy + sr * sy;
        e9 = crsp * sy - sr * cy;
        e10 = cr * cp;
    }

    public void setFrustumProjection(float left, float right, float bottom, float top, float znear, float zfar) {
        loadZero();
        e0 = 2.0f * znear / (right - left);
        e5 = 2.0f * znear / (top - bottom);
        e8 = (right + left) / (right - left);
        e9 = (top + bottom) / (top - bottom);
        e10 = (znear + zfar) / (znear - zfar);
        e11 = -1.0f;
        e14 = 2.0f * znear * zfar / (znear - zfar);
    }

    public void setPerspectiveProjectionV(float fovy, float aspect, float znear, float zfar) {
        float f = 1.0f / (float) Math.tan(fovy / 2.0f);
        loadZero();
        e0 = f / aspect;
        e5 = f;
        e10 = (znear + zfar) / (znear - zfar);
        e11 = -1.0f;
        e14 = 2.0f * znear * zfar / (znear - zfar);
    }

    public void setPerspectiveProjectionH(float fovx, float aspect, float znear, float zfar) {
        float f = 1.0f / (float) Math.tan(fovx);
        loadZero();
        e0 = f;
        e5 = f * aspect;
        e10 = (znear + zfar) / (znear - zfar);
        e11 = -1.0f;
        e14 = 2.0f * znear * zfar / (znear - zfar);
    }

    public void setOrthoProjection(float left, float right, float bottom, float top, float znear, float zfar) {
        loadZero();
        e0 = 2.0f / (right - left);
        e5 = 2.0f / (top - bottom);
        e10 = 2.0f / (znear - zfar);
        e12 = (left + right) / (left - right);
        e13 = (bottom + top) / (bottom - top);
        e14 = (znear + zfar) / (znear - zfar);
        e15 = 1.0f;
    }

    public void setLookAt(vector3f eye, vector3f target, vector3f upVector) {
        vector3f dir = vector3f.sub(target, eye);
        dir.normalize();

        vector3f right = vector3f.crossProduct(dir, upVector);
        right.normalize();

        vector3f up = vector3f.crossProduct(right, dir);
        up.normalize();

        matrix4f rot = new matrix4f(
                right.ex, up.ex, -dir.ex, 0.0f,
                right.ey, up.ey, -dir.ey, 0.0f,
                right.ez, up.ez, -dir.ez, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f);

        matrix4f trans = new matrix4f();
        vector3f trVector = new vector3f(eye);
        trVector.negate();
        trans.setTranslation(trVector);

        matrix4f v=matrix4f.mul(rot, trans);
        this.e0=v.e0;
        this.e1=v.e1;
        this.e2=v.e2;
        this.e3=v.e3;
        this.e4=v.e4;
        this.e5=v.e5;
        this.e6=v.e6;
        this.e7=v.e7;
        this.e8=v.e8;
        this.e9=v.e9;
        this.e10=v.e10;
        this.e11=v.e11;
        this.e12=v.e12;
        this.e13=v.e13;
        this.e14=v.e14;
        this.e15=v.e15;
    }


    public float e0, e4, e8, e12,
            e1, e5, e9, e13,
            e2, e6, e10, e14,
            e3, e7, e11, e15;
}