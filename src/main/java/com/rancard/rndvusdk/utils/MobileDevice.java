package com.rancard.rndvusdk.utils;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.rancard.rndvusdk.R;
import com.rancard.rndvusdk.interfaces.ContactsLoaderListener;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by: Robert Wilson.
 * Date: Mar 01, 2016
 * Time: 4:14 PM
 * Package: com.multimedia.joyonline.utils
 * Project: JoyOnline-Android
 */


public class MobileDevice {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /*
    Determines if device is connected to the internet.
    Internet connection could be through any interface; wifi, mobile network, bluetooth, etc
    NOTE: Add <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission> to AndroidManifest.xml
     */
    public static boolean isInternetConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting());
    }

    /*
    Determines if device is on wifi network.
    Use wifi connections for data intensive operations and mobile connections for less data intensive onces to save cost.
     */
    public static boolean isOnWifiNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
    }

    /*
    Determines if device is on mobile network.
    NOTE : Performing data intensive network operations on a mobile network connections are relatively expensive
     */
    public static boolean isOnMobileNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_MOBILE;
    }

    /*
    Determines if device has fron-facing camera
     */
    public static boolean hasFrontFacingCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
    }

    /*
    Determins if device has camera. It could be the back or front-facing camera or any other
     */
    public static boolean hasCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }


    public static String getStringFromResource(Context context, int resId, Object... formatArgs) {
        return context.getResources().getString(resId, formatArgs);
    }

    /*
    Determines if device has a camera flash light
     */
    public static boolean hasCameraFlashLight(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }



    /*
    Determines if device has bluetooth
     */
    public static boolean hasBluetooth(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
    }

    /*
    Determines if device is capable of handling any implicit intent with the action given.
    Call this to ensure that your app doesn't crush in the event where there's no application installed
    to satisfy the action you want to perform
     */
    public static boolean canHandleIntent(Context context, String action) {
        return context.getPackageManager().queryIntentActivities(new Intent(action), PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }


    public static String getPrimaryEmailAddress(Context context) {
        String primaryEmailAddress = "";
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(context).getAccounts();
        if (accounts != null) {
            for (Account account : accounts) {
                if (emailPattern.matcher(account.name).matches()) {
                    primaryEmailAddress = account.name;
                }
            }
        }

        return primaryEmailAddress;
    }

    /*
    Utilities for performing very basic but common actions
     */
    public static class PerformCommonActions {

        public interface YesNoDialogListener {
            public void onYesClicked(DialogInterface dialog, int which);

            public void onNoClicked(DialogInterface dialog, int which);
        }

        /*
        Sends out SMS
         */
        public static void sendSMS(String msisdn, String message) throws SecurityException {
            SmsManager.getDefault().sendTextMessage(msisdn, null, message, null, null);
        }

//        public static void sendTollFreeSMS(Context context, String message) throws SecurityException {
//            NetworkOperator networkOperator = MobileDevice.PerformCommonActions.getNetworkOperator(context);
//            if (networkOperator != null && networkOperator != NetworkOperator.UNKNOWN_NWTWORK) {
//                MobileDevice.PerformCommonActions.sendSMS(networkOperator.getTollFreeCode(), message);
//            }else{
//
//            }
//        }


        public static void showToastMessage(Context context, String message, boolean shouldLastLonger) {
            Toast t = null;
            if (shouldLastLonger) {
                t = Toast.makeText(context, message, Toast.LENGTH_LONG);
            } else {
                t = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            }
            t.show();
        }

        public static void showMessageDialog(Context context, String title, String message) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        }


        public static void showMessageDialogWithListener(Context context, String title, String message, final YesNoDialogListener listener) {
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle(title)
                        .setMessage(message)
                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listener.onYesClicked(dialog, which);
                            }
                        });
                builder.create().show();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public static void showYesNoDialog(Context context, String title, String message, final YesNoDialogListener listener) {
            try{
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle)
                        .setTitle(title)
                        .setMessage(message)
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listener.onNoClicked(dialog, which);
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listener.onYesClicked(dialog, which);
                            }
                        });
                builder.setCancelable(false);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                Button bq = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                bq.setTextColor(Color.GRAY);
            }catch (Exception e){
                e.printStackTrace();
            }
            //builder.setCanceledOnTouchOutside(false);
        }

        public static void showCustomDialog(Context context, String cancel, String ok, String title, String message, final YesNoDialogListener listener) {
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle(title)
                        .setMessage(message)
                        .setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listener.onNoClicked(dialog, which);
                            }
                        })
                        .setPositiveButton(ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listener.onYesClicked(dialog, which);
                            }
                        });
                builder.setCancelable(false);
                builder.create().show();
            }catch (Exception e){
            e.printStackTrace();
        }

            //builder.setCanceledOnTouchOutside(false);
        }

        public static void showNonRecoverableErrorDialog(final Activity context, String message) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setTitle("Fatal Error")
                    .setMessage(message)
                    .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            context.finish();
                        }
                    });
            builder.create().show();
        }

        public static Bitmap getBitmapFromURL(String imageUrl) {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        public static String getDevicePhoneNumber(Context context) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return telephonyManager.getLine1Number();
        }

        public static String getDeviceSerialNumber(Context context) {



            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    return telephonyManager.getDeviceId();
                }else {
                    return "0000000";
                }

            }else{
                //... Permission has already been granted, obtain the UUID
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                return telephonyManager.getDeviceId();
            }

        }

        public static String getDeviceSoftwareVersion(Context context) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return telephonyManager.getDeviceSoftwareVersion();
        }

        public static String getNetworkOperatorName(Context context) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return telephonyManager.getNetworkOperatorName();
        }

        public static String getNetworkOperatorCode(Context context) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return telephonyManager.getNetworkOperator();
        }


        public static String getDeviceType(Context context){
            return Build.BRAND+ " "+Build.MODEL;


        }

        public static String getOSVersion(Context context){
            return Build.VERSION.RELEASE;
        }

        public static String getOSType(Context context){

            Field[] fields = Build.VERSION_CODES.class.getFields();
            String fieldName = "";
            for (Field field : fields) {
                try{
                    fieldName = field.getName();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return fieldName;
        }

        public static String getUserAgent(){
            String userAgent = "";
            try{
                userAgent = System.getProperty("http.agent");
            }catch (Exception e){
                e.printStackTrace();
            }
            return userAgent;
        }

//        public static String getLocation(Context context) {
//            String country = "";
//            try {
//                List<Address> myList = new ArrayList<>();
//
//                Geocoder myLocation = new Geocoder(context);
//                try {
//                    myList = myLocation.getFromLocation(getGPS(context)[0], getGPS(context)[1], 1);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    country = "";
//                }
//                if (myList != null) {
//                    try {
//                        country = myList.get(0).getCountryName();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        country = "";
//                    }
//                }
//            }catch (Exception e){
//                country = "";
//            }
//            return country;
//        }




        public static String bytesToHex(byte[] bytes) {
            StringBuilder sbuf = new StringBuilder();
            for(int idx=0; idx < bytes.length; idx++) {
                int intVal = bytes[idx] & 0xff;
                if (intVal < 0x10) sbuf.append("0");
                sbuf.append(Integer.toHexString(intVal).toUpperCase());
            }
            return sbuf.toString();
        }

        /**
         * Get utf8 byte array.
         * @param str
         * @return  array of NULL if error was found
         */
        public static byte[] getUTF8Bytes(String str) {
            try { return str.getBytes("UTF-8"); } catch (Exception ex) { return null; }
        }

        /**
         * Load UTF8withBOM or any ansi text file.
         * @param filename
         * @return
         * @throws IOException
         */
        public static String loadFileAsString(String filename) throws IOException {
            final int BUFLEN=1024;
            BufferedInputStream is = new BufferedInputStream(new FileInputStream(filename), BUFLEN);
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFLEN);
                byte[] bytes = new byte[BUFLEN];
                boolean isUTF8=false;
                int read,count=0;
                while((read=is.read(bytes)) != -1) {
                    if (count==0 && bytes[0]==(byte)0xEF && bytes[1]==(byte)0xBB && bytes[2]==(byte)0xBF ) {
                        isUTF8=true;
                        baos.write(bytes, 3, read-3); // drop UTF8 bom marker
                    } else {
                        baos.write(bytes, 0, read);
                    }
                    count+=read;
                }
                return isUTF8 ? new String(baos.toByteArray(), "UTF-8") : new String(baos.toByteArray());
            } finally {
                try{ is.close(); } catch(Exception ex){}
            }
        }

        /**
         * Returns MAC address of the given interface name.
         * @param interfaceName eth0, wlan0 or NULL=use first interface
         * @return  mac address or empty string
         */
        public static String getMACAddress(String interfaceName) {
            try {
                List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
                for (NetworkInterface intf : interfaces) {
                    if (interfaceName != null) {
                        if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                    }
                    byte[] mac = intf.getHardwareAddress();
                    if (mac==null) return "";
                    StringBuilder buf = new StringBuilder();
                    for (int idx=0; idx<mac.length; idx++)
                        buf.append(String.format("%02X:", mac[idx]));
                    if (buf.length()>0) buf.deleteCharAt(buf.length()-1);
                    return buf.toString();
                }
            } catch (Exception ex) { } // for now eat exceptions
            return "";
        /*try {
            // this is so Linux hack
            return loadFileAsString("/sys/class/net/" +interfaceName + "/address").toUpperCase().trim();
        } catch (IOException ex) {
            return null;
        }*/
        }

        /**
         * Get IP address from first non-localhost interface
         */
        public static String getIPAddress(boolean useIPv4) {
            try {
                List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
                for (NetworkInterface intf : interfaces) {
                    List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                    System.out.println("Install addresses " + addrs.toString());
                    for (InetAddress addr : addrs) {
                        if (!addr.isLoopbackAddress()) {
                            String sAddr = addr.getHostAddress();
                            //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                            boolean isIPv4 = sAddr.indexOf(':')<0;

                            if (useIPv4) {
                                if (isIPv4)
                                    return sAddr;
                            } else {
                                if (!isIPv4) {
                                    int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                    return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                                }
                            }
                        }
                    }
                }
            } catch (Exception ex) { } // for now eat exceptions
            return "";
        }




        /**
         * Asynchronously loads all phone contacts on mobile device
         * @param context
         * @param contactsLoaderListener
         */
        public static void loadPhoneContacts(Context context, ContactsLoaderListener contactsLoaderListener) {
            new ContactsLoaderTask(context, contactsLoaderListener).loadContacts();
        }

        public static void logDebug(String TAG,String info){
            Log.d(TAG,info);
        }

        public static void logInfo(String TAG,String info){
            Log.i(TAG,info);
        }

        public static void logError(String TAG,String error){
            Log.e(TAG,error);
        }
    }


    /*
    A facade for getting commonly used intents
    The idea is to use these intent in startActivityForResult so you can handle processing of responses after
    the intent's action is performed
     */
    public static class CommonIntents {

        /*
        Returns intent for sharing message via apps like Email,Gmail,WhatsApp,Facebook,Twitter,Google Plus etc
         */
        public static Intent getIntentForSharing(String subject, String message) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            shareIntent.putExtra(Intent.EXTRA_TEXT, message);

            return shareIntent;
        }

        /*
        Returns intent for viewing url in a browser
         */
        public static Intent getIntentForViewingWebpage(String url) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        }

        public static Intent getContactsPickerIntent() {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            return intent;
        }
    }

    public static int getRandomColor() {
        SecureRandom rgen = new SecureRandom();
        return Color.HSVToColor(150, new float[]{
                rgen.nextInt(359), 1, 1
        });
    }


    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    /**
     * @param context
     * @return the screen width in pixels
     */
    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }


}
