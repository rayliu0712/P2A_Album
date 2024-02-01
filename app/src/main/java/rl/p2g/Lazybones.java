package rl.p2g;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

public class Lazybones {
    public static void ll(Object obj) {
        Log.d("nachoneko", String.valueOf(obj));
    }

    public static void tt(Context context, Object obj) {
        Toast.makeText(context, String.valueOf(obj), Toast.LENGTH_SHORT).show();
    }

    public static void dd(Context context, String title, String msg, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context).setCancelable(false)
                .setTitle(title).setMessage(msg).setPositiveButton("OK", listener)
                .create().show();
    }
}