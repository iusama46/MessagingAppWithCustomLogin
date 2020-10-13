package co.usam.donateblood;

/**
 * Created by Ussama Iftikhar on 13-Oct-2020.
 * Email iusama46@gmail.com
 * Email iusama466@gmail.com
 * Github https://github.com/iusama46
 */
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.io.File;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class Utils {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getRootDirPath(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File file = ContextCompat.getExternalFilesDirs(context.getApplicationContext(),
                    null)[0];
            return file.getAbsolutePath();
        } else {
            return context.getApplicationContext().getDataDir().getAbsolutePath();
        }
    }
}
