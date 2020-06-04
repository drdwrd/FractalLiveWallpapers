package drwdrd.adev.engine;

import android.util.Log;


public class LogSystem {

    private LogSystem() {

    }

    public static void debug(String tag, String msg) {
        Log.d(tag, msg);
    }

    public static void info(String tag, String msg) {
        Log.i(tag, msg);
    }

    public static void warning(String tag, String msg) {
        Log.w(tag, msg);
    }

    public static void error(String tag, String msg) {
        Log.e(tag, msg);
    }
}