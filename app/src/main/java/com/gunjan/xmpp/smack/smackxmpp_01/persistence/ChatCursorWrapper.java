package com.gunjan.xmpp.smack.smackxmpp_01.persistence;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.util.Log;

import com.gunjan.xmpp.smack.smackxmpp_01.model.chat;

/**
 * Created by saurav on 7/16/2018.
 */

public class ChatCursorWrapper extends CursorWrapper {

    private static final String LOGTAG = "ChatCursorWrapper" ;

    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public ChatCursorWrapper(Cursor cursor) {
        super(cursor);
    }
    public chat getChat() {

        String jid = getString(getColumnIndex(chat.Cols.CONTACT_JID));
        String contactType = getString(getColumnIndex(chat.Cols.CONTACT_TYPE));
        String lastMessage = getString(getColumnIndex(chat.Cols.LAST_MESSAGE));
        long unreadCount = getLong(getColumnIndex(chat.Cols.UNREAD_COUNT));
        long lastMessageTimeStamp = getLong(getColumnIndex(chat.Cols.LAST_MESSAGE_TIME_STAMP));
        int uniqueId = getInt(getColumnIndex(chat.Cols.CHAT_UNIQUE_ID));

        Log.d(LOGTAG,"Got a chat from database the unique ID is :"+uniqueId);



        chat.ContactType chatType = null;

        if (contactType.equals("GROUP")) {
            chatType = chat.ContactType.GROUP;
        } else if (contactType.equals("ONE_ON_ONE")) {
            chatType = chat.ContactType.ONE_ON_ONE;
        } else if (contactType.equals("STRANGER")) {
            chatType = chat.ContactType.STRANGER;
        }
        chat chat = new chat(jid,lastMessage,chatType, lastMessageTimeStamp,unreadCount);
        chat.setPersistID(uniqueId);
        return chat;
    }

}
