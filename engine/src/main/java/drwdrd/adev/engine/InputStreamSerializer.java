package drwdrd.adev.engine;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;


public class InputStreamSerializer extends InputStream {

    public InputStreamSerializer(int bufferSize) {
        dataBuffer = new byte[bufferSize];
    }

    public InputStreamSerializer(InputStream in, ByteOrder order, int bufferSize) {
        this.inputStream = in;
        dataBuffer = new byte[bufferSize];
        serializer = new DataSerializer(order);
    }

    public void open(InputStream in, ByteOrder order) {
        this.inputStream = in;
        serializer = new DataSerializer(order);
    }

    public int available() throws IOException {
        return inputStream.available();
    }

    public final short readShort() throws IOException {
        if (checkGet(2) == 2) {
            short c = serializer.readShort(dataBuffer, dataMarker);
            dataMarker += 2;
            return c;
        }
        throw new IOException("Cannot read data.");
    }

    public final int readUnsignedShort() throws IOException {
        if (checkGet(2) == 2) {
            int c = serializer.readUnsignedShort(dataBuffer, dataMarker);
            dataMarker += 2;
            return c;
        }
        throw new IOException("Cannot read data.");
    }

    public final char readChar() throws IOException {
        if (checkGet(2) == 2) {
            char c = serializer.readChar(dataBuffer, dataMarker);
            dataMarker += 2;
            return c;
        }
        throw new IOException("Cannot read data.");
    }

    public final int readInt() throws IOException {
        if (checkGet(4) == 4) {
            int c = serializer.readInt(dataBuffer, dataMarker);
            dataMarker += 4;
            return c;
        }
        throw new IOException("Cannot read data.");
    }

    public final long readLong() throws IOException {
        if (checkGet(8) == 8) {
            long c = serializer.readLong(dataBuffer, dataMarker);
            dataMarker += 8;
            return c;
        }
        throw new IOException("Cannot read data.");
    }

    public final float readFloat() throws IOException {
        if (checkGet(4) == 4) {
            float c = serializer.readFloat(dataBuffer, dataMarker);
            dataMarker += 4;
            return c;
        }
        throw new IOException("Cannot read data.");
    }

    public final double readDouble() throws IOException {
        if (checkGet(8) == 8) {
            double c = serializer.readDouble(dataBuffer, dataMarker);
            dataMarker += 8;
            return c;
        }
        throw new IOException("Cannot read data.");
    }

    public final int readChars(byte b[], int off, int elems) throws IOException {
        int nbytes;
        if ((nbytes = checkGet(2 * elems)) > 0) {
            System.arraycopy(dataBuffer, dataMarker, b, off, nbytes);
            serializer.swapw(b, off, elems);
            dataMarker += nbytes;
        }
        return nbytes;
    }

    public final int readInts(byte b[], int off, int elems) throws IOException {
        int nbytes;
        if ((nbytes = checkGet(4 * elems)) > 0) {
            System.arraycopy(dataBuffer, dataMarker, b, off, nbytes);
            serializer.swapdw(b, off, elems);
            dataMarker += nbytes;
        }
        return nbytes;
    }

    public final int readLongs(byte b[], int off, int elems) throws IOException {
        int nbytes;
        if ((nbytes = checkGet(8 * elems)) > 0) {
            System.arraycopy(dataBuffer, dataMarker, b, off, nbytes);
            serializer.swapqw(b, off, elems);
            dataMarker += nbytes;
        }
        return nbytes;
    }

    public final int readFloats(byte b[], int off, int elems) throws IOException {
        int nbytes;
        if ((nbytes = checkGet(4 * elems)) > 0) {
            System.arraycopy(dataBuffer, dataMarker, b, off, nbytes);
            serializer.swapdw(b, off, elems);
            dataMarker += nbytes;
        }
        return nbytes;
    }

    public final int readDoubles(byte b[], int off, int elems) throws IOException {
        int nbytes;
        if ((nbytes = checkGet(8 * elems)) > 0) {
            System.arraycopy(dataBuffer, dataMarker, b, off, nbytes);
            serializer.swapqw(b, off, elems);
            dataMarker += nbytes;
        }
        return nbytes;
    }

    public final int readUnsignedShorts(byte b[], int off, int elems) throws IOException {
        int nbytes;
        if ((nbytes = checkGet(2 * elems)) > 0) {
            System.arraycopy(dataBuffer, dataMarker, b, off, nbytes);
            serializer.swapw(b, off, elems);
            dataMarker += nbytes;
        }
        return nbytes;
    }

    public final int readShorts(byte b[], int off, int elems) throws IOException {
        int nbytes;
        if ((nbytes = checkGet(2 * elems)) > 0) {
            System.arraycopy(dataBuffer, dataMarker, b, off, nbytes);
            serializer.swapw(b, off, elems);
            dataMarker += nbytes;
        }
        return nbytes;
    }

    public final int read(byte b[], int off, int len) throws IOException {
        int nbytes;
        if ((nbytes = checkGet(len)) > 0) {
            System.arraycopy(dataBuffer, dataMarker, b, off, nbytes);
            dataMarker += nbytes;
        }
        return nbytes;
    }

    public int read() throws IOException {
        if (checkGet(1) == 1) {
            int c = (int) (dataBuffer[dataMarker] & 0xff);
            dataMarker += 1;
            return c;
        }
        throw new IOException("Cannot read data.");
    }

    public final int readUnsignedByte() throws IOException {
        if (checkGet(1) == 1) {
            int c = (int) (dataBuffer[dataMarker] & 0xff);
            dataMarker += 1;
            return c;
        }
        throw new IOException("Cannot read data.");
    }

    public final void close() throws IOException {
        if (inputStream != null) {
            inputStream.close();
        }
    }

    public final long skip(long byteCount) throws IOException {
        dataMarker = 0;
        dataLength = 0;
        return super.skip(byteCount);
    }

    private int checkGet(int size) throws IOException {
        if (size > dataBuffer.length)                            //if size is bigger than buffer, realloc buffer
        {
            byte[] newDataBuffer = null;
            newDataBuffer = new byte[size];
            System.arraycopy(dataBuffer, 0, newDataBuffer, 0, dataBuffer.length);
            dataBuffer = newDataBuffer;
            newDataBuffer = null;
        }
        if (dataMarker == dataLength) {
            dataMarker = 0;
            dataLength = 0;
        }
        //check buffer
        if ((dataMarker + size) > dataLength) {
            if (dataMarker < dataLength) {
                dataLength -= dataMarker;
                System.arraycopy(dataBuffer, dataMarker, dataBuffer, 0, dataLength);
                dataMarker = 0;
            }
            //fill buffer
            int bytesRead;
            if ((bytesRead = inputStream.read(dataBuffer, dataLength, size)) == -1) {
                throw new IOException("Cannot read data.");
            }
            dataLength += bytesRead;
        }
        if (dataLength < size) {
            return dataLength;
        }
        return size;
    }

    private InputStream inputStream = null;
    private DataSerializer serializer = null;
    private byte[] dataBuffer = null;
    private int dataLength = 0;
    private int dataMarker = 0;
}