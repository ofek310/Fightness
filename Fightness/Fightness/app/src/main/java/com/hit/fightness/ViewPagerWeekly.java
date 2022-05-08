package com.hit.fightness;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerWeekly extends FragmentPagerAdapter {

    private final List<Fragment> fragmentName = new ArrayList<>();
    private final List<String> fragmentTitle = new ArrayList<>();
    private int tabsNum;

    public ViewPagerWeekly(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentName.get(position);
    }

    @Override
    public int getCount() {
        return fragmentTitle.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitle.get(position);
    }

    public void AddFragment(Fragment fragment, String title) {
        fragmentName.add(fragment);
        fragmentTitle.add(title);
    }
}
