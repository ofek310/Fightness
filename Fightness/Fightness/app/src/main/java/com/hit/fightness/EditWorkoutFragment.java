package com.hit.fightness;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class EditWorkoutFragment extends DialogFragment implements AdapterView.OnItemSelectedListener {

    ArrayList<Workout> workoutArrayList = new ArrayList<Workout>();
    ArrayList<Workout> sortList = new ArrayList<Workout>();
    Workout workout1;


    public EditWorkoutFragment() {
        // Required empty public constructor
    }

    public static EditWorkoutFragment newInstance(int day, int num_workouts, String date) {
        EditWorkoutFragment fragment = new EditWorkoutFragment();
        Bundle args = new Bundle();
        args.putInt("day", day);
        args.putInt("num_workouts", num_workouts);
        args.putString("date", date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_workout, container, false);

//        TextInputEditText name = view.findViewById(R.id.edit_name);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Spinner name_workout = view.findViewById(R.id.spiner_name_workout);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.name_workout, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        name_workout.setAdapter(adapter);
        name_workout.setOnItemSelectedListener(this);

        Spinner trainer = view.findViewById(R.id.trainer_spiner);
        ArrayAdapter<CharSequence> adapter_training = ArrayAdapter.createFromResource(getContext(), R.array.name_trainer, android.R.layout.simple_spinner_item);
        adapter_training.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        trainer.setAdapter(adapter_training);
        trainer.setOnItemSelectedListener(this);

//        TextInputEditText trainer = view.findViewById(R.id.trainer_edit);
//        TextInputEditText time = view.findViewById(R.id.hour_edit);
        Spinner hours = view.findViewById(R.id.hour_spiner);
        ArrayAdapter<CharSequence> adapter_hours = ArrayAdapter.createFromResource(getContext(), R.array.hours, android.R.layout.simple_spinner_item);
        adapter_hours.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hours.setAdapter(adapter_hours);
        hours.setOnItemSelectedListener(this);

        Spinner parti = view.findViewById(R.id.parti_spiner);
        ArrayAdapter<CharSequence> adapter_parti = ArrayAdapter.createFromResource(getContext(), R.array.parti, android.R.layout.simple_spinner_item);
        adapter_parti.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        parti.setAdapter(adapter_parti);
        parti.setOnItemSelectedListener(this);

        Spinner subs = view.findViewById(R.id.sub_spiner);
        ArrayAdapter<CharSequence> adapter_subs = ArrayAdapter.createFromResource(getContext(), R.array.sub, android.R.layout.simple_spinner_item);
        adapter_subs.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subs.setAdapter(adapter_subs);
        subs.setOnItemSelectedListener(this);

//        TextInputEditText quantity = view.findViewById(R.id.participants_edit);
//        TextInputEditText sub = view.findViewById(R.id.sub_edit);
        Button save_btn = view.findViewById(R.id.btn_save_edit);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name_workout.getSelectedItemPosition()!= 0 || trainer.getSelectedItemPosition()!= 0 || hours.getSelectedItemPosition()!= 0 || parti.getSelectedItemPosition()!= 0) {

                    DatabaseReference ref = FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/").getReference("workoutsSchedule")
                            .child("day" + getArguments().getInt("day"));


                    String num_workout = "workout" + getArguments().getInt("num_workouts");
                    String workout_id = ref.child(num_workout).child("id").push().getKey();

                    Workout workout = new Workout(getArguments().getString("date"), name_workout.getSelectedItem().toString(), trainer.getSelectedItem().toString(), hours.getSelectedItem().toString(), Integer.parseInt(parti.getSelectedItem().toString()), subs.getSelectedItem().toString(), getArguments().getInt("day"), workout_id, new ArrayList<String>(), new ArrayList<String>());


                    DatabaseReference ref1 = FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/").getReference("workoutsSchedule");

                    ref1.child("day" + getArguments().getInt("day")).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Workout workout = dataSnapshot.getValue(Workout.class);
                                workoutArrayList.add(workout);
                            }

                            boolean added = false;
                            int pos_add = -1;
                            for (int i = 0; i<workoutArrayList.size() && !added; i++) {
                                String currentString = workoutArrayList.get(i).getTime();
                                String[] separated = currentString.split(":");

                                String newString = hours.getSelectedItem().toString();
                                String[] newSep = newString.split(":");

                                if (!added && Integer.parseInt(separated[0]) >= Integer.parseInt(newSep[0])) {
                                    added = true;
                                    pos_add = i;
                                }
                            }
                            if (!added) {
                                pos_add = workoutArrayList.size();
                            }

                            for (int j = workoutArrayList.size() - 1; j >= pos_add; j--) {
                                int index = j+1;

                                FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/").getReference("workoutsSchedule").child("day" + getArguments().getInt("day"))
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ArrayList<String> part = (ArrayList<String>) snapshot.child("workout"+(index-1)).child("participants").getValue();
                                        workout1 = snapshot.child("workout"+(index-1)).getValue(Workout.class);
                                        ref.child("workout"+(index)).setValue(workout1);
                                        ref.child("workout"+(index)).child("participants").setValue(part);
                                        }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                            ref.child("workout"+pos_add).setValue(workout);
                            ref.child("workout"+pos_add).child("date").setValue(getArguments().getString("date"));

                            getDialog().dismiss();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            error.toString();
                        }
                    });

                } else {
                    Toast.makeText(getContext(), getString(R.string.miss_info), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    public void listWorkouts() {

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}