package com.g19p2.g19p2app;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by robin on 2018-03-18.
 * Collection of all elements of a card that displays
 * the information of a visit.
 */

public class VisitCard {
    CardView card;
    TextView date_label, time_label, status_label,
            date, time, status;
    Context context;

    public VisitCard(CardView card, TextView date_label, TextView time_label,
                      TextView status_label, TextView date, TextView time, TextView status,
                     Context context) {
        this.card = card;
        this.date_label = date_label;
        this.time_label = time_label;
        this.status_label = status_label;
        this.date = date;
        this.time = time;
        this.status = status;
        this.context = context;
    }

    /**
     * Formats the text of the card elements to match that of the given visit.
     * precondition: the card is initially invisible
     * @param visit - Visit whose information is to be displayed on the card
     */
    public void formatCard(Visit visit) {
        card.setVisibility(View.VISIBLE);

        date_label.setVisibility(View.VISIBLE);
        date.setVisibility(View.VISIBLE);
        date.setText(visit.getDate());

        time_label.setVisibility(View.VISIBLE);
        time.setVisibility(View.VISIBLE);
        time.setText(visit.getTime());

        status_label.setVisibility(View.VISIBLE);
        status.setVisibility(View.VISIBLE);
        if(visit.getAuthorized()) {
            status.setText("AUTHORIZED");
            status.setTextColor(context.getResources().getColor(R.color.colorAuthorized));
        }
        else {
            status.setText("DENIED");
            status.setTextColor(context.getResources().getColor(R.color.colorDenied));
        }
    }

    /**
     * Makes all the contents of this card gone
     */
    public void setGone() {
        card.setVisibility(View.GONE);
        date_label.setVisibility(View.GONE);
        time_label.setVisibility(View.GONE);
        status_label.setVisibility(View.GONE);
        date.setVisibility(View.GONE);
        time.setVisibility(View.GONE);
        status.setVisibility(View.GONE);
    }
}