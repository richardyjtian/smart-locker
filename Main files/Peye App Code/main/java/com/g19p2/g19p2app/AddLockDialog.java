package com.g19p2.g19p2app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by robin on 2018-03-30.
 * Dialog used to get information for a new lock.
 */

public class AddLockDialog extends AppCompatDialogFragment {
    private EditText edit_lock_url;
    private AddLockDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_addlock, null);

        builder.setView(view)
                .setTitle("Add Lock")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.applyTexts(edit_lock_url.getText().toString());
                    }
                });

        edit_lock_url = view.findViewById(R.id.edit_lock_url);


        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        try {
            listener = (AddLockDialogListener) getTargetFragment();
        } catch(ClassCastException e) {
            throw new ClassCastException (context.toString() + "must implement AddLockDialogListener");
        }

        super.onAttach(context);
    }
}