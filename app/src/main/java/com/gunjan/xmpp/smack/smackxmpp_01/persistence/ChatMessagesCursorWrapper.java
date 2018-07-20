package com.gunjan.xmpp.smack.smackxmpp_01.persistence;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.gunjan.xmpp.smack.smackxmpp_01.model.CgatMessage;

/**
 * Created by saurav on 7/17/2018.
 */

public class ChatMessagesCursorWrapper  extends CursorWrapper{
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public ChatMessagesCursorWrapper(Cursor cursor) {
        super(cursor);
    }
    public CgatMessage getChatMessage()
    {
        String message = getString(getColumnIndex(CgatMessage.Cols.MESSAGE));
        long timestamp = getLong(getColumnIndex(CgatMessage.Cols.TIMESTAMP));
        String messageType = getString(getColumnIndex(CgatMessage.Cols.MESSAGE_TYPE));
        String counterpartJid = getString(getColumnIndex(CgatMessage.Cols.CONTACT_JID));
        int uniqueId = getInt(getColumnIndex(CgatMessage.Cols.CHAT_MESSAGE_UNIQUE_ID));

        CgatMessage.Type chatMessageType = null;

        if( messageType.equals("SENT"))
        {
            chatMessageType = CgatMessage.Type.SENT;
        }

        else if(messageType.equals("RECEIVED"))
        {
            chatMessageType = CgatMessage.Type.RECEIVE;
        }
        CgatMessage chatMessage = new CgatMessage(message,timestamp,chatMessageType,counterpartJid);
        chatMessage.setPersistID(uniqueId);

        return  chatMessage;
    }
}
