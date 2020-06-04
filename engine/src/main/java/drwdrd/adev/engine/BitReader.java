package drwdrd.adev.engine;


public interface BitReader {

    public short readShort(byte[] data, int offset);

    public int readUnsignedShort(byte[] data, int offset);

    public char readChar(byte[] data, int offset);

    public int readInt(byte[] data, int offset);

    public long readLong(byte[] data, int offset);

    public float readFloat(byte[] data, int offset);

    public double readDouble(byte[] data, int offset);

    public void swapw(byte[] data, int off, int elems);

    public void swapdw(byte[] data, int off, int elems);

    public void swapqw(byte[] data, int off, int elems);
}
