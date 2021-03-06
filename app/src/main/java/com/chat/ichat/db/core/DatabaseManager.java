package com.chat.ichat.db.core;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.chat.ichat.core.Logger;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by vidhun on 06/07/16.
 */
public class DatabaseManager {
    private static final String TAG = "DatabaseManager";

    private static SQLiteHelper sqliteHelper;
    private static DatabaseManager instance;

    private SQLiteDatabase database;
    private AtomicInteger connectionCounter;

    private DatabaseManager() {
        connectionCounter = new AtomicInteger(0);
    }

    public static synchronized void init(Context context) {
        instance = new DatabaseManager();
        sqliteHelper = new SQLiteHelper(context);
        Logger.d(new DatabaseManager(), "DatabaseManager initialized");
    }

    public static synchronized DatabaseManager getInstance() {
        if(instance == null) {
            Logger.e(new DatabaseManager(), "DatabaseManager not initialized");
            throw new IllegalStateException("DatabaseManager not initialized");
        }
        return instance;
    }

    public static SQLiteHelper getSQLiteHelper() {
        if(sqliteHelper == null) {
            Logger.e(new DatabaseManager(), "DatabaseManager not initialized");
            throw new IllegalStateException("DatabaseManager not initialized");
        }
        return sqliteHelper;
    }

    public synchronized SQLiteDatabase openConnection() {
        if(connectionCounter.incrementAndGet() == 1) {
            database = sqliteHelper.getWritableDatabase();
        }
        return database;
    }

    public synchronized void closeConnection() {
        if(connectionCounter.decrementAndGet() == 0) {
            database.close();
            database = null;
        }
    }
}
