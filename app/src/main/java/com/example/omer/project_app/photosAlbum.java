package com.example.omer.project_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class photosAlbum extends Fragment {
    Button createAlbum;
    ListView albumList;
    TextView textView;
    List<String> items = new ArrayList<>();
    List<Bitmap> images = new ArrayList<>();
    SimpleViewAdapterLV lv;
    DatabaseClass mydb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photos_album, container, false);
        mydb = new DatabaseClass(getContext());
        createAlbum = (Button) view.findViewById(R.id.album_add);
        albumList = (ListView) view.findViewById(R.id.album_list);
        textView = (TextView) view.findViewById(R.id.no_photo);
        if (getArguments() != null) {
            accountCheck();
        }
        return view;
    }

    private void accountCheck() {
        int id = 0;
        int accId = getArguments().getInt("userId");
        Cursor res = mydb.getAllAccountData();
        while (res.moveToNext()) {
            if (res.getInt(1) == 1) {
                id = res.getInt(0);
                break;
            }
        }
        if (id == accId) {
            populateList(accId, 1);
        } else if (id != accId) {
            createAlbum.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.VISIBLE);
            textView.setText("Photo Album");
            populateList(accId, 0);
        }
    }

    private void populateList(final int accId, int i) {
        final List<Album> albums = new ArrayList<>();
        byte[] image;
        Bitmap bitmap;
        Cursor alb = mydb.getAllAlbum(accId);
        int fId = 0, fr = 0;
        Cursor active = mydb.getAllAccountData();
        while (active.moveToNext()) {
            if (active.getInt(1) == 1) {
                fId = active.getInt(0);
                break;
            }
        }
        Cursor res = mydb.friendList(fId);
        while (res.moveToNext()) {
            if (res.getInt(1) == accId) {
                fr = 1;
                break;
            }
        }
        while (alb.moveToNext()) {
            image = alb.getBlob(4);
            bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            Album album = new Album(alb.getInt(0), accId, alb.getString(2), alb.getString(3), bitmap);
            albums.add(album);
        }
        if (images.isEmpty()) {
            if (i == 1) {
                for (int no = 0; no < albums.size(); no++) {
                    images.add(albums.get(no).thumbnail);
                    items.add(albums.get(no).albumName);
                }
            }
            if (i == 0) {
                createAlbum.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.VISIBLE);
                textView.setText("Photo Album");

                if (fr == 1) {
                    for (int no = 0; no < albums.size(); no++) {
                        if (!albums.get(no).albumPrivacy.equals("Only me")) {
                            images.add(albums.get(no).thumbnail);
                            items.add(albums.get(no).albumName);
                        }
                    }
                } else if (fr == 0) {
                    for (int no = 0; no < albums.size(); no++) {
                        if (albums.get(no).albumPrivacy.equals("Public")) {
                            images.add(albums.get(no).thumbnail);
                            items.add(albums.get(no).albumName);
                        }
                    }
                }
            }
        }
        lv = new SimpleViewAdapterLV(getActivity(), items, images);
        albumList.setAdapter(lv);

        if (!lv.isEmpty()) {
            albumList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    loadFragment(new Photos(), albums.get(position).albumId, albums.get(position).accId);
                }
            });
        }
        createAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClick(accId);
            }
        });
    }

    private void buttonClick(final int accId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.simple_dialogs, null);

        TextView textView = (TextView) dialogView.findViewById(R.id.dialog_text);
        LinearLayout linearLayout1 = (LinearLayout) dialogView.findViewById(R.id.one_box);
        LinearLayout linearLayout3 = (LinearLayout) dialogView.findViewById(R.id.spinner_box);
        final EditText editText = (EditText) dialogView.findViewById(R.id.dialog_edit1);
        Button button1 = (Button) dialogView.findViewById(R.id.dialog_btn1);
        Button button2 = (Button) dialogView.findViewById(R.id.dialog_btn2);
        final Spinner spinner = (Spinner) dialogView.findViewById(R.id.dialog_spinner);
        RelativeLayout relativeLayout = (RelativeLayout) dialogView.findViewById(R.id.dialog_box);
        ViewGroup.LayoutParams param1 = relativeLayout.getLayoutParams();
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) spinner.getLayoutParams();

        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        textView.setText("Create Photo Album");
        button1.setText("Create");
        button1.setVisibility(View.VISIBLE);
        button2.setText("Cancel");
        button2.setVisibility(View.VISIBLE);
        linearLayout1.setVisibility(View.VISIBLE);
        linearLayout3.setVisibility(View.VISIBLE);
        editText.setVisibility(View.VISIBLE);
        editText.setHint("Enter album name");
        List<String> choice = new ArrayList<>();
        param1.height = 450;
        layoutParams.topMargin = 120;
        choice.add("Public");
        choice.add("Friends");
        choice.add("Only me");
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, choice);
        spinner.setVisibility(View.VISIBLE);
        spinner.setAdapter(adapter);
        dialog.show();

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().isEmpty()) {
                    editText.setError("Enter album name");
                } else if (editText.getText().toString().length() < 2) {
                    editText.setError("Album name must be minimum 3 letters");
                } else {
                    int chk = 0;
                    for (int i = 0; i < lv.getCount(); i++) {
                        if (editText.getText().toString().equals(lv.getItem(i))) {
                            editText.setError("Name already exists");
                            chk = 1;
                            break;
                        }
                    }
                    if (chk == 0) {
                        Bitmap image;
                        items.add(editText.getText().toString());
                        images.add(BitmapFactory.decodeResource(getResources(), R.drawable.ic_addphoto));
                        image=images.get(0);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        try {
                            stream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mydb.insertAlbumData(accId, editText.getText().toString(), adapter.getItem(spinner.getSelectedItemPosition()), byteArray);
                        Toast.makeText(getActivity(), "Album created", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        refresh();
                    }
                }
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void refresh() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.detach(photosAlbum.this).attach(photosAlbum.this).commit();
    }

    private void loadFragment(Fragment fragment, int albumId, int accId) {
        Bundle bundle = new Bundle();
        bundle.putInt("albumId", albumId);
        bundle.putInt("accId", accId);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, fragment).addToBackStack(null).commit();
        fragment.setArguments(bundle);
    }
}
