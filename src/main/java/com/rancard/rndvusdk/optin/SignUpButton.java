package com.rancard.rndvusdk.optin;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;

import com.rancard.rndvusdk.RendezvousResponse;
import com.rancard.rndvusdk.interfaces.RendezvousRequestListener;

/**
 * Created by enoch on 7/11/16.
 */
public class SignUpButton extends AppCompatButton {
    protected OptInManager optInManager;

    public SignUpButton(Context context) {
        super(context);
        setupSignUp();
        setClickable(true);
        setFocusable(true);
    }

    public SignUpButton(Context context, AttributeSet attrs){
        super(context, attrs);
        setupSignUp();
        setClickable(true);
        setFocusable(true);
    }

    public SignUpButton(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        setupSignUp();
        setClickable(true);
        setFocusable(true);
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

    public void registerSignUpListener(RendezvousRequestListener listener){
        optInManager.registerSignUpListener(listener);
    }

    public void signUp(OptInManager.SignUpRequest signRequest){
        optInManager.signUpUser(signRequest);
    }

}
