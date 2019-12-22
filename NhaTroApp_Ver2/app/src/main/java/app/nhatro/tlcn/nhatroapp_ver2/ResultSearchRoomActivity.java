package app.nhatro.tlcn.nhatroapp_ver2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class ResultSearchRoomActivity extends AppCompatActivity {

    private String area;
    private RecyclerView ResultList;
    private DatabaseReference RoomRef, PostsRef;
    private Toolbar mToolbar;
    private ImageButton reSultPostImgBtn;
    private ArrayList<Post> postResultlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_search_room);


        mToolbar = (Toolbar) findViewById(R.id.result_searchRoom_page_toolbar);
        reSultPostImgBtn = (ImageButton) findViewById(R.id.result_Post);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Search Result ");
        RoomRef = FirebaseDatabase.getInstance().getReference().child("Rooms");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        area = getIntent().getExtras().getString("address").toString();
        ResultList = (RecyclerView) findViewById(R.id.all_result_search);
        ResultList.setHasFixedSize(true);
        GetPostListHaveKeyWordEqualArea();

        reSultPostImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetPostListHaveKeyWordEqualArea();
                //Toast.makeText(ResultSearchRoomActivity.this, "1", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void GetPostListHaveKeyWordEqualArea() {
       PostsRef.addChildEventListener(new ChildEventListener() {
           @Override
           public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               Post post = dataSnapshot.getValue(Post.class);
               String postDes = ConvertStringUTF8_String(post.description.toLowerCase());
               String keyWord = ConvertStringUTF8_String(area.toLowerCase());
               if (postDes.contains(keyWord)){
                   postResultlist.add(post);
               }
               RecyclerViewAdapter adapter = new RecyclerViewAdapter(ResultSearchRoomActivity.this, postResultlist);
               ResultList.setAdapter(adapter);
               ResultList.setLayoutManager(new LinearLayoutManager(ResultSearchRoomActivity.this));
           }

           @Override
           public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

           }

           @Override
           public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

           }

           @Override
           public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
    }

    public String ConvertStringUTF8_String(String str){

        String tmp = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

        return pattern.matcher(tmp).replaceAll("");
    }

    @Override
    protected void onStart() {
        //DisplayAllPostHaveKeywordEqualArea();
        super.onStart();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            SendUserToMainActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(ResultSearchRoomActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
