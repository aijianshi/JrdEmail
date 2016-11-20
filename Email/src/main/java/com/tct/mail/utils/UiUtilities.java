package com.tct.mail.utils;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.tct.mail.ui.ConnectionAlertDialog;
import com.tct.mail.utils.LogUtils;

public class UiUtilities {
    /// TCT: Tag for connection alert dialog @{
    public static final String TAG_CONNECTION_ALERT_DIALOG = "connection-alert-dialog";
    /// @}

    /**
     * TCT: TCT if is Wifi Only
     * @param context Context
     * @return boolean
     */
    public static boolean isWifiOnly(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(null != networkInfo && networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
            return true;
        }
        return false;
    }

    /**
     * TCT: Display a connection alert dialog to prompt that no network available
     */
    public static void showConnectionAlertDialog(FragmentManager fragMagr) {
        if (fragMagr == null) {
            LogUtils.e(LogUtils.TAG, "Fragment manager is null");
            return;
        }
        final FragmentTransaction ft = fragMagr.beginTransaction();
        final Fragment prev = fragMagr.findFragmentByTag(TAG_CONNECTION_ALERT_DIALOG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        final DialogFragment newFragment = ConnectionAlertDialog.newInstance();
        ft.add(newFragment, TAG_CONNECTION_ALERT_DIALOG).commitAllowingStateLoss();
    }
}