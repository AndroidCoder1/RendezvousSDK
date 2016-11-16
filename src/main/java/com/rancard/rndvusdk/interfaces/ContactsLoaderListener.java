package com.rancard.rndvusdk.interfaces;

import com.rancard.rndvusdk.models.PhoneContact;

import java.util.List;

/**
 * Created by: Robert Wilson.
 * Date: Feb 26, 2016
 * Time: 11:23 AM
 * Package: com.rancard.rndvusdk.interfaces
 * Project: Rendezvous-Android
 */
public interface ContactsLoaderListener
{
    public void onBeforeContactsLoaderStarts();
    public void onContactsLoaderCompleted(List<PhoneContact> phoneContacts);
}
