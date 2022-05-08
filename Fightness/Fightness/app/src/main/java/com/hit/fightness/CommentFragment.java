package com.hit.fightness;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentFragment extends Fragment {

    private EditText addComment;
    private ImageView imageProfile;
    private TextView post;
    ProgressDialog pd;
    String id;

    RecyclerView recyclerViewComments;
    private CommentAdapter commentAdapter;

    ArrayList<Comment> commentList = new ArrayList<Comment>();
    private String postId;
    private String authorId;

    FirebaseUser fUser;

    public static CommentFragment newInstance(String postId, String authorId){
        CommentFragment commentFragment = new CommentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("postId", postId);
        bundle.putString("authorId", authorId);
        commentFragment.setArguments(bundle);
        return  commentFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comment, container, false);



        androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar_comment);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.comments));
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_tip, new FeedFragment()).commit();
            }
        });

        postId = getArguments().getString("postId");
        authorId = getArguments().getString("authorId");

        recyclerViewComments = view.findViewById(R.id.recyclerview_comments);
        recyclerViewComments.setHasFixedSize(true);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(getContext()));
        commentAdapter = new CommentAdapter(getContext(), commentList, postId, authorId);

        recyclerViewComments.setAdapter(commentAdapter);



        commentList = new ArrayList<>();


        addComment = view.findViewById(R.id.add_comment_et);
        imageProfile = view.findViewById(R.id.image_user_comment);
        post = view.findViewById(R.id.post_comment);

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        getUserImage();

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/").getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (TextUtils.isEmpty(addComment.getText().toString())) {
                            Toast.makeText(getContext(), getString(R.string.commentNot), Toast.LENGTH_SHORT).show();
                        } else {
                            putComment();
                            addNotification(postId, authorId, addComment.getText().toString(), id);
                            new NotificationActivity(getContext(), getString(R.string.commented_post) + addComment.getText().toString(), snapshot.child("tokens").child(authorId).child("token").getValue().toString());
                            addComment.setText("");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        pd = new ProgressDialog(getContext());
        pd.setMessage(getString(R.string.upload));
        pd.show();

        getComment();

        return view;
    }

    private void getComment() {
        FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment comment = snapshot.getValue(Comment.class);
                    commentList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
                pd.dismiss();
                commentAdapter = new CommentAdapter(getContext(), commentList, postId, authorId);

                recyclerViewComments.setAdapter(commentAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void putComment() {
        HashMap<String, Object> map = new HashMap<>();

        DatabaseReference ref = FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Comments").child(postId);
        id = ref.push().getKey();

        map.put("id", id);
        map.put("comment", addComment.getText().toString());
        map.put("publisher", fUser.getUid());



        ref.child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), getString(R.string.commentAdded), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getUserImage() {
        FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("users").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails user = snapshot.getValue(UserDetails.class);

                if (user.getImage() == null) {
                    Glide.with(((AppCompatActivity)getActivity()))
                            .load(R.drawable.ic_baseline_account_circle_24)
                            .into(imageProfile);
                } else {
                    Glide.with(((AppCompatActivity)getActivity()))
                            .load(user.getImage()).circleCrop()
                            .into(imageProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addNotification(String postid ,String publisher, String comment, String commentId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", fUser.getUid());
        map.put("text", " "+getString(R.string.commented)+" " + comment);
        map.put("postId", postid);
        map.put("isPost", true);


        FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Notifications").child(publisher).child(commentId).setValue(map);
    }



}