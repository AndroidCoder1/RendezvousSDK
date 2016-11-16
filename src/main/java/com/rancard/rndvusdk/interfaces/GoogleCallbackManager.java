package com.rancard.rndvusdk.interfaces;

import android.content.Intent;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.rancard.rndvusdk.models.UserProfile;
import com.rancard.rndvusdk.utils.Constants;

/**
 * Created by RSL-PROD-003 on 10/31/16.
 */
public class GoogleCallbackManager {

    public static UserProfile onActivityResult(int requestCode, Intent data){
        UserProfile userProfile = null;
        if (data != null){
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == Constants.RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    // Signed in successfully, show authenticated UI.
                    GoogleSignInAccount acct = result.getSignInAccount();
                    userProfile = new UserProfile(acct);
                }
            }
        }
        return userProfile;
    }
}
