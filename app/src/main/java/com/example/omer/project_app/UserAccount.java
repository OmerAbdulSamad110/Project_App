package com.example.omer.project_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class UserAccount extends Fragment {
    ListView listView;
    LinearLayout linearLayout;
    RecyclerView recyclerView;
    ImageView userMainImage, friendImage, photoImage, postImage;
    Button add_Btn, unAdd_btn, cancel_Btn, accept_Btn, remove_Btn, describeBtn;
    RelativeLayout friends_button, photos_button, post_button;
    TextView userName;
    Toolbar toolbar;
    DatabaseClass mydb;
    PostSt post;
    PostViewAdapter adapter;
    Bitmap bp;

    int i = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        mydb = new DatabaseClass(getContext());

        listView = (ListView) view.findViewById(R.id.acc_list);
        recyclerView = (RecyclerView) view.findViewById(R.id.acc_post);
        userMainImage = (ImageView) view.findViewById(R.id.acc_img);
        friendImage = (ImageView) view.findViewById(R.id.frnd_img);
        photoImage = (ImageView) view.findViewById(R.id.phts_img);
        postImage = (ImageView) view.findViewById(R.id.post_img);
        add_Btn = (Button) view.findViewById(R.id.acc_add);
        unAdd_btn = (Button) view.findViewById(R.id.acc_unAdd);
        cancel_Btn = (Button) view.findViewById(R.id.acc_cancel);
        describeBtn = (Button) view.findViewById(R.id.acc_describe);
        friends_button = (RelativeLayout) view.findViewById(R.id.acc_friend);
        photos_button = (RelativeLayout) view.findViewById(R.id.acc_photo);
        post_button = (RelativeLayout) view.findViewById(R.id.post_btn);
        userName = (TextView) view.findViewById(R.id.acc_name);
        toolbar = (Toolbar) view.findViewById(R.id.acc_toolbar);
        linearLayout = (LinearLayout) view.findViewById(R.id.accept_Btns);
        accept_Btn = (Button) view.findViewById(R.id.acc_accept);
        remove_Btn = (Button) view.findViewById(R.id.acc_remove);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        friendImage.setImageResource(R.drawable.defaultuser);
        toolbar.inflateMenu(R.menu.menu_icon);

        setTitle();
        return view;
    }

    private void setTitle() {
        Cursor res1, res2;
        String title = null;
        int id1, id2 = 0;
        if (getArguments() != null) {
            id1 = getArguments().getInt("userId");
            res1 = mydb.getSpecificAccountData(id1);
            res2 = mydb.getAllAccountData();
            if (res1.moveToNext()) {
                if (res1.getInt(0) == id1) {
                    title = res1.getString(3);
                }
            }
            if (title != null) {
                toolbar.setTitle(title);
            }
            while (res2.moveToNext()) {
                if (res2.getInt(1) == 1) {
                    id2 = res2.getInt(0);
                    break;
                }
            }
            if (id2 != 0) {
                accountCheck(id1, id2);
            }
        }
    }

    private void accountCheck(int id1, int id2) {
        byte[] image;
        Bitmap bitmap;
        Cursor res = mydb.getAllAccountData();
        while (res.moveToNext()) {
            if (res.getInt(1) == 1) {
                image = res.getBlob(2);
                bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                postImage.setImageBitmap(bitmap);
                break;
            }
        }
        if (id1 == id2) {
            populateAccount(id1, 1);
            populatePost(id1, 1);
        } else if (id1 != id2) {
            populateAccount(id1, 0);
            populatePost(id1, 0);
        }
    }

    private void populatePost(int userId, int i) {
        List<PostSt> posts = new ArrayList<>();
        int postId;
        String prName, pDate, pAvail;
        byte[] image1, image2;
        Bitmap bp1, bp2;
        Cursor postRV = mydb.recycleViewPost(userId);
        while (postRV.moveToNext()) {
            postId = postRV.getInt(0);
            image1 = postRV.getBlob(2);
            bp1 = BitmapFactory.decodeByteArray(image1, 0, image1.length);
            prName = postRV.getString(4);
            pDate = postRV.getString(3);
            pAvail = postRV.getString(9);

            if (postRV.getInt(10) == 1 && postRV.getInt(11) == 1) {
                image2 = postRV.getBlob(6);
                bp2 = BitmapFactory.decodeByteArray(image2, 0, image2.length);
                post = new PostSt(postId, userId, bp1, prName, pDate, postRV.getString(5), bp2, pAvail, 1, 1);
                posts.add(post);
            } else if (postRV.getInt(10) == 1 && postRV.getInt(11) == 0) {
                image2 = postRV.getBlob(6);
                bp2 = BitmapFactory.decodeByteArray(image2, 0, image2.length);
                post = new PostSt(postId, userId, bp1, prName, pDate, "", bp2, pAvail, 1, 0);
                posts.add(post);
            } else if (postRV.getInt(10) == 0 && postRV.getInt(11) == 1) {
                post = new PostSt(postId, userId, bp1, prName, pDate, postRV.getString(5), null, pAvail, 0, 1);
                posts.add(post);
            }
        }

        if (posts.size() != 0) {
            if (i == 1) {
                adapter = new PostViewAdapter(getContext(), posts);
                recyclerView.setAdapter(adapter);
            } else if (i == 0) {
                post_button.getLayoutParams().height = 0;
                post_button.getLayoutParams().width = 0;
                post_button.setVisibility(View.INVISIBLE);

                int fr = 0, fId = 0;
                Cursor active = mydb.getAllAccountData();
                while (active.moveToNext()) {
                    if (active.getInt(1) == 1) {
                        fId = active.getInt(0);
                        break;
                    }
                }
                Cursor res = mydb.friendList(fId);
                while (res.moveToNext()) {
                    if (res.getInt(1) == userId) {
                        fr = 1;
                        break;
                    }
                }
                List<PostSt> posts1 = new ArrayList<>();
                if (fr == 1) {
                    for (int no = 0; no < posts.size(); no++) {
                        if (!posts.get(no).postPrivacy.equals("Only me")) {
                            posts1.add(posts.get(no));
                        }
                    }
                } else if (fr == 0) {
                    for (int no = 0; no < posts.size(); no++) {
                        if (posts.get(no).postPrivacy.equals("Public")) {
                            posts1.add(posts.get(no));
                        }
                    }
                }
                adapter = new PostViewAdapter(getContext(), posts1);
                recyclerView.setAdapter(adapter);
            }
            postAdapterClick(posts);
        }
    }

    private void postAdapterClick(final List<PostSt> posts) {
        Cursor res = mydb.getAllAccountData(), privacy = mydb.getSpecificPrivacy(getArguments().getInt("userId"));
        int activeId = 0, fr = 0;
        int userId = getArguments().getInt("userId");
        while (res.moveToNext()) {
            if (res.getInt(1) == 1) {
                activeId = res.getInt(0);
            }
        }
        String priv = null;
        if (privacy.moveToNext()) {
            priv = privacy.getString(2);
        }
        Cursor friend = mydb.friendList(activeId);
        while (friend.moveToNext()) {
            if (friend.getInt(1) == userId) {
                fr = 1;
                break;
            }
        }
        final int finalActiveId = activeId;
        final int finalFr = fr;
        final String finalPriv = priv;
        adapter.setOnItemClickListener(new PostViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                int postId = posts.get(position).postId;
                int accId = posts.get(position).accId;
                loadPost(new Post(), postId, accId);
            }

            @Override
            public void onImageClick(int position) {
                refresh();
            }

            @Override
            public void onNameClick(int position) {
                refresh();
            }

            @Override
            public void onLikeClick(int position) {
                int postId = posts.get(position).postId;
                int accId = posts.get(position).accId;
                int postLikes = 0;
                Cursor post = mydb.postOpened(postId, accId);
                if (post.moveToNext()) {
                    postLikes = post.getInt(7);
                }
                if (finalFr == 1 && !finalPriv.equals("Only me")) {
                    postLikes = postLikes + 1;
                    mydb.insertPostLikes(postId, accId, postLikes);
                    mydb.insertLikeP(postId, finalActiveId);
                    adapter.notifyItemChanged(position);
                    Toast.makeText(getActivity(), "Post liked", Toast.LENGTH_SHORT).show();
                } else if (finalPriv.equals("Public")) {
                    postLikes = postLikes + 1;
                    mydb.insertPostLikes(postId, accId, postLikes);
                    mydb.insertLikeP(postId, finalActiveId);
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
                mydb.deleteLikeP(postId, finalActiveId);
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

    private void populateAccount(final int id, final int i) {
        byte[] image;
        List<String> describeList = new ArrayList<>();
        Bitmap bitmap = null, bitmap1 = null;
        Cursor photo, res, album, privacy;
        List<Integer> aId = new ArrayList<>();
        res = mydb.getSpecificAccountData(id);
        if (res.moveToNext()) {
            image = res.getBlob(2);
            userName.setText(res.getString(3));
            bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            bp = bitmap;
            userMainImage.setImageBitmap(bitmap);

            describeList.add(res.getString(10));
            describeList.add(res.getString(7));
            describeList.add(res.getString(6));
        }

        album = mydb.getAllAlbum(id);
        while (album.moveToNext()) {
            aId.add(album.getInt(0));
        }
        if (!aId.isEmpty()) {
            photo = mydb.getAllPhoto(aId.get(0), id);
            if (photo.moveToNext()) {
                image = photo.getBlob(3);
                bitmap1 = BitmapFactory.decodeByteArray(image, 0, image.length);
            }
            photoImage.setImageBitmap(bitmap);
            if (bitmap1 != null) {
                photoImage.setImageBitmap(bitmap1);
            } else if (bitmap1 == null) {
                photoImage.setImageResource(R.drawable.nophoto);
                photoImage.setPadding(5, 5, 5, 0);
            }
        }
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                menuAction(i);
                return false;
            }
        });
        if (i == 1) {
            add_Btn.getLayoutParams().width = 0;
            unAdd_btn.getLayoutParams().width = 0;
            cancel_Btn.getLayoutParams().width = 0;

            add_Btn.getLayoutParams().height = 0;
            unAdd_btn.getLayoutParams().height = 0;
            cancel_Btn.getLayoutParams().height = 0;

            linearLayout.getLayoutParams().height = 0;
            linearLayout.getLayoutParams().width = 0;

            if (!describeList.isEmpty()) {
                List<String> strings = new ArrayList<>();
                for (int no = 0; no < describeList.size(); no++) {
                    if (!describeList.get(no).equals("")) {
                        strings.add(describeList.get(no));
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, strings);
                listView.setAdapter(adapter);
                describeBtn.setText("More about yourself");
            }
            //Set friend box image
        } else if (i == 0) {
            int activeId = 0, fr = 0, reqGot = 0, reqSend = 0;
            final int userId = id;
            String priv = null;
            privacy = mydb.getSpecificPrivacy(userId);
            if (privacy.moveToNext()) {
                priv = privacy.getString(7);
            }
            Cursor res1 = mydb.getAllAccountData();
            while (res1.moveToNext()) {
                if (res1.getInt(1) == 1) {
                    activeId = res1.getInt(0);
                    break;
                }
            }
            Cursor friend = mydb.friendList(activeId);
            while (friend.moveToNext()) {
                if (friend.getInt(1) == userId) {
                    fr = 1;
                    break;
                }
            }
            Cursor ress = mydb.reqSend(activeId);
            while (ress.moveToNext()) {
                if (ress.getInt(0) == userId) {
                    reqSend = 1;
                    break;
                }
            }
            Cursor resg = mydb.reqData(activeId);
            while (resg.moveToNext()) {
                if (resg.getInt(1) == userId) {
                    reqGot = 1;
                    break;
                }
            }
            if (fr == 1) {
                add_Btn.setVisibility(View.INVISIBLE);
                unAdd_btn.setVisibility(View.VISIBLE);
                cancel_Btn.setVisibility(View.INVISIBLE);
                linearLayout.setVisibility(View.INVISIBLE);
            } else if (reqSend == 1) {
                add_Btn.setVisibility(View.INVISIBLE);
                unAdd_btn.setVisibility(View.INVISIBLE);
                cancel_Btn.setVisibility(View.VISIBLE);
                linearLayout.setVisibility(View.INVISIBLE);
            } else if (reqGot == 1) {
                add_Btn.setVisibility(View.INVISIBLE);
                unAdd_btn.setVisibility(View.INVISIBLE);
                cancel_Btn.setVisibility(View.INVISIBLE);
                linearLayout.setVisibility(View.VISIBLE);
            } else if (priv.equals("Every one")) {
                add_Btn.setVisibility(View.VISIBLE);
                unAdd_btn.setVisibility(View.INVISIBLE);
                cancel_Btn.setVisibility(View.INVISIBLE);
                linearLayout.setVisibility(View.INVISIBLE);
            } else if (priv.equals("No one")) {
                add_Btn.getLayoutParams().width = 0;
                unAdd_btn.getLayoutParams().width = 0;
                cancel_Btn.getLayoutParams().width = 0;

                add_Btn.getLayoutParams().height = 0;
                unAdd_btn.getLayoutParams().height = 0;
                cancel_Btn.getLayoutParams().height = 0;

                linearLayout.getLayoutParams().height = 0;
                linearLayout.getLayoutParams().width = 0;
            }

            final int finalActiveId = activeId;
            add_Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mydb.sendRequest(finalActiveId, userId);
                    refresh();
                    Toast.makeText(getActivity(), "Friend request send", Toast.LENGTH_SHORT).show();
                }
            });
            unAdd_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mydb.removeFriend(finalActiveId, userId);
                    mydb.removeFriend(userId, finalActiveId);
                    refresh();
                    Toast.makeText(getActivity(), "User unfriended", Toast.LENGTH_SHORT).show();
                }
            });
            cancel_Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mydb.reqCancel(finalActiveId, userId);
                    refresh();
                    Toast.makeText(getActivity(), "Request cancel", Toast.LENGTH_SHORT).show();
                }
            });
            accept_Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mydb.insertFriendList(finalActiveId, userId);
                    mydb.insertFriendList(userId, finalActiveId);
                    mydb.reqCancel(userId, finalActiveId);
                    refresh();
                    Toast.makeText(getActivity(), "Request accepted", Toast.LENGTH_SHORT).show();
                }
            });
            remove_Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mydb.reqCancel(userId, finalActiveId);
                    refresh();
                    Toast.makeText(getActivity(), "Request cancel", Toast.LENGTH_SHORT).show();
                }
            });
            String pName = null;
            res = mydb.getSpecificAccountData(id);
            if (res.moveToNext()) {
                pName = res.getString(4);
            }
            describeBtn.setText("More about " + pName);
            if (!describeList.isEmpty()) {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, describeList);
                listView.setAdapter(adapter);
            }

            //Set friend box image
            //Photo box image
        }
        setListViewHeightBasedOnChildren(listView);
        buttonClicks();
    }

    private void buttonClicks() {
        final int userId = getArguments().getInt("userId");
        //On Describe clicked
        describeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new userDescription(), userId);
            }
        });
        //On Photo Album Clicked
        photos_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new photosAlbum(), userId);
            }
        });
        //On Post Maker Clicked
        post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new PostMaker());
            }
        });
        friends_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new FriendListSt(), userId);
            }
        });
    }

    private void menuAction(int i) {
        String[] dialogList = new String[]{};
        if (i == 0) {
            dialogList = new String[]{"Refresh", "View Profile Picture"};
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setItems(dialogList,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int position) {
                            switch (position) {
                                case 0:
                                    refresh();
                                    break;
                                case 1:
                                    viewProfilePic();
                                    break;
                            }
                        }
                    });
            builder.show();
        } else if (i == 1) {
            dialogList = new String[]{"Refresh", "View Profile Picture", "Change Profile Picture"};
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setItems(dialogList,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int position) {
                            switch (position) {
                                case 0:
                                    refresh();
                                    break;
                                case 1:
                                    viewProfilePic();
                                    break;
                                case 2:
                                    loadFragment(new photosAlbum(), getArguments().getInt("userId"));
                                    break;
                            }
                        }
                    });
            builder.show();
        }
    }

    private void viewProfilePic() {
        if (bp != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final LayoutInflater inflater = getActivity().getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.image_big_box, null);
            final ImageView images = (ImageView) dialogView.findViewById(R.id.photo_box_big);
            builder.setView(dialogView);
            final AlertDialog dialog = builder.create();
            images.setImageBitmap(bp);
            dialog.show();
        }
    }

    //Solve list problem in scrollview
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


    public void refresh() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(UserAccount.this).attach(UserAccount.this).commit();
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, fragment).addToBackStack(null).commit();
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
        bundle.putInt("i", 1);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, fragment).addToBackStack(null).commit();
        fragment.setArguments(bundle);
    }
}
