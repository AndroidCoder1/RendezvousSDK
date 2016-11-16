package com.rancard.rndvusdk.optin;

import com.rancard.rndvusdk.Rendezvous;
import com.rancard.rndvusdk.exceptions.RendezvousException;
import com.rancard.rndvusdk.exceptions.RendezvousSdkNotInitializedException;
import com.rancard.rndvusdk.interfaces.RendezvousRequestListener;

import lombok.Data;

/**
 * Created by enoch on 7/11/16.
 */
public class OptInManager {

    private static volatile OptInManager instance;
    private static Rendezvous rendezvous;
    private RendezvousRequestListener signUplistener, loginlistener;

    public static OptInManager getInstance(){
        if (instance == null){
            synchronized (OptInManager.class){
                if (instance == null){
                    instance = new OptInManager();
                }
            }
        }

        return instance;
    }

    OptInManager(){
        if (!Rendezvous.isInitialized()){
            throw new RendezvousSdkNotInitializedException("Rendezvous.initializeSdk() has not been called first");
        }
        rendezvous = Rendezvous.getInstance();
        if (rendezvous == null){
            throw new RendezvousException("Rendezvous instance is NULL");
        }
    }

    public void registerSignUpListener(RendezvousRequestListener signUplistener){
        this.signUplistener = signUplistener;
    }

    public void signUpUser(String password, String userId, String userType, String fullname, String avatarUrl){
        rendezvous.signUp(password, userId, userType, fullname, avatarUrl, this.signUplistener);
    }

    public void signUpUser(SignUpRequest signUpRequest){
        rendezvous.signUp(signUpRequest.getPassword(),
                signUpRequest.getEmail(),
                "email",
                signUpRequest.getFullName(),
                signUpRequest.getAvatarUrl(),
                this.signUplistener);
    }

    @Data
    public static class SignUpRequest {
        private String msisdn = "";
        private String password = "";
        private String email = "";
        private String fullName = "";
        private String avatarUrl = "";

        public String getMsisdn() {
            return msisdn;
        }

        public void setMsisdn(String msisdn) {
            this.msisdn = msisdn;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }
    }
}
