package com.mohammedev.schoolender;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Firebase Storage";
    private EditText nameEdt, emailEdt, passEdt;
    private Button signUpBtn;

    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        nameEdt = findViewById(R.id.edit_name);
        emailEdt = findViewById(R.id.edit_email);
        passEdt = findViewById(R.id.edit_pass);

        signUpBtn = findViewById(R.id.signUp_btn);

        mAuth = FirebaseAuth.getInstance();

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void signUp(){
        String email = emailEdt.getText().toString().trim();
        String name = nameEdt.getText().toString().trim();
        String password = passEdt.getText().toString().trim();

        if (email.isEmpty() | password.isEmpty()){
            Toast.makeText(this, "Email or Password is empty", Toast.LENGTH_SHORT).show();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEdt.setError("this email is not valid.");
            emailEdt.requestFocus();
        }
        else if (password.length() < 6){
            passEdt.setError("this password is weak!");
            passEdt.requestFocus();

        }else{
            mAuth.createUserWithEmailAndPassword(email , password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(MainActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                SchoolenderData schoolenderData = new SchoolenderData(name , email);
                                Toast.makeText(MainActivity.this, mAuth.getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();

                                databaseReference.setValue(schoolenderData);
                                Toast.makeText(MainActivity.this, "Uploaded Completely ", Toast.LENGTH_SHORT).show();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
    }
}