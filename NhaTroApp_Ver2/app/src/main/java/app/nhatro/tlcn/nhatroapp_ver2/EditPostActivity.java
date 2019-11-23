package app.nhatro.tlcn.nhatroapp_ver2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.internal.Util;
import com.squareup.picasso.Picasso;

public class EditPostActivity extends AppCompatActivity {


    private Toolbar mToolbar;
    private ImageView PostImage;
    private EditText PostDescription, PostNumberPhone;
    private Button EditPostButton;
    private DatabaseReference EditPostRef;
    private FirebaseAuth mAuth;
    private String PostKey, currentUserID, description, image, numberPhone;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        PostKey = getIntent().getExtras().get("PostKey").toString();
        EditPostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey);

        loadingBar = new ProgressDialog(this);
        PostImage = (ImageButton) findViewById(R.id.edit_post_image);
        PostDescription = (EditText) findViewById(R.id.edit_post_description);
        PostNumberPhone = (EditText) findViewById(R.id.edit_post_phone);
        EditPostButton = (Button) findViewById(R.id.edit_post_button);

        EditPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    description = dataSnapshot.child("description").getValue().toString();
                    numberPhone = dataSnapshot.child("numberphone").getValue().toString();
                    image = dataSnapshot.child("postimage").getValue().toString();

                    PostDescription.setText(description);
                    PostNumberPhone.setText(numberPhone);
                    Picasso.with(EditPostActivity.this).load(image).into(PostImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        EditPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdatePost();
            }
        });

        mToolbar = (Toolbar) findViewById(R.id.update_post_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Edit Post");


    }

    private void UpdatePost() {

        String descriptionPost = PostDescription.getText().toString();
        String numberphonePost = PostNumberPhone.getText().toString();

        if (TextUtils.isEmpty(descriptionPost)){
            Toast.makeText(EditPostActivity.this, "Please write content... ", Toast.LENGTH_SHORT).show();
        }
        else {
            if (TextUtils.isEmpty(numberphonePost)){
                Toast.makeText(EditPostActivity.this, "Please write numberphone... ", Toast.LENGTH_SHORT).show();
            }
            else {
                loadingBar.setTitle("Update post");
                loadingBar.setMessage("Please wait...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);

                EditPostRef.child("description").setValue(descriptionPost);
                EditPostRef.child("numberphone").setValue(numberphonePost);
                loadingBar.dismiss();
                SendUserToMainActivity();
                Toast.makeText(EditPostActivity.this, "Edit post successfully!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == android.R.id.home){
            SendUserToMainActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(EditPostActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
