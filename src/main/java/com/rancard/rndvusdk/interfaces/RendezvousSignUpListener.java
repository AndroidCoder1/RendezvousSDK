package com.rancard.rndvusdk.interfaces;

import com.rancard.rndvusdk.models.UserProfile;

/**
 * Created by: Robert Wilson.
 * Date: Feb 02, 2016
 * Time: 4:03 PM
 * Package: com.rancard.rndvusdk.interfaces
 * Project: Rendezvous SDK
 */
public interface RendezvousSignUpListener
{
    void onBefore();
    void onResponse(UserProfile userProfile);
    void onError(Exception e);
    void onCancel();
}
