package com.rancard.rndvusdk.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;

import com.rancard.rndvusdk.models.PhoneContact;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by: Robert Wilson.
 * Date: Feb 18, 2016
 * Time: 10:28 AM
 * Package: com.rancard.rndvusdk.utils
 * Project: Rendezvous-Android
 */
public class Utils {

//    public static final String CLIENT_ID = "qB0ugjfMB1sV7A"; // SANDBOX=74nc4r6rn6vu,aTUw4Cr1pFNZr6 | PRODUCTION=qB0ugjfMB1sV7A
//    public static final long STORE_ID = 135L; // SANDBOX=135L | PRODUCTION=158L
    public static final long DEFAULT_LIMIT = 30L;
    public static final long DEFAULT_PAGE = 1L;


    public static List<String> extractUrlsFromText(String text) {
        List<String> urls = new ArrayList<String>();
        Pattern pattern = Pattern.compile("\\(?\\b(http://|www[.])[-A-Za-z0-9+&amp;@#/%?=~_()|!:,.;" +
                "]*[-A-Za-z0-9+&amp;@#/%=~_()|]");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String url = matcher.group();
            if (url.startsWith("(") && url.endsWith(")")) {
                url = url.substring(1, url.length() - 1);
            }
            urls.add(url);
        }
        return urls;
    }

    public static CharSequence getTimeElapsed(long epochTime)
    {
        return DateUtils
                .getRelativeTimeSpanString(
                        epochTime,
                        System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS);
    }

    public static String getSHA1String(Context con, String packagename) {
        String something = "";
        try {

            PackageInfo info = con.getPackageManager().getPackageInfo(
                    packagename, PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                something = new String(Base64.encode(md.digest(), 0));
                Log.d("Hash key", something);
            }

        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
        return something;
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static int dpToPixel(Context context, float dp){
        final float scale = context.getResources().getDisplayMetrics().density;
        int pixels = (int) (dp * scale + 0.5f);
        return pixels;
    }



    // To animate view slide out from top to bottom
    public static void slideToBottom(View view){
        TranslateAnimation animate = new TranslateAnimation(0,0,0,view.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }



    public static PhoneContact findPhoneContact(String name, MemoryCache mCache) {
        for (PhoneContact phoneContact : mCache.getLocalContacts()) {
            if (phoneContact.getDisplayName().equalsIgnoreCase(name)) {
                return phoneContact;
            }

            if (!phoneContact.getEmailAddresses().isEmpty()) {
                if (phoneContact.getEmailAddresses().get(0).equalsIgnoreCase(name)) {
                    return phoneContact;
                }
            }
        }
        return new PhoneContact();
    }


    public static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }
}