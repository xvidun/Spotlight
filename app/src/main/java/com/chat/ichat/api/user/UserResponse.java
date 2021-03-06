package com.chat.ichat.api.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.chat.ichat.api.ErrorResponse;

import java.util.Date;

/**
 * Created by vidhun on 16/10/16.
 */

public class UserResponse {
    @SerializedName("error")
    ErrorResponse error;
    @SerializedName("user")
    @Expose
    private _User user;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("expires")
    @Expose
    private String expires;
    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("is_otp_sent")
    @Expose
    private boolean isOtpSent;
    @Expose
    @SerializedName("verification_uuid")
    private String verificationUuid;
    @Expose
    @SerializedName("is_verification_success")
    boolean isVerificationSuccess;

    public boolean isVerificationSuccess() {
        return isVerificationSuccess;
    }

    public ErrorResponse getError() {
        return error;
    }

    public boolean isSuccess() {
        return error==null;
    }

    public _User getUser() {
        return user;
    }

    public boolean isOtpSent() {
        return isOtpSent;
    }

    public String getVerificationUuid() {
        return verificationUuid;
    }

    public void setUser(_User user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getExpires() {
        Long ex = Long.parseLong(expires);
        return new Date(ex);
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String toString() {
        return "UserResponse{" +
                "user=" + user +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", expires='" + expires + '\'' +
                ", accessToken='" + accessToken + '\'' +
                '}';
    }
}
