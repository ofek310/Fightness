package com.hit.fightness.ui.contact;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hit.fightness.DoneWorkout;
import com.hit.fightness.HistoryAdapter;
import com.hit.fightness.MainActivity;
import com.hit.fightness.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HistoryFragment extends Fragment {

    private HistoryViewModel slideshowViewModel;
    HistoryAdapter historyAdapter;
    ArrayList<DoneWorkout> doneList = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                new ViewModelProvider(this).get(HistoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_history, container, false);

        FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("users").child(MainActivity.firebaseAuth.getCurrentUser().getUid()).child("myWorkouts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                doneList.removeAll(doneList);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DoneWorkout done = dataSnapshot.getValue(DoneWorkout.class);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                    String currentDateandTime = sdf.format(new Date()); //today
                    String done_date = done.getDate();

                    String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                    String[] separated1 = currentTime.split(":");
                    String[] separeted2 = done.getTime().split(":");
                    int hour1 = Integer.parseInt(separated1[0]);
                    int hour2 = Integer.parseInt(separeted2[0]);

                    try {
                        Date currentTimeDate = sdf.parse(currentDateandTime);
                        Date endTimeDate = sdf.parse(done_date);
//                        currentTimeDate.compareTo(endTimeDate); // false / current time has not passed end time.
                        if (new SimpleDateFormat("dd.MM.yyyy").parse(done_date).before(currentTimeDate)) {
                            // true / end time has passed current time.
                            doneList.add(done);
                        } else if (done_date.equals(currentDateandTime)) {
                            if (hour1 >= hour2) {
                                doneList.add(done);
                            }
                        }
                    } catch (ParseException ignored) {

                    }

                }

                RecyclerView recyclerView = root.findViewById(R.id.recycler_history);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                historyAdapter = new HistoryAdapter(getContext(), doneList);
                recyclerView.setAdapter(historyAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return root;
    }
}