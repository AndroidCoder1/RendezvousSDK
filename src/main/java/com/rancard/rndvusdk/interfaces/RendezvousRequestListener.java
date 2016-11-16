package com.rancard.rndvusdk.interfaces;

import com.rancard.rndvusdk.RendezvousResponse;

/**
 * Created by: Robert Wilson.
 * Date: Feb 02, 2016
 * Time: 4:03 PM
 * Package: com.rancard.rndvusdk.interfaces
 * Project: Rendezvous SDK
 */
public interface RendezvousRequestListener
{
    void onBefore();
    void onResponse(RendezvousResponse response);
    void onError(Exception e);
}
