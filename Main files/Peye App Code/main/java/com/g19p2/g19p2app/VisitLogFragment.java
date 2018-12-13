package com.g19p2.g19p2app;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 * Displays the user's most recent visits on cardviews.
 */
public class VisitLogFragment extends Fragment {

    ArrayList<VisitCard> VisitCards;
    ArrayList<Visit> Visits;
    Constants c;

    public VisitLogFragment() {
        VisitCards = new ArrayList<VisitCard>();
        Visits = new ArrayList<Visit>();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // initialize instance of constants
        c = new Constants();

        // load the cards and visits into their specified ArrayLists
        initializeCards();  getVisits();

        // initially set all cards to be gone
        for(VisitCard vc : VisitCards)
            vc.setGone();

        // format a visit card for each visit, or until number of cards
        //is exhausted
        for(int i = 0; i < Math.min(Visits.size(), VisitCards.size()); i++)
            VisitCards.get(i).formatCard(Visits.get(i));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_visit_log, container, false);
    }


    /**
     * Generates the visits whose information is to be displayed on the visit cards,
     * and adds them to an easily accessible ArrayList.
     * postcondition: most recent visits are put at lower indices of Visits
     *
     * Requests a JSON Array from the server containing recent visits, and adds each
     * JSON Object as a Visit object to the Visits ArrayList.
     * postcondition: most recent visits are put at lower indices of Visits
     */
    private void getVisits() {
         // Expecting JSON string formatted like:
         // {"visits":[ {"date":"2018-03-22" , "time":"23:44" , "authorized":true , "method":1} , {"date":... } ] }

        c.client = new G19P2Client();
//        String reply = "[{\"hid\": \"4\", \"username\": \"newtest\", \"time\": \"12:28AM on April 02, 2018\", \"status\": \"denied\"}, {\"hid\": \"5\", \"username\": \"newtest\", \"time\": \"12:28AM on April 02, 2018\", \"status\": \"authorized\"}, {\"hid\": \"6\", \"username\": \"newtest\", \"time\": \"12:31AM on April 02, 2018\", \"status\": \"authorized\"}]";

        String reply;
        // attempt to get JSON string from request
        try {
            reply = c.client.execute(G19P2Client.REQUEST_GET_VISITS).get();
        } catch(Exception e) {
            Toast.makeText(getActivity().getApplicationContext(), "failed to get reply", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONArray jsonArray = new JSONArray(reply);
            // takes the information out of each JSON object to construct
            // a Visit object to add to Visits
            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject visit = jsonArray.getJSONObject(i);

                String[] time = visit.getString("time").split(" on ");

                String status = visit.getString("status");
                boolean status_bool = status.equals("denied") ? false : true;

                Visits.add(new Visit(time[1], time[0], status_bool));
            }
        } catch(JSONException e) {
            Toast.makeText(getActivity().getApplicationContext(), "reply was not JSON string", Toast.LENGTH_SHORT).show();
            return;
        }

        Collections.reverse(Visits);
    }

    /**
     * Creates objects out of each CardView and its contents, and adds them to
     * an easily accessible ArrayList.
     */
    private void initializeCards() {
        CardView card0 = (CardView) getView().findViewById(R.id.card0);
        TextView time_label_0 = (TextView) getView().findViewById(R.id.time_label_0);
        TextView time_0 = (TextView) getView().findViewById(R.id.time_0);
        TextView date_label_0 = (TextView) getView().findViewById(R.id.date_label_0);
        TextView date_0 = (TextView) getView().findViewById(R.id.date_0);
        TextView status_label_0 = (TextView) getView().findViewById(R.id.status_label_0);
        TextView status_0 = (TextView) getView().findViewById(R.id.status_0);

        VisitCard visitCard0 = new VisitCard(card0, date_label_0, time_label_0, status_label_0,
                 date_0, time_0, status_0, getActivity().getApplicationContext());
        VisitCards.add(visitCard0);

        CardView card1 = (CardView) getView().findViewById(R.id.card1);
        TextView time_label_1 = (TextView) getView().findViewById(R.id.time_label_1);
        TextView time_1 = (TextView) getView().findViewById(R.id.time_1);
        TextView date_label_1 = (TextView) getView().findViewById(R.id.date_label_1);
        TextView date_1 = (TextView) getView().findViewById(R.id.date_1);
        TextView status_label_1 = (TextView) getView().findViewById(R.id.status_label_1);
        TextView status_1 = (TextView) getView().findViewById(R.id.status_1);

        VisitCard visitCard1 = new VisitCard(card1, date_label_1, time_label_1, status_label_1,
                 date_1, time_1, status_1, getActivity().getApplicationContext());
        VisitCards.add(visitCard1);

        CardView card2 = (CardView) getView().findViewById(R.id.card2);
        TextView time_label_2 = (TextView) getView().findViewById(R.id.time_label_2);
        TextView time_2 = (TextView) getView().findViewById(R.id.time_2);
        TextView date_label_2 = (TextView) getView().findViewById(R.id.date_label_2);
        TextView date_2 = (TextView) getView().findViewById(R.id.date_2);
        TextView status_label_2 = (TextView) getView().findViewById(R.id.status_label_2);
        TextView status_2 = (TextView) getView().findViewById(R.id.status_2);

        VisitCard visitCard2 = new VisitCard(card2, date_label_2, time_label_2, status_label_2,
                date_2, time_2, status_2, getActivity().getApplicationContext());
        VisitCards.add(visitCard2);

        CardView card3 = (CardView) getView().findViewById(R.id.card3);
        TextView time_label_3 = (TextView) getView().findViewById(R.id.time_label_3);
        TextView time_3 = (TextView) getView().findViewById(R.id.time_3);
        TextView date_label_3 = (TextView) getView().findViewById(R.id.date_label_3);
        TextView date_3 = (TextView) getView().findViewById(R.id.date_3);
        TextView status_label_3 = (TextView) getView().findViewById(R.id.status_label_3);
        TextView status_3 = (TextView) getView().findViewById(R.id.status_3);

        VisitCard visitCard3 = new VisitCard(card3, date_label_3, time_label_3, status_label_3,
                 date_3, time_3, status_3, getActivity().getApplicationContext());
        VisitCards.add(visitCard3);

        CardView card4 = (CardView) getView().findViewById(R.id.card4);
        TextView time_label_4 = (TextView) getView().findViewById(R.id.time_label_4);
        TextView time_4 = (TextView) getView().findViewById(R.id.time_4);
        TextView date_label_4 = (TextView) getView().findViewById(R.id.date_label_4);
        TextView date_4 = (TextView) getView().findViewById(R.id.date_4);
        TextView status_label_4 = (TextView) getView().findViewById(R.id.status_label_4);
        TextView status_4 = (TextView) getView().findViewById(R.id.status_4);

        VisitCard visitCard4 = new VisitCard(card4, date_label_4, time_label_4, status_label_4,
                date_4, time_4, status_4, getActivity().getApplicationContext());
        VisitCards.add(visitCard4);

        CardView card5 = (CardView) getView().findViewById(R.id.card5);
        TextView time_label_5 = (TextView) getView().findViewById(R.id.time_label_5);
        TextView time_5 = (TextView) getView().findViewById(R.id.time_5);
        TextView date_label_5 = (TextView) getView().findViewById(R.id.date_label_5);
        TextView date_5 = (TextView) getView().findViewById(R.id.date_5);
        TextView status_label_5 = (TextView) getView().findViewById(R.id.status_label_5);
        TextView status_5 = (TextView) getView().findViewById(R.id.status_5);

        VisitCard visitCard5 = new VisitCard(card5, date_label_5, time_label_5, status_label_5,
                date_5, time_5, status_5, getActivity().getApplicationContext());
        VisitCards.add(visitCard5);

        CardView card6 = (CardView) getView().findViewById(R.id.card6);
        TextView time_label_6 = (TextView) getView().findViewById(R.id.time_label_6);
        TextView time_6 = (TextView) getView().findViewById(R.id.time_6);
        TextView date_label_6 = (TextView) getView().findViewById(R.id.date_label_6);
        TextView date_6 = (TextView) getView().findViewById(R.id.date_6);
        TextView status_label_6 = (TextView) getView().findViewById(R.id.status_label_6);
        TextView status_6 = (TextView) getView().findViewById(R.id.status_6);

        VisitCard visitCard6 = new VisitCard(card6, date_label_6, time_label_6, status_label_6,
                date_6, time_6, status_6, getActivity().getApplicationContext());
        VisitCards.add(visitCard6);

        CardView card7 = (CardView) getView().findViewById(R.id.card7);
        TextView time_label_7 = (TextView) getView().findViewById(R.id.time_label_7);
        TextView time_7 = (TextView) getView().findViewById(R.id.time_7);
        TextView date_label_7 = (TextView) getView().findViewById(R.id.date_label_7);
        TextView date_7 = (TextView) getView().findViewById(R.id.date_7);
        TextView status_label_7 = (TextView) getView().findViewById(R.id.status_label_7);
        TextView status_7 = (TextView) getView().findViewById(R.id.status_7);

        VisitCard visitCard7 = new VisitCard(card7, date_label_7, time_label_7, status_label_7,
                date_7, time_7, status_7, getActivity().getApplicationContext());
        VisitCards.add(visitCard7);

        CardView card8 = (CardView) getView().findViewById(R.id.card8);
        TextView time_label_8 = (TextView) getView().findViewById(R.id.time_label_8);
        TextView time_8 = (TextView) getView().findViewById(R.id.time_8);
        TextView date_label_8 = (TextView) getView().findViewById(R.id.date_label_8);
        TextView date_8 = (TextView) getView().findViewById(R.id.date_8);
        TextView status_label_8 = (TextView) getView().findViewById(R.id.status_label_8);
        TextView status_8 = (TextView) getView().findViewById(R.id.status_8);

        VisitCard visitCard8 = new VisitCard(card8, date_label_8, time_label_8, status_label_8,
                date_8, time_8, status_8, getActivity().getApplicationContext());
        VisitCards.add(visitCard8);

        CardView card9 = (CardView) getView().findViewById(R.id.card9);
        TextView time_label_9 = (TextView) getView().findViewById(R.id.time_label_9);
        TextView time_9 = (TextView) getView().findViewById(R.id.time_9);
        TextView date_label_9 = (TextView) getView().findViewById(R.id.date_label_9);
        TextView date_9 = (TextView) getView().findViewById(R.id.date_9);
        TextView status_label_9 = (TextView) getView().findViewById(R.id.status_label_9);
        TextView status_9 = (TextView) getView().findViewById(R.id.status_9);

        VisitCard visitCard9 = new VisitCard(card9, date_label_9, time_label_9, status_label_9,
                date_9, time_9, status_9, getActivity().getApplicationContext());
        VisitCards.add(visitCard9);
    }


}
