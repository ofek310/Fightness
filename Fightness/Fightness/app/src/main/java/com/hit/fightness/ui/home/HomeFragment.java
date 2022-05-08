package com.hit.fightness.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hit.fightness.DrawerMenuActivity;
import com.hit.fightness.HomePageFragment;
import com.hit.fightness.MainActivity;
import com.hit.fightness.R;
import com.hit.fightness.TipPageFragment;
import com.hit.fightness.TrainingFragment;
import com.hit.fightness.UserDetails;
import com.hit.fightness.ViewPagerHome;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    ViewPager pager;
    TabLayout mTablaoyout;
    TabItem first, second, three;
    ViewPagerHome adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        pager = root.findViewById(R.id.viewPagerHome);
        mTablaoyout = root.findViewById(R.id.tabLayoutHome);
        first = root.findViewById(R.id.firstitem);
        second = root.findViewById(R.id.seconditem);
        three = root.findViewById(R.id.threeitem);

        adapter = new ViewPagerHome(getChildFragmentManager(), ViewPagerHome.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, mTablaoyout.getTabCount());
        pager.setAdapter(adapter);

        mTablaoyout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        pager.setCurrentItem(0, true);

        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTablaoyout));


        return root;

    }
}