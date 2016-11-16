package com.rancard.rndvusdk.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.util.HashMap;

/**
 * Created by: Robert Wilson.
 * Date: Feb 26, 2016
 * Time: 2:58 AM
 * Package: com.rancard.rndvusdk.utils
 * Project: Rendezvous-Android
 */
public class RendezvousDeviceManager
{
    public static HashMap<String, String> getDeviceInfo(Context context)
    {
        HashMap<String, String> deviceInfo = new HashMap<>();
        deviceInfo.put(Constants.DEVICE_MANUFACTURER, Build.MANUFACTURER);
        deviceInfo.put(Constants.DEVICE_MODEL, Build.MODEL);
        deviceInfo.put(Constants.OS_VERSION, Build.VERSION.RELEASE);
        deviceInfo.put(Constants.DEVISE_SERIAL_NUMBER, Build.SERIAL);
        if ( context != null ) {
            deviceInfo.put(Constants.DEVISE_UNIQUE_ID, Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if ( manager != null ) {
                deviceInfo.put(Constants.DEVISE_NETWORK_OPERATOR, manager.getNetworkOperatorName());
                deviceInfo.put(Constants.DEVISE_NETWORK_OPERATOR_CODE, manager.getNetworkOperator());

                boolean xlarge = (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4;
                boolean large = (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;

                deviceInfo.put(Constants.DEVISE_TYPE, (xlarge || large ? "Tablet" : "Phone"));
            }
        }
        return deviceInfo;
    }


    public static boolean isInternetConnectionAvailable(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting());
    }

    public boolean isOnWifi(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static boolean isOnMobileData(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_MOBILE;
    }

}
