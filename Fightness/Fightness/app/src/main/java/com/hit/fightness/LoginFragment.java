package com.hit.fightness;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginFragment extends Fragment {

    EditText email, password;
    Button forgotPassword;
    Button loginButton;

    String DIALOG_FRAGMENT_TAG = "dialog";

    float v = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        email = view.findViewById(R.id.email_id);
        password = view.findViewById(R.id.password_id);

        forgotPassword = view.findViewById(R.id.forgotPassword);
        loginButton = view.findViewById(R.id.login_btn);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/");

        loginButton = view.findViewById(R.id.login_btn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_str = email.getText().toString();
                String password_str = password.getText().toString();
                ProgressDialog pd = new ProgressDialog(getContext());
                pd.setMessage(getString(R.string.loading));
                pd.show();

                MainActivity.firebaseAuth.signInWithEmailAndPassword(email_str, password_str).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Snackbar.make(view, R.string.login_succ, Snackbar.LENGTH_SHORT).show();
                            email.setText("");
                            password.setText("");
                            String token = FirebaseInstanceId.getInstance().getToken();
                            database.getReference().child("tokens").child(MainActivity.firebaseAuth.getCurrentUser().getUid()).child("token").setValue(token);

                        } else {
                            Snackbar.make(view, R.string.login_not, Snackbar.LENGTH_SHORT).show();
                        }
                        pd.dismiss();
                    }
                });
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ForgotPasswordFragment fragment = new ForgotPasswordFragment();
                fragment.show(getActivity().getSupportFragmentManager(), DIALOG_FRAGMENT_TAG);
            }
        });

        return view;
    }
}