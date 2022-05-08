package com.hit.fightness;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;


public class ImageSourceFragment extends BottomSheetDialogFragment {

    File file;
    Uri image_profile_uri;
    boolean fromlauncher = false;
    ImageView user_image;


    public ImageSourceFragment() {
        // Required empty public constructor
    }

    //camera
    private ActivityResultLauncher<Uri> takeImageResult = registerForActivityResult(new ActivityResultContracts.TakePicture(), isSuccess -> {
        if (isSuccess && file != null) {
            fromlauncher=true;
            Bundle res = new Bundle();
            res.putString("df2", image_profile_uri.toString());
            getParentFragmentManager().setFragmentResult("datafrom2", res);
            dismiss();
        }
    });

    //gallery
    private ActivityResultLauncher<String> takeImageGallery = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            image_profile_uri = result;
            fromlauncher=true;
            Bundle res = new Bundle();
            res.putString("df2", image_profile_uri.toString());
            getParentFragmentManager().setFragmentResult("datafrom2", res);
            dismiss();
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_image_source, container, false);

        ImageButton gallery = root.findViewById(R.id.post_gallery);
        ImageButton camera = root.findViewById(R.id.post_camera);
        Button close = root.findViewById(R.id.close_source);


        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeImageGallery.launch("image/*");
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(getActivity().getFilesDir(), "picFromCamera");
                Uri uri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", file);
                takeImageResult.launch(getTmpFileUri());
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

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


}
