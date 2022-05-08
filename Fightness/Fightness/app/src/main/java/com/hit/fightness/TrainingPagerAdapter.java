package com.hit.fightness;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TrainingPagerAdapter extends FragmentStatePagerAdapter {

    public TrainingPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        //need to change 4 to number of children in firebase
        return TrainingOptionsFragment.newInstance("training"+position % 4);
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }
}
