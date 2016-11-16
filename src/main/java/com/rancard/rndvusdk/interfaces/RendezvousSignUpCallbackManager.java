package com.rancard.rndvusdk.interfaces;

/**
 * Created by RSL-PROD-003 on 10/31/16.
 */
public class RendezvousSignUpCallbackManager {

    RendezvousSignUpListener listener;

    public RendezvousSignUpCallbackManager() {
            this.listener = null;
        }

    public void setRendezvousSignUpListener(RendezvousSignUpListener listener) {
        this.listener = listener;
    }


}
