package com.rancard.rndvusdk.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.rancard.rndvusdk.DefaultRendezvousRequestListener;
import com.rancard.rndvusdk.R;
import com.rancard.rndvusdk.Rendezvous;
import com.rancard.rndvusdk.RendezvousLogActivity;
import com.rancard.rndvusdk.RendezvousResponse;
import com.rancard.rndvusdk.interfaces.RendezvousRequestListener;
import com.rancard.rndvusdk.models.User;
import com.rancard.rndvusdk.utils.Constants;
import com.rancard.rndvusdk.utils.MemoryCache;
import com.rancard.rndvusdk.utils.MobileDevice;
import com.rancard.rndvusdk.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

//import com.google.android.gms.auth.api.Auth;
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
//import com.google.android.gms.auth.api.signin.GoogleSignInResult;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GooglePlayServicesUtil;
//import com.google.android.gms.common.api.GoogleApiClient;


public class RendezvousSignUpActivity extends AppCompatActivity
{
    private static final int RC_SIGN_IN = 7;
    Button btnFacebook, btnGoogle;
    CallbackManager callbackManager;
    LoginButton loginButton;
//    ConnectionResult mConnectionResult;
//    private boolean mIntentInProgress;
//    private boolean mSignInClicked;
    ProgressDialog dialog;
    private boolean signOut = false;
    EditText email_et, password_et, fullname_et;
    Button submitBtn;
    MemoryCache cache;
    private int signOutState = 0;
    private EditText fullNameField;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 99;
    private final static String TAG = RendezvousSignUpActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        cache = MemoryCache.getInstance(RendezvousSignUpActivity.this);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        FacebookSdk.setApplicationId(Rendezvous.facebookAppId);
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.rendezvous_sign_up);

//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail().requestProfile()
//                .build();
//
//        mGoogleApiClient = new GoogleApiClient.Builder(SignUpActivity.this)
//                .enableAutoManage(SignUpActivity.this, this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();

        email_et = (EditText) findViewById(R.id.et_email);
        password_et = (EditText) findViewById(R.id.et_password);
        fullname_et = (EditText) findViewById(R.id.et_full_name);
        submitBtn = (Button) findViewById(R.id.btn_to_login);

        btnFacebook = (Button) findViewById(R.id.btn_facebook);
        btnGoogle = (Button) findViewById(R.id.btn_google_plus);
        loginButton = (LoginButton) findViewById(R.id.login_button);


//        Button btnAlreadyHaveAnAccount = (Button) findViewById(R.id.btn_already_have_an_account);
//        btnAlreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//                finish();
//            }
//        });
//
//        Button btnSkip = (Button) findViewById(R.id.btn_skip);
//        btnSkip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                cache.putBool(Constants.HAS_SKIPPED, true);
//                AnalyticsUtils.sendEvent(mTracker, "Sign Up", "Skipped Sign Up");
//                //startActivity(new Intent(com.rancard.rndvusdk.activities.SignUpActivity.this, MainActivity.class));
//                finish();
//            }
//        });


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Rendezvous.getInstance().logActivity("email", cache.getUser().getEmail(), RendezvousLogActivity.ATTEMPTED_SIGN_UP, new DefaultRendezvousRequestListener());

                if(fullname_et.getText().toString().trim().equals("")){
                    fullname_et.setError("Enter Full Name to sign in");
                } else if(password_et.getText().toString().trim().equals("")){
                    password_et.setError("Enter Password to sign in");
                } else if(email_et.getText().toString().trim().equals("")){
                    email_et.setError("Enter Email to sign in");
                } else if(!Utils.isValidEmail(email_et.getText().toString().trim())){
                    email_et.setError("Enter Valid Email");
                }else {
                    User user = new User();
                    user.setProfile("");
                    user.setEmail(email_et.getText().toString().trim());
                    user.setName(fullname_et.getText().toString().trim());
                    user.setGender("");
                    user.setId("000");
                    user.setBirthday("");
                    user.setLoginFrom("custom");
                    cache.storeUser(user);
                    doSignIn(email_et.getText().toString().trim(), password_et.getText().toString().trim(), fullname_et.getText().toString().trim());
                }
            }
        });


        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Rendezvous.getInstance().logActivity("email", cache.getUser().getEmail(), RendezvousLogActivity.ATTEMPTED_SIGN_UP, new DefaultRendezvousRequestListener());
                loginButton.performClick();
            }
        });

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Rendezvous.getInstance().logActivity("email", cache.getUser().getEmail(), RendezvousLogActivity.ATTEMPTED_SIGN_UP, new DefaultRendezvousRequestListener());
                //Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                //startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });


        setUpFacebook();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == RC_SIGN_IN) {
//
//            int hasWriteContactsPermission = 0;
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                hasWriteContactsPermission = checkSelfPermission(Manifest.permission.GET_ACCOUNTS);
//                if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
//                    requestPermissions(new String[]{Manifest.permission.GET_ACCOUNTS},
//                            REQUEST_CODE_ASK_PERMISSIONS);
//                    return;
//                }
//                getGoogleProfileInformation(data);
//            } else {
//
//                getGoogleProfileInformation(data);
//            }
//
//        } else{

            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);

//        }
    }




    private void setUpFacebook(){
        List<String> permissionNeeds = Arrays.asList("email");
        loginButton.setReadPermissions(permissionNeeds);
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

                                        getFaceBookProfileInformation(object);
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender,birthday");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        System.out.println("onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        System.out.println("onError");
                        FacebookSdk.sdkInitialize(getApplicationContext());
                        LoginManager.getInstance().logOut();
                        Log.v("LoginActivity", String.valueOf(exception.getCause()));
                        try {
                            Log.v("LoginActivity", exception.getCause().toString());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
//        if(mGoogleApiClient != null) {
//            if (mGoogleApiClient.isConnected()) {
//                mGoogleApiClient.disconnect();
//            }
//        }
    }






//    @Override
//    public void onConnectionFailed(ConnectionResult result) {
//        try {
//            if (!result.hasResolution()) {
//                GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
//                return;
//            }
//            if (!mIntentInProgress) {
//                mConnectionResult = result;
//            }
//        }catch (Exception e){
//
//        }
//    }
//
//
//    private void getGoogleProfileInformation(Intent data) {
//        try {
//            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            if (result.isSuccess()) {
//                GoogleSignInAccount acct = result.getSignInAccount();
//                String name = acct.getDisplayName();
//                String profile = "";
//                if(acct.getPhotoUrl() != null) {
//                    profile = acct.getPhotoUrl().toString();
//                }
//                String birthday = "";
//                String id = acct.getId();
//                String gender = "";
////            if (gplusUser.getGender() == 0)
////                gender = "Male";
////            else if (gplusUser.getGender() == 1)
////                gender = "Female";
////            else
//                gender = "Other";
//                String email = acct.getEmail();
//
//                User user = new User();
//                user.setProfile(profile);
//                user.setEmail(email);
//                user.setName(name);
//                user.setGender(gender);
//                user.setId(id);
//                user.setBirthday(birthday);
//                user.setLoginFrom("google");
//                cache.storeUser(user);
//
//                doSignIn(email, id, name);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            // MobileDevice.PerformCommonActions.showToastMessage(S);
//        }
//
//    }





    private void getFaceBookProfileInformation(JSONObject object){

        String id = "", profile = "", name = "", email = "", gender = "", birthday = "";

        if(object != null) {
            try {
                id = object.getString("id");
                profile = "http://graph.facebook.com/" + id + "/picture?type=large";
                name = object.getString("name");
                email = object.getString("email");
                gender = object.getString("gender");
                birthday = object.getString("birthday");


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        User user = new User();
        user.setProfile(profile);
        user.setEmail(email);
        user.setName(name);
        user.setGender(gender);
        user.setId(id);
        user.setBirthday(birthday);
        user.setLoginFrom("facebook");
        cache.storeUser(user);
        doSignIn(email, id, name);
    }


    private void doSignIn(final String email, final String password, final String fullname){

        new Thread(new Runnable() {
            @Override
            public void run() {


                Rendezvous.getInstance().signUp(password, email, "email", fullname, cache.getSocialAvatarUrl(), new RendezvousRequestListener() {
                    @Override
                    public void onBefore() {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    dialog = ProgressDialog.show(RendezvousSignUpActivity.this, "", "Please wait...", true, false);
                                }catch (Exception e){

                                }
                            }
                        });

                    }

                    @Override
                    public void onResponse(final RendezvousResponse response) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    dialog.dismiss();
                                }catch (Exception e){

                                }
                                validate(response.getBody());
                                System.out.println("Hash " + response.getBody());
                            }
                        });


                    }

                    @Override
                    public void onError(final Exception e) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if(cache.getUser().getLoginFrom().equalsIgnoreCase("facebook")){
                                    FacebookSdk.sdkInitialize(getApplicationContext());
                                    LoginManager.getInstance().logOut();
                                }
                                try {
                                    System.out.println("Hash " + e.getMessage());
                                    dialog.dismiss();
                                }catch (Exception e){

                                }
                            }
                        });

                    }
                });


            }
        }).start();


    }

    private void validate(String response) {

        String res = response;
        if(res != null){
            if(!res.isEmpty()){
                try {
                    JSONObject jObj = new JSONObject(res);
                    if(jObj.has("data")) {
                        JSONArray arr = jObj.getJSONArray("data");
                        if(arr != null){
                            if(arr.length() > 0){
                                String jj = arr.getJSONObject(0).getString("signUpStatus");
                                if(jj.equalsIgnoreCase("SIGN UP")){

                                    cache.putBool(Constants.HAS_SIGNED_IN, true);
                                    cache.putBool(Constants.RE_SIGN_IN, true);

                                    Rendezvous.getInstance().logActivity( "email", cache.getUser().getEmail(), RendezvousLogActivity.SIGN_UP, new DefaultRendezvousRequestListener());

                                    if(!cache.getBool(Constants.HAS_SKIPPED)) {

                                        Intent intent = new Intent(Constants.MAIN_DEFAULT_ACTIVITY);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();

                                    }else{
                                        cache.putBool(Constants.HAS_SKIPPED, false);
                                        finish();
                                    }

                                }else if(jj.equalsIgnoreCase("SIGN IN")){

                                    MobileDevice.PerformCommonActions.showMessageDialogWithListener(RendezvousSignUpActivity.this, "ACCOUNT ALREADY EXISTS", "You already have an account with"+ Utils.getApplicationName(RendezvousSignUpActivity.this)+". We are logging you in instead.", new MobileDevice.PerformCommonActions.YesNoDialogListener() {
                                        @Override
                                        public void onYesClicked(DialogInterface dialog, int which) {

                                            Rendezvous.getInstance().logActivity("email", cache.getUser().getEmail(), RendezvousLogActivity.SIGN_UP, new DefaultRendezvousRequestListener());

                                            if(!cache.getBool(Constants.HAS_SKIPPED)) {

                                                Intent intent = new Intent(Constants.MAIN_DEFAULT_ACTIVITY);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();

                                            }else{
                                                cache.putBool(Constants.HAS_SKIPPED, false);
                                                finish();
                                            }
                                        }

                                        @Override
                                        public void onNoClicked(DialogInterface dialog, int which) {


                                        }
                                    });
                                }

                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_CODE_ASK_PERMISSIONS:
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                 //   getGoogleProfileInformation();
//                } else {
//                    // Permission Denied
//                    Toast.makeText(SignUpActivity.this, "Get Accounts failed... Please Grant permission", Toast.LENGTH_SHORT)
//                            .show();
//                }
//                break;
//            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }

//    private static Scope buildScope() {
//        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
//    }

}
