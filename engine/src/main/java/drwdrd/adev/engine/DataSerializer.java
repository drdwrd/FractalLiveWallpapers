package drwdrd.adev.engine;

import java.nio.ByteOrder;


public class DataSerializer implements BitReader {

    public DataSerializer(ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
        if (ByteOrder.nativeOrder() == byteOrder) {
            reader = new BitReaderNoSwap();
        } else {
            reader = new BitReaderSwap();
        }
    }

    public ByteOrder getByteOrder() {
        return byteOrder;
    }

    public short readShort(byte[] data, int offset) {
        return reader.readShort(data, offset);
    }

    public int readUnsignedShort(byte[] data, int offset) {
        return reader.readUnsignedShort(data, offset);
    }

    public char readChar(byte[] data, int offset) {
        return reader.readChar(data, offset);
    }

    public int readInt(byte[] data, int offset) {
        return reader.readInt(data, offset);
    }

    public long readLong(byte[] data, int offset) {
        return reader.readLong(data, offset);
    }

    public float readFloat(byte[] data, int offset) {
        return reader.readFloat(data, offset);
    }

    public double readDouble(byte[] data, int offset) {
        return reader.readDouble(data, offset);
    }

    public void swapw(byte[] data, int off, int elems) {
        reader.swapw(data, off, elems);
    }

    public void swapdw(byte[] data, int off, int elems) {
        reader.swapdw(data, off, elems);
    }

    public void swapqw(byte[] data, int off, int elems) {
        reader.swapqw(data, off, elems);
    }


    private class BitReaderSwap implements BitReader {

        public short readShort(byte[] data, int offset) {
            return (short) (
                    (data[offset + 0] & 0xff) << 8 |
                            (data[offset + 1] & 0xff));
        }

        public int readUnsignedShort(byte[] data, int offset) {
            return (int) (
                    (data[offset + 0] & 0xff) << 8 |
                            (data[offset + 1] & 0xff));
        }

        public char readChar(byte[] data, int offset) {
            return (char) (
                    (data[offset + 0] & 0xff) << 8 |
                            (data[offset + 1] & 0xff));
        }

        public int readInt(byte[] data, int offset) {
            return (
                    (data[offset + 0]) << 24 |
                            (data[offset + 1] & 0xff) << 16 |
                            (data[offset + 2] & 0xff) << 8 |
                            (data[offset + 3] & 0xff));
        }

        public long readLong(byte[] data, int offset) {
            return (
                    (long) (data[offset + 0]) << 56 |
                            (long) (data[offset + 1] & 0xff) << 48 |
                            (long) (data[offset + 2] & 0xff) << 40 |
                            (long) (data[offset + 3] & 0xff) << 32 |
                            (long) (data[offset + 4] & 0xff) << 24 |
                            (long) (data[offset + 5] & 0xff) << 16 |
                            (long) (data[offset + 6] & 0xff) << 8 |
                            (long) (data[offset + 7] & 0xff));
        }

        public float readFloat(byte[] data, int offset) {
            return Float.intBitsToFloat(readInt(data, offset));
        }

        public double readDouble(byte[] data, int offset) {
            return Double.longBitsToDouble(readLong(data, offset));
        }

        public void swapw(byte[] data, int off, int elems) {
            for (int i = 0; i < elems; i++) {
                byte b1 = data[off + 2 * i];
                byte b2 = data[off + 2 * i + 1];
                data[off + 2 * i] = b2;
                data[off + 2 * i + 1] = b1;
            }
        }

        public void swapdw(byte[] data, int off, int elems) {
            for (int i = 0; i < elems; i++) {
                byte b1 = data[off + 4 * i];
                byte b2 = data[off + 4 * i + 1];
                byte b3 = data[off + 4 * i + 2];
                byte b4 = data[off + 4 * i + 3];
                data[off + 4 * i] = b4;
                data[off + 4 * i + 1] = b3;
                data[off + 4 * i + 2] = b2;
                data[off + 4 * i + 3] = b1;
            }
        }

        public void swapqw(byte[] data, int off, int elems) {
            for (int i = 0; i < elems; i++) {
                byte b1 = data[off + 8 * i];
                byte b2 = data[off + 8 * i + 1];
                byte b3 = data[off + 8 * i + 2];
                byte b4 = data[off + 8 * i + 3];
                byte b5 = data[off + 8 * i + 4];
                byte b6 = data[off + 8 * i + 5];
                byte b7 = data[off + 8 * i + 6];
                byte b8 = data[off + 8 * i + 7];
                data[off + 8 * i] = b8;
                data[off + 8 * i + 1] = b7;
                data[off + 8 * i + 2] = b6;
                data[off + 8 * i + 3] = b5;
                data[off + 8 * i + 4] = b4;
                data[off + 8 * i + 5] = b3;
                data[off + 8 * i + 6] = b2;
                data[off + 8 * i + 7] = b1;
            }
        }

    }

    private class BitReaderNoSwap implements BitReader {

        public short readShort(byte[] data, int offset) {
            return (short) (
                    (data[offset + 1] & 0xff) << 8 |
                            (data[offset + 0] & 0xff));
        }

        public int readUnsignedShort(byte[] data, int offset) {
            return (int) (
                    (data[offset + 1] & 0xff) << 8 |
                            (data[offset + 0] & 0xff));
        }

        public char readChar(byte[] data, int offset) {
            return (char) (
                    (data[offset + 1] & 0xff) << 8 |
                            (data[offset + 0] & 0xff));
        }

        public int readInt(byte[] data, int offset) {
            return (
                    (data[offset + 3]) << 24 |
                            (data[offset + 2] & 0xff) << 16 |
                            (data[offset + 1] & 0xff) << 8 |
                            (data[offset + 0] & 0xff));
        }

        public long readLong(byte[] data, int offset) {
            return (
                    (long) (data[offset + 7]) << 56 |
                            (long) (data[offset + 6] & 0xff) << 48 |
                            (long) (data[offset + 5] & 0xff) << 40 |
                            (long) (data[offset + 4] & 0xff) << 32 |
                            (long) (data[offset + 3] & 0xff) << 24 |
                            (long) (data[offset + 2] & 0xff) << 16 |
                            (long) (data[offset + 1] & 0xff) << 8 |
                            (long) (data[offset + 0] & 0xff));
        }

        public float readFloat(byte[] data, int offset) {
            return Float.intBitsToFloat(readInt(data, offset));
        }

        public double readDouble(byte[] data, int offset) {
            return Double.longBitsToDouble(readLong(data, offset));
        }

        public void swapw(byte[] data, int off, int elems) {

        }

        public void swapdw(byte[] data, int off, int elems) {

        }

        public void swapqw(byte[] data, int off, int elems) {

        }
    }

    private BitReader reader;
    private ByteOrder byteOrder;
}