package com.lyamkin.rss;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.List;


public class FeedFetchingService extends IntentService {
    private static final String CHANNEL_ID = "com.lyamkin.rss.extra.channel_id";

    public static void startActionUpdateChannel(Context context, long channelId) {
        Intent intent = new Intent(context, FeedFetchingService.class);
        intent.putExtra(CHANNEL_ID, channelId);
        context.startService(intent);
    }

    public FeedFetchingService() {
        super("FeedFetchingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final long channelId = intent.getLongExtra(CHANNEL_ID, -1);
            handleActionUpdateChannel(channelId);
        }
    }

    private void handleActionUpdateChannel(final long channelId) {
        final MyDatabase db = MyDatabase.getOpenedInstance(this);
        String url = db.getUrlByChannelId(channelId);
        if (url != null) {
            RssParser.FeedParsedCallback callback = new RssParser.FeedParsedCallback() {
                @Override
                public void onFeedParsed(List<FeedItem> feedItems) {
                    for (FeedItem item : feedItems) {
                        ContentValues values = new ContentValues();
                        values.put("description", item.description);
                        values.put("title", item.title);
                        values.put("url", item.link);
                        values.put("channel_id", channelId);
                        values.put("time", System.currentTimeMillis() / 1000);
                        getContentResolver().insert(Uri.parse(FeedContentProvider.NEWS_URI.toString() + "/" + channelId), values);
                    }
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction("channel updated");
                    broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    broadcastIntent.putExtra(CHANNEL_ID, channelId);
                    sendBroadcast(broadcastIntent);
                    getContentResolver().notifyChange(Uri.parse(FeedContentProvider.NEWS_URI.toString() + "/" + channelId), null);
                }
            };
            new RssParser(callback, url);
        }
    }

}
