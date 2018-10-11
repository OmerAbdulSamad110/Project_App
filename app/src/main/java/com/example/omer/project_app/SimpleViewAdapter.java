package com.example.omer.project_app;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class SimpleViewAdapter extends RecyclerView.Adapter<SimpleViewAdapter.UserViewHolder> {
    List<Account> accounts;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    SimpleViewAdapter(List<Account> accounts) {
        this.accounts = accounts;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        CardView cardView;
        TextView textView;

        public UserViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.simplels_img);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            textView = (TextView) itemView.findViewById(R.id.simplels_txt);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_recycler, parent, false);
        UserViewHolder uvh = new UserViewHolder(view, mListener);
        return uvh;
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.imageView.setImageBitmap(accounts.get(position).userDP);
        holder.textView.setText(accounts.get(position).userName);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }
}
