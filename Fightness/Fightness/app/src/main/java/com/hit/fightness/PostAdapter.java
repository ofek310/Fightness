package com.hit.fightness;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hendraanggrian.appcompat.widget.SocialTextView;

import java.util.HashMap;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context context;
    private List<Post> mPosts;

    private FirebaseUser firebaseUser;
    FragmentManager fragmentManager;

    public PostAdapter(Context mContext, List<Post> mPosts) {
        this.context = mContext;
        this.mPosts = mPosts;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private CommentListener listener;

    interface CommentListener{
        void onCommentClicked(String postid, String publisher);
    }
    public void setListener(CommentListener listener){
        this.listener =listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Post post = mPosts.get(position);
        Glide.with(context)
                .load(post.getImageurl())
                .into(holder.postImage);
        holder.description.setText(post.getDescription());

        if (post.getPublisher().equals(firebaseUser.getUid())) {
            holder.delete.setVisibility(View.VISIBLE);
        }

        FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("users").child(post.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails userDetails = snapshot.getValue(UserDetails.class);

                if (userDetails.getImage() == null) {
                    Glide.with(holder.itemView.getContext())
                            .load(R.drawable.ic_baseline_account_circle_24)
                            .into(holder.imageProfile);
                } else {
                    Glide.with(holder.itemView.getContext())
                            .load(userDetails.getImage()).circleCrop()
                            .into(holder.imageProfile);
                }

                holder.username.setText(userDetails.getName());
                holder.author.setText(userDetails.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        isLiked(post.getPostid(), holder.like, holder);
        numOfLikes(post.getPostid(), holder.numLikes, holder);
        getComments(post.getPostid(), holder.numComments, holder);


        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/").getReference().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (holder.like.getTag().equals("like")) {
                                    FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Likes").child(post.getPostid()).child(firebaseUser.getUid()).setValue(true);
                                    addNotification(post.getPostid(), post.getPublisher(), holder);
                                    new NotificationActivity(holder.itemView.getContext(), firebaseUser.getDisplayName() + holder.itemView.getContext().getString(R.string.liked), snapshot.child("tokens").child(post.getPublisher()).child("token").getValue().toString());
                                } else {
                                    FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Likes").child(post.getPostid()).child(firebaseUser.getUid()).removeValue();

                                    for (DataSnapshot dataSnapshot : snapshot.child("Notifications").child(post.getPublisher()).getChildren()) {
                                        if (dataSnapshot.child("postId").getValue().equals(post.getPostid())) {
                                             FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Notifications").child(post.getPublisher()).child(dataSnapshot.getKey()).removeValue();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Likes").child(post.getPostid()).child(firebaseUser.getUid()).removeValue();
                        }
                    });
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCommentClicked(post.getPostid(), post.getPublisher());
            }
        });

        holder.numComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                listener.onCommentClicked(post.getPostid(), post.getPublisher());
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.delete).setMessage(R.string.sure).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Likes").child(post.getPostid()).removeValue();
                        FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Comments").child(post.getPostid()).removeValue();
                        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(post.getImageurl());
                        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // File deleted successfully
                                Toast.makeText(context, "onSuccess: deleted file",Toast.LENGTH_SHORT).show();
                                FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Notifications").child(post.getPublisher()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            if (dataSnapshot.child("postId").getValue().equals(post.getPostid())) {
                                                dataSnapshot.getRef().removeValue();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Uh-oh, an error occurred!
                                Toast.makeText(context, "onFailure: did not delete file",Toast.LENGTH_SHORT).show();
                            }
                        });
                        FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Posts").child(post.getPostid()).removeValue();

                    }
                }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setCancelable(false).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageProfile;
        public ImageView postImage;
        public ImageView like;
        public ImageView comment;
        public ImageView delete;

        public TextView username;
        public TextView numLikes;
        public TextView author;
        public TextView numComments;
        SocialTextView description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.user_image_post);
            postImage = itemView.findViewById(R.id.post_image_big);
            like = itemView.findViewById(R.id.like_post);
            comment = itemView.findViewById(R.id.comment_post);
            delete = itemView.findViewById(R.id.delete_post);

            username = itemView.findViewById(R.id.user_name_post);
            numLikes = itemView.findViewById(R.id.num_likes_post);
            author = itemView.findViewById(R.id.author_post);
            numComments = itemView.findViewById(R.id.num_comments_post);
            description = itemView.findViewById(R.id.description_post);
        }

    }

    private void isLiked(String postId, ImageView imageView, PostAdapter.ViewHolder holder) {
        FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) { //postid
                if (snapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_like_post);
                    imageView.setTag("liked");

                } else {
                    imageView.setImageResource(R.drawable.ic_unlike_post);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void numOfLikes(String postId, final TextView text, PostAdapter.ViewHolder holder) {
        FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                text.setText(snapshot.getChildrenCount() + " " + holder.itemView.getContext().getString(R.string.likes));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private  void getComments(String postId, TextView text, PostAdapter.ViewHolder holder) {
        FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                text.setText(holder.itemView.getContext().getString(R.string.viewall)+" "+ snapshot.getChildrenCount() + " "+ holder.itemView.getContext().getString(R.string.comments));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addNotification(String postid ,String publisher, PostAdapter.ViewHolder holder) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", firebaseUser.getUid());
        map.put("text", holder.itemView.getContext().getString(R.string.liked));
        map.put("postId", postid);
        map.put("isPost", true);

        FirebaseDatabase.getInstance("https://fightness-93a50-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("Notifications").child(publisher).push().setValue(map);

    }
}
