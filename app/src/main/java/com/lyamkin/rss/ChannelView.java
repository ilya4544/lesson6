package com.lyamkin.rss;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;


public class ChannelView extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLoaderManager().initLoader(0, null, this);
        setListAdapter(new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, null, new String[]{"name", "url"}, new int[]{android.R.id.text1, android.R.id.text2}, 0));

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ChannelView.this, FeedView.class);
                intent.putExtra(FeedView.EXTRA_CHANNEL_ID, l);
                startActivity(intent);
            }
        });
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                AddDialog addDialog = new AddDialog();
                Bundle args = new Bundle();
                args.putBoolean("edit", true);
                args.putLong("channel_id", l);
                Cursor item = (Cursor) adapterView.getItemAtPosition(i);
                args.putString("channel_title", item.getString(item.getColumnIndex("name")));
                args.putString("channel_url", item.getString(item.getColumnIndex("url")));
                addDialog.setArguments(args);
                getFragmentManager().beginTransaction().add(addDialog, "").commit();
                return true;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.channels, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            AddDialog addDialog = new AddDialog();
            Bundle args = new Bundle();
            args.putBoolean("edit", false);
            addDialog.setArguments(args);
            getFragmentManager().beginTransaction().add(addDialog, "").commit();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, FeedContentProvider.CHANNELS_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ((CursorAdapter)getListAdapter()).swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((CursorAdapter)getListAdapter()).swapCursor(null);
    }
}
