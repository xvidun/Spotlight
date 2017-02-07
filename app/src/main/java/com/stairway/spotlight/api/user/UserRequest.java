package com.stairway.spotlight.api.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by vidhun on 16/10/16.
 */

public class UserRequest {
    @SerializedName("user")
    @Expose
    private _User user;

    public UserRequest(String countryCode, String mobile) {
        this.user = new _User(countryCode, mobile);
    }

    public UserRequest(){
    }

    public _User getUser() {
        return user;
    }

    public void setUser(_User user) {
        this.user = user;
    }
}
