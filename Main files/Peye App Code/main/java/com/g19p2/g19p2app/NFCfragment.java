package com.g19p2.g19p2app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NFCfragment extends DialogFragment {

    Constants c = new Constants();
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 42069;

    public NFCfragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        askPermission();

        Button share_btn = (Button) getView().findViewById(R.id.share_btn);
        share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText phone_number = (EditText) getView().findViewById(R.id.phone_number);
                shareToken(phone_number.getText().toString());
            }
        });

        Button set_token = (Button) getView().findViewById(R.id.set_token);
        set_token.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText token_field = (EditText) getView().findViewById(R.id.token_field);
                setToken(token_field.getText().toString());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_write,container,false);
        return view;
    }

    public void shareToken(String phone_number) {
        if(phone_number.equals(""))
            Toast.makeText(getActivity().getApplicationContext(), "please enter a number", Toast.LENGTH_SHORT).show();
        else {
            Toast.makeText(getActivity().getApplicationContext(), "sending to: " + phone_number, Toast.LENGTH_SHORT).show();
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone_number, null, "Next message will contain the key to use for this lock. Sent by user: " + c.userName, null,null);
            smsManager.sendTextMessage(phone_number, null, c.token, null,null);
            Toast.makeText(getActivity().getApplicationContext(), "sent!", Toast.LENGTH_SHORT).show();
        }
    }

    public void setToken(String token) {
        if(!token.equals("")) {
            Toast.makeText(getActivity().getApplicationContext(), token, Toast.LENGTH_SHORT).show();
            c.nfc_tag = token;
        }
        else {
            Toast.makeText(getActivity().getApplicationContext(), "reset", Toast.LENGTH_SHORT).show();
            c.nfc_tag = c.token;
        }
    }

    private void askPermission() {
        if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this.getActivity(),
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }
}
