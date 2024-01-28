package rl.p2g;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

public class Lazy {
    private final Context context;
    public Lazy(Context context) {
        this.context = context;
    }

    public void ll(String msg) {
        Log.d("tag", msg);
    }

    public void tt(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public void dd(String title, String msg) {
        new AlertDialog.Builder(context).setCancelable(false)
                .setTitle(title).setMessage(msg).setPositiveButton("OK", null)
                .create().show();
    }

    public void dd(String title, String msg, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context).setCancelable(false)
                .setTitle(title).setMessage(msg).setPositiveButton("OK", listener)
                .create().show();
    }
}