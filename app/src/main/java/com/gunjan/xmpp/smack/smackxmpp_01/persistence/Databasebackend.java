package com.gunjan.xmpp.smack.smackxmpp_01.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.gunjan.xmpp.smack.smackxmpp_01.model.CgatMessage;
import com.gunjan.xmpp.smack.smackxmpp_01.model.Contact;
import com.gunjan.xmpp.smack.smackxmpp_01.model.chat;

/**
 * Created by saurav on 7/16/2018.
 */

public class Databasebackend extends SQLiteOpenHelper {
    private static final String LOGTAG = "DatabaseBackend";
    private static Databasebackend instance = null;
    private static final String DATABASE_NAME = "roosterPlus_db";
    private static final int DATABASE_VERSION = 2;
    public Databasebackend(Context context) {
        super(context, DATABASE_NAME, null,DATABASE_VERSION);
    }
    private static String CREATE_CHAT_LIST_STATEMENT = "create table "
            + chat.TABLE_NAME + "("
            + chat.Cols.CHAT_UNIQUE_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
            + chat.Cols.CONTACT_TYPE + " TEXT, " + chat.Cols.CONTACT_JID + " TEXT,"
            + chat.Cols.LAST_MESSAGE + " TEXT, " + chat.Cols.UNREAD_COUNT + " NUMBER,"
            + chat.Cols.LAST_MESSAGE_TIME_STAMP + " NUMBER"
            + ");";

    //Create Contact List Table
    private static String CREATE_CONTACT_LIST_STATEMENT = "create table "
            + Contact.TABLE_NAME + "("
            + Contact.Cols.CONTACT_UNIQUE_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
            + Contact.Cols.SUBSCRIPTION_TYPE + " TEXT, " + Contact.Cols.CONTACT_JID + " TEXT,"
            + Contact.Cols.PROFILE_IMAGE_PATH + " TEXT,"
            + Contact.Cols.PENDING_STATUS_FROM + " NUMBER DEFAULT 0,"
            + Contact.Cols.PENDING_STATUS_TO + " NUMBER DEFAULT 0,"
            + Contact.Cols.ONLINE_STATUS + " NUMBER DEFAULT 0"
            + ");";

    //Create Chat Message List Table
    private static String CREATE_CHAT_MESSAGES_STATEMENT = "create table "
            + CgatMessage.TABLE_NAME + "("
            + CgatMessage.Cols.CHAT_MESSAGE_UNIQUE_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
            + CgatMessage.Cols.MESSAGE + " TEXT, "
            + CgatMessage.Cols.MESSAGE_TYPE + " TEXT, "
            + CgatMessage.Cols.TIMESTAMP + " NUMBER, "
            + CgatMessage.Cols.CONTACT_JID + " TEXT"
            + ");";
    public static synchronized Databasebackend getInstance(Context context) {
        Log.d(LOGTAG,"Getting db instance");
        if (instance == null) {
            instance = new Databasebackend(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_CHAT_LIST_STATEMENT);
        db.execSQL(CREATE_CHAT_MESSAGES_STATEMENT);
        db.execSQL(CREATE_CONTACT_LIST_STATEMENT);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2 && newVersion >= 2) {
            Log.d(LOGTAG,"Upgrading db to version 2....");
            db.execSQL("ALTER TABLE " + Contact.TABLE_NAME + " ADD COLUMN "
                    + Contact.Cols.PENDING_STATUS_TO + " NUMBER DEFAULT 0");
            db.execSQL("ALTER TABLE " + Contact.TABLE_NAME + " ADD COLUMN "
                    + Contact.Cols.PENDING_STATUS_FROM + " NUMBER DEFAULT 0");
            db.execSQL("ALTER TABLE " + Contact.TABLE_NAME + " ADD COLUMN "
                    + Contact.Cols.ONLINE_STATUS + " NUMBER DEFAULT 0");
        }
    }
}
