package com.qubuss.test.sqlsynstest;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by qubuss on 25.02.2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>{

    private ArrayList<Contact> arrayList = new ArrayList<>();

    public RecyclerAdapter(ArrayList<Contact> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.nameTV.setText(arrayList.get(position).getName());
        int sync_status = arrayList.get(position).getSync_status();

        if(sync_status == DbContract.SYNC_STATUS_OK){
            holder.syncImg.setImageResource(R.drawable.ok);
        }else {
            holder.syncImg.setImageResource(R.drawable.sync);
        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView syncImg;
        TextView nameTV;

        public MyViewHolder(View itemView) {
            super(itemView);

            syncImg = (ImageView) itemView.findViewById(R.id.syncImg);
            nameTV = (TextView) itemView.findViewById(R.id.nameTV);
        }
    }
}
