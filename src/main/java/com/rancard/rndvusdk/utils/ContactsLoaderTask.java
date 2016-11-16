package com.rancard.rndvusdk.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;

import com.rancard.rndvusdk.interfaces.ContactsLoaderListener;
import com.rancard.rndvusdk.models.PhoneContact;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

/**
 * Created by: Robert Wilson.
 * Date: Feb 26, 2016
 * Time: 3:18 AM
 * Package: com.rancard.rndvusdk.utils
 * Project: Rendezvous-Android
 */
public class ContactsLoaderTask extends AsyncTask<Void, Void, List<PhoneContact>>
{
    private ContactsLoaderListener contactsLoaderListener;
    private CountDownLatch countDownLatch;
    private List<PhoneContact> phoneContacts;
    private Context context;

    public ContactsLoaderTask(Context context, ContactsLoaderListener contactsLoaderListener)
    {
        this.context = context;
        this.contactsLoaderListener = contactsLoaderListener;
        this.countDownLatch = new CountDownLatch(1);
    }

    public void loadContacts()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            this.executeOnExecutor(Executors.newSingleThreadExecutor());
        }
        else {
            this.execute();
        }
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        this.contactsLoaderListener.onBeforeContactsLoaderStarts();
    }

    @Override
    protected List<PhoneContact> doInBackground(Void... voids)
    {
        new PhoneContactsLoaderThread().start();
        try {
            countDownLatch.await();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        return phoneContacts;
    }

    @Override
    protected void onPostExecute(List<PhoneContact> phoneContacts)
    {
        super.onPostExecute(phoneContacts);
        this.contactsLoaderListener.onContactsLoaderCompleted(phoneContacts);
    }

    private class PhoneContactsLoaderThread extends Thread
    {
        final Uri PHONE_CONTACTS_URI = ContactsContract.Contacts.CONTENT_URI;
        final Uri PHONE_NUMBERS_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        final Uri EMAIL_ADDRESSES_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;

        final String CONTACT_ID_COLUMN_NAME = ContactsContract.Contacts._ID;
        final String CONTACT_DISPLAYNAME_COLUMN_NAME = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) ?
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY : ContactsContract.Contacts.DISPLAY_NAME;
        final String CONTACT_HAS_PHONENUMBER_COLUMN_NAME = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        @Override
        public void run()
        {
            try {
                phoneContacts = new ArrayList<>();
                Cursor cursor = context.getContentResolver().query(PHONE_CONTACTS_URI, null, null, null, null);

                if (cursor.getCount() > 0) {
                    String id, displayName = "";
                    int phoneNumbersCount = 0;

                    while (cursor.moveToNext()) {
                        PhoneContact.Builder builder = new PhoneContact.Builder();
                        List<String> phoneNumbers = new ArrayList<String>();
                        List<String> emailAddresses = new ArrayList<String>();

                        id = cursor.getString(cursor.getColumnIndex(CONTACT_ID_COLUMN_NAME));
                        displayName = cursor.getString(cursor.getColumnIndex(CONTACT_DISPLAYNAME_COLUMN_NAME));

                        phoneNumbersCount = Integer.parseInt(cursor.getString(cursor.getColumnIndex
                                (CONTACT_HAS_PHONENUMBER_COLUMN_NAME)));

                        if (phoneNumbersCount > 0) {
                            Cursor numbersCursor = context.getContentResolver().query(
                                    PHONE_NUMBERS_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " +
                                            "?", new String[]{id}, null
                            );

                            if (numbersCursor != null && numbersCursor.getCount() > 0) {
                                String phoneNumber = "";
                                while (numbersCursor.moveToNext()) {
                                    phoneNumber = numbersCursor.getString(numbersCursor.getColumnIndex
                                            (ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    phoneNumbers.add(phoneNumber);
                                }
                                numbersCursor.close();
                            }

                        }


                        builder.setId(id)
                                .setDisplayName(displayName)
                                .setPhoneNumbers(phoneNumbers)
                                .setEmailAddresses(emailAddresses);

                        PhoneContact contact = builder.build();

                        if (!"".equals(contact.getDisplayName()) && !"".equals(contact.getPrimaryPhoneNumber())) {
                            phoneContacts.add(contact);
                        }

                    }
                }

                cursor.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {

            }
            countDownLatch.countDown();
        }
    }
}
