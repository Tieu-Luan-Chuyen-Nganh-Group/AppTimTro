package app.nhatro.tlcn.nhatroapp_ver2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText usernameEdittext, fullnameEdittext, birthdayEdittext;
    private Button SaveButton;
    private CircleImageView profileImage;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private ProgressDialog loadingBar;

    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        loadingBar = new ProgressDialog(this);

        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        usernameEdittext = (EditText) findViewById(R.id.editText_Setup_Username);
        fullnameEdittext = (EditText) findViewById(R.id.editText_Setup_Fullname);
        birthdayEdittext = (EditText) findViewById(R.id.editText_Setup_Birthday);
        SaveButton = (Button) findViewById(R.id.button_Setup_Save);
        profileImage = (CircleImageView) findViewById(R.id.Setup_img);

        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveAccountSetupInformation();
            }
        });
    }

    private void SaveAccountSetupInformation() {

        String username = usernameEdittext.getText().toString();
        String fullname = fullnameEdittext.getText().toString();
        String birthday = birthdayEdittext.getText().toString();

        if(TextUtils.isEmpty(username)){
            Toast.makeText(this, "please write username...", Toast.LENGTH_SHORT).show();
        }
        else {
            if(TextUtils.isEmpty(fullname)){
                Toast.makeText(this, "please write fullname...", Toast.LENGTH_SHORT).show();
            }
            else {
                if(TextUtils.isEmpty(birthday)){
                    Toast.makeText(this, "please write birthday...", Toast.LENGTH_SHORT).show();
                }
                else {
                    loadingBar.setTitle("Saving information!");
                    loadingBar.setMessage("Please wait!!!");
                    loadingBar.show();
                    loadingBar.setCanceledOnTouchOutside(true);
                    HashMap userMap = new HashMap();
                    userMap.put("username", username);
                    userMap.put("fullname", fullname);
                    userMap.put("birthday", birthday);
                    userMap.put("Role", "User");
                    userMap.put("Phone", "");
                    userMap.put("Address", "");
                    userMap.put("Email", "");
                    userMap.put("Gender", "");

                    UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()){
                                Toast.makeText(SetupActivity.this, "Your account is created successfully!", Toast.LENGTH_LONG).show();
                                SendUserToMainActivity();
                                loadingBar.dismiss();
                        }
                            else {
                                Toast.makeText(SetupActivity.this, "error occur:" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
                }
            }
        }
    }

    private void SendUserToMainActivity() {
        Intent mainActivity = new Intent(SetupActivity.this, MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivity);
        finish();
    }
}
