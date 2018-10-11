package com.example.omer.project_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PostMaker extends Fragment {
    ImageView accountPic, postPic;
    TextView accountName, shareWith, addPic, removePic;
    EditText postTxt;
    LinearLayout linearLayout;
    Button share;
    DatabaseClass mydb;
    byte[] image, getImage;
    Bitmap bitmap;
    String name;
    int id;
    int hasImage = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pmaker, container, false);
        mydb = new DatabaseClass(getContext());

        accountPic = (ImageView) view.findViewById(R.id.pr_img);
        postPic = (ImageView) view.findViewById(R.id.post_img);

        accountName = (TextView) view.findViewById(R.id.pr_name);
        shareWith = (TextView) view.findViewById(R.id.share_with);
        addPic = (TextView) view.findViewById(R.id.add_img);
        removePic = (TextView) view.findViewById(R.id.remove_img);
        postTxt = (EditText) view.findViewById(R.id.post_box);
        linearLayout = (LinearLayout) view.findViewById(R.id.share_box);
        share = (Button) view.findViewById(R.id.post_btn);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        accountCheck();
        return view;
    }

    private void accountCheck() {
        int i = 0;
        Cursor res = mydb.getAllAccountData();
        while (res.moveToNext()) {
            if (res.getInt(1) == 1) {
                id = res.getInt(0);
                image = res.getBlob(2);
                name = res.getString(3);
                bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                i = 1;
                break;
            }
        }
        if (i == 1) {
            accountPic.setImageBitmap(bitmap);
            accountName.setText(name);
            Cursor privacy = mydb.getSpecificPrivacy(id);
            if (privacy.moveToNext()) {
                shareWith.setText(privacy.getString(1));
            }
            buttonClicks(id, image, name);
        }
    }

    private void buttonClicks(final int id, final byte[] image, final String name) {
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] dialogList = {"Public", "Friends", "Only me"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select who can see your post");
                builder.setItems(dialogList,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int position) {
                                switch (dialogList[position]) {
                                    case "Public":
                                        shareWith.setText("Public");
                                        break;
                                    case "Friends":
                                        shareWith.setText("Friends");
                                        break;
                                    case "Only me":
                                        shareWith.setText("Only me");
                                        break;
                                }
                            }
                        });
                builder.show();
            }
        });
        postPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Bitmap bitmap = ((BitmapDrawable) postPic.getDrawable()).getBitmap();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final LayoutInflater inflater = getActivity().getLayoutInflater();
                View dailiogView = inflater.inflate(R.layout.image_big_box, null);
                final ImageView imageView = (ImageView) dailiogView.findViewById(R.id.photo_box_big);
                builder.setView(dailiogView);
                final AlertDialog dialog = builder.create();
                imageView.setImageBitmap(bitmap);
                dialog.show();
            }
        });
        addPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pictureGallery();
            }
        });
        removePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.nophoto);
                postPic.setImageBitmap(bitmap);
                addPic.setText("Add Image");
                removePic.setVisibility(View.INVISIBLE);
                hasImage = 0;
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date current = Calendar.getInstance().getTime();
                DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                String time = dateFormat.format(current);
                dateFormat = new SimpleDateFormat("MMM dd");
                String day = dateFormat.format(current);
                String postDate = day + " at " + time;

                if (postTxt.getText().toString().equals("") && hasImage == 0) {
                    Toast.makeText(getActivity(), "Please enter text or upload image to create post", Toast.LENGTH_SHORT).show();
                } else if (!postTxt.getText().toString().equals("") || hasImage == 1) {

                    if (!postTxt.getText().toString().equals("") && hasImage == 1) {
                        mydb.insertPostData(id, image, name, postDate, postTxt.getText().toString(), getImage, shareWith.getText().toString(), 1, 1);
                    } else if (postTxt.getText().toString().equals("") && hasImage == 1) {
                        mydb.insertPostData(id, image, name, postDate, postTxt.getText().toString(), getImage, shareWith.getText().toString(), 1, 0);
                    } else if (!postTxt.getText().toString().equals("") && hasImage == 0) {
                        mydb.insertPostData(id, image, name, postDate, postTxt.getText().toString(), getImage, shareWith.getText().toString(), 0, 1);
                    }
                    loadFragment();
                }
            }
        });

    }

    private void pictureGallery() {
        final String[] items = {"Camera", "Gallery", "Cancel"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (items[which].equals("Camera")) {
                    Intent cintent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cintent, 1);
                } else if (items[which].equals("Gallery")) {
                    Intent gintent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    gintent.setType("image/*");
                    startActivityForResult(Intent.createChooser(gintent, "Select source"), 2);
                } else if (items[which].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == 1) {
                Bundle bundle = data.getExtras();
                final Bitmap bitmap = (Bitmap) bundle.get("data");
                postPic.setImageBitmap(bitmap);
                addPic.setText("Add Different Image");
                removePic.setVisibility(View.VISIBLE);
                hasImage = 1;
                getImage = convertImage(bitmap);
            } else if (requestCode == 2) {
                Uri select = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), select);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                postPic.setImageBitmap(bitmap);
                addPic.setText("Add Different Image");
                removePic.setVisibility(View.VISIBLE);
                hasImage = 1;
                getImage = convertImage(bitmap);
            }
        }
    }

    private byte[] convertImage(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    private void loadFragment() {
        FragmentManager manager = getFragmentManager();
        manager.popBackStack();
    }
}
