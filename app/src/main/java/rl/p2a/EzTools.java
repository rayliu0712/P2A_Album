package rl.p2a;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

public class EzTools {
    public static void log(Object obj) {
        Log.d("nacho", String.valueOf(obj));
    }

    public static void toast(Context context, Object obj) {
        Toast.makeText(context, String.valueOf(obj), Toast.LENGTH_SHORT).show();
    }

    public static void dialog(Context context, String title, String msg, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context).setCancelable(false)
                .setTitle(title).setMessage(msg).setPositiveButton("OK", listener)
                .create().show();
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception ignored) {
        }
    }
}