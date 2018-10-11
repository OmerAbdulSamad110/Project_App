package com.example.omer.project_app;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CommnetViewAdapter extends RecyclerView.Adapter<CommnetViewAdapter.UserViewHolder> {
    Context context;
    List<Comment> comments;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onImageClick(int position);

        void onNameClick(int position);

        void onLikeClick(int position);

        void onUnlikeClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    CommnetViewAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView textView1, textView2, textView3, textView4, textView5, textView6;
        ImageView imageView;

        public UserViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.comment_view);
            textView1 = (TextView) itemView.findViewById(R.id.c_name);
            textView2 = (TextView) itemView.findViewById(R.id.c_txt);
            textView3 = (TextView) itemView.findViewById(R.id.c_date);
            textView4 = (TextView) itemView.findViewById(R.id.c_like);
            textView5 = (TextView) itemView.findViewById(R.id.c_unlike);
            textView6 = (TextView) itemView.findViewById(R.id.c_likedBy);
            imageView = (ImageView) itemView.findViewById(R.id.c_img);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        listener.onItemClick(position);
                    }
                }
            });

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        listener.onImageClick(position);
                    }
                }
            });

            textView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        listener.onNameClick(position);
                    }
                }
            });

            textView4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        listener.onLikeClick(position);
                    }
                }
            });
            textView5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        listener.onUnlikeClick(position);
                    }
                }
            });
        }
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_comment_view, parent, false);
        UserViewHolder uvh = new UserViewHolder(view, mListener);
        return uvh;
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        int activeId = 0, i = 0, cLike = 0;
        DatabaseClass mydb = new DatabaseClass(context);
        Cursor cmnt = mydb.viewSpecificC(comments.get(position).postId, comments.get(position).commentId);
        Cursor res = mydb.getAllAccountData();
        while (res.moveToNext()) {
            if (res.getInt(1) == 1) {
                activeId = res.getInt(0);
                break;
            }
        }
        if (cmnt.moveToNext()) {
            cLike = cmnt.getInt(7);
        }
        holder.imageView.setImageBitmap(comments.get(position).commenterImg);
        holder.textView1.setText(comments.get(position).commenterName);
        holder.textView2.setText(comments.get(position).comment);
        holder.textView3.setText(comments.get(position).commentDate);

        //Changes in like
        if (cLike >= 1) {
            holder.textView6.setText("Liked by " + cLike);
        } else if (cLike == 0) {
            holder.textView6.setVisibility(View.INVISIBLE);
        }

        //If Liked
        Cursor liked = mydb.viewActiveLikeC(comments.get(position).postId, comments.get(position).commentId, activeId);
        if (liked.moveToNext()) {
            if (liked.getInt(3) == activeId) {
                i = 1;
            }
        }
        if (i == 1) {
            holder.textView4.setVisibility(View.INVISIBLE);
            holder.textView5.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
}
