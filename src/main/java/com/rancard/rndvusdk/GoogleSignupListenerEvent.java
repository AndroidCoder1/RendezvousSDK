package com.rancard.rndvusdk;

/**
 * Created by RSL-PROD-003 on 11/2/16.
 */
public class GoogleSignupListenerEvent {

    String listener;
    public GoogleSignupListenerEvent(String listener){
        this.listener = listener;
    }

   public String getListener(){
        return listener;
    }

}
