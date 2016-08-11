package com.stairway.data.source.message;

/**
 * Created by vidhun on 06/08/16.
 */
public class MessageResult {
    private String message;
    private String chatId;
    private String fromId;
    private DeliveryStatus deliveryStatus;
    private String messageId;

    public static enum DeliveryStatus {
        NOT_SENT,
        SENT,
        DELIVERED,
        READ
    }

    public MessageResult(String chatId, String fromId, String message) {
        this.message = message;
        this.chatId = chatId;
        this.fromId = fromId;
    }

    public MessageResult(String chatId, String fromId, String message, DeliveryStatus deliveryStatus) {
        this.chatId = chatId;
        this.deliveryStatus = deliveryStatus;
        this.fromId = fromId;
        this.message = message;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getChatId() {
        return chatId;
    }

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public String getFromId() {
        return fromId;
    }

    public String getMessage() {
        return message;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Message: "+message+", DS:"+deliveryStatus+", from"+fromId+", chatId:"+chatId+", "+messageId;
    }
}
