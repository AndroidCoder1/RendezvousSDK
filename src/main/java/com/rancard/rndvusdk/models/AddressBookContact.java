package com.rancard.rndvusdk.models;

import android.content.res.Resources;
import android.support.v4.util.LongSparseArray;

import java.util.ArrayList;

/**
 * Created by RSL-PROD-003 on 8/3/16.
 */
public class AddressBookContact {
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private long id;
    private Resources res;
    private String name;
    private ArrayList<String> emailList;
    private ArrayList<String> phonesList;
    private LongSparseArray<String> emails;
    private LongSparseArray<String> phones;

    public AddressBookContact(long id, String name, Resources res) {
        emailList = new ArrayList<>();
        phonesList = new ArrayList<>();
        this.id = id;
        this.name = name;
        this.res = res;
    }

    @Override
    public String toString() {
        return "id : "+id+" name : "+name+" emailList : "+emailList.toString()+" phoneList : "+phonesList.toString();
    }

    public void addEmail(int type, String address) {
        if (emails == null) {
            emails = new LongSparseArray<String>();
        }
        emails.put(type, address);
    }

    public void addPhone(int type, String number) {
        if (phones == null) {
            phones = new LongSparseArray<String>();
        }
        phones.put(type, number);
    }

    public ArrayList<String> getEmailList() {
        if (emails != null) {
            for (int i = 0; i < emails.size(); i++) {
                emailList.add(emails.valueAt(i));
            }
        }else {
            emailList = new ArrayList<>();
        }
        return emailList;
    }

    public void setEmailList(ArrayList<String> emailList) {
        this.emailList = emailList;
    }

    public ArrayList<String> getPhonesList() {
        if (phones != null) {
            for (int i = 0; i < phones.size(); i++) {
                phonesList.add(phones.valueAt(i));
            }
        }
        return phonesList;
    }

    public void setPhonesList(ArrayList<String> phonesList) {
        this.phonesList = phonesList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}