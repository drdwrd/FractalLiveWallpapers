package drwdrd.adev.engine;


public class matrix2f {
    public matrix2f() {

    }

    public matrix2f(float e0, float e1, float e2, float e3) {
        this.e0 = e0;
        this.e2 = e2;

        this.e1 = e1;
        this.e3 = e3;
    }

    public matrix2f(matrix2f m) {
        e0 = m.e0;
        e2 = m.e2;

        e1 = m.e1;
        e3 = m.e3;
    }

    public matrix2f(float[] e) {
        e0 = e[0];
        e2 = e[2];

        e1 = e[1];
        e3 = e[3];
    }

    public void loadIdentity() {
        e0 = 1.0f;
        e2 = 0.0f;

        e1 = 0.0f;
        e3 = 1.0f;
    }

    public void loadZero() {
        e0 = 0.0f;
        e2 = 0.0f;

        e1 = 0.0f;
        e3 = 0.0f;
    }

    public boolean equals(matrix2f m) {
        return ((m != null) &&
                (e0 == m.e0) && (e2 == m.e2) &&
                (e1 == m.e1) && (e3 == m.e3));
    }

    public int hashCode() {
        return (Float.floatToIntBits(e0) ^ Float.floatToIntBits(e2) ^
                Float.floatToIntBits(e1) ^ Float.floatToIntBits(e3));
    }

    public void get(float[] m) {
        m[0] = e0;
        m[2] = e2;

        m[1] = e1;
        m[3] = e3;
    }

    public void add(matrix2f m) {
        e0 += m.e0;
        e2 += m.e2;

        e1 += m.e1;
        e3 += m.e3;
    }

    static public matrix2f add(matrix2f m1, matrix2f m2) {
        float e0 = m1.e0 + m2.e0;
        float e2 = m1.e2 + m2.e2;

        float e1 = m1.e1 + m2.e1;
        float e3 = m1.e3 + m2.e3;

        return new matrix2f(e0,e1,e2,e3);
    }

    public void sub(matrix2f m) {
        e0 -= m.e0;
        e2 -= m.e2;

        e1 -= m.e1;
        e3 -= m.e3;
    }

    static public matrix2f sub(matrix2f m1, matrix2f m2) {
        float e0 = m1.e0 - m2.e0;
        float e2 = m1.e2 - m2.e2;

        float e1 = m1.e1 - m2.e1;
        float e3 = m1.e3 - m2.e3;

        return new matrix2f(e0,e1,e2,e3);
    }

    public void mul(matrix2f m) {
        e0 = e0 * m.e0 + e2 * m.e1;
        e2 = e0 * m.e2 + e2 * m.e3;

        e1 = e1 * m.e0 + e3 * m.e1;
        e3 = e1 * m.e2 + e3 * m.e3;
    }

    static public matrix2f mul(matrix2f m1, matrix2f m2) {
        float e0 = m1.e0 * m2.e0 + m1.e2 * m2.e1;
        float e2 = m1.e0 * m2.e2 + m1.e2 * m2.e3;

        float e1 = m1.e1 * m2.e0 + m1.e3 * m2.e1;
        float e3 = m1.e1 * m2.e2 + m1.e3 * m2.e3;

        return new matrix2f(e0,e1,e2,e3);
    }

    public void mul(float s) {
        e0 *= s;
        e2 *= s;

        e1 *= s;
        e3 *= s;
    }

    static public matrix2f mul(float s, matrix2f m) {
        float e0 = s * m.e0;
        float e2 = s * m.e2;

        float e1 = s * m.e1;
        float e3 = s * m.e3;

        return new matrix2f(e0,e1,e2,e3);
    }

    public void negate() {
        e0 = -e0;
        e2 = -e2;

        e1 = -e1;
        e3 = -e3;
    }

    static public matrix2f negate(matrix2f m) {
        float e0 = -m.e0;
        float e2 = -m.e2;

        float e1 = -m.e1;
        float e3 = -m.e3;

        return new matrix2f(e0,e1,e2,e3);
    }

    public void transpose() {
        float tmp = e1;
        e1 = e2;
        e2 = tmp;
    }

    public float det() {
        return e0 * e3 - e1 * e2;
    }

    public void inverse() {
        float idet = 1.0f / det();
        float tmp = e0;

        e0 = idet * e3;
        e2 = -idet * e2;

        e1 = -idet * e1;
        e3 = idet * tmp;
    }

    public float e0, e2,
            e1, e3;
}
