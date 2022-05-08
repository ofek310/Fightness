package com.hit.fightness;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TipPageFragment extends Fragment {

    public TipPageFragment() {
        // Required empty public constructor
    }

    static BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;
    static Fragment addpost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_tip_page, container, false);

        bottomNavigationView = root.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.nav_feed:
                        selectorFragment = new FeedFragment();
                        break;
                    case R.id.nav_add:
                        selectorFragment = new AddPostFragment();
                        addpost = selectorFragment;
                        break;
                    case R.id.nav_like:
                        selectorFragment = new NotificationsPostFragment();
                        break;
                }
                if (selectorFragment != null) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_tip, selectorFragment).commit();
                }
                return true;
            }
        });

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_tip, new FeedFragment()).commit();

        return root;
    }
}