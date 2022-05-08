package com.hit.fightness;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class TrainingOptionsFragment extends Fragment {

    final String DIALOG_FRAGMENT_TAG = "dialog_fragment";

    public static TrainingOptionsFragment newInstance(String training) {
        TrainingOptionsFragment trainingOptionsFragment = new TrainingOptionsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("training", training);
        trainingOptionsFragment.setArguments(bundle);
        return trainingOptionsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_training_options, container, false);

        ImageView imageView = root.findViewById(R.id.training_image);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference home = database.getReference("home");
        DatabaseReference training = home.child(getArguments().getString("training"));
        DatabaseReference training_image = training.child("image");


        training_image.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Glide.with(getContext())
                        .load(snapshot.getValue())
                        .into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TrainingReviewFragment reviewFragment = TrainingReviewFragment.newInstance(getArguments().getString("training"));
                reviewFragment.show(getActivity().getSupportFragmentManager(), DIALOG_FRAGMENT_TAG);
            }
        });
        return root;
    }
}