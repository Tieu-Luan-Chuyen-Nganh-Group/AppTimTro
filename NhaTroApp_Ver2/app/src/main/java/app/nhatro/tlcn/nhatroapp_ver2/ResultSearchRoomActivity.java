package app.nhatro.tlcn.nhatroapp_ver2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ResultSearchRoomActivity extends AppCompatActivity {

    private String area;
    private RecyclerView ResultList;
    private DatabaseReference RoomRef, PostsRef;
    private static final int REQUEST_CALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_search_room);

        RoomRef = FirebaseDatabase.getInstance().getReference().child("Rooms");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        area = getIntent().getExtras().getString("address").toString();
        ResultList = (RecyclerView) findViewById(R.id.all_result_search);
        ResultList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ResultSearchRoomActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        ResultList.setLayoutManager(linearLayoutManager);

        DisplayAllPostHaveKeywordEqualArea();

    }

    private void DisplayAllPostHaveKeywordEqualArea() {

        Query theSameArea =PostsRef.orderByChild("description").startAt(area).endAt(area +"\uf8ff");
        FirebaseRecyclerAdapter<Post, PostsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Post, PostsViewHolder>(
                        Post.class,
                        R.layout.all_posts_layout,
                        PostsViewHolder.class,
                        theSameArea
                )
                {
                    @Override
                    protected void populateViewHolder(PostsViewHolder postsViewHolder, Post post, int i) {
                        final String PostKey = getRef(i).getKey();

                        postsViewHolder.setFullname(post.getFullname());
                        postsViewHolder.setDescription(post.getDescription());
                        postsViewHolder.setDate(post.getDate());
                        postsViewHolder.setTime(post.getTime());
                        postsViewHolder.setProfileimage(getApplicationContext() , post.getProfileimage());
                        postsViewHolder.setPostimage(getApplicationContext(), post.getPostimage());
                        postsViewHolder.phoneImageButon.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ResultSearchRoomActivity.this);
                                builder.setTitle("Make a phone");
                                final TextView textView = new TextView(ResultSearchRoomActivity.this);
                                textView.setText("Are you sure you want to make a phone?");
                                builder.setView(textView);
                                textView.setTextSize(16);
                                textView.setPadding(50,50,50,50);
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        PostsRef.child(PostKey).child("numberphone").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()){
                                                    String NumberPhone = dataSnapshot.getValue().toString();
                                                    if (ContextCompat.checkSelfPermission(ResultSearchRoomActivity.this, Manifest.permission.CALL_PHONE)
                                                            != PackageManager.PERMISSION_GRANTED){
                                                        ActivityCompat.requestPermissions(ResultSearchRoomActivity.this,
                                                                new String[] {Manifest.permission.CALL_PHONE}, REQUEST_CALL);

                                                    }
                                                    else {
                                                        String dual = "tel:"+ NumberPhone;
                                                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dual)));
                                                    }
                                                }
                                                else {
                                                    Toast.makeText(ResultSearchRoomActivity.this, "Sory! Exist a prolem so you can't call anyone now!", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                });

                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                Dialog dialog = builder.create();
                                dialog.show();
                                dialog.getWindow().setBackgroundDrawableResource(R.color.backgroundAlerDialog);
                            }
                        });
                    }
                };
        ResultList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder{

        View mView;
        //private DatabaseReference PostsRef;
        ImageButton phoneImageButon, optionsImageButton;
        NavigationView a;

        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            // PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
            phoneImageButon = (ImageButton) mView.findViewById(R.id.post_phone_imageButton);
            optionsImageButton = (ImageButton) mView.findViewById(R.id.post_selectOptions_imageButton);

        }


        public void setFullname(String fullname) {
            TextView usernametxt = (TextView) mView.findViewById(R.id.post_username);
            usernametxt.setText(fullname);
        }

        public void setProfileimage(Context ctx, String profileimage) {
            CircleImageView imagetxt = (CircleImageView) mView.findViewById(R.id.post_profile_image);
            Picasso.with(ctx).load(profileimage).into(imagetxt);
        }

        public void setDescription(String description) {
            TextView descriptiontxt = (TextView) mView.findViewById(R.id.post_description);
            descriptiontxt.setText(description);
        }

        public void setDate(String date) {
            TextView datetxt = (TextView) mView.findViewById(R.id.post_date);
            datetxt.setText(date);
        }

        public void setTime(String time) {
            TextView timetxt = (TextView) mView.findViewById(R.id.post_time);
            timetxt.setText("    " + time);
        }

        public void setPostimage(Context ctx , String postimage) {
            ImageView imagetxt = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(postimage).into(imagetxt);
        }
    }

}
