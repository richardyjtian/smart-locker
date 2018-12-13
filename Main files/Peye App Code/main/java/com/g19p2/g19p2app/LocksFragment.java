package com.g19p2.g19p2app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Manages the locks the user has access to.
 */
public class LocksFragment extends Fragment implements AddLockDialogListener {

    private ArrayList<LockCard> LockCards;
    private ArrayList<Lock> Locks;
    private Constants c;

    public LocksFragment() {
        LockCards = new ArrayList<LockCard>();
        Locks = new ArrayList<Lock>();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // generate new instance of constants
        c = new Constants();

        // format each LockCard and set them each to empty
        initializeCards();
        for(LockCard lc : LockCards)
            lc.setGone();

        // get the Lock IDs of the user
        getLocks();

        // format each invisible card with the corresponding lock ID
        for(int i = 0; i < Math.min(LockCards.size(), Locks.size()); i++) {
            LockCards.get(i).formatCard(Locks.get(i));
        }

        FloatingActionButton add_lock_btn = (FloatingActionButton) getView().findViewById(R.id.add_lock_btn);
        add_lock_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_locks, container, false);
    }

    /**
     * Opens the dialog for adding a lock
     */
    public void openDialog() {
        AddLockDialog addLockDialog = new AddLockDialog();
        addLockDialog.show(getActivity().getSupportFragmentManager(), "add lock dialog");
        addLockDialog.setTargetFragment(LocksFragment.this, 1);
    }

    /**
     * Method where the dialog string end up.
     * Add the lock from here.
     * @param lock_url   stream url of the lock to add
     */
    @Override
    public void applyTexts(String lock_url) {
        addLock(lock_url);
    }

    /**
     * Attempts to add the lock associated to the lock ID
     * to the user's account.
     * @param lock_url
     */
    public void addLock(String lock_url) {
        c.client = new G19P2Client();
        try {
            c.client.execute(G19P2Client.REQUEST_ADD_LOCK, lock_url).get();
            LockCards.clear();  Locks.clear();
            onViewCreated(getView(), null);
        } catch (Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "failed to get reply", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    /**
     * Gets the Lock IDs of all the locks the user is authorized to have.
     */
    private void getLocks() {
        c.client = new G19P2Client();
        String reply;

        // attempt to get JSON string from server
        try {
            reply = c.client.execute(G19P2Client.REQUEST_GET_LOCKS).get();
        } catch(Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "failed to get reply", Toast.LENGTH_SHORT).show();
            return;
        }
        //String reply = "[{\"lid\": \"1\", \"streamURL\": \"hi.com\"}, {\"lid\": \"2\", \"streamURL\": \"hello.com\"}, {\"lid\": \"3\", \"streamURL\": \"helloagain.com\"}, {\"lid\": \"4\", \"streamURL\": \"hihihi.com\"}]";


        // attempt to extract lock ID strings from JSON string
        try {
            JSONArray jsonArray = new JSONArray(reply);

            // take all the lock IDs and add them to LockIDs
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String lock_id = jsonObject.getString("lid");
                String lock_url = jsonObject.getString("streamURL");
                Locks.add(new Lock(lock_id, lock_url));
            }
        } catch(JSONException e) {
            Toast.makeText(getActivity().getApplicationContext(), "reply was not JSON string", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    /**
     * Creates an object out of each CardView and its contents, and adds them to
     * an easily accessible ArrayList.
     */
    private void initializeCards() {
        CardView card0 = (CardView) getView().findViewById(R.id.lock0);
        TextView lock_id_label_0 = (TextView) getView().findViewById(R.id.lock_id_label_0);
        TextView lock_id_0 = (TextView) getView().findViewById(R.id.lock_id_0);
        ImageButton unlock_btn_0 = (ImageButton) getView().findViewById(R.id.unlock_btn_0);
        View divider_0 = (View) getView().findViewById(R.id.divider_0);

        LockCard lockCard0 = new LockCard(card0, lock_id_label_0, lock_id_0, unlock_btn_0, divider_0, getActivity().getApplicationContext(), getActivity().getSupportFragmentManager());
        LockCards.add(lockCard0);

        CardView card1 = (CardView) getView().findViewById(R.id.lock1);
        TextView lock_id_label_1 = (TextView) getView().findViewById(R.id.lock_id_label_1);
        TextView lock_id_1 = (TextView) getView().findViewById(R.id.lock_id_1);
        ImageButton unlock_btn_1 = (ImageButton) getView().findViewById(R.id.unlock_btn_1);
        View divider_1 = (View) getView().findViewById(R.id.divider_1);

        LockCard lockCard1 = new LockCard(card1, lock_id_label_1, lock_id_1, unlock_btn_1, divider_1, getActivity().getApplicationContext(), getActivity().getSupportFragmentManager());
        LockCards.add(lockCard1);

        CardView card2 = (CardView) getView().findViewById(R.id.lock2);
        TextView lock_id_label_2 = (TextView) getView().findViewById(R.id.lock_id_label_2);
        TextView lock_id_2 = (TextView) getView().findViewById(R.id.lock_id_2);
        ImageButton unlock_btn_2 = (ImageButton) getView().findViewById(R.id.unlock_btn_2);
        View divider_2 = (View) getView().findViewById(R.id.divider_2);

        LockCard lockCard2 = new LockCard(card2, lock_id_label_2, lock_id_2, unlock_btn_2, divider_2, getActivity().getApplicationContext(), getActivity().getSupportFragmentManager());
        LockCards.add(lockCard2);

        CardView card3 = (CardView) getView().findViewById(R.id.lock3);
        TextView lock_id_label_3 = (TextView) getView().findViewById(R.id.lock_id_label_3);
        TextView lock_id_3 = (TextView) getView().findViewById(R.id.lock_id_3);
        ImageButton unlock_btn_3 = (ImageButton) getView().findViewById(R.id.unlock_btn_3);
        View divider_3 = (View) getView().findViewById(R.id.divider_3);

        LockCard lockCard3 = new LockCard(card3, lock_id_label_3, lock_id_3, unlock_btn_3, divider_3, getActivity().getApplicationContext(), getActivity().getSupportFragmentManager());
        LockCards.add(lockCard3);

        CardView card4 = (CardView) getView().findViewById(R.id.lock4);
        TextView lock_id_label_4 = (TextView) getView().findViewById(R.id.lock_id_label_4);
        TextView lock_id_4 = (TextView) getView().findViewById(R.id.lock_id_4);
        ImageButton unlock_btn_4 = (ImageButton) getView().findViewById(R.id.unlock_btn_4);
        View divider_4 = (View) getView().findViewById(R.id.divider_4);

        LockCard lockCard4 = new LockCard(card4, lock_id_label_4, lock_id_4, unlock_btn_4, divider_4, getActivity().getApplicationContext(), getActivity().getSupportFragmentManager());
        LockCards.add(lockCard4);

        CardView card5 = (CardView) getView().findViewById(R.id.lock5);
        TextView lock_id_label_5 = (TextView) getView().findViewById(R.id.lock_id_label_5);
        TextView lock_id_5 = (TextView) getView().findViewById(R.id.lock_id_5);
        ImageButton unlock_btn_5 = (ImageButton) getView().findViewById(R.id.unlock_btn_5);
        View divider_5 = (View) getView().findViewById(R.id.divider_5);

        LockCard lockCard5 = new LockCard(card5, lock_id_label_5, lock_id_5, unlock_btn_5, divider_5, getActivity().getApplicationContext(), getActivity().getSupportFragmentManager());
        LockCards.add(lockCard5);

        CardView card6 = (CardView) getView().findViewById(R.id.lock6);
        TextView lock_id_label_6 = (TextView) getView().findViewById(R.id.lock_id_label_6);
        TextView lock_id_6 = (TextView) getView().findViewById(R.id.lock_id_6);
        ImageButton unlock_btn_6 = (ImageButton) getView().findViewById(R.id.unlock_btn_6);
        View divider_6 = (View) getView().findViewById(R.id.divider_6);

        LockCard lockCard6 = new LockCard(card6, lock_id_label_6, lock_id_6, unlock_btn_6, divider_6, getActivity().getApplicationContext(), getActivity().getSupportFragmentManager());
        LockCards.add(lockCard6);

        CardView card7 = (CardView) getView().findViewById(R.id.lock7);
        TextView lock_id_label_7 = (TextView) getView().findViewById(R.id.lock_id_label_7);
        TextView lock_id_7 = (TextView) getView().findViewById(R.id.lock_id_7);
        ImageButton unlock_btn_7 = (ImageButton) getView().findViewById(R.id.unlock_btn_7);
        View divider_7 = (View) getView().findViewById(R.id.divider_7);

        LockCard lockCard7 = new LockCard(card7, lock_id_label_7, lock_id_7, unlock_btn_7, divider_7, getActivity().getApplicationContext(), getActivity().getSupportFragmentManager());
        LockCards.add(lockCard7);

        CardView card8 = (CardView) getView().findViewById(R.id.lock8);
        TextView lock_id_label_8 = (TextView) getView().findViewById(R.id.lock_id_label_8);
        TextView lock_id_8 = (TextView) getView().findViewById(R.id.lock_id_8);
        ImageButton unlock_btn_8 = (ImageButton) getView().findViewById(R.id.unlock_btn_8);
        View divider_8 = (View) getView().findViewById(R.id.divider_8);

        LockCard lockCard8 = new LockCard(card8, lock_id_label_8, lock_id_8, unlock_btn_8, divider_8, getActivity().getApplicationContext(), getActivity().getSupportFragmentManager());
        LockCards.add(lockCard8);

        CardView card9 = (CardView) getView().findViewById(R.id.lock9);
        TextView lock_id_label_9 = (TextView) getView().findViewById(R.id.lock_id_label_9);
        TextView lock_id_9 = (TextView) getView().findViewById(R.id.lock_id_9);
        ImageButton unlock_btn_9 = (ImageButton) getView().findViewById(R.id.unlock_btn_9);
        View divider_9 = (View) getView().findViewById(R.id.divider_9);

        LockCard lockCard9 = new LockCard(card9, lock_id_label_9, lock_id_9, unlock_btn_9, divider_9, getActivity().getApplicationContext(), getActivity().getSupportFragmentManager());
        LockCards.add(lockCard9);
    }
}

