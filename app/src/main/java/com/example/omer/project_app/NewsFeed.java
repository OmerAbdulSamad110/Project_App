package com.example.omer.project_app;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class NewsFeed extends Fragment {
    ImageView postImg;
    RelativeLayout postBtn;
    RecyclerView recyclerView;
    TextView textView;
    DatabaseClass mydb;
    PostSt post;
    PostViewAdapter adapter;
    String priv;
    int userId, postId, activeId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_1_newsfeed, container, false);
        mydb = new DatabaseClass(getContext());
        postImg = (ImageView) view.findViewById(R.id.post_img);
        postBtn = (RelativeLayout) view.findViewById(R.id.post_btn);
        recyclerView = (RecyclerView) view.findViewById(R.id.feed_post);
        textView = (TextView) view.findViewById(R.id.nopost_txt);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        populateId();
        return view;
    }

    private void populateId() {
        byte[] image;
        Bitmap bitmap = null;
        Cursor res = mydb.getAllAccountData();
        while (res.moveToNext()) {
            if (res.getInt(1) == 1) {
                activeId = res.getInt(0);
                image = res.getBlob(2);
                bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                break;
            }
        }
        postImg.setImageBitmap(bitmap);

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new PostMaker(), activeId);
            }
        });
        populatePosts(activeId);
    }

    private void populatePosts(int activeId) {
        Cursor postRV = null, friends = mydb.allFriendList(activeId);
        List<PostSt> posts = new ArrayList<>();
        List<PostSt> posts1 = new ArrayList<>();
        int postId, i = 0;
        String prName, pDate, pAvail, pLC;
        byte[] image1, image2;
        Bitmap bp1, bp2;

        while (friends.moveToNext()) {
            if (friends.getInt(2) == activeId) {
                i++;
            }
        }
        if (i == 0) {
            postRV = mydb.newsFeed1(activeId);
        } else if (i > 0) {
            postRV = mydb.newsFeed(activeId);
        }
        while (postRV.moveToNext()) {
            postId = postRV.getInt(0);
            userId = postRV.getInt(1);
            image1 = postRV.getBlob(2);
            prName = postRV.getString(3);
            bp1 = BitmapFactory.decodeByteArray(image1, 0, image1.length);
            pDate = postRV.getString(6);
            pAvail = postRV.getString(7);
            pLC = postRV.getString(8);

            if (postRV.getInt(9) == 1 && postRV.getInt(10) == 1) {
                image2 = postRV.getBlob(5);
                bp2 = BitmapFactory.decodeByteArray(image2, 0, image2.length);
                post = new PostSt(postId, userId, bp1, prName, pDate, postRV.getString(4), bp2, pAvail, 1, 1);
                posts.add(post);
            } else if (postRV.getInt(9) == 1 && postRV.getInt(10) == 0) {
                image2 = postRV.getBlob(5);
                bp2 = BitmapFactory.decodeByteArray(image2, 0, image2.length);
                post = new PostSt(postId, userId, bp1, prName, pDate, "", bp2, pAvail, 1, 0);
                posts.add(post);
            } else if (postRV.getInt(9) == 0 && postRV.getInt(10) == 1) {
                post = new PostSt(postId, userId, bp1, prName, pDate, postRV.getString(4), null, pAvail, pLC, 0, 1);
                posts.add(post);
            }
        }
        int accId;
        String privacy;

        for (int no = 0; no < posts.size(); no++) {
            accId = posts.get(no).accId;
            privacy = posts.get(no).postPrivacy;
            if (privacy.equals("Only me") && accId == activeId) {
                posts1.add(posts.get(no));
            } else if (!privacy.equals("Only me")) {
                posts1.add(posts.get(no));
            }
        }

        if (posts.size() == 0) {
            recyclerView.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.VISIBLE);
        } else if (posts.size() != 0) {
            adapter = new PostViewAdapter(getContext(), posts1);
            recyclerView.setAdapter(adapter);
            postClick(posts1);
        }
    }

    private void postClick(final List<PostSt> posts) {

        adapter.setOnItemClickListener(new PostViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                int postId = posts.get(position).postId;
                int accId = posts.get(position).accId;
                loadPost(new Post(), postId, accId);
            }

            @Override
            public void onImageClick(int position) {
                postId = posts.get(position).postId;
                userId = posts.get(position).accId;
                loadFragment(new UserAccount(), userId);
            }

            @Override
            public void onNameClick(int position) {
                postId = posts.get(position).postId;
                userId = posts.get(position).accId;
                loadFragment(new UserAccount(), userId);
            }

            @Override
            public void onLikeClick(int position) {
                int postId = posts.get(position).postId;
                userId = posts.get(position).accId;
                int postLikes = 0;
                priv = posts.get(position).postPrivacyLC;
                Cursor post = mydb.postOpened(postId, userId);
                if (post.moveToNext()) {
                    postLikes = post.getInt(7);
                }
                if (!priv.equals("Only me")) {
                    postLikes = postLikes + 1;
                    mydb.insertPostLikes(postId, userId, postLikes);
                    mydb.insertLikeP(postId, activeId);
                    adapter.notifyItemChanged(position);
                    Toast.makeText(getActivity(), "Post liked", Toast.LENGTH_SHORT).show();
                } else if (userId == activeId) {
                    postLikes = postLikes + 1;
                    mydb.insertPostLikes(postId, userId, postLikes);
                    mydb.insertLikeP(postId, activeId);
                    adapter.notifyItemChanged(position);
                    Toast.makeText(getActivity(), "Post liked", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onUnLikeClick(int position) {
                int postId = posts.get(position).postId;
                int accId = posts.get(position).accId;
                int postLikes = 0;
                Cursor post = mydb.postOpened(postId, accId);
                if (post.moveToNext()) {
                    postLikes = post.getInt(7);
                }
                postLikes = postLikes - 1;
                mydb.insertPostLikes(postId, accId, postLikes);
                mydb.deleteLikeP(postId, activeId);
                adapter.notifyItemChanged(position);
                Toast.makeText(getActivity(), "Post unliked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCommentClick(int position) {
                int postId = posts.get(position).postId;
                int accId = posts.get(position).accId;
                loadPost(new Post(), postId, accId);
            }
        });
    }

    private void loadFragment(Fragment fragment, int userId) {
        Bundle bundle = new Bundle();
        bundle.putInt("userId", userId);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, fragment).addToBackStack(null).commit();
        fragment.setArguments(bundle);
    }

    private void loadPost(Fragment fragment, int pId, int aId) {
        Bundle bundle = new Bundle();
        bundle.putInt("pid", pId);
        bundle.putInt("aid", aId);
        bundle.putInt("i", 0);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, fragment).addToBackStack(null).commit();
        fragment.setArguments(bundle);
    }

}
