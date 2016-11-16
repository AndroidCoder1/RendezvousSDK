package com.rancard.rndvusdk.optin;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.rancard.rndvusdk.Rendezvous;
import com.rancard.rndvusdk.RendezvousResponse;
import com.rancard.rndvusdk.interfaces.RendezvousRequestListener;
import com.rancard.rndvusdk.utils.Constants;

import java.util.Arrays;
import java.util.List;

/**
 * Created by enoch on 7/11/16.
 */
public class FacebookSignUpButton extends AppCompatButton implements View.OnClickListener {
    protected OptInManager optInManager;
    private LoginButton loginButton;
    private Context context;

    public FacebookSignUpButton(Context context) {
        super(context);
        this.context = context;
        setupSignUp();
        setClickable(true);
        setFocusable(true);
        setOnClickListener(this);
    }

    public FacebookSignUpButton(Context context, AttributeSet attrs){
        super(context, attrs);
        this.context = context;
        setupSignUp();
        setClickable(true);
        setFocusable(true);
        setOnClickListener(this);
    }

    public FacebookSignUpButton(Context context, AttributeSet attrs, int defStyle){
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

    @Override
    public void onClick(View v) {
        Rendezvous.loginFrom = Constants.LOGIN_FROM_FACEBOOK;
        LoginManager.getInstance().logOut();
        LoginManager loginButton = LoginManager.getInstance();
        List<String> permissionNeeds = Arrays.asList("email", "public_profile");
        loginButton.logInWithReadPermissions((Activity)context, permissionNeeds);

    }

    public void logOut(){
        LoginManager.getInstance().logOut();
    }

}
