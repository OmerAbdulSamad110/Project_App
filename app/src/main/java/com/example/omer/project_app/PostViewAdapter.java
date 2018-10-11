package com.example.omer.project_app;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class PostViewAdapter extends RecyclerView.Adapter<PostViewAdapter.UserViewHolder> {
    Context context;
    List<PostSt> posts;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onImageClick(int position);

        void onNameClick(int position);

        void onLikeClick(int position);

        void onUnLikeClick(int position);

        void onCommentClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    PostViewAdapter(Context context, List<PostSt> posts) {
        this.context = context;
        this.posts = posts;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView textView1, textView2, textView3, textView4;
        Button button1, button2, button3;
        ImageView imageView1, imageView2;

        public UserViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.post_view);
            imageView1 = (ImageView) itemView.findViewById(R.id.p_img);
            imageView2 = (ImageView) itemView.findViewById(R.id.post_img);
            textView1 = (TextView) itemView.findViewById(R.id.p_name);
            textView2 = (TextView) itemView.findViewById(R.id.post_date);
            textView3 = (TextView) itemView.findViewById(R.id.post_av);
            textView4 = (TextView) itemView.findViewById(R.id.post_txt);
            button1 = (Button) itemView.findViewById(R.id.btn_like);
            button2 = (Button) itemView.findViewById(R.id.btn_unlike);
            button3 = (Button) itemView.findViewById(R.id.btn_cmnt);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        listener.onItemClick(position);
                    }
                }
            });

            imageView1.setOnClickListener(new View.OnClickListener() {
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

            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        listener.onLikeClick(position);
                    }
                }
            });
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        listener.onUnLikeClick(position);
                    }
                }
            });

            button3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        listener.onCommentClick(position);
                    }
                }
            });
        }
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_post_view, parent, false);
        UserViewHolder uvh = new UserViewHolder(view, mListener);
        return uvh;
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        int activeId = 0, i = 0;
        DatabaseClass mydb = new DatabaseClass(context);
        Cursor res = mydb.getAllAccountData();
        while (res.moveToNext()) {
            if (res.getInt(1) == 1) {
                activeId = res.getInt(0);
                break;
            }
        }
        holder.imageView1.setImageBitmap(posts.get(position).accImg);
        holder.textView1.setText(posts.get(position).accName);
        holder.textView2.setText(posts.get(position).postDate);
        holder.textView3.setText(posts.get(position).postPrivacy);

        if (posts.get(position).hasImage == 1 && posts.get(position).hasText == 1) {
            holder.imageView2.setImageBitmap(posts.get(position).postImage);
            holder.textView4.setText(posts.get(position).postText);
        } else if (posts.get(position).hasImage == 1 && posts.get(position).hasText == 0) {
            holder.imageView2.setImageBitmap(posts.get(position).postImage);
            holder.textView4.setPadding(0, 0, 0, 0);
            holder.textView4.setTextSize(0);
        } else if (posts.get(position).hasImage == 0 && posts.get(position).hasText == 1) {
            holder.textView4.setText(posts.get(position).postText);
            holder.imageView2.getLayoutParams().height = 0;
        }

        //If Liked
        Cursor liked = mydb.viewActiveLikeP(posts.get(position).postId, activeId);
        if (liked.moveToNext()) {
            if (liked.getInt(2) == activeId) {
                i = 1;
            }
        }
        if (i == 1) {
            holder.button1.setVisibility(View.INVISIBLE);
            holder.button2.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
