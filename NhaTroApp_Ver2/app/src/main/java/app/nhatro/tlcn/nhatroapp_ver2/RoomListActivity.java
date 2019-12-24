package app.nhatro.tlcn.nhatroapp_ver2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class RoomListActivity extends AppCompatActivity {

    private ImageButton addNewroomBtn;
    private Toolbar mToolbar;
    private RecyclerView roomList;

    private DatabaseReference RoomRefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        mToolbar = (Toolbar) findViewById(R.id.admin_RoomList_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Room List");

        RoomRefs = FirebaseDatabase.getInstance().getReference().child("Rooms");

        addNewroomBtn = (ImageButton) findViewById(R.id.imgBtn_add_new_room);

        roomList = (RecyclerView) findViewById(R.id.all_rooms_list);
        roomList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(RoomListActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        roomList.setLayoutManager(linearLayoutManager);


        addNewroomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToAddNewRoomActivity();
            }
        });

        DisplayAllRoomList();
    }

    private void DisplayAllRoomList() {
        final FirebaseRecyclerAdapter<Room , RoomViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Room, RoomViewHolder>(
                        Room.class,
                        R.layout.room_list,
                        RoomViewHolder.class,
                        RoomRefs
                )
                {
                    @Override
                    protected void populateViewHolder(RoomViewHolder roomViewHolder, Room room, int i) {

                        final String RoomKey = getRef(i).getKey();

                        roomViewHolder.setAddress(room.getAddress());
                        roomViewHolder.setPhone(room.getPhone());
                        roomViewHolder.setPrice(room.getPhone());
                        roomViewHolder.setDescription(room.getDescription());
                        roomViewHolder.setImg1(getApplicationContext(), RoomKey);
                        roomViewHolder.setImg2(getApplicationContext(), RoomKey);
                        roomViewHolder.setImg3(getApplicationContext(), RoomKey);
                        roomViewHolder.setImg4(getApplicationContext(), RoomKey);
                    }
                };

        roomList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public DatabaseReference RoomRef;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            RoomRef = FirebaseDatabase.getInstance().getReference().child("Rooms");

        }

        public void setAddress(String address) {
            TextView txtAddress = (TextView) mView.findViewById(R.id.editText_RoomList_Address);
            txtAddress.setText(address);
        }

        public void setPrice(String price) {
            TextView txtPrice= (TextView) mView.findViewById(R.id.editText_RoomList_Price);
            txtPrice.setText(price);
        }

        public void setPhone(String phone) {
            TextView txtPhone= (TextView) mView.findViewById(R.id.editText_RoomList_Phone);
            txtPhone.setText(phone);
        }

        public void setDescription(String description) {
            TextView txtDescription= (TextView) mView.findViewById(R.id.editText_RoomList_Description);
            txtDescription.setText(description);
        }

        public void setImg1(final Context ctx, String RoomKey) {
            RoomRef.child(RoomKey).child("imageList").child("0").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        ImageView imagetxt = (ImageView) mView.findViewById(R.id.ImageView_Roomlist_Img1);
                        Picasso.with(ctx).load(dataSnapshot.getValue().toString()).into(imagetxt);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        public void setImg2(final Context ctx, String RoomKey) {
            RoomRef.child(RoomKey).child("imageList").child("1").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        ImageView imagetxt = (ImageView) mView.findViewById(R.id.ImageView_Roomlist_Img2);
                        Picasso.with(ctx).load(dataSnapshot.getValue().toString()).into(imagetxt);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        public void setImg3(final Context ctx, String RoomKey) {
            RoomRef.child(RoomKey).child("imageList").child("2").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        ImageView imagetxt = (ImageView) mView.findViewById(R.id.ImageView_Roomlist_Img3);
                        Picasso.with(ctx).load(dataSnapshot.getValue().toString()).into(imagetxt);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        public void setImg4(final Context ctx, String RoomKey) {
            RoomRef.child(RoomKey).child("imageList").child("3").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        ImageView imagetxt = (ImageView) mView.findViewById(R.id.ImageView_Roomlist_Img4);
                        Picasso.with(ctx).load(dataSnapshot.getValue().toString()).into(imagetxt);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void SendUserToAddNewRoomActivity() {
        Intent addNerRoomActivity = new Intent(RoomListActivity.this, AddNewRoomActivity.class);
        addNerRoomActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(addNerRoomActivity);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            SendUserToAdminActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void SendUserToAdminActivity() {
        Intent adminIntent = new Intent(RoomListActivity.this, AdminActivity.class);
        startActivity(adminIntent);
        finish();
    }
}
