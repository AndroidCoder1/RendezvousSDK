package com.rancard.rndvusdk.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.rancard.rndvusdk.Config;
import com.rancard.rndvusdk.DefaultRendezvousRequestListener;
import com.rancard.rndvusdk.Rendezvous;
import com.rancard.rndvusdk.RendezvousLogActivity;
import com.rancard.rndvusdk.interfaces.GoogleCallbackManager;
import com.rancard.rndvusdk.interfaces.GoogleSignInCompleteListener;
import com.rancard.rndvusdk.interfaces.RendezvousSignUpListener;
import com.rancard.rndvusdk.models.UserProfile;
import com.rancard.rndvusdk.optin.OptInManager;
import com.rancard.rndvusdk.utils.MemoryCache;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;


public class RndvuSignUpActivity extends AppCompatActivity
{
    public static CallbackManager callbackManager;
    public static LoginManager loginButton;
    MemoryCache cache;
    GoogleSignInCompleteListener listener;
    static UserProfile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        cache = MemoryCache.getInstance(RndvuSignUpActivity.this);

        if (Config.facebookLogin) {
            FacebookSdk.sdkInitialize(this.getApplicationContext());
            FacebookSdk.setApplicationId(Rendezvous.facebookAppId);
            callbackManager = CallbackManager.Factory.create();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(Config.googleLogin) {
            userProfile = GoogleCallbackManager.onActivityResult(requestCode, data);
            if (userProfile != null) {
                EventBus.getDefault().post("");
            }
        }
        if(Config.facebookLogin) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    public void registerSignUpListener(final RendezvousSignUpListener listener){

        if(Config.googleLogin){

            setCustomButtonListner(new GoogleSignInCompleteListener() {
                @Override
                public void callback(UserProfile userProfile) {
                    if(userProfile != null) {
                        cache.storeUserProfile(userProfile);
                        listener.onResponse(userProfile);
                        signUpRendezvousUser();
                    }else{
                        listener.onError(new Exception("Google Authentication failed"));
                    }
                }
            });

        }
        if(Config.facebookLogin) {
            loginButton = LoginManager.getInstance();
            System.out.println(callbackManager.toString() + " " + FacebookSdk.getApplicationId());
            loginButton.registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {

                            System.out.println("onSuccess");

                            String accessToken = loginResult.getAccessToken()
                                    .getToken();
                            Log.i("accessToken", accessToken);

                            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),

                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(JSONObject object, GraphResponse response) {
                                            Log.i("LoginActivity", response.toString());
                                            UserProfile userProfile = new UserProfile(object);
                                            cache.storeUserProfile(userProfile);
                                            listener.onResponse(userProfile);
                                            signUpRendezvousUser();

                                        }
                                    });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,email,gender,birthday");
                            request.setParameters(parameters);
                            request.executeAsync();
                        }

                        @Override
                        public void onCancel() {
                            AccessToken token = AccessToken.getCurrentAccessToken();
                            if (token != null) {
                                GraphRequest request = GraphRequest.newMeRequest(
                                        token,
                                        new GraphRequest.GraphJSONObjectCallback() {
                                            @Override
                                            public void onCompleted(
                                                    JSONObject object,
                                                    GraphResponse response) {
                                                UserProfile userProfile = new UserProfile(object);
                                                cache.storeUserProfile(userProfile);
                                                listener.onResponse(userProfile);
                                                signUpRendezvousUser();

                                            }
                                        });
                                Bundle parameters = new Bundle();
                                parameters.putString("fields", "id,name,email,gender, birthday");
                                request.setParameters(parameters);
                                request.executeAsync();
                            }
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            listener.onError(exception);
                            FacebookSdk.sdkInitialize(getApplicationContext());
                            LoginManager.getInstance().logOut();
                        }
                    });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
   public void onEvent(String e){
        listener.callback(userProfile);
     }

    public void setCustomButtonListner(GoogleSignInCompleteListener listener) {
        this.listener = listener;
    }

    public void signUpRendezvousUser(){
        OptInManager optInManager = OptInManager.getInstance();
        optInManager.signUpUser("",cache.getUserProfile().getEmail(),"email",cache.getUserProfile().getName(),cache.getUserProfile().getProfile());
        Rendezvous.getInstance().logActivity("email",cache.getUserProfile().getEmail(), RendezvousLogActivity.SIGN_UP,new DefaultRendezvousRequestListener());
        Rendezvous.getInstance().uploadUserContacts(RndvuSignUpActivity.this);
    }
}
