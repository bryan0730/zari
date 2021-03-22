package com.example.hustarmobileapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuAdapterHolder> {

    private final String TAG = "MenuAdapter";

    private ArrayList<MenuDomain> menuDomainArrayList;
    private int images[];
    private Context context;

    private OnItemClickListener mListener = null;

    public MenuAdapter(Context context, ArrayList<MenuDomain> menuDomainArrayList, int images[]){
        this.context = context;
        this.menuDomainArrayList = menuDomainArrayList;
        this.images = images;
    }

    @NonNull
    @Override
    public MenuAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.menu_item,null);
        MenuAdapterHolder menuAdapterHolder = new MenuAdapterHolder(view);
        return menuAdapterHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MenuAdapterHolder holder, int position) {
        holder.menuNameTextv.setTag(menuDomainArrayList.get(position).getMenuSeq());
        holder.menuNameTextv.setText(menuDomainArrayList.get(position).getMenuName());
        holder.menuPrictTextv.setText(menuDomainArrayList.get(position).getMenuPrice());
        holder.menuImageView.setImageResource(images[position]);
    }

    @Override
    public int getItemCount() {
        return menuDomainArrayList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    public interface  OnItemClickListener{
        void onItemClick(View v, int positon);
    }

    public class MenuAdapterHolder extends RecyclerView.ViewHolder{

        TextView menuNameTextv, menuPrictTextv;
        ImageView menuImageView;

        public MenuAdapterHolder(@NonNull View itemView) {
            super(itemView);
            menuNameTextv = itemView.findViewById(R.id.menuNameTextv1);
            menuPrictTextv = itemView.findViewById(R.id.menuPriceTextv1);
            menuImageView = itemView.findViewById(R.id.menuImageView1);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "클릭 : " + menuNameTextv.getTag(), Toast.LENGTH_SHORT).show();
                    int position = getBindingAdapterPosition();
                    Log.i(TAG, "어뎁터 포지션 : "+position);
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
