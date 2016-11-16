package com.rancard.rndvusdk.interfaces;

import com.rancard.rndvusdk.models.UserProfile;

/**
 * Created by RSL-PROD-003 on 11/3/16.
 */
public interface GoogleSignInCompleteListener {
    public void callback(UserProfile userProfile);
}
