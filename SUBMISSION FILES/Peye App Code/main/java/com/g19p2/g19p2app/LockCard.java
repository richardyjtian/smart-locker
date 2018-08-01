package com.g19p2.g19p2app;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by robin on 2018-03-29.
 * Collection of all elements of a card that displays
 * the information of a lock, and the ability to
 * acquire its key.
 */

public class LockCard {
    CardView card;
    TextView lock_id_label, lock_id;
    ImageButton unlock_button;
    View divider;
    String card_lock_id, card_lock_url;
    Context thisContext;
    FragmentManager manager;
    Constants c;

    public LockCard(CardView card, TextView lock_id_label, TextView lock_id,
                    ImageButton unlock_button, View divider, Context thisContext, FragmentManager manager) {
        this.card = card;
        this.lock_id_label = lock_id_label;
        this.lock_id = lock_id;
        this.unlock_button = unlock_button;
        this.divider = divider;
        this.thisContext = thisContext;
        this.manager = manager;

        c = new Constants();
    }

    /**
     * Formats the text of the card elements to match that of the given lock.
     * precondition: the card is initially invisible
     * @param lock - Lock object to format the card with
     */
    public void formatCard(Lock lock) {
        // takes the lock ID and URL the card is associated to
        card_lock_id = lock.getLock_id();
        card_lock_url = lock.getLock_url();
        // sets the card's contents to visible
        card.setVisibility(View.VISIBLE);
        lock_id_label.setVisibility(View.VISIBLE);
        lock_id.setVisibility(View.VISIBLE);
        unlock_button.setVisibility(View.VISIBLE);
        lock_id.setText(lock.getLock_id());
        divider.setVisibility(View.VISIBLE);

        // on click, each card's button will produce the live stream
        // of its corresponding lock
        unlock_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //c.url = "http://techslides.com/demos/sample-videos/small.mp4";
                c.url = card_lock_url;
                c.urlValid = true;
                c.lock_id = card_lock_id;

                Toast.makeText(thisContext, c.url, Toast.LENGTH_SHORT).show();
                // create a stream fragment and set its arguments
                Fragment streamFragment = new StreamFragment();
                // switch the fragments
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.fragment_container, streamFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    /**
     * Makes all the contents of this card gone
     */
    public void setGone() {
        card.setVisibility(View.GONE);
        lock_id_label.setVisibility(View.GONE);
        lock_id.setVisibility(View.GONE);
        unlock_button.setVisibility(View.GONE);
        divider.setVisibility(View.GONE);
    }
}
