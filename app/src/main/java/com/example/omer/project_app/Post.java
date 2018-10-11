package com.example.omer.project_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Post extends Fragment {
    ImageView personPic, postPic, postMenu, cmntBtn2;
    TextView personName, postTxt, postTime, postAvail, postLiked;
    EditText cmntEdt;
    Button likeBtn, unlikeBtn, cmntBtn1;
    RecyclerView recyclerView;
    RelativeLayout endPart, accountPart;
    CommnetViewAdapter cadapter;
    Context context;
    ScrollView scrollView;
    DatabaseClass mydb;
    int id;
    LinearLayout layout;
    InputMethodManager imm;
    int postId;
    int accId;
    int commentIdG;
    int pos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        personPic = (ImageView) view.findViewById(R.id.p_img);
        postPic = (ImageView) view.findViewById(R.id.post_img);
        postMenu = (ImageView) view.findViewById(R.id.post_op);
        personName = (TextView) view.findViewById(R.id.p_name);
        postTxt = (TextView) view.findViewById(R.id.post_txt);
        postTime = (TextView) view.findViewById(R.id.post_date);
        postAvail = (TextView) view.findViewById(R.id.post_av);
        postLiked = (TextView) view.findViewById(R.id.post_liked);
        likeBtn = (Button) view.findViewById(R.id.btn_like);
        unlikeBtn = (Button) view.findViewById(R.id.btn_unlike);
        cmntBtn1 = (Button) view.findViewById(R.id.btn_cmnt);
        recyclerView = (RecyclerView) view.findViewById(R.id.post_cmnt);
        endPart = (RelativeLayout) view.findViewById(R.id.post_end);
        accountPart = (RelativeLayout) view.findViewById(R.id.p_acc);
        scrollView = (ScrollView) view.findViewById(R.id.post);
        layout = (LinearLayout) view.findViewById(R.id.cmnt_box);
        cmntEdt = (EditText) view.findViewById(R.id.cmnt_edt);
        cmntBtn2 = (ImageView) view.findViewById(R.id.cmnt_btn_img);
        mydb = new DatabaseClass(getContext());
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        //Set recyler view Problem
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, 0);
                scrollView.pageScroll(View.FOCUS_UP);
                scrollView.smoothScrollTo(0, 0);
            }
        });
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        if (getArguments() != null) {
            postId = getArguments().getInt("pid");
            accId = getArguments().getInt("aid");
            insertPost();
        }
        menuClick();
        return view;
    }

    private int accountCheck() {
        int i = 0;
        int aId = 0;
        Cursor res = mydb.getAllAccountData();
        while (res.moveToNext()) {
            if (res.getInt(1) == 1) {
                aId = res.getInt(0);
                i = 1;
                break;
            }
        }
        if (i == 1) {
            return aId;
        } else {
            return 0;
        }
    }

    private void insertPost() {
        byte[] image;
        Bitmap bitmap;
        int postLikes = 0;
        Cursor post = mydb.postOpened(postId, accId);
        if (post.moveToNext()) {
            if (post.getInt(10) == 1 && post.getInt(11) == 1) {
                postTxt.setText(post.getString(5));
                image = post.getBlob(6);
                bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                postPic.setImageBitmap(bitmap);
            } else if (post.getInt(10) == 1 && post.getInt(11) == 0) {
                image = post.getBlob(6);
                bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                postPic.setImageBitmap(bitmap);
                postTxt.setPadding(0, 0, 0, 0);
                postTxt.setTextSize(0);
            } else if (post.getInt(10) == 0 && post.getInt(11) == 1) {
                postTxt.setText(post.getString(5));
                postPic.getLayoutParams().height = 0;
            }
            image = post.getBlob(2);
            bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            personPic.setImageBitmap(bitmap);
            personName.setText(post.getString(4));
            postAvail.setText(post.getString(9));
            postTime.setText(post.getString(3));
            postLikes = post.getInt(7);
        }
        cmntBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cmntEdt.getText().toString().isEmpty()) {
                    insertComments(postId);
                }
            }
        });

        populateComments(postId);
        postClicked(accId, postLikes);
    }

    private void menuClick() {
        postMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] strings = {"Change Audience", "Delete Post", "Edit Post"};
                contextDialog(strings);
            }
        });
    }

    private void postClicked(final int accId, final int postLikes) {
        int like = 0, activeId = 0, fr = 0;
        String priv = null;
        Cursor privacy = mydb.getSpecificPrivacy(accId), res = mydb.getAllAccountData();
        while (res.moveToNext()) {
            if (res.getInt(1) == 1) {
                activeId = res.getInt(0);
                break;
            }
        }
        Cursor liked = mydb.viewActiveLikeP(postId, activeId);
        if (liked.moveToNext()) {
            if (liked.getInt(2) == activeId) {
                like = 1;
            }
        }
        if (like == 1) {
            int ig = postLikes - 1;
            likeBtn.setVisibility(View.INVISIBLE);
            unlikeBtn.setVisibility(View.VISIBLE);
            if (postLikes == 1) {
                postLiked.setText("You liked the post");
            } else if (postLikes > 1) {
                postLiked.setText("You and " + ig + " other");
            }
        } else if (like == 0) {
            likeBtn.setVisibility(View.VISIBLE);
            unlikeBtn.setVisibility(View.INVISIBLE);
            if (postLikes >= 1) {
                postLiked.setText("Liked by " + postLikes);
            }
        }
        Cursor friend = mydb.friendList(activeId);
        while (friend.moveToNext()) {
            if (friend.getInt(1) == accId) {
                fr = 1;
                break;
            }
        }
        if (privacy.moveToNext()) {
            priv = privacy.getString(2);
        }

        final int finalActiveId1 = activeId;
        unlikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mydb.insertPostLikes(postId, accId, postLikes - 1);
                mydb.deleteLikeP(postId, finalActiveId1);
                Toast.makeText(getActivity(), "Post unliked", Toast.LENGTH_SHORT).show();
                insertPost();
            }
        });
        if (accId != activeId) {
            if (fr == 0 && priv == "Public") {
                buttonClicks(activeId, postLikes);
                if (postLikes == 0) {
                    postLiked.setText("Be first to like");
                }
            } else if (fr == 1 && !priv.equals("Only me")) {
                buttonClicks(activeId, postLikes);
                if (postLikes == 0) {
                    postLiked.setText("Be first to like");
                }
            } else {
                cmntEdt.setVisibility(View.INVISIBLE);
                cmntBtn2.setVisibility(View.INVISIBLE);
                if (postLikes == 0) {
                    postLiked.setText("likes are disabled");
                }
            }
            postMenu.setVisibility(View.INVISIBLE);
        } else if (accId == activeId) {
            buttonClicks(activeId, postLikes);
            if (postLikes == 0) {
                postLiked.setText("Be first to like");
            } else if (postLikes <= 1) {
                postLiked.setText("Liked by " + postLikes);
            }
        }
    }

    private void buttonClicks(final int activeId, final int postLikes) {
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mydb.insertPostLikes(postId, accId, postLikes + 1);
                mydb.insertLikeP(postId, activeId);
                Toast.makeText(getActivity(), "Post liked", Toast.LENGTH_SHORT).show();
                insertPost();
            }
        });
        cmntBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmntEdt.setFocusable(true);
            }
        });
    }


    private void insertComments(int postId) {
        Cursor res;
        Date current = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String time = dateFormat.format(current);
        dateFormat = new SimpleDateFormat("MMM dd");
        String day = dateFormat.format(current);
        String commentDate = day + " at " + time;

        if (accountCheck() != 0) {
            id = accountCheck();
            res = mydb.getSpecificAccountData(id);
            if (res.moveToNext()) {
                mydb.insertCommentData(postId, res.getInt(0), res.getBlob(2), res.getString(3), cmntEdt.getText().toString(), commentDate);
            }
            cmntEdt.setText("");
            imm.hideSoftInputFromWindow(layout.getWindowToken(), 0);
            populateComments(postId);
        }
    }

    private void populateComments(int postId) {
        List<Comment> comments = new ArrayList<>();
        int cId, crId, cLikes;
        String crName, cDate, c;
        byte[] image;
        Bitmap bp;
        Cursor cmnt = mydb.viewComment(postId);
        while (cmnt.moveToNext()) {
            cId = cmnt.getInt(0);
            crId = cmnt.getInt(2);
            image = cmnt.getBlob(3);
            bp = BitmapFactory.decodeByteArray(image, 0, image.length);
            crName = cmnt.getString(4);
            c = cmnt.getString(5);
            cDate = cmnt.getString(6);
            cLikes = cmnt.getInt(7);
            Comment comment = new Comment(cId, postId, crId, bp, crName, c, cDate, cLikes);
            comments.add(comment);
        }

        if (comments.size() != 0) {
            cadapter = new CommnetViewAdapter(getContext(), comments);
            recyclerView.setAdapter(cadapter);
            commentClicked(comments);
        }
    }

    private void commentClicked(final List<Comment> comments) {
        int activeId = 0;
        Cursor res = mydb.getAllAccountData();
        while (res.moveToNext()) {
            if (res.getInt(1) == 1) {
                activeId = res.getInt(0);
                break;
            }
        }
        final int finalActiveId = activeId;
        cadapter.setOnItemClickListener(new CommnetViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                commentIdG = comments.get(position).commentId;
                pos = position;
                if (comments.get(position).commenterId == finalActiveId) {
                    String[] strings = {"Edit comment", "Delete comment"};
                    contextDialog(strings);
                } else if (accId == finalActiveId && comments.get(position).commenterId != finalActiveId) {
                    String[] strings = {"Delete comment"};
                    contextDialog(strings);
                }
            }

            @Override
            public void onImageClick(int position) {
                int userId = comments.get(position).commenterId;
                if (userId == accId && getArguments().getInt("i") == 1) {
                    sameFragment();
                } else if (getArguments().getInt("i") == 0) {
                    loadFragment(new UserAccount(), userId);
                } else {
                    loadFragment(new UserAccount(), userId);
                }
            }

            @Override
            public void onNameClick(int position) {
                int userId = comments.get(position).commenterId;
                if (userId == accId && getArguments().getInt("i") == 1) {
                    sameFragment();
                } else if (getArguments().getInt("i") == 0) {
                    loadFragment(new UserAccount(), userId);
                } else {
                    loadFragment(new UserAccount(), userId);
                }
            }

            @Override
            public void onLikeClick(int position) {
                int commentL = 0;
                int commentId = comments.get(position).commentId;
                Cursor comment = mydb.viewSpecificC(postId, commentId);
                if (comment.moveToNext()) {
                    commentL = comment.getInt(7);
                }
                commentL = commentL + 1;
                mydb.insertCommentLikes(postId, commentId, commentL);
                mydb.insertLikeC(postId, commentId, finalActiveId);
                cadapter.notifyItemChanged(position);
            }

            @Override
            public void onUnlikeClick(int position) {
                int commentL = 0;
                int commentId = comments.get(position).commentId;
                Cursor comment = mydb.viewSpecificC(postId, commentId);
                if (comment.moveToNext()) {
                    commentL = comment.getInt(7);
                }
                commentL = commentL - 1;
                mydb.insertCommentLikes(postId, commentId, commentL);
                mydb.deleteLikeC(postId, finalActiveId, commentId);
                cadapter.notifyItemChanged(position);
            }
        });
    }

    private void contextDialog(final String[] dialogList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(dialogList,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int position) {
                        switch (dialogList[position]) {
                            case "Change Audience":
                                actionDialog("pA");
                                break;
                            case "Delete Post":
                                actionDialog("pD");
                                break;
                            case "Edit Post":
                                actionDialog("pE");
                                break;
                            case "Edit comment":
                                actionDialog("cE");
                                break;
                            case "Delete comment":
                                actionDialog("cD");
                                break;
                        }
                    }
                });
        builder.show();
    }

    private void actionDialog(String string) {
        String post = null, comment = null, privacy = null;
        int commenterId = 0, activeId = 0;

        Cursor pst = mydb.postOpened(postId, accId), cmnt = mydb.viewSpecificC(postId, commentIdG), res = mydb.getAllAccountData();
        if (pst.moveToNext()) {
            post = pst.getString(5);
            privacy = pst.getString(9);
        }
        if (cmnt.moveToNext()) {
            commenterId = cmnt.getInt(2);
            comment = cmnt.getString(5);
        }
        while (res.moveToNext()) {
            if (res.getInt(1) == 1) {
                activeId = res.getInt(0);
                break;
            }
        }
        final int finalCommenterId = commenterId;
        final int finalActiveId = activeId;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.setting_dialog, null);

        TextView textView = (TextView) dialogView.findViewById(R.id.setting_text);
        TextView textView1 = (TextView) dialogView.findViewById(R.id.text1);
        LinearLayout linearLayout1 = (LinearLayout) dialogView.findViewById(R.id.big_box);
        LinearLayout linearLayout3 = (LinearLayout) dialogView.findViewById(R.id.spinner_box);
        LinearLayout linearLayout4 = (LinearLayout) dialogView.findViewById(R.id.text_box);
        final EditText editText = (EditText) dialogView.findViewById(R.id.big_edit);
        Button button1 = (Button) dialogView.findViewById(R.id.setting_btn1);
        Button button2 = (Button) dialogView.findViewById(R.id.setting_btn2);
        final Spinner spinner = (Spinner) dialogView.findViewById(R.id.pri_spinner);
        RelativeLayout relativeLayout = (RelativeLayout) dialogView.findViewById(R.id.setting_box);
        ViewGroup.LayoutParams params = relativeLayout.getLayoutParams();

        List<String> listItems1 = new ArrayList<>();
        listItems1.add("Public");
        listItems1.add("Friends");
        listItems1.add("No one");
        final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, listItems1);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        if (string == "pA") {
            textView.setText("Privacy");
            linearLayout3.setVisibility(View.VISIBLE);
            spinner.setAdapter(adapter1);
            spinner.setSelection(adapter1.getPosition(privacy));
            button1.setText("Change");
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mydb.updatePostPrivacy(postId, accId, adapter1.getItem(spinner.getSelectedItemPosition()));
                    dialog.dismiss();
                    Toast.makeText(getActivity(), "Privacy updated", Toast.LENGTH_SHORT);
                    insertPost();
                }
            });
            dialog.show();
        } else if (string == "pD") {
            textView.setText("Delete Post?");
            linearLayout4.setVisibility(View.VISIBLE);
            textView1.setText("Are you sure you want to delete this post?");
            button1.setText("Confirm");
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mydb.deletePost(postId, accId);
                    mydb.deleteAllComments(postId);
                    mydb.deleteAllLikeC(postId);
                    mydb.deleteAllLikeP(postId);
                    dialog.dismiss();
                    sameFragment();
                    Toast.makeText(getActivity(), "Post deleted", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.show();
        } else if (string == "pE") {
            params.height = 450;
            linearLayout1.setVisibility(View.VISIBLE);
            textView.setText("Edit Post?");
            editText.setText(post);
            button1.setText("Edit");
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!editText.getText().toString().equals("")) {
                        mydb.editPosts(postId, accId, editText.getText().toString());
                        dialog.dismiss();
                        Toast.makeText(getActivity(), "Post edited", Toast.LENGTH_SHORT).show();
                        insertPost();
                    }
                }
            });
            dialog.show();
        } else if (string == "cE") {
            params.height = 450;
            textView.setText("Edit Comment?");
            linearLayout1.setVisibility(View.VISIBLE);
            editText.setText(comment);

            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!editText.getText().toString().equals("")) {
                        if (finalCommenterId == finalActiveId) {
                            mydb.editComments(postId, commentIdG, editText.getText().toString());
                            Toast.makeText(getActivity(), "Comment edited", Toast.LENGTH_SHORT);
                            dialog.dismiss();
                            insertPost();
                        }
                    }
                }
            });
            dialog.show();
        } else if (string == "cD") {
            textView.setText("Delete Comment?");
            linearLayout4.setVisibility(View.VISIBLE);
            textView1.setText("Are you sure you want to delete this comment?");
            button1.setText("Confirm");
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (finalCommenterId == finalActiveId || accId == finalActiveId) {
                        mydb.deleteLikeC(postId, finalCommenterId, commentIdG);
                        mydb.deleteComment(postId, commentIdG);
                        Toast.makeText(getActivity(), "Comment deleted", Toast.LENGTH_SHORT);
                        dialog.dismiss();
                        refresh();
                    }
                }
            });
            dialog.show();
        }
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void loadFragment(Fragment fragment, int id) {

        Bundle bundle = new Bundle();
        bundle.putInt("userId", id);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, fragment).addToBackStack(null).commit();
        fragment.setArguments(bundle);
    }

    private void sameFragment() {
        FragmentManager manager = getFragmentManager();
        manager.popBackStack();
    }

    public void refresh() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(Post.this).attach(Post.this).commit();
    }
}
