package com.hit.fightness;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Telephony;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MoreFragment extends DialogFragment {
    String date;
    String hour;
    String amount;
    ArrayList<String> participants = new ArrayList<String>();
    ArrayList<Participant> names = new ArrayList<Participant>();
    MoreAdapter moreAdapter;

    public MoreFragment() {
        // Required empty public constructor
    }

    public static MoreFragment newInstance(String name,String date, String hour, String trainer, ArrayList<String> participants) {
        MoreFragment moreFragment = new MoreFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name",name);
        bundle.putString("date", date);
        bundle.putString("hour", hour);
        bundle.putString("trainer", trainer);
        bundle.putStringArrayList("array",participants);
        moreFragment.setArguments(bundle);
        return moreFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_more, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

//        participants.removeAll(participants);

        TextView name_training = root.findViewById(R.id.name_workout_more);
        TextView date_more = root.findViewById(R.id.date_more);
        TextView hour_more = root.findViewById(R.id.hour_more);
        TextView trainer = root.findViewById(R.id.trainer_name_more);
        Button ok_btn = root.findViewById(R.id.btn_ok_more);
        RecyclerView recyclerViewMore = root.findViewById(R.id.recycler_participants);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference users = database.getReference("users");

        name_training.setText(getArguments().getString("name"));
        date_more.setText(getArguments().getString("date"));
        hour_more.setText(getArguments().getString("hour"));
        trainer.setText(getArguments().getString("trainer"));
        participants = getArguments().getStringArrayList("array");

        names.removeAll(names);
        for (int i = 0; i < participants.size(); i++) {

            users.child(participants.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Participant participant = new Participant(snapshot.child("name").getValue().toString());
                    names.add(participant);
                    moreAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        recyclerViewMore.setLayoutManager(new LinearLayoutManager(getContext()));
        moreAdapter = new MoreAdapter(names);
        recyclerViewMore.setAdapter(moreAdapter);

        TextView title = root.findViewById(R.id.title_pati_list);
        if (participants.size() == 0) {
            title.setText(getString(R.string.no_parti));
            recyclerViewMore.setVisibility(View.GONE);
        }

        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        return root;
    }
}