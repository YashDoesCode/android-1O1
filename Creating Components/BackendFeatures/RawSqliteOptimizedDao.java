package com.keeper.components.backend;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class RawSqliteOptimizedDao extends SQLiteOpenHelper {

    private static final String DB_NAME = "optimized_store.db";
    private static final int DB_VERSION = 1;

    public RawSqliteOptimizedDao(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE items (id INTEGER PRIMARY KEY, key TEXT UNIQUE, payload BLOB);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS items;");
        onCreate(db);
    }

    public void insertBatchFast(String[] keys, byte[][] payloads) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "INSERT OR REPLACE INTO items (key, payload) VALUES (?, ?);";
        SQLiteStatement stmt = db.compileStatement(sql);
        db.beginTransaction();
        try {
            for (int i = 0; i < keys.length; i++) {
                stmt.clearBindings();
                stmt.bindString(1, keys[i]);
                stmt.bindBlob(2, payloads[i]);
                stmt.execute();
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}
