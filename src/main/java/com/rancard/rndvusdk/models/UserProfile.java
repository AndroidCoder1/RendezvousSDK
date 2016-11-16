package com.rancard.rndvusdk.models;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by RSL-PROD-003 on 10/27/16.
 */
public class UserProfile {

    private String id = "";
    private String profile = "";
    private String name = "";
    private String email = "";
    private String gender = "";
    private String birthday = "";


    public UserProfile(JSONObject jsonObject){

        if(jsonObject != null) {
            try {

                id = (jsonObject.has("id")) ? jsonObject.getString("id") : "";
                profile = "http://graph.facebook.com/" + id + "/picture?type=large";
                name = (jsonObject.has("name")) ? jsonObject.getString("name") : "";
                email = (jsonObject.has("email")) ? jsonObject.getString("email") : "";
                gender = (jsonObject.has("gender")) ? jsonObject.getString("gender") : "";
                birthday = (jsonObject.has("birthday")) ? jsonObject.getString("birthday") : "";

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public UserProfile(GoogleSignInAccount acct){

        if(acct != null) {
            try {

                id = (acct.getId() != null) ? acct.getId() : "";
                profile = (acct.getPhotoUrl() != null) ? acct.getPhotoUrl().toString(): "";
                name = (acct.getDisplayName() != null) ? acct.getDisplayName() : "";
                email = (acct.getEmail() != null) ? acct.getEmail() : "";
                gender =  "";
                birthday =  "";
                System.out.println(">>>>>>In user profile google "+ id+profile+name+email);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public UserProfile(){

            try {

                id = "";
                profile = "";
                name =  "";
                email = "";
                gender =  "";
                birthday =  "";

            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "id='" + id + '\'' +
                ", profile='" + profile + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", birthday='" + birthday + '\'' +
                '}';
    }
}
