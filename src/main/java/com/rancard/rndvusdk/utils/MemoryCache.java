package com.rancard.rndvusdk.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rancard.rndvusdk.Rendezvous;
import com.rancard.rndvusdk.models.EventLog;
import com.rancard.rndvusdk.models.PhoneContact;
import com.rancard.rndvusdk.models.User;
import com.rancard.rndvusdk.models.UserProfile;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by: Robert Wilson.
 * Date: Feb 11, 2016
 * Time: 3:42 PM
 * Package: com.multimedia.joyonline.utils
 * Project: JoyOnline-Android
 */
public class MemoryCache
{
    private static final String CACHE = "CACHE";
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private static MemoryCache mMemoryCache;
    private Gson mGson;
    private static final String ITEM_ID = "item_";
    public static boolean isEmailSignupAllowed = false;

    public static final String UNKNOWN = "";
    private final String UNCONFIRMED_MSISDN = "UNCONFIRMED_MSISDN_KEY";
    private final String CONFIRMED_MSISDN = "CONFIRMED_MSISDN_KEY";
    private final String FULLNAME = "FULLNAME_KEY";
    private final String GENDER = "GENDER_KEY";
    private final String EMAIL = "EMAIL";
    private final String HAS_CONFIRMED_MSISDN = "HAS_CONFIRMED_MSISDN_KEY";
    private final String HAS_REGISTERED_ON_GCM = "HAS_REGISTERED_ON_GCM_KEY";
    private final String GCM_ID = "GCM_ID_KEY";
    private final String HAS_WATCHED_CAMPAIGN_VIDEO = "HAS_WATCHED_CAMPAIGN_VIDEO_KEY";
    private final String HAS_COMPLETED_SHARING = "HAS_COMPLETED_SHARING_KEY";
    private final String HAS_SEEN_SHOWCASE = "HAS_SEEN_SHOWCASE";
    private final String HAS_POSTED_CONTACTS = "HAS_POSTED_CONTACTS_KEY";
    private final String SOCIAL_PROFILE_NAME = "SOCIAL_PROFILE_NAME";
    private final String SOCIAL_PROFILE_PHOTO = "SOCIAL_PROFILE_PHOTO";
    private final String SOCIAL_PROFILE_EMAIL = "SOCIAL_PROFILE_EMAIL";
    private final String HAS_SIGNED_IN = "HAS_SIGNED_IN";
    private final String HAS_SUBMITTED_PREFS = "HAS_SUBMITTED_PREFS";
    private final String HAS_SEEN_RECOMMENDATIONS = "HAS_SUBMITTED_RECS";
    private final String USER_PREFERENCES = "USER_PREFERENCES";
    private final String LOOPS_ALREADY_CREATED = "LOOPS_ALREADY_CREATED";
    private final String FOLLOWED_FRIENDS = "FOLLOWED_FRIENDS";
    private final String IS_PENDING_SMS_ACTIVATION = "IS_PENDING_SMS_ACTIVATION";
    private final String SHOULD_ENFORCE_PREFS_SELECTION = "SHOULD_ENFORCE_PREFS_SELECTION";
    private final String INVITATION_PAGE_URL = "INVITATION_PAGE_URL";
    private final String BADGE_NUMBER = "BADGE_NUMBER";
    private final String NUMBER_OF_USER_CONTACTS_READ = "NUMBER_OF_USER_CONTACTS_READ";
    private final String SHARE_URL = "SHARE_URL";
    private final String LAST_CATEGORY_REFRESH = "LAST_CATEGORY_REFRESH";
    private final String LAST_UPDATE_REFRESH = "LAST_UPDATE_REFRESH";
    private final String REFERRER_INFO = "REFERRER_INFO";
    private final String GOT_TAGS = "GOT_TAGS";
    private final String DECISION_API = "DECISION_API";
    private final String APP_VERSION_ON_STORE = "APP_VERSION_ON_STORE";
    private final String SESSION_TOKEN = "session_token";
    private final String HAS_SKIPPED = "has_skipped";
    private final String HAS_SEEN_REF = "has_seen_ref";
    private final String SHOW_GRAPH = "show_graph_dialog";
    private final String LAST_REFRESH = "last_refresh";
    private final String CACHED_EVENTS = "cachedEvents";
    private Rendezvous mRendezvousSdk;


    private MemoryCache(Context context)
    {
        mContext = context;
        mSharedPreferences = context.getSharedPreferences(CACHE, Context.MODE_PRIVATE);
        mGson = new Gson();
        mRendezvousSdk = Rendezvous.getInstance();

    }

    public synchronized void putInt(String key, int number)
    {
        mSharedPreferences.edit().putInt(key, number).commit();
    }

    public synchronized void putLong(String key, long number)
    {
        mSharedPreferences.edit().putLong(key, number).commit();
    }

    public synchronized void putBool(String key, boolean value)
    {
        mSharedPreferences.edit().putBoolean(key, value).commit();
    }

    public synchronized void putString(String key, String value)
    {
        mSharedPreferences.edit().putString(key, value).commit();
    }

    public synchronized void putFavouriteCount(String key, long value)
    {
        mSharedPreferences.edit().putLong(key, value);
    }

    public int getInt(String key)
    {
        return mSharedPreferences.getInt(key, 0);
    }

    public boolean hasInt(String key)
    {
        return mSharedPreferences.contains(key);
    }

    public long getLong(String key)
    {
        return mSharedPreferences.getLong(key, 0L);
    }

    public boolean getBool(String key)
    {
        return mSharedPreferences.getBoolean(key, false);
    }

    public String getString(String key)
    {
        return mSharedPreferences.getString(key, null);
    }

    public String getStringWith0Default(String key)
    {
        return mSharedPreferences.getString(key, "0");
    }

    public long getFavouriteCount(String key)
    {
        return mSharedPreferences.getLong(key, 0L);
    }

    public synchronized void storeUser(User user)
    {
        final Type type = new TypeToken<User>() {}.getType();
        mSharedPreferences.edit().putString(Constants.USER, mGson.toJson(user, type)).commit();

    }

    public synchronized void storeUserProfile(UserProfile user)
    {
        final Type type = new TypeToken<UserProfile>() {}.getType();
        mSharedPreferences.edit().putString(Constants.USER_PROFILE, mGson.toJson(user, type)).commit();

    }


    public synchronized void storeLastAppUpdateRefresh(Long refreshTime)
    {
        mSharedPreferences.edit().putLong(LAST_REFRESH, refreshTime).commit();

    }


    public Long getLastAppUpdateRefresh()
    {
        Long refreshTime = mSharedPreferences.getLong(LAST_REFRESH, 0L);
        return refreshTime;
    }

    public User getUser()
    {

        final Type type = new TypeToken<User>() {}.getType();
        User user = mGson.fromJson(mSharedPreferences.getString(Constants.USER, null), type);
        if(user == null){
            user = new User();
        }
        return user;
    }


    public UserProfile getUserProfile()
    {

        final Type type = new TypeToken<UserProfile>() {}.getType();
        UserProfile user = mGson.fromJson(mSharedPreferences.getString(Constants.USER_PROFILE, null), type);
        if(user == null){
            user = new UserProfile();
        }
        return user;
    }

    public synchronized void addLatestUserWhoGotRecommendations(String genre, String name)
    {
        final Type type = new TypeToken<LinkedHashMap<String, ArrayList<String>>>() {}.getType();
        LinkedHashMap<String,ArrayList<String>> items = getLatestUsersYouRecommendedTo();
        ArrayList<String> emails = new ArrayList<>();
        if(items.containsKey(genre)){
            emails = items.get(genre);
            emails.add(0,name.toLowerCase());
        }else{
            emails.add(0,name.toLowerCase());
        }
        items.put(genre,emails);
        mSharedPreferences.edit().putString(Constants.LATEST_RECOMMENDED_TO_USERS, mGson.toJson(items, type)).commit();
    }

    public LinkedHashMap<String, ArrayList<String>> getLatestUsersYouRecommendedTo()
    {
        final Type type = new TypeToken<LinkedHashMap<String, ArrayList<String>>>() {}.getType();

        if(mGson.fromJson(mSharedPreferences.getString(Constants.LATEST_RECOMMENDED_TO_USERS, null), type) == null){
            return new LinkedHashMap<String, ArrayList<String>>(0);
        }else {
            LinkedHashMap<String, ArrayList<String>> hm = new LinkedHashMap<String, ArrayList<String>>((LinkedHashMap)mGson.fromJson(mSharedPreferences.getString(Constants.LATEST_RECOMMENDED_TO_USERS, null), type)){
                protected boolean removeEldestEntry(Entry eldest) {
                    if(this.size() > 10)
                        return true;
                    else
                        return false;
                }
            };
            //hm = mGson.fromJson(mSharedPreferences.getString(Constants.LATEST_RECOMMENDED_TO_USERS, null), type);

            return hm;
        }

    }

    public synchronized int addRecentContacts(PhoneContact phoneContact)
    {
        final Type type = new TypeToken<LinkedList<PhoneContact>>() {}.getType();

        LinkedList<PhoneContact> phoneContacts = getRecentContacts();
        if(!phoneContacts.contains(phoneContact)) {
            phoneContacts.add(phoneContact);
            mSharedPreferences.edit().putString(Constants.RECENT_CONTACTS, mGson.toJson(phoneContacts, type)).commit();
        }

        return phoneContacts != null ? phoneContacts.size() : 0;
    }

    public LinkedList<PhoneContact> getRecentContacts()
    {

        final Type type = new TypeToken<LinkedList<PhoneContact>>() {}.getType();

        if(mGson.fromJson(mSharedPreferences.getString(Constants.RECENT_CONTACTS, null), type) == null){
            return new LinkedList<>();
        }else {
            return mGson.fromJson(mSharedPreferences.getString(Constants.RECENT_CONTACTS, null), type);
        }

    }


    public synchronized int addEventLog(EventLog eventLog)
    {
        final Type type = new TypeToken<LinkedList<EventLog>>() {}.getType();

        LinkedList<EventLog> eventLogs = getEventLogs();
        if(!eventLogs.contains(eventLog)) {
            eventLogs.add(eventLog);
            mSharedPreferences.edit().putString(Constants.EVENT_LOGS, mGson.toJson(eventLogs, type)).commit();
        }

        return eventLogs != null ? eventLogs.size() : 0;
    }

    public synchronized int removeEventLog(EventLog eventLog)
    {
        final Type type = new TypeToken<LinkedList<EventLog>>() {}.getType();

        LinkedList<EventLog> eventLogs = getEventLogs();
        if(eventLogs.contains(eventLog)) {
            eventLogs.remove(eventLog);
            mSharedPreferences.edit().putString(Constants.EVENT_LOGS, mGson.toJson(eventLogs, type)).commit();
        }

        return eventLogs != null ? eventLogs.size() : 0;
    }

    public LinkedList<EventLog> getEventLogs()
    {

        final Type type = new TypeToken<LinkedList<EventLog>>() {}.getType();

        if(mGson.fromJson(mSharedPreferences.getString(Constants.EVENT_LOGS, null), type) == null){
            return new LinkedList<>();
        }else {
            return mGson.fromJson(mSharedPreferences.getString(Constants.EVENT_LOGS, null), type);
        }

    }

    public ArrayList<PhoneContact> getLocalContacts()
    {
        final Type type = new TypeToken<ArrayList<PhoneContact>>() {}.getType();

        if(mGson.fromJson(mSharedPreferences.getString(Constants.LOCAL_CONTACTS, null), type) == null){
            return new ArrayList<>();
        }else {
            ArrayList<PhoneContact> hm = mGson.fromJson(mSharedPreferences.getString(Constants.LOCAL_CONTACTS, null), type);
            Collections.sort(hm, new Comparator<PhoneContact>() {
                @Override
                public int compare(PhoneContact lhs, PhoneContact rhs) {
                    return lhs.getDisplayName().compareTo(rhs.getDisplayName());
                }
            });
            return hm;
        }

    }

    public synchronized void saveLocalContacts(List<PhoneContact> contacts)
    {
        List<PhoneContact> tempContacts = new ArrayList<>(contacts);
        for(PhoneContact contact : tempContacts){
            if(contact.getEmailAddresses().contains(getUser().getEmail())){
                contacts.remove(contact);
            }
        }
        HashSet<PhoneContact> set = new HashSet<>(contacts);
        ArrayList<PhoneContact> contactss = new ArrayList<PhoneContact>(set);
        final Type type = new TypeToken<List<PhoneContact>>() {}.getType();

        mSharedPreferences.edit().putString(Constants.LOCAL_CONTACTS, mGson.toJson(contactss, type)).commit();

    }


    public static MemoryCache getInstance(Context context)
    {
        if ( mMemoryCache == null ) {
            mMemoryCache = new MemoryCache(context);
        }
        return mMemoryCache;
    }

    public void storeSocialDisplayName(String displayName) {
        putString(SOCIAL_PROFILE_NAME, displayName);
    }

    public String getSocialGender() {
        return getString(GENDER);
    }


    public void storeSocialGender(String displayName) {
        putString(GENDER, displayName);
    }

    public String getSocialDisplayName() {
        return getString(SOCIAL_PROFILE_NAME);
    }



    public void storeSocialAvatarUrl(String url) {
        putString(SOCIAL_PROFILE_PHOTO, url);
    }

    public String getSocialAvatarUrl() {
        return getUser().getProfile();
    }

    public void storeSocialEmailAddress(String email) {
        putString(SOCIAL_PROFILE_EMAIL, email);
    }

    public void setHasSkipped(boolean value) {
        putBool(HAS_SKIPPED, value);
    }

    public void setHasSignedIn(boolean value) {
        putBool(HAS_SIGNED_IN, value);
    }
}
