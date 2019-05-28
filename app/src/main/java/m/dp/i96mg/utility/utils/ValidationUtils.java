package m.dp.i96mg.utility.utils;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.webkit.URLUtil;

import java.net.InetAddress;

/**
 * Created by PC on 20/12/2017.
 */
public class ValidationUtils {

    public static final int TYPE_EMAIL=1,TYPE_PHONE=2,TYPE_NAME=3,TYPE_TEXT=4,TYPE_LONG_TEXT=5 , TYPE_PASSWORD=6;

    //<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name
            return !ipAddr.equals("");
        } catch (Exception e) {
            return false;
        }

    }

    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }

        return false;
    }

    //<uses-permission android:name="android.permission.GET_ACCOUNTS" />
    public static boolean deviceHasGoogleAccount(Context context) {
        AccountManager accMan = AccountManager.get(context);
        Account[] accArray = accMan.getAccountsByType("com.google");
        return accArray.length >= 1;
    }


    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }


    public static Boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static Boolean isAvLen(String str, int from, int to) {
        return str.length() >= from && str.length() < to;
    }

    public static Boolean isEqual(String str1, String str2) {
        return str1.equals(str2);
    }


    //Different between URL and NetworkURL http://stackoverflow.com/questions/23866700/difference-between-isnetworkurl-and-isvalidurl
    public static Boolean isUrl(String url) {
        return URLUtil.isValidUrl(url);
    }

    public static Boolean isNetworkUrl(String url) {
        return URLUtil.isNetworkUrl(url);
    }

    public static Boolean haveParticularChars(String str, char chars[]) {
        for (int i = 0; i < chars.length; i++)
            if (str.contains("" + chars[i]))
                return true;
        return false;
    }

    public static Boolean isMail(CharSequence str) {

        return str != null && android.util.Patterns.EMAIL_ADDRESS.matcher(str).matches();
    }

    public static Boolean isIPAddress(CharSequence str) {

        return str != null && android.util.Patterns.IP_ADDRESS.matcher(str).matches();
    }

    private static Boolean isPhone(CharSequence str) {

        return str != null && android.util.Patterns.PHONE.matcher(str).matches();
    }


    //<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    public static boolean isLocationEnabled(Context context) {
        LocationManager lm = null;
        boolean gps_enabled = false, network_enabled = false;
        if (lm == null)
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        return gps_enabled || network_enabled;
    }

    //<uses-permission android:name="android.permission.BLUETOOTH"  android:required="false" />
    public static boolean isBluetoothEnabled() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return (mBluetoothAdapter == null) || mBluetoothAdapter.isEnabled();
    }

    public static int batteryLevel(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
        return (level * 100) / scale;
    }

    public static boolean validateTexts(String data, int validationType){

        if(isEmpty(data))return false;

        if(validationType==TYPE_EMAIL){
            return isMail(data);
        }else if(validationType==TYPE_PHONE){
            return  isPhone(data);
        }else if(validationType==TYPE_TEXT){
            return isAvLen(data,3,25);
        }else if(validationType==TYPE_LONG_TEXT){
            return isAvLen(data,10,200);
        }else if(validationType==TYPE_PASSWORD){
            return isAvLen(data,6,35);
        }
        return false;
    }
}