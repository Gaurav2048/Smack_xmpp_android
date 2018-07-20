package com.gunjan.xmpp.smack.smackxmpp_01.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gunjan.xmpp.smack.smackxmpp_01.persistence.ChatMessagesCursorWrapper;
import com.gunjan.xmpp.smack.smackxmpp_01.persistence.Databasebackend;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saurav on 7/15/2018.
 */

public class CgatMessageModel  {

    private static CgatMessageModel sCgatMessageModel;
    private Context context;
    List<CgatMessage> messages;
    private SQLiteDatabase mDatabase;

    private static CgatMessageModel sChatMessagesModel;


    public static  CgatMessageModel get(Context context){
        if(sCgatMessageModel==null)
        {
            sCgatMessageModel=new CgatMessageModel(context);

        }
        return  sCgatMessageModel;
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


    private CgatMessageModel (Context context){
        this.context=context;
        mDatabase = Databasebackend.getInstance(context).getWritableDatabase();

    }
    private ChatMessagesCursorWrapper queryMessages(String whereClause , String [] whereArgs)
    {
        Cursor cursor = mDatabase.query(
                CgatMessage.TABLE_NAME,
                null ,//Columns - null selects all columns
                whereClause,
                whereArgs,
                null ,//groupBy
                null, //having
                null//orderBy
        );
        return new ChatMessagesCursorWrapper(cursor);
    }

public List<CgatMessage> getMessgeList(){
        messages = new ArrayList<>();
        CgatMessage cgatMessage = new CgatMessage("gunjan here", System.currentTimeMillis(), CgatMessage.Type.SENT,"gunjan@server.com");

        messages.add(cgatMessage);
    CgatMessage cgatMessage1 = new CgatMessage("gunjan here", System.currentTimeMillis(), CgatMessage.Type.RECEIVE,"gunjan@server.com");

    messages.add(cgatMessage1);
    CgatMessage cgatMessage2 = new CgatMessage("gunjan here", System.currentTimeMillis(), CgatMessage.Type.SENT,"gunjan@server.com");

    messages.add(cgatMessage2);

return messages;

}

public boolean addMessage(CgatMessage message){

    ContentValues values = message.getContentValues();
    if ((mDatabase.insert(CgatMessage.TABLE_NAME, null, values)== -1))
    {
        return false;
    }else
    {
        chatModel.get(context).updateLastMessageDetails(message);
        return true;
    }


}
public List <CgatMessage> getMessages(String counterpartJid) {

    List<CgatMessage> messages = new ArrayList<>();

    ChatMessagesCursorWrapper cursor = queryMessages("contactJid= ?",new String [] {counterpartJid});

    try
    {
        cursor.moveToFirst();
        while( !cursor.isAfterLast())
        {
            messages.add(cursor.getChatMessage());
            cursor.moveToNext();
        }

    }finally {
        cursor.close();
    }
    return messages;
    }

}
