package com.hit.fightness;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    ViewPagerAdapter viewPagerAdapter;
    ImageButton facebook, whatsapp, instagram;
    float v = 0;

    //create the users tabel in firebase
//    FirebaseDatabase database = FirebaseDatabase.getInstance();
//    DatabaseReference users = database.getReference("users");

    public static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener authStateListener = firebaseAuth -> {
//                viewPagerAdapter.getItemPosition()
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {//signup or login להיכנס לאפליקציה
            Intent intent = new Intent(MainActivity.this, DrawerMenuActivity.class);
            startActivity(intent);
            finish();
        } else { //sign out לחזור לעמוד הראשי של התחברות

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);
        facebook = findViewById(R.id.facebook);
        whatsapp = findViewById(R.id.whatsapp);
        instagram = findViewById(R.id.insta);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.AddFragment(new LoginFragment(), getString(R.string.login));
        viewPagerAdapter.AddFragment(new GuestFragment(), getString(R.string.guest));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });

        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = "972527251313";
                String url = "https://api.whatsapp.com/send?phone="+phone;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                i.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
                startActivity(i);
            }
        });

        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://instagram.com/fightness_adam?r=nametag");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage("com.instagram.android");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://instagram.com/fightness_adam?r=nametag")));
                }
            }
        });
    }
}