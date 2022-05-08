package com.hit.fightness;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hit.fightness.ui.aboutus.AboutUsFragment;
import com.hit.fightness.ui.contact.HistoryFragment;
import com.hit.fightness.ui.home.HomeFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

public class DrawerMenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private AppBarConfiguration mAppBarConfiguration;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    View headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_drawer_menu);
        Toolbar toolbar = findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();


        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home_page, R.id.nav_aboutus, R.id.nav_history, R.id.nav_terms, R.id.nav_profile)
                .setDrawerLayout(drawer)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        if (MainActivity.firebaseAuth.getCurrentUser().isAnonymous()) {
            navigationView.getMenu().findItem(R.id.nav_profile).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_history).setVisible(false);
//            navigationView.getMenu().getItem(R.id.nav_history).setVisible(false);
//            navigationView.getMenu().getItem(R.id.nav_profile).setVisible(false);
        }

        navigationView.getMenu().findItem(R.id.instagram_menu).setOnMenuItemClickListener(menuItem -> {
            Uri uri = Uri.parse("https://instagram.com/fightness_adam?r=nametag");
            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

            likeIng.setPackage("com.instagram.android");

            try {
                startActivity(likeIng);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://instagram.com/fightness_adam?r=nametag")));
            }
            return true;
        });

        navigationView.getMenu().findItem(R.id.facebook_menu).setOnMenuItemClickListener(menuItem -> {
            String url = "https://www.facebook.com/adam.eli.5";
            Uri uri = Uri.parse(url);

            try {
                ApplicationInfo applicationInfo = getApplicationContext().getPackageManager().getApplicationInfo("com.facebook.katana", 0);
                if (applicationInfo.enabled) {
                    // http://stackoverflow.com/a/24547437/1048340
                    uri = Uri.parse("fb://facewebmodal/f?href=" + url);
                }
            } catch (PackageManager.NameNotFoundException ignored) {
            }
            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
            try {
                startActivity(likeIng);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(url)));
            }
            return true;
        });

        navigationView.getMenu().findItem(R.id.whatsapp_menu).setOnMenuItemClickListener(menuItem -> {
            String phone = "972527251313";
            String url = "https://api.whatsapp.com/send?phone="+phone;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            i.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
            startActivity(i);
            return true;
        });

        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(menuItem -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.logout).setMessage(R.string.sure_logout).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    MainActivity.firebaseAuth.signOut();
                    finish();
                }
            }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    navigationView.getCheckedItem().setChecked(false);
                    drawer.closeDrawer(GravityCompat.START);
                }
            }).setCancelable(false).show();
            return true;
        });
        headerView = navigationView.getHeaderView(0);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference users = database.getReference("users");

        ImageView image_profile_menu = headerView.findViewById(R.id.imageView_menu);
        TextView user_name = headerView.findViewById(R.id.user_name_menu);
        TextView user_mail = headerView.findViewById(R.id.email_user);

        if (!MainActivity.firebaseAuth.getCurrentUser().isAnonymous()) {
            users.child(MainActivity.firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserDetails user = snapshot.getValue(UserDetails.class);
                    if (user.getImage() == null) {
                        Glide.with(getBaseContext())
                                .load(R.drawable.ic_baseline_account_circle_24)
                                .into(image_profile_menu);
                    } else {
                        Glide.with(getBaseContext())
                                .load(user.getImage()).circleCrop()
                                .into(image_profile_menu);
                    }
                    user_name.setText(user.getName());
                    user_mail.setText(user.getEmail());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
        return true;
    }



    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference users = database.getReference("users");

        ImageView image_profile_menu = headerView.findViewById(R.id.imageView_menu);
        TextView user_name = headerView.findViewById(R.id.user_name_menu);
        TextView user_mail = headerView.findViewById(R.id.email_user);

        users.child(MainActivity.firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails user = snapshot.getValue(UserDetails.class);
                if (user.getImage() == null) {
                    Glide.with(getBaseContext())
                            .load(R.drawable.ic_baseline_account_circle_24)
                            .into(image_profile_menu);
                } else {
                    Glide.with(getBaseContext())
                            .load(user.getImage()).circleCrop()
                            .into(image_profile_menu);
                }
                user_name.setText(user.getName());
                user_mail.setText(user.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}