package com.hit.fightness;

import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hit.fightness.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerHome extends FragmentPagerAdapter {

    private int tabsNum;

    public ViewPagerHome(@NonNull FragmentManager fm,int behavior, int tabs)
    {
        super(fm, behavior);
        this.tabsNum = tabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                notifyDataSetChanged();
                return new HomePageFragment();

            case 1:
                return new TrainingFragment();
            case 2:
                if (!MainActivity.firebaseAuth.getCurrentUser().isAnonymous()) {
                    return new TipPageFragment();
                } else {
                    return new AnonymusFragment();
                }

            default: return null;
        }
    }

    @Override
    public int getCount() {
        return tabsNum;
    }

}
