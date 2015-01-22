package com.lyamkin.rss;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class AddDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        final boolean edit = getArguments().getBoolean("edit", false);

        if (!edit)
            dialogBuilder.setTitle(getActivity().getResources().getString(R.string.add_title));
        else
            dialogBuilder.setTitle(getActivity().getResources().getString(R.string.edit_title));

        LayoutInflater inflater = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        View view = inflater.inflate(R.layout.add_dialog, null);
        dialogBuilder.setView(view);

        final EditText editTitle = (EditText) view.findViewById(R.id.editTitle);
        final EditText editUrl = (EditText) view.findViewById(R.id.editUrl);

        if (edit) {
            editTitle.setText(getArguments().getString("channel_title"));
            editUrl.setText(getArguments().getString("channel_url"));
            view.findViewById(R.id.delete).setVisibility(View.VISIBLE);
            view.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().getContentResolver().delete(FeedContentProvider.CHANNELS_URI, "" + getArguments().getLong("channel_id"), null);
                    dismiss();
                }
            });
        }

        if (!edit)
            dialogBuilder.setPositiveButton(getActivity().getResources().getString(R.string.add_text), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ContentValues values = new ContentValues();
                    values.put("name", editTitle.getText().toString());
                    values.put("url", editUrl.getText().toString());
                    if (!edit)
                        getActivity().getContentResolver().insert(FeedContentProvider.CHANNELS_URI, values);
                    else {
                        values.put("_id", getArguments().getLong("channel_id"));
                        getActivity().getContentResolver().update(FeedContentProvider.CHANNELS_URI, values, null, null);
                    }
                    dismiss();
                }
            });
        else
            dialogBuilder.setPositiveButton(getActivity().getResources().getString(R.string.save_text), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ContentValues values = new ContentValues();
                    values.put("name", editTitle.getText().toString());
                    values.put("url", editUrl.getText().toString());
                    if (!edit)
                        getActivity().getContentResolver().insert(FeedContentProvider.CHANNELS_URI, values);
                    else {
                        values.put("_id", getArguments().getLong("channel_id"));
                        getActivity().getContentResolver().update(FeedContentProvider.CHANNELS_URI, values, null, null);
                    }
                    dismiss();
                }
            });

        dialogBuilder.setNegativeButton(getActivity().getResources().getString(R.string.cancel_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });
        return dialogBuilder.create();
    }
}
