package com.g19p2.g19p2app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity implements NfcAdapter.OnNdefPushCompleteCallback,
        NfcAdapter.CreateNdefMessageCallback{

    private DrawerLayout drawer_layout;
    private ActionBarDrawerToggle toggle;
    private NfcAdapter mNfcAdapter;
    private Constants c = new Constants();
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 42069;

    // disable the back button
    @Override
    public void onBackPressed() {}


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // visit log is the first page upon launch
        setTitle("Visit log");
        FragmentTransaction init = getSupportFragmentManager().beginTransaction();
        init.add(R.id.fragment_container, new VisitLogFragment());
        init.commit();

        // toggle open and close the action bar
        drawer_layout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer_layout, R.string.open, R.string.close);

        drawer_layout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // display the username on the navigation drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView username_field = (TextView) headerView.findViewById(R.id.name);
        username_field.setText(c.userName);

        TextView account_type = (TextView) headerView.findViewById(R.id.account_label);
        account_type.setText("home owner");

        // format the navigation drawer
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        // set item as selected to persist highlight
                        item.setChecked(true);
                        // close drawer when item is tapped
                        drawer_layout.closeDrawers();

                        Fragment newFragment;
                        FragmentManager manager = getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();

                        // new fragment to be loaded depends on the id of item
                        switch (item.getItemId()) {
                            case R.id.nav_visitlog:
                                setTitle("Visit log");
                                newFragment = new VisitLogFragment();
                                break;
                            case R.id.nav_locks:
                                setTitle("Locks");
                                newFragment = new LocksFragment();
                                break;
                            case R.id.nav_nfc:
                                setTitle("NFC");
                                newFragment = new NFCfragment();
                                break;
                            case R.id.nav_logout:
                                Intent logout = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(logout);
                            default:
                                setTitle("Visit log");
                                newFragment = new VisitLogFragment();
                                break;

                        }

                        transaction.replace(R.id.fragment_container, newFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();

                        item.setChecked(false);

                        return true;
                    }
                }
        );

        String channel_name = "main channel";

        //Check if NFC is available on device
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(mNfcAdapter != null) {
            Toast.makeText(this, "NFC connected",
                    Toast.LENGTH_SHORT).show();

            //This will refer back to createNdefMessage for what it will send
            mNfcAdapter.setNdefPushMessageCallback(this, this);

            //This will be called if the message is sent successfully
            mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
        }
        else {
            Toast.makeText(this, "NFC not available on this device",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {
        //This is called when the system detects that our NdefMessage was
        //Successfully sent.
        c.nfc_tag = c.token;
        Toast.makeText(this, "Unlocked",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        //This will be called when another NFC capable device is detected.
        //We'll write the createRecords() method in just a moment
        NdefRecord[] recordsToAttach = createRecords(c.nfc_tag);

        //When creating an NdefMessage we need to provide an NdefRecord[]
        return new NdefMessage(recordsToAttach);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) return true;

        return super.onOptionsItemSelected(item);
    }

    public NdefRecord[] createRecords(String message) {

        NdefRecord[] records = new NdefRecord[2  ];
        //To Create Messages Manually if API is less than
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {

            byte[] payload = message.getBytes(Charset.forName("UTF-8"));
            NdefRecord record = new NdefRecord(
                    NdefRecord.TNF_WELL_KNOWN,      //Our 3-bit Type name format
                    NdefRecord.RTD_TEXT,            //Description of our payload
                    new byte[0],                    //The optional id for our Record
                    payload);                       //Our payload for the Record
            records[0] = record;
        }
        //Api is high enough that we can use createMime, which is preferred.
        else {
            byte[] payload = message.getBytes(Charset.forName("UTF-8"));
            NdefRecord record = NdefRecord.createMime("text/plain",payload);
            records[0] = record;
        }
        records[1] = NdefRecord.createApplicationRecord(getPackageName());
        return records;
    }

    private void handleNfcIntent(Intent NfcIntent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(NfcIntent.getAction())) {
            Parcelable[] receivedArray =
                    NfcIntent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if(receivedArray != null) {
                Toast.makeText(this, "Received Messages", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Received Blank Parcel", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    @Override
    public void onNewIntent(Intent intent) {
        handleNfcIntent(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }
}