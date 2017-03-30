package com.stairway.spotlight.screens.home;

import android.os.Parcelable;

import com.stairway.spotlight.models.MessageResult;

import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * Created by vidhun on 14/07/16.
 */
public class ChatItem implements Serializable{
    private String chatId;
    private String chatName;
    private String lastMessage;
    private DateTime time;
    private int notificationCount;
    private MessageResult.MessageStatus messageStatus;

    public ChatItem(String chatId, String chatName, String lastMessage, DateTime time, MessageResult.MessageStatus messageStatus, int notificationCount) {
        this.chatId = chatId;
        this.chatName = chatName;
        this.lastMessage = lastMessage;
        this.time = time;
        this.notificationCount = notificationCount;
        this.messageStatus = messageStatus;
    }

    public ChatItem() {
    }

    public MessageResult.MessageStatus getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(MessageResult.MessageStatus messageStatus) {
        this.messageStatus = messageStatus;
    }

    public String getChatId() {
        return chatId;
    }

    public String getChatName() {
        return chatName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public DateTime getTime() {
        return time;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setTime(DateTime time) {
        this.time = time;
    }

    public int getNotificationCount() {
        return notificationCount;
    }

    public void setNotificationCount(int notificationCount) {
        this.notificationCount = notificationCount;
    }

    @Override
    public String toString() {
        return "ChatListItemModel{" +
                "chatId='" + chatId + '\'' +
                ", chatName='" + chatName + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                ", time='" + time + '\'' +
                ", notificationCount=" + notificationCount +
                '}';
    }
}
