package edu.yonatan.myshoppingcart;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddItemCustomDialog extends Dialog implements View.OnClickListener {


    private DatabaseReference mRef;
    private FirebaseAuth mAuth;

    private Context c;
    private Dialog d;
    private Button add, cancel, plus, minus;
    private EditText name, desc, quantity;

    int currentQuantity;


    public AddItemCustomDialog(@NonNull Context a) {
        super(a);

        this.c = a;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.add_alert_box);


        initViews();


        add.setOnClickListener(this);
        cancel.setOnClickListener(this);


        plus.setOnClickListener(v->{



           currentQuantity = Integer.valueOf(quantity.getText().toString());

            if(currentQuantity>=1&&currentQuantity<9) {
                currentQuantity += 1;

                quantity.setText(String.valueOf(currentQuantity));
            }


        });


        minus.setOnClickListener(v->{

         currentQuantity = Integer.valueOf(quantity.getText().toString());



            if(currentQuantity>1&&currentQuantity<=9) {
                currentQuantity -= 1;

                quantity.setText(String.valueOf(currentQuantity));
            }



        });





    }



    private void changeQuantity(){




    }

    private void initViews() {

        mRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();


        add = findViewById(R.id.positiveAlertBtn);
        cancel = findViewById(R.id.negativeAlertBtn);
        name = findViewById(R.id.nameET);
        desc = findViewById(R.id.descET);

        plus = findViewById(R.id.plusBtn);
        minus = findViewById(R.id.minusBtn);

        quantity =  findViewById(R.id.quantityET);
        quantity.setFilters(new InputFilter[]{ new FilterMinMaxNumber("1", "999")});
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.positiveAlertBtn:


                updateList();

                break;
            case R.id.negativeAlertBtn:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }





    public void updateList() {

        String name = this.name.getText().toString();
        String desc = this.desc.getText().toString();
        String quantity = this.quantity.getText().toString();


        if (name.isEmpty()) {


        } else {


            String currentMessageDate;

            //gets current date + formatted:
            Calendar calendarDate = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd,yyyy");
            currentMessageDate = simpleDateFormat.format(calendarDate.getTime());


            HashMap<String, Object> shoppingCart = new HashMap();

            String messageKey = mRef.push().getKey();

            shoppingCart.put("name", name);
            shoppingCart.put("description", desc);
            shoppingCart.put("date", currentMessageDate);
            shoppingCart.put("quantity", quantity);
            shoppingCart.put("checked", false);


            mRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("userDetails").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    mRef.child("carts").child(dataSnapshot.child("listID").getValue().toString()).child("myItems").child(messageKey).updateChildren(shoppingCart);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }


    }

}






