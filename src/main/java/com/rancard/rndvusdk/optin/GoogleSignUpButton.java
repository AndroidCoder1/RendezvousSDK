package com.rancard.rndvusdk.optin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.rancard.rndvusdk.Rendezvous;
import com.rancard.rndvusdk.RendezvousResponse;
import com.rancard.rndvusdk.interfaces.GoogleSignInCompleteListener;
import com.rancard.rndvusdk.interfaces.RendezvousRequestListener;
import com.rancard.rndvusdk.utils.Constants;

/**
 * Created by enoch on 7/11/16.
 */
public class GoogleSignUpButton extends AppCompatButton implements View.OnClickListener {
    protected OptInManager optInManager;
    private Context context;
    GoogleApiClient mGoogleApiClient;
    GoogleSignInCompleteListener listener;

    public GoogleSignUpButton(Context context) {
        super(context);
        this.context = context;
        setupSignUp();
        setClickable(true);
        setFocusable(true);
        setOnClickListener(this);
    }

    public GoogleSignUpButton(Context context, AttributeSet attrs){
        super(context, attrs);
        this.context = context;
        setupSignUp();
        setClickable(true);
        setFocusable(true);
        setOnClickListener(this);
    }

    public GoogleSignUpButton(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        this.context = context;
        setupSignUp();
        setClickable(true);
        setFocusable(true);
        setOnClickListener(this);
    }

    private void setupSignUp(){
        optInManager = OptInManager.getInstance();
        optInManager.registerSignUpListener(new RendezvousRequestListener() {
            @Override
            public void onBefore() {
                Log.i("RNDVU", "BEFORE BEFORE");
            }

            @Override
            public void onResponse(RendezvousResponse rendezvousResponse) {
                Log.i("RNDVU", rendezvousResponse.getBody());
            }

            @Override
            public void onError(Exception e) {
                if (e!= null) e.printStackTrace();
            }
        });
    }



    public void signUp(OptInManager.SignUpRequest signRequest){
        optInManager.signUpUser(signRequest);
    }

    void setGoogleSignInCompleteListener(GoogleSignInCompleteListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        Rendezvous.loginFrom = Constants.LOGIN_FROM_GOOGLE;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        logOut();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        ((Activity)context).startActivityForResult(signInIntent, Constants.RC_SIGN_IN);
    }

    public void logOut(){

        if(mGoogleApiClient.isConnected()) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            //clearLocalSignInDetails();
                        }
                    });
        }
    }

}
