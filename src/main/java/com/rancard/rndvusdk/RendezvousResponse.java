package com.rancard.rndvusdk;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Response;

/**
 * Created by: Robert Wilson.
 * Date: Feb 02, 2016
 * Time: 7:11 PM
 * Package: com.rancard.rndvusdk
 * Project: Rendezvous SDK
 */
public class RendezvousResponse
{
    private final String mBody;
    private final long mContentLength;
    private final String mContentType;
    private final String mMessage;
    private final Headers mHeaders;
    private final int mCode;
    private final boolean isSuccessful;
    private final boolean isRedirect;

    private RendezvousResponse(Response response) throws IOException
    {
        mBody = response.body().string();
        mContentLength = response.body().contentLength();
        mContentType = response.body().contentType().toString();
        mMessage = response.message();
        mCode = response.code();
        isSuccessful = response.isSuccessful();
        isRedirect = response.isRedirect();
        mHeaders = response.headers();
    }

    public String getBody()
    {
        return mBody;
    }

    public int getCode()
    {
        return mCode;
    }

    public long getContentLength()
    {
        return mContentLength;
    }

    public String getContentType()
    {
        return mContentType;
    }

    public Headers getHeaders()
    {
        return mHeaders;
    }

    public String getMessage()
    {
        return mMessage;
    }

    public boolean isRedirect()
    {
        return isRedirect;
    }

    public boolean isSuccessful()
    {
        return isSuccessful;
    }

    public static RendezvousResponse transform(Response response) throws Exception
    {
        return new RendezvousResponse(response);
    }
}
