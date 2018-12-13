package com.g19p2.g19p2app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Displays a livestream of a view just outside the door, and
 * allows for remote lock and unlock of the door.
 */
public class StreamFragment extends Fragment {
    private static final String DUMMY_URL = "http://techslides.com/demos/sample-videos/small.mp4";
    private static final String REAL_URL = "https://acerate-scorpion-5728.dataplicity.io/?action=stream";

    private String streamURL;
    private Constants c;

    public StreamFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        WebView webview = (WebView) getView().findViewById(R.id.webview);
        // get URL from the server
        streamURL = getURL();

        // play the video linked in the URL
        webview.loadUrl(streamURL);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        c = new Constants();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stream, container, false);
    }

    private String getURL() {
        c.client = new G19P2Client();
        String reply;
        try {
            reply = c.client.execute(G19P2Client.REQUEST_GET_STREAM_URL, c.lock_id).get();
        }catch(Exception e){
            reply = "THREAD ERROR";
        }
        Toast.makeText(getActivity().getApplicationContext(), reply, Toast.LENGTH_SHORT).show();
        return reply;
    }
}
