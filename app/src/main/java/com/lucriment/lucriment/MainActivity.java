package com.lucriment.lucriment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener {

    private Button signUpButton;
    private EditText emailText, firstName, lastName;
    private EditText passwordText;
    private TextView signInTextView;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private InputMethodManager imm;
    private DatabaseReference Ref;
    private Button googleButton, fakeButton;
    private LoginButton FacebookLoginButton;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private CallbackManager mCallbackManager;
    private TextInputLayout firstNameInputLayout, lastNameInputLayout, emailInputLayout, passwordInputLayout;
    private String nameRegex = "^[A-Za-z]{1,20}$";
    private String passwordRegex = "^[A-Za-z0-9@%+/‘!#$^?:(){}~`-]{6,128}$";
    private String emailRegex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialize input method manager for hiding keyboard
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // initizlie firebase authentication
        firebaseAuth = FirebaseAuth.getInstance();
        // check if user is already logged in
        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }
        //create progress bar
        progressDialog = new ProgressDialog(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        // connect buttons to view
        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        signUpButton = (Button) findViewById(R.id.signInButton);
        emailText = (EditText)  findViewById(R.id.email);
        passwordText = (EditText) findViewById(R.id.password);
        googleButton = (Button) findViewById(R.id.googleButton);
        firstNameInputLayout = (TextInputLayout) findViewById(R.id.firstNameInputLayout);
        lastNameInputLayout = (TextInputLayout) findViewById(R.id.lastNameInputLayout);
        emailInputLayout = (TextInputLayout) findViewById(R.id.emailInputLayout);
        passwordInputLayout = (TextInputLayout) findViewById(R.id.passwordLayout);
        fakeButton = (Button) findViewById(R.id.fakeButton);
        FacebookLoginButton = (LoginButton) findViewById(R.id.facebookButton);
        FacebookLoginButton.setText("sign in with facebook");

        //  FacebookLoginButton.setOnClickListener(this);
        mCallbackManager = CallbackManager.Factory.create();
        FacebookLoginButton.setReadPermissions("email", "public_profile");
        FacebookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
                Toast.makeText(MainActivity.this,"Success",Toast.LENGTH_SHORT);
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");

                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                Toast.makeText(MainActivity.this,"Login Failure",Toast.LENGTH_SHORT);
                // ...
            }
        });


        Ref = FirebaseDatabase.getInstance().getReference().child("users");
        // add listener to sign up button and sign in button
        signUpButton.setOnClickListener(this);
        googleButton.setOnClickListener(this);
        fakeButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if(v == signUpButton){
            signUp();
        }
        if(v == googleButton){
            signIn();
        }
        if(v == fakeButton) {
            FacebookLoginButton.performClick();
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        Intent i = new Intent(this, StartActivity.class);
        startActivity(i);
        return true;
    }

    private void signUp(){
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();
        String first = firstName.getText().toString().trim();
        String last = lastName.getText().toString().trim();
        imm.hideSoftInputFromWindow(emailText.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(passwordText.getWindowToken(), 0);
        emailInputLayout.setErrorEnabled(false);
        if(!email.matches(emailRegex)){
            emailInputLayout.setError("Invalid Email");
            emailText.requestFocus();
            return;
        }
        if(!password.matches(passwordRegex)){
            passwordInputLayout.setError("Invalid Password");
            passwordText.requestFocus();
            return;
        }
        if(!first.matches(nameRegex)){
            firstNameInputLayout.setError("Invalid First Name");
            firstName.requestFocus();
            return;
        }
        if(!last.matches(nameRegex)){
            lastNameInputLayout.setError("Invalid Last Name");
            lastName.requestFocus();
            return;
        }


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
                            Toast.makeText(MainActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            UserInfo UserInformation = new UserInfo(firstName.getText() +" "+lastName.getText(), lastName.getText().toString(),
                                    firstName.getText().toString(),user.getUid(),user.getEmail(),"student");
                            Ref.child(user.getUid()).setValue(UserInformation);
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(UserInformation.getFullName()).build();
                            user.updateProfile(profileUpdates);
                            sendEmailVerification();
                            finish();
                            startActivity(new Intent(getApplicationContext(), TutorListActivity.class));
                        }else{
                            progressDialog.dismiss();
                            String a = task.getException().toString();
                            if(!task.isSuccessful()) {
                                try {
                                    throw task.getException();
                                } catch(FirebaseAuthWeakPasswordException e) {
                                   // mTxtPassword.setError(getString(R.string.error_weak_password));
                                 //   mTxtPassword.requestFocus();
                                } catch(FirebaseAuthInvalidCredentialsException e) {
                                 //   mTxtEmail.setError(getString(R.string.error_invalid_email));
                                 //   mTxtEmail.requestFocus();
                                } catch(FirebaseAuthUserCollisionException e) {
                                    Toast.makeText(MainActivity.this, "The email you have entered is already in use.", Toast.LENGTH_LONG).show();

                                    //   mTxtEmail.setError(getString(R.string.error_user_exists));
                                 //   mTxtEmail.requestFocus();
                                } catch(Exception e) {
                                    Log.e(TAG, e.getMessage());
                                }
                            }
                        }
                    }
                });

    }
    private void sendEmailVerification(){
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {


                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(MainActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        progressDialog.setMessage("Signing up...");
        progressDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        } else{
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }
    //handle facebook login
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Success",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity(new Intent(MainActivity.this, TutorListActivity.class));
                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "The email associated with this facebook account is already registered.",
                                    Toast.LENGTH_SHORT).show();
                            LoginManager.getInstance().logOut();

                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            finish();
                            startActivity(new Intent(MainActivity.this, TutorListActivity.class));
                            progressDialog.dismiss();

                            //  updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}