package com.rancard.rndvusdk;

/**
 * Created by: Robert Wilson.
 * Date: Feb 02, 2016
 * Time: 12:42 PM
 * Package: com.rancard.rndvusdk
 * Project: Rendezvous SDK
 */
public enum RendezvousEnvironment
{
    PRODUCTION("Production"),
    STAGING("Staging"),
    SANDBOX("Staging"),
    PRODUCTION_MIRROR("Production Mirror");

    private String mSimpleName;

    RendezvousEnvironment(String simpleName)
    {
        mSimpleName = simpleName;
    }

    public String getSimpleName()
    {
        return mSimpleName;
    }
}
