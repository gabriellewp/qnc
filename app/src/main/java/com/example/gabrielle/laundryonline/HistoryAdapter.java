package com.example.gabrielle.laundryonline;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.gabrielle.laundryonline.db.LaundryOrder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by gabrielle on 5/28/2016.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder>{
    private DatabaseReference mDatabase;
    private List<LaundryOrder> orderList;
    private int timeRange;
    private Context context;
    private Intent intentToDetailOrder;
    private LaundryOrder lo;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout ll;
        public TextView takenDate, packageType, status, datetakenFuture, timeFuture, addressLabelFuture, noteFuture, idPresent, dateTakenPresent, dateReturnPresent, idPast, dateTakenPast, dateReturnPast;
        public Button actionButton;
        private String orderID;
        public MyViewHolder(View view, int timeRange) {
            super(view);
            view.setBackgroundResource(R.drawable.past_order_bg);
            ll = (LinearLayout) view.findViewById(R.id.linearlayoutorder);
//            takenDate = (TextView) view.findViewById(R.id.taken_date);
//            packageType = (TextView) view.findViewById(R.id.package_type);
//            status = (TextView) view.findViewById(R.id.history_status);
            actionButton = (Button) view.findViewById(R.id.delete_button);

            if(timeRange==0){ //past
                idPast = (TextView) view.findViewById(R.id.orderIDPast);
                dateTakenPast = (TextView) view.findViewById(R.id.dateTakenPast);
                dateReturnPast =(TextView) view.findViewById(R.id.dateReturnPast);
            }else if(timeRange==1){ //present
                idPresent = (TextView) view.findViewById(R.id.orderIDPresent);
                dateTakenPresent =(TextView) view.findViewById(R.id.dateTakenPresent);
                dateReturnPresent = (TextView) view.findViewById(R.id.dateReturnPresent);
            }else if(timeRange==2){ //future
                datetakenFuture = (TextView) view.findViewById(R.id.dateTakenFuture);
                timeFuture = (TextView) view.findViewById(R.id.timeFuture);
                addressLabelFuture = (TextView) view.findViewById(R.id.addressLabelFuture);
                noteFuture = (TextView) view.findViewById(R.id.noteFuture);
            }

        }

    }

    public HistoryAdapter(List<LaundryOrder> orderList, int timeRange, Context _context) {
        this.orderList = orderList;
        this.timeRange = timeRange;
        this.context = _context;
        intentToDetailOrder = new Intent(context, ShowOrderDetailActivity.class);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }
    public int getTimeRange(){
        return this.timeRange;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_list_row_present, parent, false);
        if(timeRange==0){//past
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.history_list_row_past, parent, false);
        }else if(timeRange==1){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.history_list_row_present, parent, false);
        }else if(timeRange==2){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.history_list_row_future, parent, false);
        }

        return new MyViewHolder(itemView, timeRange);

    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
       lo = orderList.get(position);
        Log.d("holderhistoryadapter",lo.getTakenDate());
        Log.d("masuksiniloh","onbindviewholder"+timeRange);
        holder.orderID = lo.getOrderID();
        Log.d("orderidinadaptert1",lo.getOrderID());

        if(timeRange==1){ //present
            holder.actionButton.setText("STATUS DITERIMA");
            holder.idPresent.setText("ID "+lo.getOrderID());
            holder.dateTakenPresent.setText("WKT PENJEMPUTAN:"+lo.getTakenDate());
            holder.dateReturnPresent.setText("WKT PENGIRIMAN"+lo.getReturnDate());
        }else if(timeRange==0){ //past
            holder.idPast.setText("ID "+lo.getOrderID());
            holder.dateTakenPast.setText("WKT PENJEMPUTAN:"+lo.getTakenDate());
            holder.dateReturnPast.setText("WKT PENGIRIMAN:"+lo.getReturnDate());
        }else if(timeRange==2){ //future
            holder.datetakenFuture.setText(lo.getTakenDate());
            holder.addressLabelFuture.setText(lo.getAddressLabel());
            holder.noteFuture.setText(lo.getNote());
        }
        holder.ll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                /// button click
                //showDialog();
                //Log.d("orderlistposition",position+"");
                //Log.d("orderidinadapter",lo.getOrderID());
                intentToDetailOrder.putExtra("orderID",holder.orderID);
                intentToDetailOrder.putExtra("timeRange",1);
                context.startActivity(intentToDetailOrder);
            }
        });
        holder.actionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                /// button click
                if(timeRange==0){
                    showDialogPast(holder.orderID);
                }else if(timeRange==1){
                    showDialogPresent(holder.orderID);
                }else if(timeRange==2){
                    showDialogFuture(holder.orderID);
                }

            }
        });

    }
    @Override
    public int getItemCount(){
        //Log.d("historadapteritemcount",orderList.size()+"");
        return orderList.size();
    }
    public void showDialogPast(String id){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialogView = inflater.inflate(R.layout.dialog_give_review, null);
        final EditText reviewText = (EditText) dialogView.findViewById(R.id.review);
        final RatingBar rtb = (RatingBar) dialogView.findViewById(R.id.ratingBar);
        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Log.d("review",reviewText.getText().toString());
                        //Log.d("ratingbar",String.valueOf(rtb.getRating()));
                        mDatabase.child("laundryOrders").child(lo.getOrderID()).child("rating").setValue(String.valueOf(rtb.getRating()));
                        mDatabase.child("laundryOrders").child(lo.getOrderID()).child("review").setValue(reviewText.getText().toString());
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public void showDialogPresent(String id){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Status Sekarang \r\n"+lo.getOrderStatus())
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public void showDialogFuture(String id){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Apakah ingin dibatalkan?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
