package com.rancard.rndvusdk;

import com.rancard.rndvusdk.utils.Constants;

import java.util.ArrayList;

/**
 * Created by RSL-PROD-003 on 10/27/16.
 */
public class Config {
    public static boolean facebookLogin = true;
    public static boolean googleLogin = true;
    public static String userType = Constants.EMAIL;
    public static String appName = "Rendezvous Sample";
    public static String CONTACT_PERMISSION_DENIED_TITLE = "ALONE ON "+appName.toUpperCase();
    public static ArrayList<String> permissionsForFacebook = new ArrayList<>();
    public static String CONTACT_PERMISSION_DENIED_DESCRIPTION = "Your friends are waiting to be discovered in "+appName.toUpperCase()+". Enable this permission to interact with your friends";
}
