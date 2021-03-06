package com.chat.ichat.db.core;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.chat.ichat.core.Logger;

/**
 * Created by vidhun on 05/07/16.
 */
public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String TAG = "SQLiteHelper";

    public static final int DATABASE_VERSION = 22;
    public static final String DATABASE_NAME = "spotlight.db";

    public SQLiteHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        try {
            db.execSQL(SQLiteContract.GenericCacheContract.SQL_CREATE_TABLE);
            db.execSQL(SQLiteContract.MessagesContract.SQL_CREATE_TABLE);
            db.execSQL(SQLiteContract.ContactsContract.SQL_CREATE_TABLE);
            db.execSQL(SQLiteContract.BotDetailsContract.SQL_CREATE_TABLE);
            db.execSQL(SQLiteContract.PhoneContactsCache.SQL_CREATE_TABLE);
        } catch (SQLException sql) {
            sql.printStackTrace();
            Logger.e(this, "Error creating database. Cannot recover");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if(oldVersion<=19) {
            db.execSQL(SQLiteContract.PhoneContactsCache.SQL_CREATE_TABLE);
        }
        if(newVersion == 22) {
            db.execSQL(SQLiteContract.SavedCards.SQL_CREATE_TABLE);
        }
        Logger.d(this, "Sqlite Cache database upgraded");
    }

    public void clearData(SQLiteDatabase db) {
        db.execSQL(SQLiteContract.GenericCacheContract.SQL_DELETE_TABLE);
        db.execSQL(SQLiteContract.MessagesContract.SQL_DELETE_TABLE);
        db.execSQL(SQLiteContract.ContactsContract.SQL_DELETE_TABLE);
        db.execSQL(SQLiteContract.BotDetailsContract.SQL_DELETE_TABLE);
        db.execSQL(SQLiteContract.PhoneContactsCache.SQL_DELETE_TABLE);
        db.execSQL(SQLiteContract.SavedCards.SQL_DELETE_TABLE);

        db.execSQL(SQLiteContract.GenericCacheContract.SQL_CREATE_TABLE);
        db.execSQL(SQLiteContract.MessagesContract.SQL_CREATE_TABLE);
        db.execSQL(SQLiteContract.ContactsContract.SQL_CREATE_TABLE);
        db.execSQL(SQLiteContract.BotDetailsContract.SQL_CREATE_TABLE);
        db.execSQL(SQLiteContract.PhoneContactsCache.SQL_CREATE_TABLE);
        db.execSQL(SQLiteContract.SavedCards.SQL_CREATE_TABLE);
        Logger.d("Sqlite Cache database cleared");
    }
}

