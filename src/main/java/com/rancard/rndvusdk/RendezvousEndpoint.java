package com.rancard.rndvusdk;

import android.util.Log;

/**
 * Created by: Robert Wilson.
 * Date: Feb 02, 2016
 * Time: 12:27 PM
 * Package: com.rancard.rndvusdk
 * Project: Rendezvous SDK
 */
public class RendezvousEndpoint
{
    private String mScheme;
    private String mHost;
    private int mPort;
    private String mPath;
    private String mApiVersion = "/v1";
    public  static String rccHost = "";

    private String mUrl;

    public RendezvousEndpoint(RendezvousEnvironment environment)
    {
        initializeEnvironment(environment);
    }

    private void initializeEnvironment(RendezvousEnvironment environment)
    {
        if (environment != null) {
            switch (environment) {
                case PRODUCTION:
                    mScheme = Production.SCHEME;
                    mHost = Production.HOST;
                    mPort = Production.PORT;
                    mPath = Production.PATH;
                    rccHost = Production.RCC_HOST;
                    mUrl = mScheme + "://" + mHost + (mPort > -1 ? ":" + mPort : "") + mPath + mApiVersion;
                    Log.d("PRODUCTION URL: ", mUrl);
                    break;
                case SANDBOX:
                    mScheme = Sandbox.SCHEME;
                    mHost = Sandbox.HOST;
                    mPort = Sandbox.PORT;
                    mPath = Sandbox.PATH;
                    rccHost = Sandbox.RCC_HOST;
                    mUrl = mScheme + "://" + mHost + (mPort > -1 ? ":" + mPort : "") + mPath + mApiVersion;
                    Log.d("SANDBOX URL: ", mUrl);
                    break;
                case STAGING:
                    mScheme = Staging.SCHEME;
                    mHost = Staging.HOST;
                    mPort = Staging.PORT;
                    mPath = Staging.PATH;
                    mUrl = mScheme + "://" + mHost + (mPort > -1 ? ":" + mPort : "") + mPath + mApiVersion;
                    Log.d("STAGING URL: ", mUrl);
                    break;

                case PRODUCTION_MIRROR:
                    mScheme = ProductionMirror.SCHEME;
                    mHost = ProductionMirror.HOST;
                    mPort = ProductionMirror.PORT;
                    mPath = ProductionMirror.PATH;
                    mUrl = mScheme + "://" + mHost + (mPort > -1 ? ":" + mPort : "") + mPath + mApiVersion;
                    Log.d("PRODUCTION_MIRROR URL: ", mUrl);
                    break;

            }
        }
    }

    public String getRoute(RendezvousRoute rendezvousRoute)
    {
        return rendezvousRoute != null ? mUrl + rendezvousRoute.getRoute() : null;
    }

    public String getRoute(RendezvousRoute rendezvousRoute, Object ...params)
    {
        return rendezvousRoute != null ? mUrl + String.format(rendezvousRoute.getRoute(), params) : null;
    }

    public String getApiVersion()
    {
        return mApiVersion;
    }

    public String getHost()
    {
        return mHost;
    }

    public int getPort()
    {
        return mPort;
    }

    public String getUrl()
    {
        return mUrl;
    }

    public String getScheme()
    {
        return mScheme;
    }

    public String getPath()
    {
        return mPath;
    }

    private static class Sandbox
    {
        private static final String SCHEME = "http";
        private static final String HOST = "sandbox.rancardmobility.com";
        private static final int PORT = 9095;
        private static final String PATH = "/sdk";
        private static final String RCC_HOST = "http://sandbox.rancardmobility.com:9006";
    }

    private static class Production
    {
        private static final String SCHEME = "https";
        private static final String HOST = "api.rancardmobility.com";
        private static final int PORT = -1;
        private static final String PATH = "/sdk";
        private static final String RCC_HOST = "http://rcc.rancardmobility.com";
    }

    private static class ProductionMirror
    {
        private static final String SCHEME = "http";
        private static final String HOST = "api-test.rancardmobility.com";
        private static final int PORT = -1;
        private static final String PATH = "";
    }

    private static class Staging
    {
        private static final String SCHEME = "https";
        private static final String HOST = "staging.rndvu.me";
        private static final int PORT = -1;
        private static final String PATH = "";
    }

}
