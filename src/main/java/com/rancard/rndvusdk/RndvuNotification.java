package com.rancard.rndvusdk;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.rancard.rndvusdk.utils.Constants;
import com.rancard.rndvusdk.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Edward on 9/17/14.
 */
// Deprecated
public class RndvuNotification {
    public static final String DISPLAY_MODE_TOAST = "toast";
    public static final String DISPLAY_MODE_SYSTEM_TRAY = "tray";
    public static final String DISPLAY_MODE_PROMPT = "prompt";
    public static final String DISPLAY_MODE_SS_INTERVENTION = "intervention";

    public static final String ACTION_INTENT_LAUNCH_EXTERNAL_BROWSER = "external_browser";
    public static final String ACTION_INTENT_LAUNCH_HASHING = "hash-";
    public static final String ACTION_INTENT_LAUNCH_RNDVU_BROWSER = "rndvu_browser";
    public static final String ACTION_INTENT_LAUNCH_RNDVU_WIDGET = "rndvu_widget";
    public static final String ACTION_INTENT_LAUNCH_ACTIVITY = "launch_activity";
    public static final String ACTION_INTENT_APP_INSTALL = "app_install";
    public static final String ACTION_INTENT_READ_NEWS = "read_news";
    public static final String ACTION_INTENT_SS_INTERVENTION = "intervention";

    public static final String NOTIFICATION_PAYLOAD_DISPLAY = "display";
    public static final String NOTIFICATION_PAYLOAD_ACTION = "action";
    public static final String NOTIFICATION_PAYLOAD_TITLE = "title";
    public static final String NOTIFICATION_PAYLOAD_MESSAGE = "message";
    public static final String NOTIFICATION_PAYLOAD_BUNDLE = "bundle";
    public static final String NOTIFICATION_PAYLOAD_ACK_URL = "ackUrl";
    public static final String NOTIFICATION_PAYLOAD_ONCLICK_URL = "onClickUrl";
    public static final String NOTIFICATION_PAYLOAD_ITEM_ID = "itemId";
    public static final String NOTIFICATION_PAYLOAD_IS_SOCIAL = "isSocial";

    public static Map<String, DisplayMode> displayModeMap = null;
    public static Map<String, ActionIntent> actionIntentMap = null;

    public static RndvuNotification latestNotification = null;

    static {
        displayModeMap = new HashMap<String, DisplayMode>();
        displayModeMap.put(DISPLAY_MODE_TOAST, DisplayMode.TOAST);
        displayModeMap.put(DISPLAY_MODE_SYSTEM_TRAY, DisplayMode.SYSTEM_TRAY);
        displayModeMap.put(DISPLAY_MODE_PROMPT, DisplayMode.PROMPT);
        displayModeMap.put(DISPLAY_MODE_SS_INTERVENTION, DisplayMode.SS_INTERVENTION);

        actionIntentMap = new HashMap<String, ActionIntent>();
        actionIntentMap.put(ACTION_INTENT_LAUNCH_EXTERNAL_BROWSER, ActionIntent.EXTERNAL_BROWSER);
        actionIntentMap.put(ACTION_INTENT_LAUNCH_RNDVU_BROWSER, ActionIntent.RNDVU_BROWSER);
        actionIntentMap.put(ACTION_INTENT_LAUNCH_ACTIVITY, ActionIntent.LAUNCH_ACTIVITY);
        actionIntentMap.put(ACTION_INTENT_READ_NEWS, ActionIntent.READ_NEWS);
        actionIntentMap.put(ACTION_INTENT_APP_INSTALL, ActionIntent.APP_INSTALL);
        actionIntentMap.put(ACTION_INTENT_SS_INTERVENTION, ActionIntent.SS_INTERVENTION);
    }

    public enum DisplayMode {
        TOAST(DISPLAY_MODE_TOAST),
        SYSTEM_TRAY(DISPLAY_MODE_SYSTEM_TRAY),
        PROMPT(DISPLAY_MODE_PROMPT),
        SS_INTERVENTION(DISPLAY_MODE_SS_INTERVENTION);

        private DisplayMode(String tag) {
            this.tag = tag;
        }

        private String tag;

        public String getTag() {
            return tag;
        }
    }

    public enum ActionIntent {
        EXTERNAL_BROWSER(ACTION_INTENT_LAUNCH_EXTERNAL_BROWSER),
        RNDVU_BROWSER(ACTION_INTENT_LAUNCH_RNDVU_BROWSER),
        RNDVU_WIDGET(ACTION_INTENT_LAUNCH_RNDVU_BROWSER),
        LAUNCH_ACTIVITY(ACTION_INTENT_LAUNCH_ACTIVITY),
        READ_NEWS(ACTION_INTENT_READ_NEWS),
        APP_INSTALL(ACTION_INTENT_APP_INSTALL),
        SS_INTERVENTION(ACTION_INTENT_SS_INTERVENTION);

        private ActionIntent(String tag) {
            this.tag = tag;
        }

        private String tag;

        public String getTag() {
            return tag;
        }
    }


    private final DisplayMode displayMode;
    private final ActionIntent actionIntent;
    private final String title;
    private final String message;
    private final String bundle;
    private final String ackUrl;
    private final String onClickUrl;
    private final Intent effectiveIntent;
    private final boolean isSocial;
        private final String itemId;


    private RndvuNotification(DisplayMode displayMode, ActionIntent actionIntent, String title, String message,
                              String bundle, String ackUrl, String onClickUrl, boolean isSocial, String itemId) {
        this.displayMode = displayMode;
        this.actionIntent = actionIntent;
        this.title = title;
        this.message = message;
        this.bundle = bundle;
        this.ackUrl = ackUrl;
        this.onClickUrl = onClickUrl;
        this.effectiveIntent = null;
        this.isSocial = isSocial;
        this.itemId = itemId;
    }

    public DisplayMode getDisplayMode() {
        return displayMode;
    }

    public ActionIntent getActionIntent() {
        return actionIntent;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getBundle() {
        return bundle;
    }

    public String getAckUrl() {
        return ackUrl;
    }

    public String getOnClickUrl() {
        return onClickUrl;
    }

    public boolean isSocial() {
        return isSocial;
    }

    public String getItemId() {
        return itemId;
    }

    public Intent getEffectiveIntent(Context context) {
        Intent effectiveIntent = null;

        switch (getActionIntent()) {
            case EXTERNAL_BROWSER:
                effectiveIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getBundle()));
                break;
            case RNDVU_BROWSER:
                effectiveIntent = new Intent("com.rancard.rndvusdk.view", Uri.parse(getBundle()));
                effectiveIntent.putExtra(Constants.URL, getBundle());
                effectiveIntent.putExtra(Constants.TITLE, getMessage());
                effectiveIntent.putExtra(Constants.ITEM_iD,getItemId());
                effectiveIntent.putExtra(Constants.DESCRIPTION,getMessage());
                if (getOnClickUrl() != null) {
                    if(!getOnClickUrl().isEmpty()) {
                        effectiveIntent.putExtra(Constants.ON_CLICK_URL, getOnClickUrl());
                    }
                }
                break;
            case APP_INSTALL:
                //Rndvu.getClient().logEvent(Rndvu.msisdn,Rndvu.email,"OPEN_APP_GCM",getItemId(),Rndvu.STORE_ID,new DefaultRndvuRequestListener());
                effectiveIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getBundle()));
                break;
            case READ_NEWS:
                if(getItemId().toString().equals("SMS")){
                    //Rndvu.getClient().logEvent(Rndvu.msisdn,Rndvu.email,"OPEN_APP_SMS",getItemId(),Rndvu.STORE_ID,new DefaultRndvuRequestListener());
                    effectiveIntent = new Intent("com.rancard.sdk.joy.ACTION_READ_NEWS_FROM_SMS", Uri.parse(getBundle()));
                    effectiveIntent.putExtra(Constants.URL, getBundle());
                    effectiveIntent.putExtra(Constants.TITLE, getTitle());
                    effectiveIntent.putExtra(Constants.ITEM_iD,getItemId());
//                    if (!Strings.isNullOrEmpty(getOnClickUrl())) {
//                        effectiveIntent.putExtra(Constants.ON_CLICK_URL, getOnClickUrl());
//                    }


                }else {
                    //Rndvu.getClient().logEvent(Rndvu.msisdn,Rndvu.email,"OPEN_APP_GCM",getItemId(),Rndvu.STORE_ID,new DefaultRndvuRequestListener());
                    effectiveIntent = new Intent("com.rancard.sdk.joy.ACTION_READ_NEWS", Uri.parse(getBundle()));
                    effectiveIntent.putExtra(Constants.URL, getBundle());
                    effectiveIntent.putExtra(Constants.TITLE, getTitle());
                    effectiveIntent.putExtra(Constants.ITEM_iD, getItemId());
                    effectiveIntent.putExtra(Constants.DESCRIPTION,getMessage());
//                    if (!Strings.isNullOrEmpty(getOnClickUrl())) {
//                        effectiveIntent.putExtra(Constants.ON_CLICK_URL, getOnClickUrl().replace("<MSISDN>", Rndvu.msisdn));
//                    }
                }
                break;
            case LAUNCH_ACTIVITY:
                break;
            default:


                break;
        }
        effectiveIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        return effectiveIntent;
    }

    public static RndvuNotification fromSmsPayload(String smsBody) {
        RndvuNotification rndvuNotification = null;
        DisplayMode displayMode = DisplayMode.SYSTEM_TRAY;
        ActionIntent actionIntent = ActionIntent.READ_NEWS;

        String title = Rendezvous.smsNotificationTitle;
        List<String> urls = Utils.extractUrlsFromText(smsBody);
        String bundle ="";
        try {
            bundle = (urls != null) ? urls.get(0) : "";
        }catch (Exception e){
            bundle = "";
        }
        String ackUrl = "";
        String onClickUrl = "";
        String itemId = "SMS";
        boolean isSocial = false;

        rndvuNotification = new RndvuNotification(displayMode, actionIntent, title, smsBody, bundle, ackUrl, onClickUrl, false, itemId);

        return rndvuNotification;
    }

    public static RndvuNotification fromGCMPayload(String gcmPayload) {
        RndvuNotification rndvuNotification = null;
        DisplayMode displayMode = DisplayMode.SYSTEM_TRAY;
        ActionIntent actionIntent = ActionIntent.READ_NEWS;

        String title = "";
        String message = "";
        String bundle = "";
        String ackUrl = "";
        String onClickUrl = "";
        boolean isSocial = false;
        String itemId = "";

        try {
            JSONObject payload = new JSONObject(gcmPayload);
            if (payload != null) {
                String display = (payload.has(NOTIFICATION_PAYLOAD_DISPLAY)) ? payload.getString(NOTIFICATION_PAYLOAD_DISPLAY) : "";
                String action = (payload.has(NOTIFICATION_PAYLOAD_ACTION)) ? payload.getString(NOTIFICATION_PAYLOAD_ACTION) : "";
                title = (payload.has(NOTIFICATION_PAYLOAD_TITLE)) ? payload.getString(NOTIFICATION_PAYLOAD_TITLE) : "";
                message = (payload.has(NOTIFICATION_PAYLOAD_MESSAGE)) ? payload.getString(NOTIFICATION_PAYLOAD_MESSAGE) : "";
                bundle = (payload.has(NOTIFICATION_PAYLOAD_BUNDLE)) ? payload.getString(NOTIFICATION_PAYLOAD_BUNDLE) : "";
                ackUrl = (payload.has(NOTIFICATION_PAYLOAD_ACK_URL)) ? payload.getString(NOTIFICATION_PAYLOAD_ACK_URL) : "";
                onClickUrl = (payload.has(NOTIFICATION_PAYLOAD_ONCLICK_URL)) ? payload.getString(NOTIFICATION_PAYLOAD_ONCLICK_URL) : "";
                isSocial = (payload.has(NOTIFICATION_PAYLOAD_IS_SOCIAL)) ? payload.getBoolean(NOTIFICATION_PAYLOAD_IS_SOCIAL) : false;
                itemId = (payload.has(NOTIFICATION_PAYLOAD_ITEM_ID)) ? payload.getString(NOTIFICATION_PAYLOAD_ITEM_ID) : "";

                displayMode = RndvuNotification.displayModeMap.get(display);
                if (displayMode == null) {
                    displayMode = DisplayMode.SYSTEM_TRAY;
                }

                actionIntent = RndvuNotification.actionIntentMap.get(action);
                if (actionIntent == null) {
                    actionIntent = ActionIntent.READ_NEWS;
                }

                rndvuNotification = new RndvuNotification(displayMode, actionIntent, title, message, bundle, ackUrl.replace("<MSISDN>",Rendezvous.msisdn),
                        onClickUrl.replace("<MSISDN>",Rendezvous.msisdn), isSocial,itemId);

            }
        } catch (JSONException jsonException) {
            rndvuNotification = null;
            Log.i(RndvuNotification.class.getName(), jsonException.getMessage());
        }

        if (title == null || title.isEmpty()) {
            title = "";
        }

        if (message == null || message.isEmpty()) {
            message = "";
        }

        if (bundle == null || bundle.isEmpty()) {
            bundle = "";
        }

        if (ackUrl == null || ackUrl.isEmpty()) {
            ackUrl = "";
        }

        if (onClickUrl == null || onClickUrl.isEmpty()) {
            onClickUrl = "";
        }

        if (itemId == null || itemId.isEmpty()) {
            itemId = "";
        }

        if (rndvuNotification == null) {
            rndvuNotification = new RndvuNotification(displayMode, actionIntent, title, message, bundle, ackUrl,
                    onClickUrl, isSocial,itemId);
        }

        RndvuNotification.latestNotification = rndvuNotification;

        return rndvuNotification;
    }
}
