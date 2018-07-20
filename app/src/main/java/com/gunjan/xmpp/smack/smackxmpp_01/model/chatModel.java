package com.gunjan.xmpp.smack.smackxmpp_01.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gunjan.xmpp.smack.smackxmpp_01.persistence.ChatCursorWrapper;
import com.gunjan.xmpp.smack.smackxmpp_01.persistence.Databasebackend;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saurav on 7/15/2018.
 */

public class chatModel {


    private static chatModel chatModel;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static chatModel get(Context context){
        if(chatModel == null){
            chatModel = new chatModel(context);
        }
        return chatModel;
    }

    private chatModel(Context context){
        mContext =context;
        mDatabase = Databasebackend.getInstance(mContext).getWritableDatabase();

    }

public List<chat> getChat(){

        List <chat> chats = new ArrayList<>();

    ChatCursorWrapper cursor = queryChats(null,null);

    try
    {
        cursor.moveToFirst();
        while( !cursor.isAfterLast())
        {
            Log.d("LOGTAG" , "Got a chat from db : Timestamp :"+cursor.getChat().getLastMessageTimeStamp());
            chats.add(cursor.getChat());
            cursor.moveToNext();
        }

    }finally {
        cursor.close();
    }

return chats;

}
    private ChatCursorWrapper queryChats(String whereClause , String [] whereArgs)
    {
        Cursor cursor = mDatabase.query(
                chat.TABLE_NAME,
                null ,//Columns - null selects all columns
                whereClause,
                whereArgs,
                null ,//groupBy
                null, //having
                null//orderBy
        );
        return new ChatCursorWrapper(cursor);
    }

    public boolean addChat( chat c )
    {
        //TODO: Check if Chat already in db before adding.
        ContentValues values = c.getContentValues();
        if ((mDatabase.insert(chat.TABLE_NAME, null, values)== -1))
        {
            return false;
        }else
        {
            return true;
        }
    }
    public boolean deleteChat(chat c)
    {
        return deleteChat(c.getPersistID());
    }
    public boolean deleteChat(int uniqueId)
    {
        int value =mDatabase.delete(chat.TABLE_NAME,chat.Cols.CHAT_UNIQUE_ID+"=?",new String[] {String.valueOf(uniqueId)});

        if(value == 1)
        {
            Log.d("LOGTAG", "Successfully deleted a record");
            return true;
        }else
        {
            Log.d("LOGTAG", "Could not delete record");
            return false;
        }
    }
    public boolean updateLastMessageDetails(CgatMessage chatMessage)
    {
        List<chat>  chats = getChatsByJid(chatMessage.getContactJid());
        if( !chats.isEmpty())
        {
            chat chat = chats.get(0);
            //Apply new settings to the chat object
            chat.setLastMessageTimeStamp(chatMessage.getTimestamp());
            chat.setLastmessage(chatMessage.getMessage());

            ContentValues values = chat.getContentValues();

            //Update the information
            int ret =mDatabase.update(chat.TABLE_NAME, values,
                    com.gunjan.xmpp.smack.smackxmpp_01.model.chat.Cols.CHAT_UNIQUE_ID + "=?",
                    new String[]{ String.valueOf(chat.getPersistID())});
            if(ret == 1)
            {
                Log.d("LOGTAG","Chat Last Message updated successfully");
                return true;
            }else
            {
                Log.d("LOGTAG","Could not update Chat Last Message info");
                return false;
            }
        }
        return false;
    }
    public List<chat> getChatsByJid(String jid)
    {
        List<chat> chats = new ArrayList<>();

        ChatCursorWrapper cursor = queryChats(chat.Cols.CONTACT_JID + "= ?",new String [] {jid});

        try
        {
            cursor.moveToFirst();
            while( !cursor.isAfterLast())
            {
                chats.add(cursor.getChat());
                cursor.moveToNext();
            }

        }finally {
            cursor.close();
        }
        return chats;
    }

    public boolean deleteMessage(CgatMessage message)
    {

        return  deleteMessage(message.getPersistID());
    }

    public boolean  deleteMessage( int uniqueId)
    {
        int value =mDatabase.delete(CgatMessage.TABLE_NAME,CgatMessage.Cols.CHAT_MESSAGE_UNIQUE_ID+"=?",new String[] {String.valueOf(uniqueId)});

        if(value == 1)
        {
            Log.d("LOGTAG", "Successfully deleted a record");
            return true;
        }else
        {
            Log.d("LOGTAG", "Could not delete record");
            return false;
        }
    }

}
