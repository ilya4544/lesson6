package com.lyamkin.rss;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabase {
    public static final String DB_NAME = "my_db";
    public static final Integer VERSION = 1;

    public static final String CREATE_TABLE_CHANNELS = "CREATE TABLE channel (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, url TEXT)";
    public static final String CREATE_TABLE_NEWS = "CREATE TABLE news (_id INTEGER PRIMARY KEY AUTOINCREMENT, channel_id INTEGER NOT NULL, title TEXT, description TEXT, " +
            "url TEXT, time INTEGER NOT NULL, FOREIGN KEY (channel_id ) REFERENCES channel (_id) ON DELETE CASCADE, UNIQUE (url) ON CONFLICT IGNORE)";

    private static MyDatabase mInstance = null;
    private Context context;
    private SQLiteDatabase db;

    private MyDatabase(Context context){
        this.context = context;
    }

    public static MyDatabase getOpenedInstance(Context context){
        if (mInstance==null)
          mInstance = new MyDatabase(context.getApplicationContext()).open();
        return mInstance;
    }

    private MyDatabase open() {
        DBHelper mDbHelper = new DBHelper(context);
        db = mDbHelper.getWritableDatabase();
        return this;
    }


    private static class DBHelper extends SQLiteOpenHelper {
        Context context;

        public DBHelper(Context context) {
            super(context, DB_NAME, null, VERSION);
            this.context = context;
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            db.execSQL("PRAGMA foreign_keys=ON");
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_TABLE_CHANNELS);
            sqLiteDatabase.execSQL(CREATE_TABLE_NEWS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

        }
    }

    public String getUrlByChannelId(long channelId){
        Cursor c = db.query("channel", new String[] {"url"},"_id" + "=" + channelId,
                null,null,null,null);
        if (c.getCount()==0) {
            c.close();
            return null;
        }
        c.moveToFirst();
        String result = c.getString(c.getColumnIndex("url"));
        c.close();
        return result;
    }

    public long createChannel(ContentValues channel) {
        return db.insert("channel",null,channel);
    }


    public boolean changeChannel(ContentValues channel, long channelId) {
        return db.update("channel", channel, "_id" + "=" + channelId,null)==1;
    }

    public Cursor getNewsByChannelId(long channelId){
        return db.query("news", new String[] {"_id", "url", "description", "title"},
    "channel_id=" +channelId,null,null,null, "time" + " DESC");
}

    public Cursor getAllChannels(){
        return db.query("channel",new String [] {"_id", "name", "url"},null,null,null,null,null);
    }

    public long createNews(ContentValues news) {
        return db.insert("news",null,news);
    }

    public boolean deleteChannel(long channelId){
        return db.delete("channel","_id=" + channelId, null)==1;
    }
}
