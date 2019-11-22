package app.nhatro.tlcn.nhatroapp_ver2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StartActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, PostsRef;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // lấy danh sách account từ firebase
        mAuth = FirebaseAuth.getInstance();
        //kiểm tra tài khoản hiện tại - nếu chưa có thì chuyển đến màn hình đăng nhập - nếu có thì lấy id user đó
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            SendUserToLoginActivity();
        }
        else {
            currentUserId = mAuth.getCurrentUser().getUid();
            UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
            PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
            UsersRef.child(currentUserId).child("Role").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String role = dataSnapshot.getValue().toString();
                    if (role.equals("Admin") ){
                        SendUserToAdminActivity();
                    }
                    else {
                        SendUserToMainActivity();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(StartActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

    private void SendUserToAdminActivity() {
        Intent adminIntent = new Intent(StartActivity.this, AdminActivity.class);
        adminIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity(adminIntent);
        finish();
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(StartActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity(mainIntent);
        finish();
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(StartActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity(loginIntent);
        finish();
    }
}
