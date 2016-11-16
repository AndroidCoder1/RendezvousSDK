package com.rancard.rndvusdk.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Robert Wilson.
 * Date: Feb 26, 2016
 * Time: 11:21 AM
 * Package: com.rancard.rndvusdk.models
 * Project: Rendezvous-Android
 */
public class PhoneContact implements Serializable,Comparable<PhoneContact>
{
    private final String id;
    private final String displayName;
    private final List<String> emailAddresses;
    private final List<String> phoneNumbers;

    private PhoneContact(Builder builder) {
        this.id = builder.id;
        this.displayName = builder.displayName;
        this.emailAddresses = builder.emailAddresses;
        this.phoneNumbers = builder.phoneNumbers;
    }

    public PhoneContact(){
        this.id = "";
        this.emailAddresses = new ArrayList<>();
        this.phoneNumbers = new ArrayList<>();
        this.displayName = "";
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getEmailAddresses() {
        return emailAddresses;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public String getPrimaryPhoneNumber() {
        return (phoneNumbers.size() > 0) ? phoneNumbers.get(0) : "";
    }

    @Override
    public String toString() {
        return getDisplayName();
    }

    public String toJson() {
        try {
            JSONObject contactJson = new JSONObject();
            contactJson.put("id", getId());
            contactJson.put("rawId", getId());
            contactJson.put("displayName", getDisplayName());
            contactJson.put("nickname", getDisplayName());

            JSONObject nameJson = new JSONObject();
            nameJson.put("formatted", getDisplayName());

            contactJson.put("name", nameJson);

            JSONArray phoneNumbers = new JSONArray();
            JSONArray emailAddresses = new JSONArray();

            if (getPhoneNumbers() != null && getPhoneNumbers().size() > 0) {
                JSONObject msisdns = null;
                for (int i = 0; i < getPhoneNumbers().size(); i++) {
                    msisdns = new JSONObject();
                    msisdns.put("type", "mobile");
                    msisdns.put("value", getPhoneNumbers().get(i));

                    phoneNumbers.put(i, msisdns);
                }
            }

            contactJson.put("phoneNumbers", phoneNumbers);

            if (getEmailAddresses() != null && getEmailAddresses().size() > 0) {
                JSONObject emails = null;
                for (int i = 0; i < getEmailAddresses().size(); i++) {
                    emails = new JSONObject();
                    emails.put("type", "other");
                    emails.put("value", getEmailAddresses().get(i));

                    emailAddresses.put(i, emails);
                }
            }

            contactJson.put("emails", emailAddresses);

            return contactJson.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject toJsonObject() {
        try {
            JSONObject contactJson = new JSONObject();
            contactJson.put("id", getId());
            contactJson.put("rawId", getId());
            contactJson.put("displayName", getDisplayName());
            contactJson.put("nickname", getDisplayName());

            JSONObject nameJson = new JSONObject();
            nameJson.put("formatted", getDisplayName());

            contactJson.put("name", nameJson);

            JSONArray phoneNumbers = new JSONArray();
            JSONArray emailAddresses = new JSONArray();

            if (getPhoneNumbers() != null && getPhoneNumbers().size() > 0) {
                JSONObject msisdns = null;
                for (int i = 0; i < getPhoneNumbers().size(); i++) {
                    msisdns = new JSONObject();
                    msisdns.put("type", "mobile");
                    msisdns.put("value", getPhoneNumbers().get(i));

                    phoneNumbers.put(i, msisdns);
                }
            }

            contactJson.put("phoneNumbers", phoneNumbers);

            if (getEmailAddresses() != null && getEmailAddresses().size() > 0) {
                JSONObject emails = null;
                for (int i = 0; i < getEmailAddresses().size(); i++) {
                    emails = new JSONObject();
                    emails.put("type", "email");
                    emails.put("value", getEmailAddresses().get(i));

                    emailAddresses.put(i, emails);
                }
            }

            contactJson.put("emails", emailAddresses);

            return contactJson;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static PhoneContact asGraphedContact(JSONObject graphResponse) throws JSONException {
        String name = graphResponse.has("name") ? graphResponse.getString("name") : "";
        String msisdn = graphResponse.has("msisdn") ? graphResponse.getString("msisdn") : "";

        List<String> phoneNumbers = new ArrayList<>();
        phoneNumbers.add(msisdn);

        PhoneContact phoneContact = new Builder()
                .setDisplayName(name)
                .setPhoneNumbers(phoneNumbers)
                .setEmailAddresses(new ArrayList<String>())
                .setId(msisdn)
                .build();

        return phoneContact;
    }

    public static PhoneContact fromGraphResponse(JSONObject graphResponse) throws JSONException {
        String id = graphResponse.has("contactId") ? graphResponse.getString("contactId") : "";
        String displayName = graphResponse.has("title") ? graphResponse.getString("title") : "";
        List<String> phoneNumbers = new ArrayList<String>();
        if (graphResponse.has("phoneNumbers")) {
            JSONArray phoneNumbersArray = graphResponse.getJSONArray("phoneNumbers");
            for (int i = 0; i < phoneNumbersArray.length(); i++) {
                phoneNumbers.add(phoneNumbersArray.getString(i));
            }
        }

        List<String> emailAddresses = new ArrayList<String>();

        return new PhoneContact.Builder()
                .setId(id)
                .setDisplayName(displayName)
                .setPhoneNumbers(phoneNumbers)
                .setEmailAddresses(emailAddresses)
                .build();

    }

    @Override
    public int compareTo(PhoneContact phoneContact) {
        return phoneContact.getDisplayName().compareTo(this.getDisplayName());
    }

    public static class Builder {
        private String id;
        private String displayName;
        private List<String> emailAddresses;
        private List<String> phoneNumbers;

        public Builder() {

        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder setEmailAddresses(List<String> emailAddresses) {
            this.emailAddresses = emailAddresses;
            return this;
        }

        public Builder setPhoneNumbers(List<String> phoneNumbers) {
            this.phoneNumbers = phoneNumbers;
            return this;
        }

        public PhoneContact build() {
            return new PhoneContact(this);
        }
    }
}
