package com.hit.fightness;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.HashMap;

public class AddPostFragment extends Fragment{
    final String DIALOG_FRAGMENT_TAG = "dialog_fragment";
    ImageSourceFragment imageSourceFragment;
    ImageView post;
    String dataRes;
    String imageUrl;
    StorageTask uploadTask;
    SocialAutoCompleteTextView description;

    public AddPostFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_add_post, container, false);

        ImageView close = root.findViewById(R.id.close_post);
        post = root.findViewById(R.id.image_added);
        Button post_btn = root.findViewById(R.id.post_btn);
        description = root.findViewById(R.id.descreption);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_tip, new FeedFragment()).commit();
                TipPageFragment.bottomNavigationView.setSelectedItemId(R.id.nav_feed);
            }
        });

        imageSourceFragment = new ImageSourceFragment();
        imageSourceFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme);
        imageSourceFragment.show(getActivity().getSupportFragmentManager(), DIALOG_FRAGMENT_TAG);

        getParentFragmentManager().setFragmentResultListener("datafrom2", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                dataRes = result.getString("df2");
                Glide.with(getContext())
                        .load(dataRes)
                        .into(post);
            }
        });

        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload();

            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageSourceFragment = new ImageSourceFragment();
                imageSourceFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme);
                imageSourceFragment.show(getActivity().getSupportFragmentManager(), DIALOG_FRAGMENT_TAG);
            }
        });


        return root;
    }

    private void upload() {
        ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage(getString(R.string.upload));
        pd.show();


        if (dataRes != null) {
            final StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("Posts").child(MainActivity.firebaseAuth.getCurrentUser().getUid()+".png");
            uploadTask = fileRef.putFile(Uri.parse(dataRes));
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloadUri = task.getResult();
                    imageUrl = downloadUri.toString();

                    DatabaseReference ref = FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Posts");
                    String postId = ref.push().getKey();

                    HashMap<String, Object> map = new HashMap<>();
                    map.put("postid", postId);
                    map.put("imageurl", imageUrl);
                    map.put("description", description.getText().toString());
                    map.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

                    ref.child(postId).setValue(map);

                    pd.dismiss();

                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_tip, new FeedFragment()).commit();
                    TipPageFragment.bottomNavigationView.setSelectedItemId(R.id.nav_feed);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), getString(R.string.cant_post), Toast.LENGTH_SHORT).show();
            pd.dismiss();
        }
    }

//    private String getFileExtension(Uri uri) {
//        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.ge)
//    }


}