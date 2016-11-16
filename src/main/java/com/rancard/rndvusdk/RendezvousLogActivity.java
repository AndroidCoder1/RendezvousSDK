package com.rancard.rndvusdk;

/**
 * Created by: Robert Wilson.
 * Date: Feb 02, 2016
 * Time: 4:30 PM
 * Package: com.rancard.rndvusdk
 * Project: Rendezvous SDK
 */
public enum RendezvousLogActivity
{
    VIEW("VIEW"),
    LIKE("LIKE"),
    UNLIKE("UNLIKE"),
    SHARE("SHARE"),
    COMMENT("COMMENT"),
    ATTEMPTED_SIGN_UP("ATTEMPTED_SIGN_UP"),
    FULL_ARTICLE_VIEW("FULL_ARTICLE_VIEW"),
    BUY("BUY"),
    ACCEPT_SHARE("ACCEPT_SHARE"),
    COPY("COPY"),
    SUBSCRIPTION("SUBSCRIPTION"),
    BOOKMARK("SUBSCRIPTION"),
    SIGN_UP("SIGN_UP"),
    SIGN_IN("SIGN_IN"),
    OPEN_APP("OPEN_APP"),
    INSTALL_APP("INSTALL_APP"),
    ATTEMPTED_SIGN_IN("ATTEMPTED_SIGN_IN"), DISLIKE("DISLIKE"), UNDISLIKE("UNDISLIKE"), UNBOOKMARK("UNBOOKMARK");

    private String mActivity;

    RendezvousLogActivity(String signal)
    {
        mActivity = signal;
    }

    public String getActivity()
    {
        return mActivity;
    }

    public static RendezvousLogActivity findByAbbr(String abbr){
        if(abbr != null) {
            for (RendezvousLogActivity v : values()) {
                if (v.name().equals(abbr)) {
                    return v;
                }
            }
        }
        return null;
    }
}
