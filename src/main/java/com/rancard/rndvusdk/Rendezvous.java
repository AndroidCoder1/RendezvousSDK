package com.rancard.rndvusdk;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v4.util.LongSparseArray;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.rancard.rndvusdk.exceptions.RendezvousException;
import com.rancard.rndvusdk.exceptions.RendezvousSdkNotInitializedException;
import com.rancard.rndvusdk.fragments.ContactsDialogFragment;
import com.rancard.rndvusdk.interfaces.RendezvousClient;
import com.rancard.rndvusdk.interfaces.RendezvousRequestListener;
import com.rancard.rndvusdk.models.AddressBookContact;
import com.rancard.rndvusdk.models.PhoneContact;
import com.rancard.rndvusdk.services.RendezvousService;
import com.rancard.rndvusdk.utils.Constants;
import com.rancard.rndvusdk.utils.MemoryCache;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by: Robert Wilson.
 * Date: Feb 02, 2016
 * Time: 12:24 PM
 * Package: com.rancard.rndvusdk
 * Project: Rendezvous SDK
 */
public final class Rendezvous implements RendezvousClient
{
    private static String TAG = Rendezvous.class.getCanonicalName();
    private static String mClientId;
    private static long mStoreId;
    private static RendezvousEnvironment mRendezvousEnvironment;
    private static String mApiKey;
    private static String mApiSecret;
    private static Context mContext;
    private static volatile Rendezvous mRendezvous;
    private static RendezvousService mService;
    private static RendezvousEndpoint mEndpoint;
    public static Long STORE_ID = 0L;
    public static String msisdn = "";
    public static String APP_PACKAGE_NAME = "";
    public static String CLIENT_ID = "";
    public static String CLIENT_ID_PROPERTY = "com.rancard.rndvu.clientId";
    public static String STORE_ID_PROPERTY = "com.rancard.rndvu.storeId";
    public static String FACEBOOK_ID_PROPERTY = "com.rancard.rndvu.facebookId";
    public static String AdId = "";
    public static String friendsSubTopicTxt = "Get opinions of your friends and see their recommendations";
    public static String friendsNoTopicsTxt = "Other Friends";
    public static String friendsNoSubTopicsTxt = "Recommend new topics to these friends";
    public static String peopleTopicTxt = "People You May Know";
    public static String peopleSubTopicTxt = "To see recommendations from your friends, add them";
    public static String friendsBtn = "Get Opinion";
    public static String noFriendsBtn = "Recommend";
    public static String peopleBtn = "Add Friend";
    public static String facebookAppId = "0";
    public static String loginFrom = "";
    /*Resource id of drawable to be used for non-social recommendations*/
    public static int nonSocialRecommendationIcon = 0;
    /*Resource id of drawable to be usef for social recomendations. This defaults to the rndvu icon*/
    public static int socialRecommedationIcon = 0;
    /*Title to be shown on notifications received via sms*/
    public static String smsNotificationTitle = "0";
    /*Origin of SMSes to be read by sdk as sms notifications
    This is to prevent sdk from reading every text message received on mobile device
    For example, if smsNotificationTargetOrigin is set to +233200662782, only text messages from
    +233200662782 will be read and treated as sms notifications
    * */
//    public static String smsNotificationTargetOrigin ="JoyOnline";

    public static List<String> mqttTopics = new ArrayList<>();

    public static String mqttBrokerUrl = "tcp://notifications.rancardmobility.com:80"; //tcp://iot.eclipse.org:1883";
    public static volatile boolean initialized = false;

    static {
        socialRecommedationIcon = R.drawable.ic_rndvu;
    }

    private static OkHttpClient.Builder newClient;

    private Rendezvous(Context context, String clientId, long storeId, RendezvousEnvironment rendezvousEnvironment)
    {
        mContext = context;
        mRendezvousEnvironment = rendezvousEnvironment;
        mEndpoint = new RendezvousEndpoint(rendezvousEnvironment);
        mService = RendezvousService.getInstance(storeId, clientId);
    }

    private Rendezvous(Context context, String clientId, long storeId, String apiKey, RendezvousEnvironment rendezvousEnvironment)
    {
        mContext = context;
        mApiKey = apiKey;
        mRendezvousEnvironment = rendezvousEnvironment;
        mEndpoint = new RendezvousEndpoint(rendezvousEnvironment);
        mService = RendezvousService.getInstance(storeId, clientId);
    }


    public static void setTargetVariables(String storeId, String clientId, String mqttBrokerUrl, String[] mqttPayload)
    {
        Rendezvous.STORE_ID = Long.valueOf(storeId);
        Rendezvous.CLIENT_ID = clientId;
        Rendezvous.mqttBrokerUrl = mqttBrokerUrl;
        Rendezvous.mqttTopics = Arrays.asList(mqttPayload);
    }

    public static void setFacebookAppId(String facebookAppId){
        Rendezvous.facebookAppId = facebookAppId;
    }

    public static void initializeSdk(Context context) throws RendezvousException{
        if (context == null){
            throw new NullPointerException("Context is NULL");
        }
        Rendezvous.mContext = context;
        loadConfigData(Rendezvous.mContext);
        useProduction();
        initialized = true;
    }

    public static boolean isInitialized(){
        return initialized;
    }

    public static void useProduction(){
        mRendezvous = new Rendezvous(mContext, CLIENT_ID, Long.valueOf(STORE_ID), RendezvousEnvironment.PRODUCTION);
    }

    public static void useSandbox(){
        mRendezvous = new Rendezvous(mContext, CLIENT_ID, Long.valueOf(STORE_ID), RendezvousEnvironment.SANDBOX);
    }



    private static void loadConfigData(Context context) throws RendezvousException{
        ApplicationInfo ai;
        Rendezvous.APP_PACKAGE_NAME = mContext.getPackageName();
        try {
            ai = context.getPackageManager().getApplicationInfo(
                    APP_PACKAGE_NAME, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RendezvousSdkNotInitializedException(e.getMessage());
        }

        if (ai == null || ai.metaData == null) {
            throw new RendezvousSdkNotInitializedException("ApplicationInfo is NULL");
        }

        Rendezvous.CLIENT_ID = ai.metaData.getString(CLIENT_ID_PROPERTY);
        Rendezvous.STORE_ID = (long) ai.metaData.getInt(STORE_ID_PROPERTY);
        System.out.println();
        long facebookID = (ai.metaData.containsKey(FACEBOOK_ID_PROPERTY)) ? Long.valueOf(ai.metaData.getString(FACEBOOK_ID_PROPERTY)) : Long.valueOf(Rendezvous.facebookAppId);
        Rendezvous.facebookAppId= String.valueOf(facebookID);
        System.out.println("OHKKKKK FACEBOOKID" +  Rendezvous.facebookAppId);

        if(Config.facebookLogin) {
            if (Rendezvous.FACEBOOK_ID_PROPERTY == null) {
                String message = FACEBOOK_ID_PROPERTY + " not set in AndroidManifest.xml. You can set in AndroidManifest.xml or Rendezvous.setFacebookAppId(getResources().getString(R.string.facebook_app_id));";
                Log.e(TAG, message);
                throw new RendezvousException(message);

            }
        }else{
            String message = FACEBOOK_ID_PROPERTY + " not configured in Config);";
            Log.e(TAG, message);
            throw new RendezvousException(message);
        }

        if (Rendezvous.CLIENT_ID == null ||
                Rendezvous.STORE_ID == null )
        {
            String message = CLIENT_ID_PROPERTY+" or "+STORE_ID_PROPERTY+" not set in AndroidManifest.xml";
            Log.e(TAG, message);
            throw new RendezvousException(message);
        }

        // Set MQTT Topics
        mqttTopics.add("rndvu");
        mqttTopics.add("rndvu/"+ CLIENT_ID);
        mqttTopics.add("rndvu/"+CLIENT_ID+"/"+ AdId);
       // mqttTopics.add("rndvu/"+CLIENT_ID+"/"+email);
       // mqttTopics.add("rndvu/"+ CLIENT_ID+"/"+ id);
    }

    public static void getAdId(Context context){


        new GetGAIDTask().execute(context);
    }

    private Rendezvous(Context context, String clientId, long storeId, String apiKey, String apiSecret, RendezvousEnvironment rendezvousEnvironment)
    {
        mContext = context;
        mApiKey = apiKey;
        mApiSecret = apiSecret;
        mRendezvousEnvironment = rendezvousEnvironment;
        mEndpoint = new RendezvousEndpoint(rendezvousEnvironment);
        mService = RendezvousService.getInstance(storeId, clientId);
    }

    public Rendezvous configure()
    {
        return this;
    }

    public Rendezvous setConnectTimeout(long milliseconds)
    {
        if ( mService == null ) {
            return null;
        }

        if ( newClient == null) {
            newClient = mService.getClient().newBuilder();
        }
        newClient.connectTimeout(milliseconds, TimeUnit.MILLISECONDS);

        return this;
    }

    public Rendezvous setReadTimeout(long milliseconds)
    {
        if ( mService == null ) {
            return null;
        }

        if ( newClient == null) {
            newClient = mService.getClient().newBuilder();
        }

        newClient.readTimeout(milliseconds, TimeUnit.MILLISECONDS);

        return this;
    }

    public void post(Uri url, HashMap<String, String> payload, HashMap<String, String> headers, RendezvousRequestListener requestListener)
    {
        if ( mService == null) {
            return;
        }

        if ( url == null ) {
            return;
        }

        if ( payload == null ) {
            return;
        }

        if ( requestListener == null) {
            return;
        }

        requestListener.onBefore();

        if ( newClient == null) {
            newClient = mService.getClient().newBuilder().connectTimeout(RendezvousService.CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
            newClient.readTimeout(RendezvousService.READ_TIMEOUT, TimeUnit.MILLISECONDS);
        }

        OkHttpClient client = newClient.build();

        FormBody.Builder builder = new FormBody.Builder();
        Set<String> paramsKeys = payload.keySet();
        for (String key : paramsKeys) {
            builder.add(key, payload.get(key));
        }

        try {
            RequestBody formBody = builder.build();

            Request.Builder requestBuilder = new Request.Builder();
            Request request;

            if ( headers != null ) {
                Set<String> headerKeys = headers.keySet();
                for (String key : headerKeys) {
                    requestBuilder.addHeader(key, headers.get(key));
                }

                request = requestBuilder
                        .post(formBody)
                        .url(url.toString())
                        .build();
            }
            else  {
                request = new Request.Builder()
                        .url(url.toString())
                        .post(formBody)
                        .build();
            }

            Response response = client.newCall(request).execute();
            requestListener.onResponse(RendezvousResponse.transform(response));
        }
        catch (Exception e) {
            requestListener.onError(e);
            e.printStackTrace();
        }

    }

    public void get(Uri url, HashMap<String, String> headers, RendezvousRequestListener requestListener)
    {
        if ( mService == null) {
            return;
        }

        if ( url == null ) {
            return;
        }

        if ( requestListener == null) {
            return;
        }

        requestListener.onBefore();

        if ( newClient == null) {
            newClient = mService.getClient().newBuilder().connectTimeout(RendezvousService.CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
            newClient.readTimeout(RendezvousService.READ_TIMEOUT, TimeUnit.MILLISECONDS);
        }

        OkHttpClient client = newClient.build();

        try {

            Request.Builder requestBuilder = new Request.Builder();
            Request request;

            if ( headers != null ) {
                Set<String> headerKeys = headers.keySet();
                for (String key : headerKeys) {
                    requestBuilder.addHeader(key, headers.get(key));
                }

                request = requestBuilder
                        .url(url.toString())
                        .build();
            }
            else  {
                request = new Request.Builder()
                        .url(url.toString())
                        .build();
            }

            Response response = client.newCall(request).execute();

            if (response != null) {
                requestListener.onResponse(RendezvousResponse.transform(response));
            }
        }
        catch (Exception e) {
            requestListener.onError(e);
            e.printStackTrace();
        }
    }

    public RendezvousEnvironment getEnvironment()
    {
        return mRendezvousEnvironment;
    }

    public String getRoute(RendezvousRoute rendezvousRoute)
    {
        if ( mEndpoint != null ) {
            return mEndpoint.getRoute(rendezvousRoute);
        }
        return null;
    }

    @Override
    public void getItems(long page, long limit, String returnFields, RendezvousRequestListener requestListener)
    {
        if ( mEndpoint != null && mService != null ) {
            Uri url = Uri.parse(mEndpoint.getRoute(RendezvousRoute.ITEMS, page, limit, returnFields));
            mService.getAsync(url, requestListener);
        }
    }

    @Override
    public void getItems(long page, long limit, long categoryId, String returnFields, RendezvousRequestListener requestListener)
    {
        if ( mEndpoint != null && mService != null ) {
            Uri url = Uri.parse(mEndpoint.getRoute(RendezvousRoute.ITEMS_BY_CATEGORY, page, limit, categoryId, returnFields));
            mService.getAsync(url, requestListener);
        }
    }

    @Override
    public void getItemsByTags(long page, long limit, String tags, String returnFields, RendezvousRequestListener requestListener)
    {
        if ( mEndpoint != null && mService != null ) {
            Uri url = Uri.parse(mEndpoint.getRoute(RendezvousRoute.ITEMS_BY_TAGS, page, limit, tags, returnFields));
            mService.getAsync(url, requestListener);
        }
    }

    @Override
    public void getSearchItems(long page, long limit, String query, String returnFields, RendezvousRequestListener requestListener)
    {
        if ( mEndpoint != null && mService != null ) {
            Uri url = Uri.parse(mEndpoint.getRoute(RendezvousRoute.SEARCH, STORE_ID, CLIENT_ID, query, page, limit, returnFields));
            mService.getAsync(url, requestListener);
        }
    }

    @Override
    public void getFeaturedItems(String byCategories, String returnFields, RendezvousRequestListener requestListener) {

        if ( mEndpoint != null && mService != null ) {
            Uri url = Uri.parse(mEndpoint.getRoute(RendezvousRoute.GET_FEATURED_ITEMS, STORE_ID, CLIENT_ID, byCategories, returnFields));
            mService.getAsyncNoPresets(url, requestListener);
        }

    }

    @Override
    public void getFriendsInApp(String email, boolean registeredUsers, RendezvousRequestListener requestListener) {
        if ( mService != null ) {
            Uri url = Uri.parse("https://api.rancardmobility.com/v1/users/social/user/graph/contacts?user.email=" + email + "&client_id=" + CLIENT_ID + "&showRegisteredUsersOnly=" + registeredUsers);
            mService.getAsyncNoPresets(url, requestListener);
        }
    }

    @Override
    public void getItems(int page, int limit, String urll, RendezvousRequestListener requestListener)
    {
        if ( mEndpoint != null && mService != null ) {
            Uri url = Uri.parse(mEndpoint.getRoute(RendezvousRoute.ITEMS_BY_ALBUM, page, limit, urll));
            mService.getAsync(url, requestListener);
        }
    }

    @Override
    public void getCategories(RendezvousRequestListener requestListener)
    {
        if ( mEndpoint != null && mService != null ) {
            Uri url = Uri.parse(String.format(mEndpoint.getRoute(RendezvousRoute.CATEGORIES), STORE_ID));
            mService.getAsync(url, requestListener);
        }
    }

    @Override
    public void getCategoriesWithGenres(RendezvousRequestListener requestListener)
    {
        if ( mEndpoint != null && mService != null ) {
            Uri url = Uri.parse(mEndpoint.getRoute(RendezvousRoute.CATEGORIES_WITH_GENRES));
            mService.getAsync(url, requestListener);
        }
    }

    @Override
    public void getTags(RendezvousRequestListener requestListener)
    {
        if ( mEndpoint != null && mService != null ) {
            Uri url = Uri.parse(mEndpoint.getRoute(RendezvousRoute.TAGS, STORE_ID));
            mService.getAsync(url, requestListener);
        }
    }

    @Override
    public void tagImages(String property, String msisdn, String email, long page, long limit, boolean abbreviate, long count, String[] tags, RendezvousRequestListener requestListener)
    {
        if ( mEndpoint != null && mService != null ) {
            String _url = String.format(mEndpoint.getRoute(RendezvousRoute.TAG_IMAGES), property,CLIENT_ID, STORE_ID);
            Uri url = Uri.parse(_url);
            HashMap<String, String> payload = new HashMap<>();
            String value = "";
            JSONArray arr = new JSONArray();
            if ( tags != null ) {

                for (String c : tags) {

                    arr.put(c);

                }
                payload.put(Constants.TAGS, value);
            }
            mService.postNoTimeOut(url, String.valueOf(arr), payload, requestListener);
        }
    }


    @Override
    public void tagNews(String params, long count, String[] tags, RendezvousRequestListener requestListener)
    {
        if ( mEndpoint != null && mService != null ) {
            String _url = String.format(mEndpoint.getRoute(RendezvousRoute.TAG_NEWS), params, CLIENT_ID, STORE_ID,count);
            Uri url = Uri.parse(_url);
            HashMap<String, String> payload = new HashMap<>();
            String value = "";
            JSONArray arr = new JSONArray();
            if ( tags != null ) {

                for (String c : tags) {

                    arr.put(c);

                }
                payload.put(Constants.TAGS, value);
            }
            mService.postNoTimeOut(url, String.valueOf(arr), payload, requestListener);
        }
    }

    @Override
    public void logActivity(final String userType, final String userId, final RendezvousLogActivity activity, final RendezvousRequestListener requestListener)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if ( mEndpoint != null && mService != null ) {
                    Uri url = Uri.parse(mEndpoint.getRoute(RendezvousRoute.LOG_ACTIVITY));
                    Map<String, String> payload = new HashMap<>();
                    payload.put(Constants.ACTION, activity.getActivity());
                    payload.put(Constants.CLIENT_iD, CLIENT_ID);
                    payload.put(Constants.STORE_ID, String.valueOf(STORE_ID));
                    payload.put(Constants.USER_TYPE, userType);
                    payload.put(Constants.USER_ID, userId);
                    payload.put(Constants.EMAIL, userId);
                    mService.post(url, payload,mContext, requestListener);
                }
            }
        }).start();

    }

    @Override
    public void logActivity(final long itemId, final String email,final String userType, final String userId, final RendezvousLogActivity activity, final RendezvousRequestListener requestListener)
    {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if ( mEndpoint != null && mService != null ) {
                    Uri url = Uri.parse(mEndpoint.getRoute(RendezvousRoute.LOG_ACTIVITY));
                    Map<String, String> payload = new HashMap<>();
                    payload.put(Constants.ITEM_ID, String.valueOf(itemId));
                    payload.put(Constants.CLIENT_iD, CLIENT_ID);
                    payload.put(Constants.STORE_ID, String.valueOf(STORE_ID));
                    payload.put(Constants.USER_TYPE, userType);
                    payload.put(Constants.USER_ID, userId);
                    payload.put(Constants.EMAIL, email);
                    payload.put(Constants.ACTION, activity.getActivity());
                    mService.post(url, payload, mContext, requestListener);
                }
            }
        }).start();


    }

    @Override
    public void createComment(long itemId, long parentId, String text, String authorType, String authorReference, RendezvousRequestListener requestListener)
    {
        if ( mEndpoint != null && mService != null ) {
            String _url = String.format(mEndpoint.getRoute(RendezvousRoute.POST_ITEM_COMMENT), itemId, CLIENT_ID, STORE_ID);
            Uri url = Uri.parse(_url);
            Map<String, String> payload = new HashMap<>();
            payload.put(Constants.ITEM_ID, String.valueOf(itemId));
            if(parentId != 0) {
                payload.put(Constants.PARENT_ID, String.valueOf(parentId));
            }
            payload.put(Constants.TEXT, text);
            payload.put(Constants.AUTHOR_TYPE, authorType);
            payload.put(Constants.AUTHOR_REFERENCE, authorReference);
            mService.post(url, payload, mContext, requestListener);
        }
    }

    @Override
    public void getComments(long itemId, long parentId, long page, long limit, RendezvousRequestListener requestListener)
    {
        if ( mEndpoint != null && mService != null ) {
            String _url = String.format(mEndpoint.getRoute(RendezvousRoute.GET_ITEM_COMMENTS), itemId, CLIENT_ID, STORE_ID, page, limit);
            Uri url = Uri.parse(_url);
            mService.getAsyncNoPresets(url, requestListener);
        }
    }

    @Override
    public void getNotifications(String email, long page, long limit, long abbreviate, boolean showRecommendation, RendezvousRequestListener requestListener){

        if ( mEndpoint != null && mService != null ) {
            String _url = String.format(mEndpoint.getRoute(RendezvousRoute.GET_NOTIFICATION_ITEMS), CLIENT_ID, STORE_ID, page, limit,email, showRecommendation, abbreviate);
            Uri url = Uri.parse(_url);
            mService.getAsyncNoPresets(url, requestListener);
        }

    }

    @Override
    public void getComment(long itemId, long commentId, boolean showMinimalData, RendezvousRequestListener requestListener)
    {
        if ( mEndpoint != null && mService != null ) {
            String _url = String.format(mEndpoint.getRoute(RendezvousRoute.GET_COMMENT), itemId, commentId, showMinimalData, CLIENT_ID, String.valueOf(STORE_ID));
            Uri url = Uri.parse(_url);
            mService.getAsyncNoPresets(url, requestListener);
        }
    }

    @Override
    public void signUp(String password, String userId, String userType, String fullname, String avatarUrl, RendezvousRequestListener requestListener)
    {
        String _url = String.format(mEndpoint.getRoute(RendezvousRoute.SIGN_UP));
        Uri url = Uri.parse(_url);
        HashMap<String, String> payload = new HashMap<>();
        payload.put(Constants.PASSWORD, String.valueOf(password));
        payload.put(Constants.NAME, String.valueOf(fullname));
        payload.put(Constants.EMAIL, String.valueOf(userId));
        payload.put(Constants.AVATAR_URL, String.valueOf(avatarUrl));
        payload.put(Constants.STORE_ID, String.valueOf(STORE_ID));
        payload.put(Constants.CLIENT_ID, String.valueOf(CLIENT_ID));
        mService.post(url, payload, mContext, requestListener);
    }


    @Override
    public void getFriends(String msisdn, int page, int limit, Boolean friendsOnly, String topics, RendezvousRequestListener requestListener)
    {
        if ( mEndpoint != null && mService != null ) {
            String _url = String.format(mEndpoint.getRoute(RendezvousRoute.GET_FRIENDS), CLIENT_ID, msisdn, STORE_ID,friendsOnly,limit, page);
            Uri url = Uri.parse(_url);
            mService.getAsyncNoPresets(url, requestListener);
        }

    }

    @Override
    public void getFriendsInGraph(String userId, String userType, int page, int limit, Boolean friendsOnly,RendezvousRequestListener requestListener)
    {
        if ( mEndpoint != null && mService != null ) {
            String _url = String.format(mEndpoint.getRoute(RendezvousRoute.GET_FRIENDS), CLIENT_ID, msisdn, STORE_ID,friendsOnly,limit, page);
            Uri url = Uri.parse(_url);
            mService.getAsyncNoPresets(url, requestListener);
        }
    }

    @Override
    public void getTopics(String msisdn, boolean countOnly, RendezvousRequestListener requestListener){

        if ( mEndpoint != null && mService != null ) {
            String _url = String.format(mEndpoint.getRoute(RendezvousRoute.GET_TOPICS), countOnly, STORE_ID, msisdn, CLIENT_ID);
            Uri url = Uri.parse(_url);
            mService.getAsyncNoPresets(url, requestListener);
        }

    }

    @Override
    public void sendTopics(String msisdn, String receiver,String limit, String tags, RendezvousRequestListener requestListener) {

        if ( mEndpoint != null && mService != null ) {
            String _url = String.format(mEndpoint.getRoute(RendezvousRoute.SEND_TOPICS));
            Uri url = Uri.parse(_url);
            Map<String, String> payload = new HashMap<>();
            payload.put(Constants.CLIENT_ID, String.valueOf(CLIENT_ID));
            payload.put(Constants.MSISDN, String.valueOf(receiver));
            payload.put(Constants.SENDER, String.valueOf(msisdn));
            payload.put(Constants.NODATA, "true");
            payload.put(Constants.SENDEMAIL, "true");
            payload.put(Constants.TAGS, tags);
            payload.put(Constants.STORE_ID, String.valueOf(CLIENT_ID));
            payload.put(Constants.LIMIT, limit);
            mService.post(url, payload, mContext, requestListener);
        }

    }


    @Override
    public void getPeopleYouMayKnow(String msisdn,int page, int limit, RendezvousRequestListener requestListener) {

        if ( mEndpoint != null && mService != null ) {
            String _url = String.format(mEndpoint.getRoute(RendezvousRoute.GET_PEOPLE_YOU_MAY_KNOW), msisdn, CLIENT_ID, STORE_ID, page, limit);
            Uri url = Uri.parse(_url);
            mService.getAsyncNoPresets(url, requestListener);
        }
    }



    @Override
    public void subscribe(String userType, String user, String action, RendezvousRequestListener requestListener){

        String _url = String.format(mEndpoint.getRoute(RendezvousRoute.SUBSCRIBE));
        Uri url = Uri.parse(_url);
        HashMap<String, String> payload = new HashMap<>();
        payload.put(Constants.CLIENT_ID, String.valueOf(CLIENT_ID));
        payload.put(Constants.USER_TYPE, userType);
        payload.put(Constants.STORE_ID, String.valueOf(STORE_ID));
        payload.put(Constants.USER, user);
        payload.put(Constants.ACTION, action);
        mService.post(url, payload, mContext, requestListener);

    }

    @Override
    public void signIn(String password, String email,RendezvousRequestListener requestListener)
    {
        String _url = String.format(mEndpoint.getRoute(RendezvousRoute.SIGN_UP));
        Uri url = Uri.parse(_url);
        HashMap<String, String> payload = new HashMap<>();
        payload.put(Constants.CLIENT_ID, String.valueOf(CLIENT_ID));
        payload.put(Constants.PASSWORD, String.valueOf(password));
        payload.put(Constants.STORE_ID, String.valueOf(STORE_ID));
        payload.put(Constants.EMAIL, String.valueOf(email));
        mService.post(url, payload, mContext, requestListener);
    }

    @Override
    public void uploadUserContacts(String userType, String userId, String contactsJson, RendezvousRequestListener rndvuRequestListener)
    {
        String _url = String.format(mEndpoint.getRoute(RendezvousRoute.PUSH_CONTACTS));
        Uri url = Uri.parse(_url);
        HashMap<String, String> payload = new HashMap<>();
        payload.put(Constants.USER_TYPE, userType);
        payload.put(Constants.USER_ID, userId);
        payload.put(Constants.CLIENT_ID, CLIENT_ID);
        payload.put(Constants.CONTACTSJSON, contactsJson);
        mService.post(url, payload, mContext, rndvuRequestListener);
    }

    @Override
    public void getStagingResponse(RendezvousRequestListener requestListener) {
        if ( mEndpoint != null && mService != null ) {
            String _url = String.format(mEndpoint.getRoute(RendezvousRoute.STAGING_RESPONSE), CLIENT_ID);
            Uri url = Uri.parse(_url);
            mService.getAsyncNoPresets(url, requestListener);
        }
    }

    public void uploadUserContacts(AppCompatActivity context){
        Log.d(Constants.TAG, "IN UPLOAD CONTACTS");
        //FragmentManager fm = fragmentManager;
        ContactsDialogFragment.show(context);
    }


    public static void retrieveContacts(Context mContext){
        try {


            final MemoryCache mCache = MemoryCache.getInstance(mContext);
            LinkedList<PhoneContact> phoneContacts = new LinkedList<PhoneContact>();
            LongSparseArray<AddressBookContact> array = new LongSparseArray<AddressBookContact>();

            String[] projection = {
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.Data.CONTACT_ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Contactables.DATA,
                    ContactsContract.CommonDataKinds.Contactables.TYPE,
            };
            String selection = ContactsContract.Data.MIMETYPE + " in (?, ?)";
            String[] selectionArgs = {
                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
            };
            String sortOrder = ContactsContract.Contacts.SORT_KEY_ALTERNATIVE;

            Uri uri = null;
            Cursor cursor = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                uri = ContactsContract.CommonDataKinds.Contactables.CONTENT_URI;
                cursor = mContext.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
            } else {
                uri = ContactsContract.Contacts.CONTENT_URI;
                cursor = mContext.getContentResolver().query(uri, null, null, null, null);
            }

            final int mimeTypeIdx = cursor.getColumnIndex(ContactsContract.Data.MIMETYPE);
            final int idIdx = cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID);
            final int nameIdx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            final int dataIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.DATA);
            final int typeIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.TYPE);

            List<AddressBookContact> listt = new ArrayList<>();

            while (cursor.moveToNext()) {

                long id = cursor.getLong(idIdx);
                AddressBookContact addressBookContact = array.get(id);
                if (addressBookContact == null) {
                    addressBookContact = new AddressBookContact(id, cursor.getString(nameIdx), mContext.getResources());
                    array.put(id, addressBookContact);
                    listt.add(addressBookContact);
                }
                int type = cursor.getInt(typeIdx);
                String data = cursor.getString(dataIdx);
                String mimeType = cursor.getString(mimeTypeIdx);
                if (mimeType.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                    // mimeType == ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
                    addressBookContact.addEmail(type, data);
                } else {
                    // mimeType == ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                    addressBookContact.addPhone(type, data);
                }

            }
            cursor.close();


            for (AddressBookContact addressBookContact : listt) {
               // if (!addressBookContact.getEmailList().isEmpty()) {
                    PhoneContact.Builder builder = new PhoneContact.Builder();
                    builder.setId(String.valueOf(addressBookContact.getId()))
                            .setDisplayName(addressBookContact.getName())
                            .setEmailAddresses(addressBookContact.getEmailList())
                            .setPhoneNumbers(addressBookContact.getPhonesList());

                    phoneContacts.add(builder.build());
            //    }
            }

            //mCache.saveLocalContacts(phoneContacts);

            JSONArray contactsJson = new JSONArray();
            List<PhoneContact> list = Collections.synchronizedList(phoneContacts);

            int count = 0;
            if (list != null) {
                try {
                    count = list.size();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mCache.putInt(Constants.FRIENDS_COUNT, count);
            //mCache.saveLocalContacts(phoneContacts);

            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    contactsJson.put(phoneContacts.get(i).toJsonObject());
                }

                final String contactsAsJsonStr = contactsJson.toString();
                System.out.println("Contacts upload");
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Rendezvous.getInstance().uploadUserContacts(Config.userType, mCache.getUser().getEmail(), contactsAsJsonStr, new RendezvousRequestListener() {
                            @Override
                            public void onBefore() {

                            }

                            @Override
                            public void onResponse(RendezvousResponse response) {

                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });

                    }
                }).start();
//
//                try {
//                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/contacts.txt"));
//                    bufferedWriter.write(contactsAsJsonStr);
//                    bufferedWriter.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Rendezvous getInstance(){
        try {
            loadConfigData(mContext);
        } catch (RendezvousException e) {
            e.printStackTrace();
        }
        Log.d(TAG, String.valueOf(mRendezvous));
        return mRendezvous;
    }


    public static Rendezvous getInstance(Context context, RendezvousEnvironment rendezvousEnvironment)
    {
        if ( mRendezvous == null ) {
            mRendezvous = new Rendezvous(context, CLIENT_ID, STORE_ID, rendezvousEnvironment);
        }
        return mRendezvous;
    }

    public static Rendezvous getInstance(Context context, String apiKey, RendezvousEnvironment rendezvousEnvironment)
    {
        if ( mRendezvous == null ) {
            mRendezvous = new Rendezvous(context, CLIENT_ID, STORE_ID, apiKey, rendezvousEnvironment);
        }
        return mRendezvous;
    }

    public static Rendezvous getInstance(Context context, String apiKey, String apiSecret, RendezvousEnvironment rendezvousEnvironment)
    {
        if ( mRendezvous == null ) {
            mRendezvous = new Rendezvous(context, CLIENT_ID, STORE_ID, apiKey, apiSecret, rendezvousEnvironment);
        }
        return mRendezvous;
    }

    private static class GetGAIDTask extends AsyncTask<Context, Integer, String> {

        @Override
        protected String doInBackground(Context... context) {
            AdvertisingIdClient.Info adInfo;
            adInfo = null;
            try {
                adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context[0]);
                if (adInfo.isLimitAdTrackingEnabled()) { // check if user has opted out of tracking
                    return "did not found GAID... sorry";
                }
                else{
                    return adInfo.getId();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            }

            return "0000";
        }

        @Override
        protected void onPostExecute(String s) {
            AdId = s;
        }
    }
}
