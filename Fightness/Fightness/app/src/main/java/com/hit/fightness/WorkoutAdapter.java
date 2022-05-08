package com.hit.fightness;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {
    final String DIALOG_FRAGMENT_TAG = "dialog_fragment";
    ArrayList<Workout> workoutList;
    Context context;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/");
    DatabaseReference users = database.getReference("users");
    public static Workout workout;
    String date;
    ArrayList<Workout> workout1 = new ArrayList<Workout>();
    ArrayList<String> waiting1 = new ArrayList<String>();
    String last_package;
    String done;

    private EditListener listener;

    interface EditListener{
        void onEditClicked(String type, String trainer, String time, int quantity, int position, long day);
    }
    public void setListener(EditListener listener){
        this.listener =listener;
    }

    public WorkoutAdapter(ArrayList<Workout> workoutList, Context context, String date) {
        this.workoutList = workoutList;
        this.context = context;
        this.date = date;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_workout,parent,false);
        WorkoutViewHolder workoutViewHolder = new WorkoutViewHolder(view);
        return workoutViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        ArrayList<String> participants = new ArrayList<String>();
        ArrayList<String> waitingList = new ArrayList<String>();
        ArrayList<DoneWorkout> doneWorkouts = new ArrayList<DoneWorkout>();
        //firebase
        workout = workoutList.get(position);
        holder.name_workoutTV.setText(workout.getType());
        holder.name_trainerTV.setText(workout.getTrainer());
        holder.timeTV.setText(workout.getTime());

        if (MainActivity.firebaseAuth.getCurrentUser().isAnonymous()) {
            holder.more_btn.setVisibility(View.GONE);
            holder.join_btn.setVisibility(View.GONE);
        }

        DatabaseReference workoutsSchedule = database.getReference("workoutsSchedule").child("day" + workout.getDay()).child("workout" + position);
        DatabaseReference reference = users.child(MainActivity.firebaseAuth.getCurrentUser().getUid());

        users.child(MainActivity.firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("myWorkouts").exists()) {
                    doneWorkouts.removeAll(doneWorkouts);
                    for (DataSnapshot dataSnapshot : snapshot.child("myWorkouts").getChildren()) {
                        DoneWorkout workout1 = dataSnapshot.getValue(DoneWorkout.class);
                        doneWorkouts.add(workout1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        workoutsSchedule.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                waitingList.removeAll(waitingList);
                if (snapshot.child("waiting").exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.child("waiting").getChildren()) {
                        String wait = MainActivity.firebaseAuth.getCurrentUser().getUid();
                        waitingList.add(wait);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        workoutsSchedule.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                participants.removeAll(participants);
                if (snapshot.child("participants").exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.child("participants").getChildren()) {
                        String str = dataSnapshot.getValue(String.class);
                        participants.add(str);
                        if (str.equals(MainActivity.firebaseAuth.getCurrentUser().getUid())) {
                            holder.join_btn.setText(holder.itemView.getContext().getString(R.string.cancel));
                            holder.join_btn.setTextColor(Color.WHITE);
                            holder.join_btn.setBackgroundResource(R.drawable.button_black_bg);
                        } else if (snapshot.child("quantity").getValue().equals(snapshot.child("participants").getChildrenCount())) {
                            holder.join_btn.setText(holder.itemView.getContext().getString(R.string.wait));
                            holder.join_btn.setTextColor(Color.BLACK);
                            holder.join_btn.setBackgroundResource(R.drawable.button_gray_bg);
                        }
                    }
                    if (snapshot.child("waiting").exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.child("waiting").getChildren()) {
                            if (dataSnapshot.getValue().equals(MainActivity.firebaseAuth.getCurrentUser().getUid())) {
                                holder.join_btn.setText(holder.itemView.getContext().getString(R.string.wating));
                                holder.join_btn.setTextColor(Color.WHITE);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        holder.more_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference workoutsSchedule1 = database.getReference("workoutsSchedule").child("day" + workout.getDay()).child("workout" + position);
                workoutsSchedule1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("participants").exists()) {
                            participants.removeAll(participants);
                            for (DataSnapshot dataSnapshot : snapshot.child("participants").getChildren()) {
                                String str = dataSnapshot.getValue(String.class);
                                participants.add(str);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
                MoreFragment moreFragment = MoreFragment.newInstance(holder.itemView.getContext().getString(R.string.training)+" "+ holder.name_workoutTV.getText().toString(), date, holder.timeTV.getText().toString(), holder.itemView.getContext().getString(R.string.trainerr) + " " + holder.name_trainerTV.getText().toString(), participants);
                moreFragment.show(manager, DIALOG_FRAGMENT_TAG);
            }
        });

        holder.join_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database.getReference().child("package").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(MainActivity.firebaseAuth.getCurrentUser().getUid()).exists()) {
                            last_package = String.valueOf(snapshot.child(MainActivity.firebaseAuth.getCurrentUser().getUid()).getChildrenCount()-1);
                            done = String.valueOf(snapshot.child(MainActivity.firebaseAuth.getCurrentUser().getUid()).child(last_package).child("done").getValue());
                            if (snapshot.child(MainActivity.firebaseAuth.getCurrentUser().getUid()).child(last_package).child("done").getValue().equals(snapshot.child(MainActivity.firebaseAuth.getCurrentUser().getUid()).child(last_package).child("amount").getValue()) && (!holder.join_btn.getText().toString().equals(holder.itemView.getContext().getString(R.string.cancel)))) {
                                Toast.makeText(context, holder.itemView.getContext().getString(R.string.noLeftTraining), Toast.LENGTH_SHORT).show();
                            } else {
                                CheckParticipantship(holder, doneWorkouts, participants, workoutsSchedule, waitingList, workout.getId(), done, database.getReference().child("package").child(MainActivity.firebaseAuth.getCurrentUser().getUid()).child(last_package).child("done"), position);

                                //notify 24 hours before this training
                                AlarmManager alarmManager = (AlarmManager) ((AppCompatActivity) context).getSystemService(Context.ALARM_SERVICE);
                                Calendar calendar = Calendar.getInstance();
                                calendar.add(Calendar.SECOND, 20); //how much time

                                Intent intent = new Intent("singh.ajit.action.DISPLAY_NOTIFICATION");
                                PendingIntent broadcast = PendingIntent.getBroadcast(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                alarmManager.setExact(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), broadcast);

                                Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
                                notificationIntent.addCategory("android.intent.category.DEFAULT");

                                Calendar cal = Calendar.getInstance();
                                cal.add(Calendar.SECOND, 15);
                                alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);

                            }
                        } else {
                            Toast.makeText(context, holder.itemView.getContext().getString(R.string.no_left_pack), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                FirebaseDatabase database1 = FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/");
                DatabaseReference users1 = database1.getReference("users");
                users1.child(MainActivity.firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("admin").getValue().equals(true)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle(holder.itemView.getContext().getString(R.string.delete)).setMessage(holder.itemView.getContext().getString(R.string.sure)).setPositiveButton(holder.itemView.getContext().getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    for (int j = 0; j < participants.size(); j++) {
                                        users1.child(participants.get(j)).child("myWorkouts").child(workoutList.get(holder.getPosition()).getId()).removeValue();
                                    }

                                    database1.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.child("workoutsSchedule").child("day" + workout.getDay()).child("workout" + position).child("participants").exists()) {
                                                for (DataSnapshot dataSnapshot : snapshot.child("workoutsSchedule").child("day" + workout.getDay()).child("workout" + position).child("participants").getChildren()) {
                                                    new NotificationActivity(holder.itemView.getContext(), "Canceled " + workout.getType() + " training at "+workout.getDate() +"time "+ workout.getTime(), snapshot.child("tokens").child(dataSnapshot.getValue().toString()).child("token").getValue().toString());

                                                }
                                            }
                                            waiting1.removeAll(waiting1);
                                            workout1.removeAll(workout1);
                                            for (DataSnapshot dataSnapshot : snapshot.child("workoutsSchedule").child("day" + workout.getDay()).getChildren()) {
                                                Workout w = dataSnapshot.getValue(Workout.class);
                                                if (dataSnapshot.child("participants").exists()) {
                                                    ArrayList<String> part = (ArrayList<String>) dataSnapshot.child("participants").getValue();
                                                    w.setParticipants(part);
                                                }
                                                if (dataSnapshot.child("waiting").exists()) {
                                                    ArrayList<String> wait = (ArrayList<String>) dataSnapshot.child("waiting").getValue();
                                                    w.setWaiting(wait);
                                                }
                                                workout1.add(w);
                                            }
                                            workout1.remove(position);
                                            FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("workoutsSchedule").child("day" + workout.getDay()).removeValue();
                                            for (int i = 0; i < workout1.size(); i++) {
                                                FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("workoutsSchedule").child("day" + workout.getDay()).child("workout" + i).setValue(workout1.get(i));
                                            }


                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }


                            }).setNegativeButton(holder.itemView.getContext().getString(R.string.no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).setCancelable(false).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                return true;
            }
        });
    }

    private void CheckParticipantship(WorkoutViewHolder holder, ArrayList<DoneWorkout> doneWorkouts, ArrayList<String> participants, DatabaseReference workoutsSchedule, ArrayList<String> waitingList, String id, String done, DatabaseReference done_package, int position) {
        AlarmManager alarmManager = (AlarmManager) ((AppCompatActivity) holder.itemView.getContext()).getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(holder.itemView.getContext(), AlarmReceiver.class);
        intent.putExtra("date", date);
        intent.putExtra("time", holder.timeTV.getText());
        PendingIntent broadcast = PendingIntent.getBroadcast(holder.itemView.getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (holder.join_btn.getText().equals(holder.itemView.getContext().getString(R.string.join))) { //join to workout
            DoneWorkout doneWorkout = new DoneWorkout(workout.getDate(), holder.name_workoutTV.getText().toString(), holder.timeTV.getText().toString(), workoutList.get(holder.getPosition()).getId());
            doneWorkouts.add(doneWorkout);
            int done_int = Integer.parseInt(done);
            done_package.setValue(++done_int);
            for (int i=0; i<doneWorkouts.size();i++) {
                users.child(MainActivity.firebaseAuth.getCurrentUser().getUid()).child("myWorkouts").child(workoutList.get(holder.getPosition()).getId()).setValue(doneWorkouts.get(i));
            }
            String participant1 = MainActivity.firebaseAuth.getCurrentUser().getUid();
            participants.add(participant1);
            workout.setParticipants(participants);
            workoutsSchedule.child("participants").setValue(participants);
            holder.join_btn.setText(holder.itemView.getContext().getString(R.string.cancel));
            holder.join_btn.setTextColor(Color.WHITE);
            holder.join_btn.setBackgroundResource(R.drawable.button_black_bg);

            try {
                String dateandtime = date + " " + holder.timeTV.getText();
                DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy hh:mm");
                Date date1 = formatter.parse(dateandtime);

                date1.setDate(date1.getDate() - 1);

                alarmManager.set(AlarmManager.RTC_WAKEUP, date1.getTime(), broadcast);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else if (holder.join_btn.getText().equals(holder.itemView.getContext().getString(R.string.cancel))) { //cancel my workout
            for (int i=0; i<doneWorkouts.size();i++) {
                if (date.equals(doneWorkouts.get(i).getDate()) && holder.timeTV.getText().equals(doneWorkouts.get(i).getTime())) {
                    doneWorkouts.remove(i);
                    int done_int = Integer.parseInt(done);
                    done_package.setValue(--done_int);
                }
            }
            for (int i=0; i<doneWorkouts.size();i++) {
                users.child(MainActivity.firebaseAuth.getCurrentUser().getUid()).child("myWorkouts").child(doneWorkouts.get(i).getId()).setValue(doneWorkouts.get(i));
            }

            for (int i=0; i<participants.size();i++) {
                if (MainActivity.firebaseAuth.getCurrentUser().getUid().equals(participants.get(i))) {
                    participants.remove(i);
                }
            }
            workoutsSchedule.child("participants").setValue(participants);
            workout.setParticipants(participants);
            holder.join_btn.setText(holder.itemView.getContext().getString(R.string.join));
            holder.join_btn.setTextColor(Color.BLACK);
            holder.join_btn.setBackgroundResource(R.drawable.button_bg);
            //cancel training notification
            alarmManager.cancel(broadcast);

            database.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child("workoutsSchedule").child("day" + workout.getDay()).child("workout" + position).child("waiting").exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.child("workoutsSchedule").child("day" + workout.getDay()).child("workout" + position).child("waiting").getChildren()) {
                            new NotificationActivity(holder.itemView.getContext(), holder.itemView.getContext().getString(R.string.space_waiting)+" " + workout.getType() +workout.getDate() +holder.itemView.getContext().getString(R.string.time)+ workout.getTime(), snapshot.child("tokens").child(dataSnapshot.getValue().toString()).child("token").getValue().toString());

                        }
                        database.getReference().child("workoutsSchedule").child("day" + workout.getDay()).child("workout" + position).child("waiting").removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else if (holder.join_btn.getText().equals(holder.itemView.getContext().getString(R.string.wait))){ //waiting list
            waitingList.add(MainActivity.firebaseAuth.getCurrentUser().getUid());
            workoutsSchedule.child("waiting").setValue(waitingList);
            holder.join_btn.setText(holder.itemView.getContext().getString(R.string.wating));
            holder.join_btn.setTextColor(Color.WHITE);
        } else {
            for (int i=0; i<waitingList.size();i++) {
                if (MainActivity.firebaseAuth.getCurrentUser().getUid().equals(waitingList.get(i))) {
                    waitingList.remove(i);
                }
            }
            workoutsSchedule.child("waiting").setValue(waitingList);
            holder.join_btn.setText(holder.itemView.getContext().getString(R.string.wait));
            holder.join_btn.setTextColor(Color.BLACK);
        }
    }


    @Override
    public int getItemCount() {
        return workoutList.size();
    }

    public class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView name_workoutTV;
        TextView name_trainerTV;
        TextView timeTV;
        Button join_btn;
        ImageButton more_btn;

        public WorkoutViewHolder(View itemView) {
            super(itemView);
            name_workoutTV = itemView.findViewById(R.id.workout_name);
            name_trainerTV = itemView.findViewById(R.id.trainer_name);
            timeTV = itemView.findViewById(R.id.time_workout);
            join_btn = itemView.findViewById(R.id.button_card);
            more_btn = itemView.findViewById(R.id.more_deatils);

        }
    }
}
