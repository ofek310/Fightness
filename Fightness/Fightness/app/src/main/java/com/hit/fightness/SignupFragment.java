package com.hit.fightness;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class SignupFragment extends DialogFragment {
    EditText username, email, password, confirm;
    Button signupButton;
    SwitchMaterial admin;
    float v = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        username = view.findViewById(R.id.username);
        email = view.findViewById(R.id.email_id);
        password = view.findViewById(R.id.password_id);
//        confirm = view.findViewById(R.id.confirm_password_id);
        signupButton = view.findViewById(R.id.login_btn);
        signupButton = view.findViewById(R.id.signup_btn);
        admin = view.findViewById(R.id.is_admin);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = username.getText().toString();
                String email_str = email.getText().toString();
                String password_str = password.getText().toString();

                FirebaseDatabase database = FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/");
                DatabaseReference users = database.getReference().child("users");

//                if (admin.isChecked()) {
//                    FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("admin").push().setValue(true);
//                }

                MainActivity.firebaseAuth.createUserWithEmailAndPassword(email_str, password_str).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                            Snackbar.make(view, getString(R.string.signup_succ), Snackbar.LENGTH_SHORT).show();
                            String token = FirebaseInstanceId.getInstance().getToken();
                            database.getReference().child("tokens").child(MainActivity.firebaseAuth.getCurrentUser().getUid()).child("token").setValue(token);
                            UserDetails newUser = new UserDetails(name, email_str, "","",0,"","",null, admin.isChecked());
                            users.child(MainActivity.firebaseAuth.getCurrentUser().getUid()).setValue(newUser);
                        } else {
                            Snackbar.make(view, getString(R.string.signup_not), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
                dismiss();
            }
        });



        return view;
    }
}