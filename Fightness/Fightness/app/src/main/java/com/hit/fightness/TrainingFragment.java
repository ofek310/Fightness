package com.hit.fightness;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hit.fightness.ui.home.HomeViewModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class TrainingFragment extends Fragment {
    final String DIALOG_FRAGMENT_TAG = "dialog_fragment";
    RecyclerView recyclerView;
    int dayInt, monthInt,yearInt;
    WorkoutAdapter workoutAdapter;
    ImageView add_workout;
    ArrayList<Workout> workoutArrayList = new ArrayList<Workout>();
    String day_string, month_string;
    ArrayList<Workout> workout1 = new ArrayList<Workout>();
    ArrayList<String> waiting1 = new ArrayList<String>();

    final int ID_WORKOUT = 1;

    public TrainingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_training, container, false);


        recyclerView = root.findViewById(R.id.recycler_view_workouts);

        add_workout = root.findViewById(R.id.add_workout);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference users = database.getReference("users");
        users.child(MainActivity.firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("admin").exists()) {
                    if (snapshot.child("admin").getValue().equals(true)) {
                        add_workout.setVisibility(View.VISIBLE);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DAY_OF_MONTH, 0);

        /* ends after 1 month from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DAY_OF_MONTH, 6);

        load_workouts_day(startDate);
        // on below line we are setting up our horizontal calendar view and passing id our calendar view to it.
        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(root,R.id.calendarView)
                // on below line we are adding a range
                // as start date and end date to our calendar.
                .range(startDate, endDate)
                // on below line we are providing a number of dates
                // which will be visible on the screen at a time.
                .datesNumberOnScreen(5)
                // at last we are calling a build method
                // to build our horizontal recycler view.
                .build();
        // on below line we are setting calendar listener to our calendar view.
        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                // on below line we are printing date
                // in the logcat which is selected.
                Log.e("TAG", "CURRENT DATE IS " + date);
                load_workouts_day(date);
            }
        });


        return root;
    }

    public void load_workouts_day(Calendar date) {

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference schedule = database.getReference("workoutsSchedule");
        DatabaseReference day = schedule.child("day"+(date.get(Calendar.DAY_OF_WEEK) - 1));

        dayInt = date.get(Calendar.DATE);
        monthInt = date.get(Calendar.MONTH)+1;
        yearInt = date.get(Calendar.YEAR);

        if (dayInt<10) {
            day_string = "0"+dayInt;
        } else {
            day_string = String.valueOf(dayInt);
        }

        if (monthInt<10) {
            month_string = "0"+monthInt;
        } else {
            month_string = String.valueOf(monthInt);
        }

        workoutAdapter = new WorkoutAdapter(workoutArrayList, getActivity(),day_string+"."+month_string+"."+yearInt );

        recyclerView.setAdapter(workoutAdapter);
        day.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                workoutArrayList.removeAll(workoutArrayList);
                for (int i = 0; i<snapshot.getChildrenCount(); i++) {
                    if (!(snapshot.child("workout"+i).child("date").getValue().equals(day_string+"."+month_string+"."+yearInt))) {
                        day.child("workout" + i).child("participants").removeValue();
                        day.child("workout"+i).child("date").setValue(day_string + "." + month_string + "." + yearInt);
                        String workout_id = day.child("workout"+i).child("id").push().getKey();
                        day.child("workout"+i).child("id").setValue(workout_id);
                    }
                    Workout workout = snapshot.child("workout"+i).getValue(Workout.class);
                    if (snapshot.child("workout"+i).child("participants").exists()) {
                        ArrayList<String> part = (ArrayList<String>) snapshot.child("workout"+i).child("participants").getValue();
                        workout.setParticipants(part);
                    } else {
                        ArrayList<String> part = new ArrayList<>();
                        workout.setParticipants(part);
                    }


                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date()); //today
                    String done_date = workout.getDate();

                    String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                    String[] separated1 = currentTime.split(":");
                    String[] separeted2 = workout.getTime().split(":");
                    int hour1 = Integer.parseInt(separated1[0]);
                    int hour2 = Integer.parseInt(separeted2[0]);

                    try {
                        Date currentTimeDate = sdf.parse(currentDateandTime);
                        Date endTimeDate = sdf.parse(done_date);
                        if (done_date.equals(currentDateandTime)) {
                            if (hour1 <= hour2) {
                                workoutArrayList.add(workout);
                            }
                        } else {
                            workoutArrayList.add(workout);
                        }
                    } catch (ParseException ignored) {

                    }

                }
                workoutAdapter = new WorkoutAdapter(workoutArrayList, getActivity(),day_string+"."+month_string+"."+yearInt);

                recyclerView.setAdapter(workoutAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                String e = error.toString();
            }
        });

        add_workout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditWorkoutFragment editWorkoutFragment = EditWorkoutFragment.newInstance(date.get(Calendar.DAY_OF_WEEK) - 1, workoutArrayList.size(), day_string+"."+month_string+"."+yearInt);
                editWorkoutFragment.show(getActivity().getSupportFragmentManager(), DIALOG_FRAGMENT_TAG);
            }
        });



    }

}