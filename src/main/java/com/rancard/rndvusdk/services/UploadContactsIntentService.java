package com.rancard.rndvusdk.services;

import android.app.IntentService;
import android.content.Intent;

import com.rancard.rndvusdk.Rendezvous;

/**
 * Created by RSL-PROD-003 on 11/4/16.
 */
public class UploadContactsIntentService extends IntentService {

    public UploadContactsIntentService() {
        super(UploadContactsIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Rendezvous.retrieveContacts(UploadContactsIntentService.this);
    }
}
