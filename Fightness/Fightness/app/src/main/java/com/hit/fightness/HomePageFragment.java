package com.hit.fightness;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomePageFragment extends Fragment {

    DataSnapshot ref;
    ProgressDialog pd;
    final String SIGNUP_FRAGMENT_TAG = "signup_dialog";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home_page, container, false);
        ImageView image_user = root.findViewById(R.id.image_user_home);
        TextView user_name = root.findViewById(R.id.full_name);
        TextView height = root.findViewById(R.id.height_val);
        TextView weight = root.findViewById(R.id.weight_val);
        TextView num_done = root.findViewById(R.id.num_done);
        ProgressBar progressBar = root.findViewById(R.id.progressBar);

        Button signup = root.findViewById(R.id.signup_admin_btn);
        Button renewal = root.findViewById(R.id.package_btn);
        LinearLayout num_training = root.findViewById(R.id.num_training);

        ViewPager viewPager = root.findViewById(R.id.view_pager_image);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference users = database.getReference("users");
        DatabaseReference home = database.getReference("home");

        database.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("package").child(MainActivity.firebaseAuth.getCurrentUser().getUid()).exists()) {
                    ref = snapshot.child("package").child(MainActivity.firebaseAuth.getCurrentUser().getUid()).child(String.valueOf(snapshot.child("package").child(MainActivity.firebaseAuth.getCurrentUser().getUid()).getChildrenCount()-1));
                    num_done.setText(ref.child("done").getValue().toString() + "/ "+  ref.child("amount").getValue().toString());
                    progressBar.setProgress(Integer.parseInt(ref.child("done").getValue().toString()));
                    progressBar.setMax(Integer.parseInt(ref.child("amount").getValue().toString()));
                } else {
                    num_training.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        users.child(MainActivity.firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("admin").exists()) {
                    if (snapshot.child("admin").getValue().equals(true)) {
                        signup.setVisibility(View.VISIBLE);
                        renewal.setVisibility(View.VISIBLE);
                        num_training.setVisibility(View.GONE);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//        viewPager.setCurrentItem(0);

        if (!MainActivity.firebaseAuth.getCurrentUser().isAnonymous()) {
           pd = new ProgressDialog(getContext());
            pd.setMessage(getString(R.string.loading));
            pd.show();
            users.child(MainActivity.firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserDetails user = snapshot.getValue(UserDetails.class);
                    if (user.getImage() == null) {
                        Glide.with(getContext())
                                .load(R.drawable.ic_baseline_account_circle_24)
                                .into(image_user);
                    } else {
                        Glide.with(getContext())
                                .load(user.getImage()).circleCrop()
                                .into(image_user);
                    }
                    user_name.setText(user.getName());
                    if (user.getWeight().equals("")) {
                        weight.setText(user.getWeight());
                    }
                    else {
                        String w = user.getWeight() + " " + getString(R.string.kg);
                        weight.setText(w);
                    }

                    height.setText(user.getHeight());


                    TrainingPagerAdapter trainingPagerAdapter = new TrainingPagerAdapter(getChildFragmentManager());
                    viewPager.setAdapter(trainingPagerAdapter);
                    pd.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            pd = new ProgressDialog(getContext());
            pd.setMessage(getString(R.string.loading));
            pd.show();
            Glide.with(getContext())
                    .load(R.drawable.ic_baseline_account_circle_24)
                    .into(image_user);
            user_name.setText(R.string.guest);
            height.setText("");
            weight.setText("");
            TrainingPagerAdapter trainingPagerAdapter = new TrainingPagerAdapter(getChildFragmentManager());
            viewPager.setAdapter(trainingPagerAdapter);
            pd.dismiss();
        }


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               SignupFragment sign = new SignupFragment();
               sign.show(getActivity().getSupportFragmentManager(), SIGNUP_FRAGMENT_TAG);
            }
        });

        renewal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackegeRenewalFragment packegeRenewalFragment = new PackegeRenewalFragment();
                packegeRenewalFragment.show(getActivity().getSupportFragmentManager(), SIGNUP_FRAGMENT_TAG);
            }
        });

        // Inflate the layout for this fragment
        return root;
    }
}