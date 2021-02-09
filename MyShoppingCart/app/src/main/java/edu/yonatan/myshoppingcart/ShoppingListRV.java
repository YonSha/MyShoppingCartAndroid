package edu.yonatan.myshoppingcart;


import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ShoppingListRV extends FirebaseRecyclerAdapter<ShoppingItem, ShoppingListRV.ShoppingListHolder> {

    private Context context;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String listID;




    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

    //ctor:
    public ShoppingListRV(@NonNull FirebaseRecyclerOptions<ShoppingItem> options, Context context) {
        super(options);
        this.context = context;

    }



    @NonNull
    @Override
    public ShoppingListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {




        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.item_row, parent, false);
        ShoppingListHolder holder = new ShoppingListHolder(v);

        return holder;

    }


    @Override
    protected void onBindViewHolder(@NonNull ShoppingListHolder holder, int i, @NonNull ShoppingItem shoppingItem) {


getListID();



holder.itemDate.setText(shoppingItem.getDate());
holder.itemTitle.setText(shoppingItem.getName());
holder.itemDesc.setText(shoppingItem.getDescription());
holder.itemQuantity.setText(shoppingItem.getQuantity());
holder.checkBox.setChecked(shoppingItem.isChecked());

holder.removeItem.setOnClickListener(v->{


   mRef.child("carts").child(listID).child("myItems").child(String.valueOf(getRef(i).getKey()))
           .removeValue().addOnCompleteListener(task -> notifyDataSetChanged());


});






    }



    private void getListID() {
        mRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("userDetails").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listID = dataSnapshot.child("listID").getValue().toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public class ShoppingListHolder extends RecyclerView.ViewHolder {

        private View cellView;

        private Button removeItem;
        private CheckBox checkBox;
        private TextView itemTitle, itemDesc, itemDate, itemQuantity;
        HashMap<String, Object> shoppingCart;


        public ShoppingListHolder(@NonNull View itemView) {
            super(itemView);

            removeItem = itemView.findViewById(R.id.removeSingleItemBtn);
            checkBox = itemView.findViewById(R.id.checkItemBtn);
            itemTitle = itemView.findViewById(R.id.itemNameTV);
            itemDesc = itemView.findViewById(R.id.itemDescTV);
            itemDate = itemView.findViewById(R.id.itemDateTV);
            itemQuantity = itemView.findViewById(R.id.quantityTV);


            cellView = itemView.findViewById(R.id.myView);




            cellView.setOnClickListener(v -> {
                shoppingCart = new HashMap();
                if(checkBox.isChecked()){


                    shoppingCart.put("checked", false);

                    mRef.child("carts").child(listID).child("myItems").child(String.valueOf(getRef(getAdapterPosition()).getKey())).updateChildren(shoppingCart);


                }else {

                    shoppingCart.put("checked", true);

                    mRef.child("carts").child(listID).child("myItems").child(String.valueOf(getRef(getAdapterPosition()).getKey())).updateChildren(shoppingCart);

                }


            });
        }




    }

}


