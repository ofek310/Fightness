package com.hit.fightness;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MoreAdapter extends RecyclerView.Adapter<MoreAdapter.MoreViewHolder>{
    ArrayList<Participant> names = new ArrayList<Participant>();

    public MoreAdapter(ArrayList<Participant> names) {
        this.names = names;
    }

    @NonNull
    @Override
    public MoreAdapter.MoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.participant_card,parent,false);
        MoreViewHolder moreViewHolder = new MoreViewHolder(view);
        return moreViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MoreViewHolder holder, int position) {
        Participant participant = names.get(position);
        holder.name.setText(participant.getName());

    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class MoreViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public MoreViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name_more_card);

        }
    }
}
