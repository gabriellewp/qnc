package com.example.gabrielle.laundryonline;

import android.location.Address;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gabrielle.laundryonline.db.LaundryOrder;
import com.example.gabrielle.laundryonline.db.UserAddressDetails;

import java.util.List;

/**
 * Created by gabrielle on 7/21/2016.
 */
public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.MyViewHolder>{
    private List<UserAddressDetails> userAddressDetailsList;
    private int selectedItem = 0;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView label, detail;

        public MyViewHolder(View view) {
            super(view);
            label = (TextView) view.findViewById(R.id.label);
            detail = (TextView) view.findViewById(R.id.detail);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Redraw the old selection and the new
                    notifyItemChanged(selectedItem);
                    selectedItem = getLayoutPosition();
                    notifyItemChanged(selectedItem);
                }
            });

        }

    }

    public AddressAdapter(List<UserAddressDetails> orderList) {
        this.userAddressDetailsList = orderList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.address_list_row, parent, false);

        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //Log.d("onbindviewholder","address");
        UserAddressDetails uad = userAddressDetailsList.get(position);
        holder.label.setText(uad.getLabelAddress());
        holder.detail.setText(uad.getCompleteAddress());
        holder.itemView.setSelected(selectedItem == position);


    }
    @Override
    public int getItemCount(){
        return userAddressDetailsList.size();
    }

}
