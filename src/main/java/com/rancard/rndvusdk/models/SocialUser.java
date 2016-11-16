package com.rancard.rndvusdk.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by RSL-PROD-003 on 11/4/16.
 */
public class SocialUser {
    long id = 0;
    String profileImage = "";
    String fullName = "";
    String emailAddress = "";
    boolean appInstalled = false;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isAppInstalled() {
        return appInstalled;
    }

    public void setAppInstalled(boolean appInstalled) {
        this.appInstalled = appInstalled;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    public SocialUser(JSONObject jsonObject){

        if(jsonObject != null) {
            try {

                id = (jsonObject.has("id")) ? Long.valueOf(jsonObject.getString("id")) : Long.valueOf("0");
                profileImage = "http://graph.facebook.com/" + id + "/picture?type=large";
                fullName = (jsonObject.has("name")) ? jsonObject.getString("name") : "";
                emailAddress = (jsonObject.has("email")) ? jsonObject.getString("email") : "";
                appInstalled = (jsonObject.has("gender")) ? Boolean.valueOf(jsonObject.getString("gender")) : false;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        return "SocialUser{" +
                "id=" + id +
                ", profileImage='" + profileImage + '\'' +
                ", fullName='" + fullName + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", appInstalled=" + appInstalled +
                '}';
    }

}
