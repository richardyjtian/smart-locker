package com.g19p2.g19p2app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private static final String sign_up_link = "https://hizhh.me/register/";

    private Constants c = new Constants();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // set the title to an empty string, because it looks nice
        setTitle("");

        Button login_btn = (Button) findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username = (EditText) findViewById(R.id.email);
                EditText password = (EditText) findViewById(R.id.password);

                String s_username = username.getText().toString();
                String s_password = password.getText().toString();

                // if either field is empty, display the appropriate message
                if (s_username.matches("")) {
                    Toast.makeText(getApplicationContext(), "username required", Toast.LENGTH_SHORT).show();
                    return;
                } else if (s_password.matches("")) {
                    Toast.makeText(getApplicationContext(), "password required", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    // communicate with server to see if email and password match
                    int verified = verifyLogin(s_username, s_password);

                    switch (verified) {
                        // if communication with the server failed
                        case 0:
                            Toast.makeText(getApplicationContext(), "server is down", Toast.LENGTH_SHORT).show();
                            break;
                        // if login information is not valid
                        case 1:
                            Toast.makeText(getApplicationContext(), "login failed", Toast.LENGTH_SHORT).show();
                            break;
                        // if login information is valid
                        case 2:
                            Toast.makeText(getApplicationContext(), "login succesful", Toast.LENGTH_SHORT).show();
                            Intent go_to_main = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(go_to_main);
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), "LOLOLOLOL", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        Button sign_up_btn = (Button) findViewById(R.id.sign_up_btn);
        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            // on click, send the user to the sign up page on their mobile browser
            public void onClick(View v) {
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse(sign_up_link)));
            }
        });
    }

    /**
     * Attempts to communicate with the server to verify the user's login information,
     * and also initializes the static values in constants for use around the app.
     *
     * @param s_username username used to verify the user's account
     * @param s_password password used to verify the user's account
     * @return 0 if communication with the server failed
     *         1 if the login information could not be verified
     *         2 if the login information was verified
     */
    private int verifyLogin(String s_username, String s_password) {
        c.client = new G19P2Client();
        c.userName = s_username;
        c.password = s_password;
        String reply;
        try {
            reply = c.client.execute(G19P2Client.REQUEST_LOGIN).get();
        }catch(Exception e){
            reply = "THREAD ERROR";
        }
        return Integer.valueOf(reply);
    }
}