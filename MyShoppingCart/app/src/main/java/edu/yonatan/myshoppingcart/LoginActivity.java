package edu.yonatan.myshoppingcart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;



public class LoginActivity extends AppCompatActivity {


    //props:
    private Button btnGmail;


    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private GoogleSignInClient mGoogleSignInClient;


    private static final int RC_SIGN_IN = 5;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();


        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();

       //init views:
        initViews();



        googleSignIn();

        //sign in the user via connected Gmail:
        btnGmail.setOnClickListener(v -> {


            signIn();



        });




    }
//sends the user to phone register activity:



    private void googleSignIn() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {

                Toast.makeText(this, "Something went wrong...", Toast.LENGTH_SHORT).show();

            }
        }
    }






    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        //checks if the user already in the db ->
                        // if so -> sends directly to the main activity,
                        //else - > send to listID activity
                        mRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.child(mAuth.getCurrentUser().getUid()).exists()){
                                        sendUserToMainActivity();
                                    }else{


                                        String email = acct.getEmail();
                                        String displayName = acct.getGivenName();
                                        String name = acct.getGivenName() + " " + acct.getFamilyName();
                                        String userID = mAuth.getCurrentUser().getUid();

                                        Map<String,String> userDetails = new HashMap<>();

                                        userDetails.put("display_name",displayName);
                                        userDetails.put("name",name);
                                        userDetails.put("email",email);
                                        userDetails.put("id",userID);


                                        mRef.child("Users").child(userID).child("userDetails").setValue(userDetails);



                                        sendUserToListIdActivity();

                                    }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });





                    } else {


                    }


                });
    }

    private void sendUserToMainActivity() {

        Intent mainActivity = new Intent(this, MainActivity.class);
        startActivity(mainActivity);
        finish();
    }


    private void sendUserToListIdActivity() {

        Intent listIdActivity = new Intent(this, ListIDActivity.class);
        startActivity(listIdActivity);
        finish();
    }

    //init views:
    private void initViews() {


        btnGmail = findViewById(R.id.btnGmailLogin);



    }
}
