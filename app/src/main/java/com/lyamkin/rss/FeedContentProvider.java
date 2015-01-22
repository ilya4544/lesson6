package com.lyamkin.rss;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class FeedContentProvider extends ContentProvider {
    private MyDatabase database;

    private static final int CHANNELS = 10;
    private static final int NEWS = 20;

    public static final Uri CHANNELS_URI = Uri.parse("content://com.lyamkin.rss.feeds/channels");
    public static final Uri NEWS_URI = Uri.parse("content://com.lyamkin.rss.feeds/news");

    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        matcher.addURI("com.lyamkin.rss.feeds", "channels", CHANNELS);
        matcher.addURI("com.lyamkin.rss.feeds", "news/#", NEWS);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = matcher.match(uri);
        if (uriType == CHANNELS) {
            int result;
            if (database.deleteChannel(Long.parseLong(selection))) result = 1;
            else result = 0;
            getContext().getContentResolver().notifyChange(uri, null);
            return result;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = matcher.match(uri);
        long id = -1;
        if (uriType == NEWS) {
            id = database.createNews(values);
            getContext().getContentResolver().notifyChange(Uri.parse(CHANNELS_URI + "/" + values.getAsLong("channel_id")), null);
        } else if (uriType == CHANNELS) {
            id = database.createChannel(values);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(uri.toString() + "/" + id);
    }

    @Override
    public boolean onCreate() {
        database = MyDatabase.getOpenedInstance(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        int uriType = matcher.match(uri);
        Cursor result = null;
        if (uriType == NEWS) {
            String lastPathSegment = uri.getLastPathSegment();
            long channelId = Long.parseLong(lastPathSegment);
            result = database.getNewsByChannelId(channelId);
        } else if (uriType == CHANNELS) {
            result = database.getAllChannels();
        }
        if (result != null)
            result.setNotificationUri(getContext().getContentResolver(), uri);
        return result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int uriType = matcher.match(uri);
        if (uriType != CHANNELS) throw new UnsupportedOperationException();
        long id = values.getAsLong("_id");
        int result;
        if (database.changeChannel(values, id)) result = 1;
        else result = 0;
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }
}
