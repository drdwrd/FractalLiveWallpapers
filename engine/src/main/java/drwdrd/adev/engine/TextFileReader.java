package drwdrd.adev.engine;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class TextFileReader {
    public static String readFromAssets(Context context, String fileName) {
        InputStream stream = null;
        try {
            stream = context.getAssets().open(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            StringBuilder text = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            return text.toString();
        } catch (IOException io) {
            io.printStackTrace();
            return null;
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }

            } catch (IOException io) {
                io.printStackTrace();
            }
        }
    }
}