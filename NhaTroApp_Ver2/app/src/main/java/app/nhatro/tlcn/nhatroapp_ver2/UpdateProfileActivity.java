package app.nhatro.tlcn.nhatroapp_ver2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class UpdateProfileActivity extends AppCompatActivity {

    private DatabaseReference UsersRef;
    private StorageReference UserProfileImageRef;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    final static int Gallery_Pick=1;
    private String currentUserId;
    private CircleImageView profileImage;
    private EditText userEditText, fullnameEditText, phoneEditText, addressEditText, genderEditText, birthdayEditText;
    private Button updateButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            SendUserToLoginActivity();
        }else{
            loadingBar = new ProgressDialog(this);
            currentUserId = mAuth.getCurrentUser().getUid();
            UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
            UserProfileImageRef=FirebaseStorage.getInstance().getReference().child("profile images");
            profileImage = (CircleImageView) findViewById(R.id.Update_img);
            userEditText = (EditText) findViewById(R.id.editText_Update_Username);
            fullnameEditText = (EditText) findViewById(R.id.editText_Update_Fullname);
            phoneEditText = (EditText) findViewById(R.id.editText_Update_NumberPhone);
            birthdayEditText = (EditText) findViewById(R.id.editText_Update_Birthday);
            addressEditText = (EditText) findViewById(R.id.editText_Update_Address);
            genderEditText = (EditText) findViewById(R.id.editText_Update_Gender);
            updateButton = (Button) findViewById(R.id.button_Update_Save);

            WriteInfoCurrentUser();

            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ValidateInfo();
                }
            });

            profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent galleryIntent=new Intent();
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent,Gallery_Pick);
                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // cho người dùng cắt ảnh khi đã chọn 1 ảnh
        if(requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null){
            Uri ImageUri=data.getData();


            CropImage.activity(ImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);

            if(resultCode==RESULT_OK){

                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Please wait, while we are updating your profile image...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();


                final Uri resultUri=result.getUri();

                final StorageReference filePath= UserProfileImageRef.child(currentUserId+".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(UpdateProfileActivity.this, "Profile Image stored successfully to Firebase storage...", Toast.LENGTH_SHORT).show();
//                            final String downloadUrl=task.getResult().getStorage().toString();
                            Task <Uri> downloadTask = task.getResult().getMetadata().getReference().getDownloadUrl();
                            downloadTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(final Uri uri) {
                                    final String downloadUrl=uri.toString();
                                    UsersRef.child("profileimage").setValue(downloadUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        /*Intent selfIntent=new Intent(SetupActivity.this,SetupActivity.class);
                                                        startActivity(selfIntent);*/
                                                        profileImage.setImageURI(uri);

                                                        Toast.makeText(UpdateProfileActivity.this, "Profile Image stored successfully to Firebase Database Successfully...", Toast.LENGTH_SHORT).show();
                                                        loadingBar.dismiss();

                                                    }else
                                                    {
                                                        String message=task.getException().getMessage();
                                                        Toast.makeText(UpdateProfileActivity.this, "Error Occured: "+message, Toast.LENGTH_SHORT).show();
                                                        loadingBar.dismiss();
                                                    }
                                                }
                                            });
                                }
                            });

                        }
                    }
                });
            } else {
                Toast.makeText(this, "Error Occured: Image can not be cropped. Try again.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }

    private void ValidateInfo() {
        String username = userEditText.getText().toString();
        String fullname = fullnameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String address = addressEditText.getText().toString();
        String gender = genderEditText.getText().toString();
        String birthday = birthdayEditText.getText().toString();

        if (TextUtils.isEmpty(username)){
            Toast.makeText(this, "Please write your username...", Toast.LENGTH_SHORT).show();
        }
        else {
            if (TextUtils.isEmpty(fullname)){
                Toast.makeText(this, "Please write your fullname...", Toast.LENGTH_SHORT).show();
            }
            else {
                if (TextUtils.isEmpty(birthday)){
                    Toast.makeText(this, "Please write your birthday...", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (TextUtils.isEmpty(gender)){
                        Toast.makeText(this, "Please write your gender...", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (TextUtils.isEmpty(phone)){
                            Toast.makeText(this, "Please write your phone...", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if (TextUtils.isEmpty(address)){
                                Toast.makeText(this, "Please write your address...", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                loadingBar.setTitle("Profile Image");
                                loadingBar.setMessage("Please wait, while we are updating your profile image...");
                                loadingBar.setCanceledOnTouchOutside(true);
                                loadingBar.show();

                                UpdateAccountInfo(username, fullname, birthday, gender, phone, address);
                            }
                        }
                    }
                }
            }
        }
    }

    private void UpdateAccountInfo(String username, String fullname, String birthday, String gender, String phone, String address) {

        HashMap userMap = new HashMap();

        userMap.put("username",username);
        userMap.put("fullname",fullname);
        userMap.put("birthday",birthday);
        userMap.put("Gender",gender);
        userMap.put("Phone",phone);
        userMap.put("Address",address);

        UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    Toast.makeText(UpdateProfileActivity.this, "Account Setting Update Successfully...", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
                else {
                    Toast.makeText(UpdateProfileActivity.this, "Error Occured, while updating account...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void WriteInfoCurrentUser() {
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    //userEditText.setText(dataSnapshot.getChildren());
                    String username = dataSnapshot.child("username").getValue().toString();
                    String fullname = dataSnapshot.child("fullname").getValue().toString();
                    String phone = dataSnapshot.child("Phone").getValue().toString();
                    String address = dataSnapshot.child("Address").getValue().toString();
                    String gender = dataSnapshot.child("Gender").getValue().toString();
                    String birthday = dataSnapshot.child("birthday").getValue().toString();
                    String profileimage = dataSnapshot.child("profileimage").getValue().toString();

                    userEditText.setText(username);
                    phoneEditText.setText(phone);
                    fullnameEditText.setText(fullname);
                    birthdayEditText.setText(birthday);
                    addressEditText.setText(address);
                    genderEditText.setText(gender);
                    Picasso.with(UpdateProfileActivity.this).load(profileimage).placeholder(R.drawable.profile).into(profileImage);
                }
                else {
                    Toast.makeText(UpdateProfileActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(UpdateProfileActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity(loginIntent);
        finish();
    }
}
