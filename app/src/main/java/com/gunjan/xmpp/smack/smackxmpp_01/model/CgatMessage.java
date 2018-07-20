package com.gunjan.xmpp.smack.smackxmpp_01.model;

import android.content.ContentValues;

import java.util.concurrent.TimeUnit;

/**
 * Created by saurav on 7/15/2018.
 */

public class CgatMessage  {

    private String message;
    private long timestamp;
    private Type type;
    private String contactJid;


    private int persistID;
    public CgatMessage(String message, long timestamp, Type type, String contactJid) {
        this.message = message;
        this.timestamp = timestamp;
        this.type = type;
        this.contactJid = contactJid;
    }

    public String getMessage() {
        return message;
    }

    public static final String TABLE_NAME = "chatMessages";

    public static final class Cols
    {
        public static final String CHAT_MESSAGE_UNIQUE_ID = "chatMessageUniqueId";
        public static final String MESSAGE = "message";
        public static final String TIMESTAMP = "timestamp";
        public static final String MESSAGE_TYPE = "messageType";
        public static final String CONTACT_JID = "contactjid";
    }



    public long getTimestamp() {
        return timestamp;
    }

    public Type getType() {
        return type;
    }

    public String getContactJid() {
        return contactJid;
    }

    public enum Type {
        SENT, RECEIVE
    }
    public String getTypeStringValue(Type type)
    {
        if(type==Type.SENT)
            return "SENT";
        else
            return "RECEIVED";
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public ContentValues getContentValues()
    {
        ContentValues values = new ContentValues();
        values.put(Cols.MESSAGE, message);
        values.put(Cols.TIMESTAMP, timestamp);
        values.put(Cols.MESSAGE_TYPE,getTypeStringValue(type));
        values.put(Cols.CONTACT_JID,contactJid);

        return values;

    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setContactJid(String contactJid) {
        this.contactJid = contactJid;
    }
    public int getPersistID() {
        return persistID;
    }

    public void setPersistID(int persistID) {
        this.persistID = persistID;
    }

}
