package com.example.omer.project_app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Photos extends Fragment {
    Toolbar toolbar;
    GridView gridView;
    GridAdapter adapter;
    DatabaseClass mydb;
    int i = 0;
    byte[] imageBA;
    Bitmap bitmap1;
    TextView heading, share_with, sw, textViewD;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        mydb = new DatabaseClass(getContext());
        toolbar = (Toolbar) view.findViewById(R.id.photo_toolbar);
        gridView = (GridView) view.findViewById(R.id.photoGrid);
        heading = (TextView) view.findViewById(R.id.heading);
        share_with = (TextView) view.findViewById(R.id.share_phts);
        sw = (TextView) view.findViewById(R.id.sw);
        textViewD = (TextView) view.findViewById(R.id.noPhts);
        toolbar = (Toolbar) view.findViewById(R.id.photo_toolbar);
        accountCheck();
        return view;
    }

    private void accountCheck() {
        List<Photo> photos = new ArrayList<>();
        int accId = 0, aid = 0, albumId;
        Cursor res = mydb.getAllAccountData();
        Cursor alb;
        if (getArguments() != null) {
            while (res.moveToNext()) {
                if (res.getInt(1) == 1) {
                    aid = res.getInt(0);
                    break;
                }
            }
            albumId = getArguments().getInt("albumId");
            accId = getArguments().getInt("accId");
            alb = mydb.getAllPhoto(albumId, accId);
            if (accId == aid && photos.isEmpty()) {
                Bitmap camera = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_camera);
                Photo photo = new Photo(0, albumId, camera);
                photos.add(photo);
            }
            byte[] image;
            Bitmap bitmap;
            while (alb.moveToNext()) {
                image = alb.getBlob(3);
                bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                Photo photo = new Photo(alb.getInt(0), albumId, bitmap);
                photos.add(photo);
            }
            populateG(photos);
            setLayout(albumId, accId, aid, photos);
        }
    }

    private void setLayout(int albumId, int accId, int aid, List<Photo> photos) {
        String title = null;
        int count = photos.size();
        Cursor res1 = mydb.getSpecificAlbum(albumId, accId);
        if (res1.moveToNext()) {
            title = res1.getString(2);
            share_with.setText(res1.getString(3));
        }
        toolbar.setTitle(title);
        if (accId == aid) {
            Cursor res2 = mydb.getSpecificAlbum(albumId, accId);
            if (res2.moveToNext()) {
                share_with.setText(res2.getString(3));
            }
            if (i == 0) {
                toolbar.inflateMenu(R.menu.menu_icon);
                toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        actionDialog();
                        return false;
                    }
                });
                i = 1;
            }
            count = count - 1;
        } else if (accId != aid) {
            sw.setVisibility(View.INVISIBLE);
            share_with.setVisibility(View.INVISIBLE);
        }
        if (count > 1) {
            heading.setText("Photos of " + title + "\n" + count + " photos");
        } else if (count == 1) {
            heading.setText("Photos of " + title + "\n" + count + " photo");
        } else {
            heading.setText("Photos of " + title);
        }
    }

    private void populateG(final List<Photo> photos) {
        if (photos.isEmpty()) {
            textViewD.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.INVISIBLE);
        }
        GridAdapter adapter = new GridAdapter(getContext(), photos);
        gridView.setAdapter(adapter);
        if (adapter != null) {
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int imgId = photos.get(position).imageId;
                    int accId = photos.get(position).accId;
                    Bitmap image = photos.get(position).image;
                    oneClick(0, imgId, image);
                }
            });

            gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    int imgId = photos.get(position).imageId;
                    int accId = photos.get(position).accId;
                    Bitmap image = photos.get(position).image;
                    oneClick(1, imgId, image);
                    return true;
                }
            });
        }
    }

    private void oneClick(int i, final int imgId, final Bitmap image) {
        if (i == 0) {
            if (imgId == 0) {
                pictureGallery();
            }
            else if (imgId != 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.image_big_box, null);
                final ImageView images = (ImageView) dialogView.findViewById(R.id.photo_box_big);
                builder.setView(dialogView);
                final AlertDialog dialog = builder.create();
                images.setImageBitmap(image);
                dialog.show();
            }
        } else if (i == 1) {
            final String[] dialogList = {"Make Profile Image", "Delete Image"};
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setItems(dialogList,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int position) {
                            switch (dialogList[position]) {
                                case "Make Profile Image":
                                    photoAction(imgId, "mp", image);
                                    break;
                                case "Delete Image":
                                    photoAction(imgId, "di", image);
                                    break;
                            }
                        }
                    });
            builder.show();
        }
    }

    private void photoAction(final int imgId, final String string, final Bitmap bp) {
        final Cursor res = mydb.getAllAccountData();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        final byte[] byteArray = stream.toByteArray();
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final int albumId = getArguments().getInt("albumId");
        final int accId = getArguments().getInt("accId");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.setting_dialog, null);

        final TextView textView = (TextView) dialogView.findViewById(R.id.setting_text);
        final TextView textView1 = (TextView) dialogView.findViewById(R.id.text1);
        final LinearLayout linearLayout4 = (LinearLayout) dialogView.findViewById(R.id.text_box);
        final Button button1 = (Button) dialogView.findViewById(R.id.setting_btn1);
        Button button2 = (Button) dialogView.findViewById(R.id.setting_btn2);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        if (string == "mp") {
            textView.setText("Change Profile Image");
            textView1.setText("Are you sure?");
            linearLayout4.setVisibility(View.VISIBLE);
            button1.setText("Confirm");
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mydb.updateAccountDP(accId, byteArray);
                    mydb.updatePostTN(accId, byteArray);
                    mydb.updateCommentTN(accId, byteArray);

                    dialog.dismiss();
                    Toast.makeText(getActivity(), "Profile image updated", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (string == "di") {
            textView.setText("Delete Image");
            textView1.setText("Are you sure you want to delete this image?");
            linearLayout4.setVisibility(View.VISIBLE);
            button1.setText("Confirm");
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    while (res.moveToNext()) {
                        if (res.getInt(1) == 1) {
                            imageBA = res.getBlob(2);
                            bitmap1 = BitmapFactory.decodeByteArray(imageBA, 0, imageBA.length);
                            break;
                        }
                    }
                    if (bitmap1.sameAs(bp)) {
                        mydb.deletePhoto(albumId, accId, imgId);
                        Bitmap bp = BitmapFactory.decodeResource(getResources(), R.drawable.defaultuser);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        final byte[] byteArray = stream.toByteArray();
                        try {
                            stream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mydb.updateAccountDP(accId, byteArray);
                        mydb.updatePostTN(accId, byteArray);
                        mydb.updateCommentTN(accId, byteArray);
                    }
                    else {
                        mydb.deletePhoto(albumId, accId, imgId);
                    }
                    dialog.dismiss();
                    accountCheck();
                    Toast.makeText(getActivity(), "Image deleted", Toast.LENGTH_SHORT).show();
                }
            });
        }
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
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
        Bitmap bitmap = null;
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == 1) {
                Bundle bundle = data.getExtras();
                bitmap = (Bitmap) bundle.get("data");
            } else if (requestCode == 2) {
                Uri select = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), select);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            int albumId = getArguments().getInt("albumId");
            int accId = getArguments().getInt("accId");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mydb.insertPhotoData(accId, albumId, byteArray);
            mydb.updateAlbumTN(albumId, accId, byteArray);
            Toast.makeText(getActivity(), "Image added to the album", Toast.LENGTH_SHORT);
            accountCheck();
        }
    }

    private void actionDialog() {
        final int albumId = getArguments().getInt("albumId");
        final int accId = getArguments().getInt("accId");
        String albumN = null;
        Cursor al = mydb.getSpecificAlbum(albumId, accId);
        if (al.moveToNext()) {
            albumN = al.getString(2);
        }
        if (!albumN.equals("Profile Pictures")) {
            final String[] dialogList = {"Change Audience", "Delete Album", "Refresh"};
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setItems(dialogList,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int position) {
                            switch (dialogList[position]) {
                                case "Change Audience":
                                    functionDialog("ca");
                                    break;
                                case "Delete Album":
                                    functionDialog("da");
                                    break;
                                case "Refresh":
                                    if (adapter != null) {
                                        adapter.notifyDataSetChanged();
                                    }
                                    break;
                            }
                        }
                    });
            builder.show();
        }
        else if (albumN.equals("Profile Pictures")) {
            final String[] dialogList = {"Change Audience", "Refresh"};
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setItems(dialogList,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int position) {
                            switch (dialogList[position]) {
                                case "Change Audience":
                                    functionDialog("ca");
                                    break;
                                case "Refresh":
                                    if (adapter != null) {
                                        adapter.notifyDataSetChanged();
                                    }
                                    break;
                            }
                        }
                    });
            builder.show();
        }
    }

    private void functionDialog(String string) {
        final int albumId = getArguments().getInt("albumId");
        final int accId = getArguments().getInt("accId");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.setting_dialog, null);

        TextView textView = (TextView) dialogView.findViewById(R.id.setting_text);
        TextView textView1 = (TextView) dialogView.findViewById(R.id.text1);
        LinearLayout linearLayout3 = (LinearLayout) dialogView.findViewById(R.id.spinner_box);
        LinearLayout linearLayout4 = (LinearLayout) dialogView.findViewById(R.id.text_box);
        Button button1 = (Button) dialogView.findViewById(R.id.setting_btn1);
        Button button2 = (Button) dialogView.findViewById(R.id.setting_btn2);
        final Spinner spinner = (Spinner) dialogView.findViewById(R.id.pri_spinner);

        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        List<String> listItems1 = new ArrayList<>();
        listItems1.add("Public");
        listItems1.add("Friends");
        listItems1.add("No one");
        final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, listItems1);
        if (string == "ca") {
            spinner.setAdapter(adapter1);
            textView.setText("Change Audience");
            linearLayout3.setVisibility(View.VISIBLE);
            button1.setText("Change");
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mydb.updateAlbumPrivacy(albumId, accId, adapter1.getItem(spinner.getSelectedItemPosition()));
                    share_with.setText(adapter1.getItem(spinner.getSelectedItemPosition()));
                    Toast.makeText(getActivity(), "Album privacy is changed", Toast.LENGTH_SHORT);
                    dialog.dismiss();
                }
            });
        } else if (string == "da") {
            textView.setText("Delete Album");
            textView1.setText("Are you sure you want to delete this album?");
            linearLayout4.setVisibility(View.VISIBLE);
            button1.setText("Confirm");
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mydb.deleteAlbum(albumId, accId);
                    mydb.deleteAllPhotos(albumId, accId);
                    dialog.dismiss();
                    //Check here
                    dismiss();
                }
            });
        }
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void dismiss() {
        FragmentManager manager = getFragmentManager();
        manager.popBackStack();
    }
}

class GridAdapter extends ArrayAdapter<Photo> {
    private final Context context;
    private final List<Photo> photos;

    public GridAdapter(Context context, List<Photo> photos) {
        super(context, R.layout.user_photo_view, photos);
        this.context = context;
        this.photos = photos;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.user_photo_view, null, true);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.photo_box);
        imageView.setImageBitmap(photos.get(position).image);
        return rowView;
    }
}