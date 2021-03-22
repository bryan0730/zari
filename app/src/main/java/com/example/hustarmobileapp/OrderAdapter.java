package com.example.hustarmobileapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderAdapterHolder> {

    private final String TAG = "OrderAdapter";

    private ArrayList<OrderDomain> orderDomainArrayList;
    private Context context;

    private OnItemClickListener mListener = null;

    public OrderAdapter(Context context, ArrayList<OrderDomain> orderDomainArrayList){
        this.context = context;
        this.orderDomainArrayList = orderDomainArrayList;
    }

    @NonNull
    @Override
    public OrderAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.order_item,null);
        OrderAdapterHolder orderAdapterHolder = new OrderAdapterHolder(view);
        return orderAdapterHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.OrderAdapterHolder holder, int position) {
        holder.orderMenuNameTextView.setText(orderDomainArrayList.get(position).getMenuName());
        holder.orderMenuPriceTextView.setText(orderDomainArrayList.get(position).getMenuPrice());

    }

    @Override
    public int getItemCount() {
        return orderDomainArrayList.size();
    }

    public int getTotalPrice(){
        int totalPrice=0;
        for(int i=0; i<orderDomainArrayList.size(); i++){
            totalPrice += Integer.parseInt(orderDomainArrayList.get(i).getMenuPrice());
        }
        return totalPrice;
    }

    public void setOnItemClickListener(OrderAdapter.OnItemClickListener listener){
        this.mListener = listener;
    }

    public interface  OnItemClickListener{
        void onItemClick(View v, int positon);
    }


    public class OrderAdapterHolder extends RecyclerView.ViewHolder {
        TextView orderMenuNameTextView, orderMenuPriceTextView;
        ImageButton imageButton;

        public OrderAdapterHolder(@NonNull View itemView) {
            super(itemView);
            orderMenuNameTextView = itemView.findViewById(R.id.orderMenuNameTextView);
            orderMenuPriceTextView = itemView.findViewById(R.id.orderMenuPriceTextView);
            imageButton = itemView.findViewById(R.id.imageButton);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getBindingAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        if(mListener != null){
                            mListener.onItemClick(view, position);
                        }
                    }
                }
            });
        }
    }
}
