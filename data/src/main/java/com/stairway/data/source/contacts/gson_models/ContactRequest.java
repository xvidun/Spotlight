package com.stairway.data.source.contacts.gson_models;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.stairway.data.source.contacts.ContactResult;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vidhun on 09/12/16.
 */

public class ContactRequest {
    @SerializedName("phone_contacts")
    @Expose
    private String contacts;

    public ContactRequest(List<ContactResult> contactResultList) {
        Type typeOfSrc = new TypeToken<List<ContactResult>>(){}.getType();
        List<_Contact> contacts = new ArrayList<>();
        for (ContactResult contactResult : contactResultList) {
            contacts.add(new _Contact(contactResult.getPhoneNumber(), contactResult.getCountryCode(), contactResult.getContactName()));
        }
        this.contacts = new Gson().toJson(contacts, typeOfSrc);
    }
}