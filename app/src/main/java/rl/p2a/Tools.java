package rl.p2a;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Tools {
    private static Context context = null;
    public static void setContext(Context context) {
        Tools.context = context;
    }

    public static void log(Object obj) {
        Log.d("nacho", String.valueOf(obj));
    }

    public static void toast(Object obj) {
        Toast.makeText(context, String.valueOf(obj), Toast.LENGTH_SHORT).show();
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception ignored) {
        }
    }

    public static String toString(Object obj) {
        return String.valueOf(obj);
    }

    public static void tryCatch(Runnable rTry) {
        try {
            rTry.run();
        } catch (Exception ignored) {
        }
    }
}