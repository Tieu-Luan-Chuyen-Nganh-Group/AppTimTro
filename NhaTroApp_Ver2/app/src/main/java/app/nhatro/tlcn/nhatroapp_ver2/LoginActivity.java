
package app.nhatro.tlcn.nhatroapp_ver2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private Button LoginButton, RegisterButton;
    private EditText UserEmailEditText, PassWordEditText;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        loadingBar = new ProgressDialog(this);
        RegisterButton = (Button) findViewById(R.id.button_Login_CreateAccount);
        LoginButton = (Button) findViewById(R.id.button_Login_Login);
        UserEmailEditText = (EditText) findViewById(R.id.editText_Login_Email);
        PassWordEditText = (EditText) findViewById(R.id.editText_Login_Password);

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowingUserToLogin();
            }
        });
    }

    // đăng nhập với email và mật khẩu
    private void AllowingUserToLogin() {
        String email = UserEmailEditText.getText().toString();
        String password = PassWordEditText.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please write your email...", Toast.LENGTH_SHORT).show();
        }
        else{
            if(TextUtils.isEmpty(password)){
                Toast.makeText(this, "Please write your password...", Toast.LENGTH_SHORT).show();
            }
            else {
                loadingBar.setTitle("Login");
                loadingBar.setMessage("Please wait...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);
                mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            String currentUserID = currentUser.getUid();
                            //String role = UsersRef.child(currentUserID).child("Role").toString();
                            //Toast.makeText(LoginActivity.this, role, Toast.LENGTH_SHORT).show();
                            UsersRef.child(currentUserID).child("Role").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String role = dataSnapshot.getValue().toString();
                                    if (!role.equals("Admin") ){
                                        SendUserToMainActivity();
                                    }
                                    else {
                                        SendUserToAdminActivity();
                                    }
                                    loadingBar.dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Error occur: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
            }
        }
    }

    private void SendUserToAdminActivity() {
        Intent adminActivity = new Intent(LoginActivity.this, AdminActivity.class);
        adminActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(adminActivity);
        finish();
    }

    // chuyển đến màn hình chính khi đăng nhập thành công
    private void SendUserToMainActivity() {
        Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivity);
        finish();

    }

    // chuyển đến màn hình đăng kí nếu chưa có tài khoản
    private void SendUserToRegisterActivity() {

        Intent registerInter = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerInter);
        //finish();
    }
}
