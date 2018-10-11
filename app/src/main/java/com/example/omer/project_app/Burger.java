package com.example.omer.project_app;

import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Burger extends Fragment {
    RelativeLayout rl;
    ListView list;
    ImageView imageView;
    TextView textView;
    DatabaseClass mydb;
    Cursor res;
    String[] items = {"Photos", "Friends", "Search", "News Feed", "Friend Requests", "Settings", "Help", "Log Out"};
    Integer[] logo = {R.mipmap.ic_photos, R.mipmap.ic_friends, R.mipmap.ic_search,
            R.mipmap.ic_newsfeed, R.mipmap.ic_friendreq,
            R.mipmap.ic_setting, R.mipmap.ic_about, R.mipmap.ic_logout};
    Bitmap bitmap;
    SimpleViewAdapterLV adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_6_burger, container, false);
        mydb = new DatabaseClass(getActivity());
        List<String> itemlist = new ArrayList<>();
        List<Bitmap> logolist = new ArrayList<>();
        for (int i = 0; i < items.length; i++) {
            itemlist.add(items[i]);
            bitmap = BitmapFactory.decodeResource(getResources(), logo[i]);
            logolist.add(bitmap);
        }
        rl = (RelativeLayout) view.findViewById(R.id.burger_acc);
        list = (ListView) view.findViewById(R.id.burger_list);
        imageView = (ImageView) view.findViewById(R.id.burger_img);
        textView = (TextView) view.findViewById(R.id.burger_txt);
        adapter = new SimpleViewAdapterLV(getActivity(), itemlist, logolist);
        list.setAdapter(adapter);
        accountCheck();
        return view;
    }

    private void accountCheck() {
        String name = null;
        int id = 0;
        Bitmap bitmap = null;
        byte[] image;
        res = mydb.getAllAccountData();
        while (res.moveToNext()) {
            if (res.getInt(1) == 1) {
                id = res.getInt(0);
                image = res.getBlob(2);
                name = res.getString(3);
                bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                break;
            }
        }
        final int finalId = id;
        if (bitmap != null && name != null) {
            imageView.setImageBitmap(bitmap);
            textView.setText(name);
            rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadFragment(new UserAccount(), finalId);
                }
            });
        }
        BurgerClick(finalId);
    }

    private void BurgerClick(final int Id) {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        loadFragment(new photosAlbum(), Id);
                        break;
                    case 1:
                        loadFragment(new FriendListSt(), Id);
                        break;
                    case 2:
                        loadTab(new FrontTabs(), 2);
                        break;
                    case 3:
                        loadTab(new FrontTabs(), 0);
                        break;
                    case 4:
                        loadTab(new FrontTabs(), 1);
                        break;
                    case 5:
                        loadFragment(new Setting());
                        break;
                    case 6:
                        break;
                    case 7:
                        logOut();
                        break;
                }
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

    private void loadFragment(Fragment fragment) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, fragment).addToBackStack(null).commit();
    }

    private void loadTab(Fragment fragment, int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, fragment).addToBackStack(null).commit();
        fragment.setArguments(bundle);
    }

    private void logOut() {
        String email = null;
        int id = 0;
        Cursor res = mydb.getAllAccountData();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View dailiogView = inflater.inflate(R.layout.setting_dialog, null);
        TextView textView = (TextView) dailiogView.findViewById(R.id.setting_text);
        TextView textView1 = (TextView) dailiogView.findViewById(R.id.text1);
        LinearLayout linearLayout4 = (LinearLayout) dailiogView.findViewById(R.id.text_box);
        Button button1 = (Button) dailiogView.findViewById(R.id.setting_btn1);
        Button button2 = (Button) dailiogView.findViewById(R.id.setting_btn2);

        builder.setView(dailiogView);
        final AlertDialog dialog = builder.create();

        while (res.moveToNext()) {
            if (res.getInt(1) == 1) {
                id = res.getInt(0);
                email = res.getString(7);
                break;
            }
        }
        if (email != null) {
            linearLayout4.setVisibility(View.VISIBLE);
            textView.setText("Log Out?");
            textView1.setText("Are you sure you want to log out ?");
            button1.setText("Yes");
            button2.setText("No");
            dialog.show();
            final String finalEmail = email;
            final int finalId = id;
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mydb.log_inOrout(finalEmail, finalId, 0);
                    dialog.dismiss();
                    FragmentManager manager = getFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.container, new Signin()).commit();
                }
            });
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
    }
}