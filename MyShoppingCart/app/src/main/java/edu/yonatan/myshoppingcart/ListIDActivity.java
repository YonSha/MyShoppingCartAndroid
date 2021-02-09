package edu.yonatan.myshoppingcart;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.utilities.Utilities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class ListIDActivity extends AppCompatActivity implements View.OnClickListener {


    private FloatingActionButton generateCodeFab, insertedCodeOkFab, insertedCodeWrongFab;
    private Button continueToMainActivityBtn;
    private EditText generatedCodeET, insertExistingIdET;
    private TextView listStartID;


    private FirebaseAuth mAuth;
    private DatabaseReference mRef;

    private String listID = "";
    private String tempOldList = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_id);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();
        initViews();


        generateCodeFabACTION();


        generateCodeFab.setOnClickListener(this);
        continueToMainActivityBtn.setOnClickListener(this);


        existingListAction();


    }

    private void existingListAction() {


        insertExistingIdET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (count < 8 ) {

                    insertedCodeWrongFab.setVisibility(View.VISIBLE);
                    insertedCodeOkFab.setVisibility(View.INVISIBLE);

                    //------

                    if (listID.isEmpty()) {


                    } else {
                        generatedCodeET.setText(listID);
                        generatedCodeET.setBackgroundColor(Color.TRANSPARENT);
                    }


                    //------

                } else {

                    insertedCodeOkFab.setVisibility(View.VISIBLE);
                    insertedCodeWrongFab.setVisibility(View.INVISIBLE);

                    if (generatedCodeET.getText().toString().contains(insertExistingIdET.getText().toString())) {
                        generatedCodeET.setBackgroundColor(Color.WHITE);

                    } else {
                        generatedCodeET.setText("REMOVED");
                        generatedCodeET.setBackgroundColor(Color.parseColor("#8b0000"));
                    }


                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void generateCodeFabACTION() {

        if (mAuth.getCurrentUser() != null) {

            mRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("userDetails").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    if (dataSnapshot.hasChild("listID")) {

                        listID = dataSnapshot.child("listID").getValue().toString();


                        generatedCodeET.setText(listID);

                    } else {


                        HashMap<String, Object> aMap = new HashMap<>();

                        StringBuilder id = new StringBuilder();
                        id.append(mRef.push().getKey().substring(3, 11).toLowerCase());
                        id.insert(0, "-Lv");

                        String idFinish = id.toString();
                        aMap.put("listID",idFinish);

                        mRef.child("Users").child(mAuth.getCurrentUser().getUid().toString()).child("userDetails").updateChildren(aMap);

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }


    private void generateListID() {


        HashMap<String, Object> aMap = new HashMap<>();

        StringBuilder id = new StringBuilder();
        id.append(mRef.push().getKey().substring(3, 11).toLowerCase());
        id.insert(0, "-Lv");

        String idFinish = id.toString();


        generatedCodeET.setText(idFinish);
        insertExistingIdET.setText(idFinish.substring(3, 11));


    }


    private void initViews() {

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();


        listStartID = findViewById(R.id.listStartIDTV);

        generatedCodeET = findViewById(R.id.generatedCodeET);
        insertExistingIdET = findViewById(R.id.insertedCodeET);

        generateCodeFab = findViewById(R.id.generateCodeFab);
        insertedCodeOkFab = findViewById(R.id.insertedCodeOkFab);
        insertedCodeWrongFab = findViewById(R.id.insertedCodeWrongFab);

        continueToMainActivityBtn = findViewById(R.id.continueToMainActivityBtn);


    }


    private void sendUserToMainActivity() {

        Intent mainActivity = new Intent(this, MainActivity.class);
        startActivity(mainActivity);
        finish();
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.generateCodeFab) {

            generateListID();

//----------------------------------------------------------------------------------------
            //-----------------------------------
        } else if (v.getId() == R.id.continueToMainActivityBtn) {


            if (generatedCodeET.getText().toString().contains("REMOVED") ||
                    generatedCodeET.getText().toString().equals(listStartID.getText().toString() + insertExistingIdET.getText().toString())) {


                LayoutInflater inflater = LayoutInflater.from(this);
                View y = inflater.inflate(R.layout.custom_list_alert_dialog, null);
                AlertDialog dialog = new AlertDialog.Builder(this).setView(y).show();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
                Button btnContinueCD = dialog.findViewById(R.id.btnContinueCD);
                Button btnCancelCD = dialog.findViewById(R.id.btnCancelCD);

                customDialogBtns(btnContinueCD, btnCancelCD, dialog);


            } else {


                listID = generatedCodeET.getText().toString().substring(0, 11);


                HashMap<String, Object> aMap = new HashMap<>();


                aMap.put(mAuth.getCurrentUser().getUid(), "user");

                mRef.child("carts").child(listID).child("owners").updateChildren(aMap);

                HashMap<String, Object> aMapTwo = new HashMap<>();

                aMapTwo.put("listID", listID);
                mRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("userDetails").updateChildren(aMapTwo);

                sendUserToMainActivity();

            }


        }


    }

    private void customDialogBtns(Button btnContinueCD, Button btnCancelCD, AlertDialog dialog) {
        btnContinueCD.setOnClickListener(z -> {
            tempOldList = listID;

            listID = listStartID.getText().toString() + insertExistingIdET.getText().toString();


            HashMap<String, Object> aMap = new HashMap<>();


            aMap.put(mAuth.getCurrentUser().getUid(), "user");

            mRef.child("carts").child(listID).child("owners").updateChildren(aMap);

            HashMap<String, Object> aMapTwo = new HashMap<>();

            aMapTwo.put("listID", listID);
            mRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("userDetails").updateChildren(aMapTwo);

            mRef.child("carts").child(tempOldList).child("owners").child(mAuth.getCurrentUser().getUid()).removeValue();
            mRef.child("carts").child(tempOldList).child("owners").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                  if(dataSnapshot.getChildrenCount() == 0){

                      mRef.child("carts").child(tempOldList).removeValue();
                  }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            dialog.dismiss();
            sendUserToMainActivity();


        });

        btnCancelCD.setOnClickListener(z -> {
            dialog.dismiss();
            sendUserToMainActivity();


        });
    }


}
