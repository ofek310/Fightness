package com.hit.fightness;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class PackegeRenewalFragment extends DialogFragment implements AdapterView.OnItemSelectedListener{

    String key = "";
    boolean added = false;
    ArrayList<Package> packages;
    int dayInt, monthInt,yearInt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_packege_renewal, container, false);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        packages = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/");


        TextInputEditText email = view.findViewById(R.id.email_package);
        TextInputEditText date = view.findViewById(R.id.date_package);
        TextInputEditText price = view.findViewById(R.id.price_package);

        Spinner amount = view.findViewById(R.id.spinner_amounttraining);
        ArrayAdapter<CharSequence> adapter_amount = ArrayAdapter.createFromResource(getContext(), R.array.amount_trainings, android.R.layout.simple_spinner_item);
        adapter_amount.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        amount.setAdapter(adapter_amount);
        amount.setOnItemSelectedListener(this);

        Button renewal_btn = view.findViewById(R.id.btn_add_package);


        date.setInputType(InputType.TYPE_NULL);
        date.setKeyListener(null);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                dayInt = calendar.get(Calendar.DATE);
                monthInt = calendar.get(Calendar.MONTH);
                yearInt = calendar.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), R.style.MyDatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month++;
                        if (month<10 && day<10) {
                            date.setText("0"+day +".0"+month+"."+year);
                        } else if (month>10 && day<10) {
                            date.setText("0"+day + "." + month + "." + year);
                        } else if (month<10 && day>10) {
                            date.setText(day + ".0" + month + "." + year);
                        } else {
                            date.setText(day + "." + month + "." + year);
                        }
                    }
                },yearInt,monthInt,dayInt);
                datePickerDialog.show();
            }
        });

        renewal_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                packages.removeAll(packages);

                DatabaseReference users = FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
                database.getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot data : snapshot.child("users").getChildren()) {
                                if (data.child("email").getValue().equals(email.getText().toString())) {
                                    key = data.getKey().toString();
                                    if (snapshot.child("package").child(key).exists()) {
                                        packages = (ArrayList<Package>) snapshot.child("package").child(key).getValue();
                                    }

                                    if (packages.size() != 0 && snapshot.child("package").child(key).child(String.valueOf(packages.size()-1)).child("done").getValue() != snapshot.child("package").child(String.valueOf(packages.size()-1)).child("amount").getValue()) {
                                        Toast.makeText(getContext(), getString(R.string.left_more), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Package p = new Package(date.getText().toString(), price.getText().toString(), Integer.parseInt(amount.getSelectedItem().toString()), email.getText().toString());
                                        packages.add(p);
                                        database.getReference().child("package").child(key).setValue(packages);
                                        added = true;
                                    }

                                }
                            }
                        }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                getDialog().dismiss();
            }
        });

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}