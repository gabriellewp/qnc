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

import java.util.List;

/**
 * Created by gabrielle on 5/28/2016.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder>{
    private List<LaundryOrder> orderList;
    private int timeRange;
    private Context context;
    private Intent intentToDetailOrder;
    private LaundryOrder lo;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout ll;
        public TextView takenDate, packageType, status;
        public Button actionButton;
        public MyViewHolder(View view) {
            super(view);
            view.setBackgroundResource(R.drawable.past_order_bg);
            ll = (LinearLayout) view.findViewById(R.id.linearlayoutorder);
            takenDate = (TextView) view.findViewById(R.id.taken_date);
            packageType = (TextView) view.findViewById(R.id.package_type);
            status = (TextView) view.findViewById(R.id.history_status);
            actionButton = (Button) view.findViewById(R.id.delete_button);

        }

    }

    public HistoryAdapter(List<LaundryOrder> orderList, int timeRange, Context _context) {
        this.orderList = orderList;
        this.timeRange = timeRange;
        this.context = _context;
        intentToDetailOrder = new Intent(context, ShowOrderDetailActivity.class);
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

        return new MyViewHolder(itemView);

    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
       lo = orderList.get(position);
        Log.d("holderhistoryadapter",lo.getTakenDate());
        Log.d("masuksiniloh","onbindviewholder"+timeRange);

        holder.takenDate.setText(lo.getTakenDate());
        if(lo.getPackage_id()==0){
            holder.packageType.setText("wash&fold");
        }else{
            holder.packageType.setText("wash&iron");
        }
        if(lo.getOrderStatus()==0){
            holder.status.setText("in progress");
        }else{
            holder.status.setText("finished");

        }
        if(timeRange==1){
            holder.actionButton.setText("STATUS DITERIMA");
        }
        holder.ll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                /// button click
                //showDialog();
                intentToDetailOrder.putExtra("orderID",lo.getOrderID());
                intentToDetailOrder.putExtra("timeRange",1);
                context.startActivity(intentToDetailOrder);
            }
        });
        holder.actionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                /// button click
                if(timeRange==0){
                    showDialogPast();
                }else if(timeRange==1){
                    showDialogPresent();
                }else if(timeRange==2){
                    showDialogFuture();
                }

            }
        });

    }
    @Override
    public int getItemCount(){
        Log.d("historadapteritemcount",orderList.size()+"");
        return orderList.size();
    }
    public void showDialogPast(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View dialogView = inflater.inflate(R.layout.dialog_give_review, null);
        final EditText reviewText = (EditText) dialogView.findViewById(R.id.review);
        final RatingBar rtb = (RatingBar) dialogView.findViewById(R.id.ratingBar);
        alertDialogBuilder.setView(dialogView);
        alertDialogBuilder
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d("review",reviewText.getText().toString());
                        Log.d("ratingbar",String.valueOf(rtb.getRating()));
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
    public void showDialogPresent(){
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
    public void showDialogFuture(){
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
