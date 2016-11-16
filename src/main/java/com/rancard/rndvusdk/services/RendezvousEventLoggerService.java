package com.rancard.rndvusdk.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;

import com.rancard.rndvusdk.Rendezvous;
import com.rancard.rndvusdk.RendezvousResponse;
import com.rancard.rndvusdk.interfaces.RendezvousRequestListener;
import com.rancard.rndvusdk.models.EventLog;
import com.rancard.rndvusdk.utils.Constants;
import com.rancard.rndvusdk.utils.MemoryCache;

import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by RSL-PROD-003 on 11/3/16.
 */
public class RendezvousEventLoggerService extends Service {

    MemoryCache cache;
    static Rendezvous rendezvous;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        cache = MemoryCache.getInstance(RendezvousEventLoggerService.this);
        if(System.currentTimeMillis() - cache.getLong(Constants.EVENT_LOGGER_SERVICE_TIME) > Constants.THREE_HOURS) {
            rendezvous = Rendezvous.getInstance();
            Log.d(Constants.TAG, "EVENT LOGGER SERVICE STARTED");
            try {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        if (isOnline()) {
                            Log.d(Constants.TAG, "EVENT LOGGER SERVICE IN ONLINE");
                            sendCachedEvent();
                        } else {
                            Log.d(Constants.TAG, "EVENT LOGGER SERVICE STOPPED");
                            stopSelf();
                        }

                    }
                }).start();
            } catch (Exception e) {
                Log.d(Constants.TAG, "EVENT LOGGER SERVICE IN EXCEPTION");
                e.printStackTrace();
            }
        }else{
            stopSelf();
        }

    }

    @Override
    public void onDestroy() {
        Log.d(Constants.TAG, "EVENT LOGGER SERVICE DESTROYED");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       return super.onStartCommand(intent, flags, startId);
    }

    public void sendCachedEvent() {
        try {
            cache.putLong(Constants.EVENT_LOGGER_SERVICE_TIME, System.currentTimeMillis());
            Log.d(Constants.TAG, "EVENT LOGGER SERVICE IN SEND CACHED EVENT");
            LinkedList<EventLog> eventLogs = cache.getEventLogs();

            if (eventLogs != null) {
                Log.d(Constants.TAG, "EVENT LOGGER SERVICE EVENT LOGS NOT NULL "+eventLogs );
                for(final EventLog eventLog : eventLogs){
                    Log.d(Constants.TAG, "EVENT LOGGER SERVICE FOR LOOP" + eventLog);

                    if(eventLog.getItemId() == 0){
                        rendezvous.logActivity("email", cache.getUserProfile().getEmail(), eventLog.getLogActivity(), new RendezvousRequestListener() {
                            @Override
                            public void onBefore() {

                            }

                            @Override
                            public void onResponse(RendezvousResponse response) {
                                try {
                                    JSONObject jObj = new JSONObject(response.getBody());
                                    Log.d(Constants.TAG, "EVENT LOGGER SERVICE RESPONSE "+jObj.toString());
                                    if(!jObj.getString("message").equalsIgnoreCase("ok")){
                                        Log.d(Constants.TAG, "EVENT LOGGER SERVICE MESSAGE NOT OK");
                                        return;
                                    }else{
                                        Log.d(Constants.TAG, "EVENT LOGGER SERVICE REMOVE EVENT LOG AFTER SUCCESSFUL TRANSMIT");
                                        cache.removeEventLog(eventLog);
                                    }
                                } catch (Exception e) {
                                    Log.d(Constants.TAG, "EVENT LOGGER SERVICE IN EXCEPTION IN RESPONSE");
                                    e.printStackTrace();
                                    return;
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                stopSelf();
                            }
                        });
                    }else {
                        rendezvous.logActivity(eventLog.getItemId(), cache.getUser().getEmail(), "", cache.getUser().getEmail(), eventLog.getLogActivity(), new RendezvousRequestListener() {
                            @Override
                            public void onBefore() {

                            }

                            @Override
                            public void onResponse(RendezvousResponse response) {
                                try {
                                    JSONObject jObj = new JSONObject(response.getBody());
                                    Log.d(Constants.TAG, "EVENT LOGGER SERVICE RESPONSE " + jObj.toString());
                                    if (!jObj.getString("message").equalsIgnoreCase("ok")) {
                                        Log.d(Constants.TAG, "EVENT LOGGER SERVICE MESSAGE NOT OK");
                                        return;
                                    } else {
                                        Log.d(Constants.TAG, "EVENT LOGGER SERVICE REMOVE EVENT LOG AFTER SUCCESSFUL TRANSMIT");
                                        cache.removeEventLog(eventLog);
                                    }
                                } catch (Exception e) {
                                    Log.d(Constants.TAG, "EVENT LOGGER SERVICE IN EXCEPTION IN RESPONSE");
                                    e.printStackTrace();
                                    return;
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                stopSelf();
                            }
                        });
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.stopSelf();

    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isAvailable() && ni.isConnected()) {
            return true;
        } else {
            return false;
        }
    }


}
