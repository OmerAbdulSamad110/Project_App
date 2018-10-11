package com.example.omer.project_app;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SearchViewAdapter extends RecyclerView.Adapter<SearchViewAdapter.UserViewHolder> {
    List<Account> accounts;
    Context context;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onAddClick(int position);

        void onCancelClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    SearchViewAdapter(List<Account> accounts, Context context) {
        this.accounts = accounts;
        this.context = context;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        CardView cardView;
        TextView textView;
        Button button1, button2;

        public UserViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.user_img);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            textView = (TextView) itemView.findViewById(R.id.user_name);
            button1 = (Button) itemView.findViewById(R.id.user_add);
            button2 = (Button) itemView.findViewById(R.id.user_cancel);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        listener.onItemClick(position);
                    }
                }
            });
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        listener.onAddClick(position);
                    }
                }
            });
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        listener.onCancelClick(position);
                    }
                }
            });
        }
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_search_view, parent, false);
        UserViewHolder uvh = new UserViewHolder(view, mListener);
        return uvh;
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.imageView.setImageBitmap(accounts.get(position).userDP);
        holder.textView.setText(accounts.get(position).userName);
        DatabaseClass mydb = new DatabaseClass(context);
        int i = 0;
        Integer id = null;
        ArrayList<Integer> requestSend = new ArrayList<Integer>();
        ArrayList<Integer> requestGot = new ArrayList<Integer>();
        ArrayList<Integer> friends = new ArrayList<Integer>();

        Cursor res1 = mydb.getAllAccountData();
        while (res1.moveToNext()) {
            if (res1.getInt(1) == 1) {
                id = res1.getInt(0);
                i = 1;
                break;
            }
        }
        if (i == 1) {
            //Request send to user
            Cursor res2 = mydb.reqSend(id);
            while (res2.moveToNext()) {
                requestSend.add(res2.getInt(0));
            }
            if (requestSend.size() >= 1) {
                for (int a = 0; a < requestSend.size(); a++) {
                    if (requestSend.get(a) == accounts.get(position).userId) {
                        holder.button1.setVisibility(View.INVISIBLE);
                        holder.button2.setVisibility(View.VISIBLE);
                    } else if (requestSend.get(a) != accounts.get(position).userId) {
                        holder.button1.setVisibility(View.VISIBLE);
                        holder.button2.setVisibility(View.INVISIBLE);
                    }
                }
            } else if (requestSend.size() == 0) {
                holder.button1.setVisibility(View.VISIBLE);
                holder.button2.setVisibility(View.INVISIBLE);
            }
            //Request got from users
            Cursor res3 = mydb.reqGot(id);
            while (res3.moveToNext()) {
                requestGot.add(res3.getInt(0));
            }
            for (int a = 0; a < requestGot.size(); a++) {
                if (requestGot.get(a) == accounts.get(position).userId) {
                    holder.button1.setVisibility(View.INVISIBLE);
                    holder.button2.setVisibility(View.INVISIBLE);
                }
            }
            //Friend users
            Cursor res4 = mydb.friendList(id);
            while (res4.moveToNext()) {
                friends.add(res4.getInt(1));
            }
            for (int a = 0; a < friends.size(); a++) {
                if (friends.get(a) == accounts.get(position).userId) {
                    holder.button1.setVisibility(View.INVISIBLE);
                    holder.button2.setVisibility(View.INVISIBLE);
                }
            }
        }
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

