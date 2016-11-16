package com.rancard.rndvusdk;

import android.util.Log;

import com.rancard.rndvusdk.interfaces.RendezvousRequestListener;

/**
 * Created by: Robert Wilson.
 * Date: Feb 02, 2016
 * Time: 4:03 PM
 * Package: com.rancard.rndvusdk.interfaces
 * Project: Rendezvous SDK
 */
public class DefaultRendezvousRequestListener implements RendezvousRequestListener
{
    public void onBefore(){

    }
    public void onResponse(RendezvousResponse response){
        Log.d("RNDVULISTENER", response.getBody());
    }
    public void onError(Exception e){
        e.printStackTrace();
    }
}
