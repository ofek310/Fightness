package com.hit.fightness;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

public class ForgotPasswordFragment extends DialogFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        TextInputLayout email = root.findViewById(R.id.forgot_mail);
        Button btn = root.findViewById(R.id.forgot_btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_str = email.getEditText().getText().toString();
                if (email_str != null && !email_str.matches("")) {
                    MainActivity.firebaseAuth.sendPasswordResetEmail(email_str).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), getString(R.string.chack_mail), Toast.LENGTH_SHORT).show();
                                getDialog().dismiss();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), getString(R.string.enter_mail), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return root;

    }
}