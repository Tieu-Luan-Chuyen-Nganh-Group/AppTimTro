package app.nhatro.tlcn.nhatroapp_ver2;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private RecyclerView postLists;
    private ImageButton addNewPostBtn;
    private CircleImageView NavProfileImg;
    private TextView NavProfileUserName;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, PostsRef;
    String currentUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // lấy danh sách account từ firebase
        mAuth = FirebaseAuth.getInstance();
        //kiểm tra tài khoản hiện tại - nếu chưa có thì chuyển đến màn hình đăng nhập - nếu có thì lấy id user đó
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            SendUserToLoginActivity();
        }else{
            currentUserId = mAuth.getCurrentUser().getUid();

            UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
            PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

            UsersRef.child(currentUserId).child("Role").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String role = dataSnapshot.getValue().toString();
                    mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
                    setSupportActionBar(mToolbar);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setDisplayShowHomeEnabled(true);
                    getSupportActionBar().setTitle("Home");

                    drawerLayout = (DrawerLayout) findViewById(R.id.drawable_layout);
                    actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout ,R.string.drawer_open,R.string.drawer_close);
                    drawerLayout.addDrawerListener(actionBarDrawerToggle);
                    actionBarDrawerToggle.syncState();
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    addNewPostBtn = (ImageButton) findViewById(R.id.imgBtn_add_new_post);
                    navigationView = (NavigationView) findViewById(R.id.navigation_view);

                    postLists = (RecyclerView) findViewById(R.id.all_uses_post_list);
                    postLists.setHasFixedSize(true);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                    linearLayoutManager.setReverseLayout(true);
                    linearLayoutManager.setStackFromEnd(true);
                    postLists.setLayoutManager(linearLayoutManager);

                    View navView = navigationView.inflateHeaderView(R.layout.navigation_header);

                    NavProfileImg = (CircleImageView) navView.findViewById(R.id.nav_profile_img);
                    NavProfileUserName = (TextView) navView.findViewById(R.id.nav_username);

                    // lấy thông tin của user hiện tại hiện thị trong màn hình thông tin
                    UsersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                if(dataSnapshot.hasChild("fullname")){
                                    String fullname=dataSnapshot.child("fullname").getValue().toString();
                                    NavProfileUserName.setText(fullname);
                                }
                                if(dataSnapshot.hasChild("profileimage")){
                                    String image=dataSnapshot.child("profileimage").getValue().toString();
                                    Picasso.with(MainActivity.this).load(image).placeholder(R.drawable.profile).into(NavProfileImg);
                                }
                                else {
                                    SendUserToSetupActivity();
                                    Toast.makeText(MainActivity.this,"Profile image or name do not exists...", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            UserMenuSelector(item);
                            return false;
                        }
                    });

                    // khi click vào button này thì chuyển đến màn hình thêm bài viết mới
                    addNewPostBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SendUserToPostActivity();
                        }
                    });

                    //hiển thị tất cả bài đăng
                    DisplayAllPosts();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }

    private void SendUserToAdminActivity() {
        Intent adminActivity = new Intent(MainActivity.this, AdminActivity.class);
        adminActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(adminActivity);
        finish();
    }

    private void DisplayAllPosts() {

        Query allPostsAreAllowedToDisplay = PostsRef.orderByChild("status").equalTo("1");
        FirebaseRecyclerAdapter<Post, PostsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Post, PostsViewHolder>
                        (
                                Post.class,
                                R.layout.all_posts_layout,
                                PostsViewHolder.class,
                                allPostsAreAllowedToDisplay
                        )
                {
                    @Override
                    protected void populateViewHolder(PostsViewHolder viewHolder, Post model, int position) {

                        viewHolder.setFullname(model.getFullname());
                        viewHolder.setDescription(model.getDescription());
                        viewHolder.setDate(model.getDate());
                        viewHolder.setTime(model.getTime());
                        viewHolder.setProfileimage(getApplicationContext() , model.getProfileimage());
                        viewHolder.setPostimage(getApplicationContext(), model.getPostimage());
                    }
                };

        postLists.setAdapter(firebaseRecyclerAdapter);
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder{


        View mView;

        public PostsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
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
            datetxt.setText("    " +date);
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

    // chuyển user tới màn hình thêm bài viết mới
    private void SendUserToPostActivity() {
        Intent postActivity = new Intent(MainActivity.this, PostActivity.class);
        startActivity(postActivity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if( currentUser == null){
            SendUserToLoginActivity();
        }
        else{
            CheckUserExistence();
        }
    }

    private void CheckUserExistence() {
        final String current_user_id = mAuth.getCurrentUser().getUid();

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(current_user_id)){
                    SendUserToSetupActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    // chuyển đến màn hình cập nhật thông tin user
    private void SendUserToSetupActivity() {
        Intent setupActivity = new Intent(MainActivity.this, SetupActivity.class);
        setupActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupActivity);
        finish();

    }

    // chuyển đến màn hình login
    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity(loginIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item) {

        switch (item.getItemId()){
            case R.id.nav_profile:
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_home:
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_newpost:
                SendUserToPostActivity();
                break;
            case R.id.nav_findRoom:
                Toast.makeText(this, "File room", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_message:
                Toast.makeText(this, "Message", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_setting:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:

                mAuth.signOut();
                SendUserToLoginActivity();
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
