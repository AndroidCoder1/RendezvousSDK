package com.rancard.rndvusdk.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.rancard.rndvusdk.services.RendezvousEventLoggerService;
import com.rancard.rndvusdk.utils.MemoryCache;

/**
 * Created by RSL-PROD-003 on 11/3/16.
 */
public class EventLogBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager cm = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
            if (cm == null)
                return;
            if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()) {
                MemoryCache cache = MemoryCache.getInstance(context);
                if(cache.getEventLogs() != null && !cache.getEventLogs().isEmpty()){
                    Intent intent1 = new Intent(context, RendezvousEventLoggerService.class);
                    context.startService(intent1);
                }
            }

        }
}
