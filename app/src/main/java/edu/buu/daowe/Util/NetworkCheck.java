package edu.buu.daowe.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkCheck {
    public static boolean checkNetwork(Context context){
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 获取代表联网状态的NetWorkInfo对象
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return false;
        }
        boolean available = networkInfo.isAvailable();
        if (available) {
            Log.e("NetWork  ", "当前的网络连接可用");
        } else {
            Log.e("NwtWork  ", "当前的网络连接不可用");
        }
        return available ;
    }
}
