package com.hit.fightness.ui.profile;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hit.fightness.MainActivity;
import com.hit.fightness.R;
import com.hit.fightness.UserDetails;
import com.hit.fightness.ui.terms.TermsViewModel;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

public class ProfileFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private TermsViewModel slideshowViewModel;

    TextInputEditText name;
    TextInputEditText email;
    TextInputEditText phone_number;
    TextInputEditText birthday;
    Spinner gender;
    TextInputEditText height;
    TextInputEditText weight;
    TextInputLayout birth;
    Button save_button;
    int dayInt, monthInt,yearInt;
    int choice;
    ImageView user_image;
    File file;
    Uri image_profile_uri;
    String imageG = "";
    boolean fromlauncher = false;
    boolean admin;
    ProgressDialog progressDialog;

    StorageReference storageProfilePic;
    StorageTask uploadTask;

    private ActivityResultLauncher<Uri> takeImageResult = registerForActivityResult(new ActivityResultContracts.TakePicture(), isSuccess -> {
        if (isSuccess && file != null) {
            Glide.with(getContext())
                    .load(file.getAbsolutePath())
                    .into(user_image);
            imageG = file.getAbsolutePath();
            fromlauncher=true;
        }
    });
    private ActivityResultLauncher<String> takeImageGallery = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            Glide.with(getContext())
                    .load(result)
                    .into(user_image);
//            imageG=result.toString();
            image_profile_uri = result;
            fromlauncher = true;
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        slideshowViewModel =
                new ViewModelProvider(this).get(TermsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        name = root.findViewById(R.id.userName);
        email = root.findViewById(R.id.email);

        phone_number = root.findViewById(R.id.phoneNumber);

        storageProfilePic = FirebaseStorage.getInstance().getReference().child("Profile Pic");

        birthday = root.findViewById(R.id.birthday);
        gender = root.findViewById(R.id.spinner);
        height = root.findViewById(R.id.height_tf);
        weight = root.findViewById(R.id.weight_tf);

        user_image = root.findViewById(R.id.image_user);
        ImageButton gallery_btn = root.findViewById(R.id.gallery_pic);
        ImageButton take_pic_btn = root.findViewById(R.id.take_pic);

        save_button = root.findViewById(R.id.save_btn);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference users = database.getReference("users");

        users.child(MainActivity.firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                        UserDetails user = snapshot.getValue(UserDetails.class);
                        email.setText(user.getEmail());
                        name.setText(user.getName());
                        phone_number.setText(user.getPhoneNumber());
                        birthday.setText(user.getBirthday());
                        gender.setSelection(user.getGender());
                        height.setText(user.getHeight());
                        weight.setText(user.getWeight());
                        admin = user.isAdmin();

                        if (user.getImage() != null) {
                            imageG = user.getImage();
                            Glide.with(getContext())
                                    .load(user.getImage())
                                    .into(user_image);
                        } else {
                            Glide.with(getContext())
                                    .load(R.drawable.ic_baseline_account_circle_24)
                                    .into(user_image);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        birth = root.findViewById(R.id.input_layout_birthday);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapter);

        gender.setOnItemSelectedListener(this);

        birthday.setInputType(InputType.TYPE_NULL);
        birthday.setKeyListener(null);
        birthday.setOnClickListener(new View.OnClickListener() {
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
                            birthday.setText("0"+day +".0"+month+"."+year);
                        } else if (month>10 && day<10) {
                            birthday.setText("0"+day + "." + month + "." + year);
                        } else if (month<10 && day>10) {
                            birthday.setText(day + ".0" + month + "." + year);
                        } else {
                            birthday.setText(day + "." + month + "." + year);
                        }
                    }
                },yearInt,monthInt,dayInt);
                datePickerDialog.show();
            }
        });

        gallery_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeImageGallery.launch("image/*");
            }
        });

        take_pic_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(getActivity().getFilesDir(), "picFromCamera");
                Uri uri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", file);
                takeImageResult.launch(getTmpFileUri());
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(getContext());
                progressDialog.setTitle(getString(R.string.set_profile));
                progressDialog.setMessage(getString(R.string.wait_p));
                progressDialog.show();

                if (fromlauncher) {
                    if (image_profile_uri != null) {
                        final StorageReference fileRef = storageProfilePic.child(MainActivity.firebaseAuth.getCurrentUser().getUid() + ".jpg");
                        uploadTask = fileRef.putFile(image_profile_uri);
                        uploadTask.continueWithTask(new Continuation() {
                            @Override
                            public Object then(@NonNull Task task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                return fileRef.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    fromlauncher = false;
                                    Uri downloadUri = task.getResult();
                                    imageG = downloadUri.toString();
                                    UserDetails newUser = new UserDetails(name.getText().toString(), email.getText().toString(), birthday.getText().toString(), phone_number.getText().toString(), gender.getSelectedItemPosition(), height.getText().toString(), weight.getText().toString(), imageG, admin);
                                    users.child(MainActivity.firebaseAuth.getCurrentUser().getUid()).setValue(newUser);
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    } else {
                        UserDetails newUser1 = new UserDetails(name.getText().toString(), email.getText().toString(), birthday.getText().toString(), phone_number.getText().toString(), gender.getSelectedItemPosition(), height.getText().toString(), weight.getText().toString(), imageG, admin);
                        users.child(MainActivity.firebaseAuth.getCurrentUser().getUid()).setValue(newUser1);
                        progressDialog.dismiss();
                    }

                    Toast.makeText(getContext(), getString(R.string.change_save), Toast.LENGTH_SHORT).show();
                }
            }
        });
        return root;
    }

    private Uri getTmpFileUri() {
        try {
            File tmpFile = File.createTempFile("tmp_image_file"+MainActivity.firebaseAuth.getCurrentUser().getUid(), ".png", getActivity().getCacheDir());
            tmpFile.createNewFile();
            tmpFile.deleteOnExit();
            file = tmpFile;
            image_profile_uri = FileProvider.getUriForFile(getActivity().getApplicationContext(), "com.hit.fightness.provider", tmpFile);
            return image_profile_uri;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        choice = i;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}