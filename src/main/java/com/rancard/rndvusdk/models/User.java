package com.rancard.rndvusdk.models;

import java.io.Serializable;

/**
 * Created by: Robert Wilson.
 * Date: Feb 21, 2016
 * Time: 7:04 PM
 * Package: com.multimedia.joyonline.models
 * Project: JoyOnline-Android
 */
public class User implements Serializable
{
    public static final String TAG = User.class.getSimpleName();
    private String id = "00";
    private String name = "";
    private String email = "";
    private String profile = "";
    private String gender = "";
    private String birthday = "";
    private String loginFrom = "";

    public String getId()
    {
        if(id == null){
            return "00";
        }else {
            return id;
        }
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName() {
        if (name == null) {
            return "";
        } else {
            return name;
        }
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getEmail()
    {
        if(email == null){
            return "";
        }else {
            return email;
        }
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getProfile()
    {
        return profile;
    }

    public void setProfile(String profile)
    {
        this.profile = profile;
    }

    public String getGender()
    {
        return gender;
    }

    public void setGender(String gender)
    {
        this.gender = gender;
    }

    public String getBirthday()
    {
        return birthday;
    }

    public void setBirthday(String birthday)
    {
        this.birthday = birthday;
    }

    public String getLoginFrom() {
        return loginFrom;
    }

    public void setLoginFrom(String loginFrom) {
        this.loginFrom = loginFrom;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }

        User user = (User) o;

        if (id != null ? !id.equals(user.id) : user.id != null) {
            return false;
        }
        if (name != null ? !name.equals(user.name) : user.name != null) {
            return false;
        }
        if (gender != null ? !gender.equals(user.gender) : user.gender != null) {
            return false;
        }
        if (email != null ? !email.equals(user.email) : user.email != null) {
            return false;
        }
        if (birthday != null ? !birthday.equals(user.birthday) : user.birthday != null) {
            return false;
        }
        if (profile != null ? !profile.equals(user.profile) : user.profile != null) {
            return false;
        }
        if (loginFrom != null ? !loginFrom.equals(user.loginFrom) : user.loginFrom != null) {
            return false;
        }
       return false;
    }

    @Override
    public int hashCode()
    {
        int result = (id != null ? id.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (birthday != null ? birthday.hashCode() : 0);
        result = 31 * result + (profile != null ? profile.hashCode() : 0);
        result = 31 * result + (loginFrom != null ? loginFrom.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", profile='" + profile + '\'' +
                ", gender='" + gender + '\'' +
                ", birthday='" + birthday + '\'' +
                ", loginFrom='" + loginFrom + '\'' +
                '}';
    }


}
