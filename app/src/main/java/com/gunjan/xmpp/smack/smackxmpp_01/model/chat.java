package com.gunjan.xmpp.smack.smackxmpp_01.model;

import android.content.ContentValues;

/**
 * Created by saurav on 7/15/2018.
 */

public class chat {

    String jid;
    String lastmessage;
    public static final String TABLE_NAME = "chats";
    private long lastMessageTimeStamp;
    private ContactType contactType;
    private int persistID;
    private long unreadCount;

    public static final class Cols
    {
        public static final String CHAT_UNIQUE_ID = "chatUniqueId";
        public static final String CONTACT_JID = "jid";
        public static final String CONTACT_TYPE = "contactType";
        public static final String LAST_MESSAGE = "lastMessage";
        public static final String UNREAD_COUNT = "unreadCount";
        public static final String LAST_MESSAGE_TIME_STAMP = "lastMessageTimeStamp";
    }
    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getLastmessage() {
        return lastmessage;
    }

    public ContactType getContactType() {
        return contactType;
    }

    public void setContactType(ContactType contactType) {
        this.contactType = contactType;
    }

    public int getPersistID() {
        return persistID;
    }

    public void setPersistID(int persistID) {
        this.persistID = persistID;
    }


    public ContentValues getContentValues()
    {
        ContentValues values = new ContentValues();
        values.put(Cols.CONTACT_JID, jid);
        values.put(Cols.CONTACT_TYPE,getTypeStringValue(contactType));
        values.put(Cols.LAST_MESSAGE,lastmessage);
        values.put(Cols.LAST_MESSAGE_TIME_STAMP,lastMessageTimeStamp);
        values.put(Cols.UNREAD_COUNT,unreadCount);

        return values;

    }
    public String getTypeStringValue(ContactType type)
    {
        if(type== ContactType.ONE_ON_ONE)
            return "ONE_ON_ONE";
          else  if(type== ContactType.GROUP)
            return "GROUP";
            else  if(type== ContactType.STRANGER)
            return "STRANGER";
            else
                return null;
    }


    public void setLastmessage(String lastmessage) {
        this.lastmessage = lastmessage;
    }

    public long getLastMessageTimeStamp() {
        return lastMessageTimeStamp;
    }

    public void setLastMessageTimeStamp(long lastMessageTimeStamp) {
        this.lastMessageTimeStamp = lastMessageTimeStamp;
    }

    public chat(String jid, String lastmessage  , ContactType contactType, long timeStamp, long unreadCount) {

        this.jid = jid;
        this.lastmessage = lastmessage;
        this.lastMessageTimeStamp = timeStamp;
        this.contactType = contactType;
        this.unreadCount = unreadCount;
    }
    public enum ContactType{
        ONE_ON_ONE,GROUP,STRANGER
    }

    public long getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(long unreadCount) {
        this.unreadCount = unreadCount;
    }
}
