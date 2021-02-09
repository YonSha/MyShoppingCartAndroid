package edu.yonatan.myshoppingcart;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton addItemBtn;
    private Button removeAllItemsBtn;

    private RecyclerView rvShoppingItems;

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;


    private TextView productsTV;
    private long productsCount;

    private String productsText;

    private String listID = "";


    private Button uploadListBtn;
    private TextView listIDTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        initViews();


        buttonDelay();


        checksUserAuth();

        getListID();


        addItemAlertBox();


        removeAllItemsBtn.setOnClickListener(v -> {


            mRef.child("carts").child(listID).child("myItems").removeValue();
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);


        });

        uploadListBtn.setOnClickListener(v -> {


            listID = listIDTV.getText().toString();
            uploadListBtn.setVisibility(View.GONE);
//            onStart();

            uploadDB();

        });


    }

    //uploads the list to the RV
    private void uploadDB() {
        productsText = getString(R.string.number_of_products);


        // init the database products and quantity count
        //products listener;
        productsAndQuantityInit();


        FirebaseRecyclerOptions<ShoppingItem> options = new FirebaseRecyclerOptions.Builder<ShoppingItem>().
                setQuery(mRef.child("carts").child(listID).child("myItems"), ShoppingItem.class).
                build();

        //set adapter:
        ShoppingListRV adapter = new ShoppingListRV(options, this);
        rvShoppingItems.setAdapter(adapter);

        adapter.startListening();
    }


    //enables the app - > the btn after X amount of time
    private void buttonDelay() {


        uploadListBtn.setEnabled(false);

        Timer buttonTimer = new Timer();
        buttonTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(() -> uploadListBtn.setEnabled(true));
            }
        }, 2500);


        if (!String.valueOf(listID).isEmpty()) {
            uploadListBtn.setEnabled(true);

        }


    }

    private void getListID() {


        if (mAuth.getCurrentUser() != null) {
            mRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("userDetails").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.hasChild("listID")) {
                        listID = dataSnapshot.child("listID").getValue().toString();
                        listIDTV.setText(listID);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        checksUserAuth();


        uploadDB();


    }

    private void checksUserAuth() {
        //checks if the user is in the database(is logged in) -> if not sends the user to login activity;
        //if he is int he database - > continue to categories activity:
        if (mAuth.getCurrentUser() == null) {

            sendUserToLoginActivity();

        } else {


        }

    }

    private void productsAndQuantityInit() {
        mRef.child("carts").child(listID).child("myItems").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                productsCount = dataSnapshot.getChildrenCount();


                HashMap<String, Object> shoppingCart = new HashMap();


                shoppingCart.put("products", productsCount);


                mRef.child("carts").child(listID).updateChildren(shoppingCart);


                productsTV.setText(productsText + " " + productsCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addItemAlertBox() {
        addItemBtn.setOnClickListener(v -> {


            AddItemCustomDialog dialog = new AddItemCustomDialog(this);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
            dialog.show();


        });


    }


    public void initViews() {


        rvShoppingItems = findViewById(R.id.rvShoppingItems);


        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvShoppingItems.setLayoutManager(manager);

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();

        addItemBtn = findViewById(R.id.addItemBtn);
        removeAllItemsBtn = findViewById(R.id.removeAllItemsBtn);

        productsTV = findViewById(R.id.productsTV);

        listIDTV = findViewById(R.id.testTV);
        uploadListBtn = findViewById(R.id.upListBtn);


    }


    //inflate top options menu:
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    /// HAMBURGER  upper menu bar hamburger
    //misc and settings:
    //not yet operational
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.menuSetting) {

            sendUserToListIdActivity();


        } else if (id == R.id.menuAbout) {
            Toast.makeText(this, "show About", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menuLogout) {
            //singout + send user to login activity
            mAuth.signOut();
            sendUserToLoginActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendUserToListIdActivity() {

        Intent listIdActivity = new Intent(this, ListIDActivity.class);
        startActivity(listIdActivity);
        finish();
    }


    //sends the user to login activity:
    private void sendUserToLoginActivity() {

        Intent loginActivity = new Intent(this, LoginActivity.class);
        startActivity(loginActivity);
        finish();

    }


}
