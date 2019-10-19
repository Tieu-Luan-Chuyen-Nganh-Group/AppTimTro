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
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private Button CreateAccountButton;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = (EditText) findViewById(R.id.editText_Register_Email);
        passwordEditText = (EditText) findViewById(R.id.editText_Register_Password);
        confirmPasswordEditText = (EditText) findViewById(R.id.editText_Register_ConfirmPassword);
        CreateAccountButton = (Button) findViewById(R.id.button_Register_CreateAccount);
        loadingBar = new ProgressDialog(this);

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });
    }

    private void CreateAccount() {

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = passwordEditText.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please write your email...", Toast.LENGTH_SHORT).show();
        }
        else{
            if(TextUtils.isEmpty(password)){
                Toast.makeText(this, "Please write your password...", Toast.LENGTH_SHORT).show();
            }
            else {
                if(TextUtils.isEmpty(confirmPassword)){
                    Toast.makeText(this, "Please confirm your password...", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(!password.equals(confirmPassword)){
                        Toast.makeText(this, "password don't match with confirm password ...", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        loadingBar.setTitle("Create new account");
                        loadingBar.setMessage("Please wait, while we are creating your account");
                        loadingBar.show();
                        loadingBar.setCanceledOnTouchOutside(true);
                        mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    SenUserToSetupActivity();
                                    Toast.makeText(RegisterActivity.this, "Your account are created successfully", Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                }
                                else{
                                    String message = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, "Error occur: " + message, Toast.LENGTH_SHORT).show();
                                    loadingBar.dismiss();
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    private void SenUserToSetupActivity() {
        Intent setupActivity = new Intent(RegisterActivity.this, SetupActivity.class);
        setupActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupActivity);
        finish();

    }
}
