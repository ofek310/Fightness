package com.hit.fightness;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hit.fightness.ui.contact.HistoryFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private Context context;
    ArrayList<DoneWorkout> doneList = new ArrayList<>();

    public HistoryAdapter(Context mContext, ArrayList<DoneWorkout> doneList) {
        this.context = mContext;
        this.doneList = doneList;
    }


    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.package_item, parent, false);
        HistoryAdapter.ViewHolder historyViewHolder = new HistoryAdapter.ViewHolder(view);
        return historyViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {

        DoneWorkout done = doneList.get(position);

        holder.date.setText(done.getDate());
        holder.time.setText(done.getTime());
        holder.name.setText(done.getName_workout());

    }

    @Override
    public int getItemCount() {
        return doneList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView date;
        TextView time;
        TextView name;
        TextView done;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date_done);
            time = itemView.findViewById(R.id.time_done);
            name = itemView.findViewById(R.id.workout_name_done);
            done = itemView.findViewById(R.id.done_or_not);
        }


    }
}


