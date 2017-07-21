package com.lucriment.lucriment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button signUpButton;
    private EditText emailText;
    private EditText passwordText;
    private TextView signInTextView;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private InputMethodManager imm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialize input method manager for hiding keyboard
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        // initizlie firebase authentication
        firebaseAuth = FirebaseAuth.getInstance();
        // check if user is already logged in
        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }
        //create progress bar
        progressDialog = new ProgressDialog(this);

        // connect buttons to view
        signUpButton = (Button) findViewById(R.id.RegisterButton);
        emailText = (EditText)  findViewById(R.id.editTextEmail);
        passwordText = (EditText) findViewById(R.id.editTextPassword);
        signInTextView = (TextView) findViewById(R.id.textViewSignIn);

        // add listener to sign up button and sign in button
        signUpButton.setOnClickListener(this);
        signInTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(v == signUpButton){
            signUp();
        }
        if(v == signInTextView){
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

    }

    private void signUp(){
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();
        imm.hideSoftInputFromWindow(emailText.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(passwordText.getWindowToken(), 0);

        // check if email is empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email", Toast.LENGTH_SHORT).show();
            return;

        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        // display progress dialog
        progressDialog.setMessage("Registering User...");
        progressDialog.show();


        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Registration Succesful", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(getApplicationContext(), TutorListActivity.class));
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Registration Failed, please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}