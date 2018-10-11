package com.example.omer.project_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.inputmethodservice.Keyboard;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Signup extends Fragment {

    EditText ed1, ed2, ed3, ed4, ed5, ed6, ed7;
    RadioGroup radioGroup;
    ImageView imageView;
    Bitmap bitmap, imageSet;
    Button btn1, btn2, btn3, btn4, btn5, btn6, upload, different, remove;
    LinearLayout layout1, layout2, layout3, layout4, layout5, layout6;
    DatabaseClass mydb;
    int img = 0;
    InputMethodManager imm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        ed1 = (EditText) view.findViewById(R.id.frstNm_edt);
        ed2 = (EditText) view.findViewById(R.id.lstNm_edt);
        ed3 = (EditText) view.findViewById(R.id.email_edt);
        ed4 = (EditText) view.findViewById(R.id.dob1_edt);
        ed5 = (EditText) view.findViewById(R.id.dob2_edt);
        ed6 = (EditText) view.findViewById(R.id.dob3_edt);
        ed7 = (EditText) view.findViewById(R.id.pass_edt);

        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);

        imageView = (ImageView) view.findViewById(R.id.pro_img);

        btn1 = (Button) view.findViewById(R.id.nxt1);
        btn2 = (Button) view.findViewById(R.id.nxt2);
        btn3 = (Button) view.findViewById(R.id.nxt3);
        btn4 = (Button) view.findViewById(R.id.nxt4);
        btn5 = (Button) view.findViewById(R.id.nxt5);
        btn6 = (Button) view.findViewById(R.id.nxt6);
        upload = (Button) view.findViewById(R.id.upload_img);
        different = (Button) view.findViewById(R.id.upload_diff_img);
        remove = (Button) view.findViewById(R.id.remove_img);

        layout1 = (LinearLayout) view.findViewById(R.id.next1);
        layout2 = (LinearLayout) view.findViewById(R.id.next2);
        layout3 = (LinearLayout) view.findViewById(R.id.next3);
        layout4 = (LinearLayout) view.findViewById(R.id.next4);
        layout5 = (LinearLayout) view.findViewById(R.id.next5);
        layout6 = (LinearLayout) view.findViewById(R.id.next6);

        mydb = new DatabaseClass(getContext());

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        Next();
        return view;
    }


    private void imageSelect() {
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
                    startActivityForResult(Intent.createChooser(gintent, "Select an app"), 2);
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
                bitmap = (Bitmap) bundle.get("data");
                imageView.setImageBitmap(bitmap);
                imageSet = bitmap;
            } else if (requestCode == 2) {
                Uri select = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), select);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(bitmap);
                imageSet = bitmap;
            }
            upload.setVisibility(View.INVISIBLE);
            different.setVisibility(View.VISIBLE);
            remove.setVisibility(View.VISIBLE);
            img = 1;
        }
    }

    private void Next() {
        final String[] string = new String[6];
        layout1.setVisibility(View.VISIBLE);
        //Next 1
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ed1.getText().toString().isEmpty()) {
                    ed1.setError("Enter First Name");
                } else if (ed2.getText().toString().isEmpty()) {
                    ed1.setError("Enter Last Name");
                } else if (!ed1.getText().toString().isEmpty() && !ed2.getText().toString().isEmpty()) {
                    string[0] = ed1.getText().toString();
                    string[1] = ed2.getText().toString();
                    layout1.setVisibility(View.INVISIBLE);
                    imm.hideSoftInputFromWindow(layout1.getWindowToken(), 0);
                    layout2.setVisibility(View.VISIBLE);
                }
            }
        });
        //Next 2
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ed3.getText().toString().isEmpty()) {
                    ed3.setError("Enter email address");
                } else if (!ed3.getText().toString().isEmpty()) {
                    int i = 0;
                    Cursor res = mydb.getAllAccountData();
                    while (res.moveToNext()) {
                        if (res.getString(7).equals(ed3.getText().toString())) {
                            i = 1;
                            break;
                        }
                    }
                    if (i == 1) {
                        ed3.setError("This email is already in use");
                    } else if (i == 0) {
                        string[2] = ed3.getText().toString();
                        layout2.setVisibility(View.INVISIBLE);
                        imm.hideSoftInputFromWindow(layout2.getWindowToken(), 0);
                        layout3.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        //Next 3
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ed4.getText().toString().isEmpty() || ed5.getText().toString().isEmpty() || ed6.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Enter complete Date of birth", Toast.LENGTH_SHORT).show();
                } else if (!ed4.getText().toString().isEmpty() && !ed5.getText().toString().isEmpty() && !ed6.getText().toString().isEmpty()) {
                    string[3] = ed4.getText().toString() + "-" + ed5.getText().toString() + "-" + ed6.getText().toString();
                    layout3.setVisibility(View.INVISIBLE);
                    imm.hideSoftInputFromWindow(layout3.getWindowToken(), 0);
                    layout4.setVisibility(View.VISIBLE);
                }
            }
        });
        //Next 4
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio1:
                        string[4] = "Male";
                        break;
                    case R.id.radio2:
                        string[4] = "Female";
                        break;
                }
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (string[4] == null) {
                    Toast.makeText(getActivity(), "Select Gender", Toast.LENGTH_SHORT).show();
                } else if (string[4] != null) {
                    layout4.setVisibility(View.INVISIBLE);
                    layout5.setVisibility(View.VISIBLE);
                }
            }
        });
        //Next5
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ed7.getText().toString().isEmpty() || ed7.getText().toString().length() < 6) {
                    ed7.setError("Enter correct Password");
                } else if (!ed7.getText().toString().isEmpty() || ed7.getText().toString().length() >= 6) {
                    string[5] = ed7.getText().toString();
                    imm.hideSoftInputFromWindow(layout5.getWindowToken(), 0);
                    layout5.setVisibility(View.INVISIBLE);
                    layout6.setVisibility(View.VISIBLE);
                }
            }
        });
        //Next 6
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageSelect();
            }
        });

        different.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageSelect();
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload.setVisibility(View.VISIBLE);
                different.setVisibility(View.INVISIBLE);
                remove.setVisibility(View.INVISIBLE);
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.defaultuser);
                imageView.setImageBitmap(bitmap);
                img = 0;
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int a = 0;
                for (int i = 0; i < string.length; i++) {
                    if (string[i] == null) {
                        a++;
                    }
                }
                if (a != 0) {
                    Toast.makeText(getActivity(), "Null found", Toast.LENGTH_SHORT).show();
                } else {
                    insertData(img, string, imageSet);
                    Toast.makeText(getActivity(), "Account created", Toast.LENGTH_SHORT).show();
                    loadFragment();
                }
            }
        });
    }

    private void insertData(int img, String[] string, Bitmap image) {
        int id = 0;

        if (image == null) {
            image = BitmapFactory.decodeResource(getResources(), R.drawable.defaultuser);
        }

        String name = string[0] + " " + string[1];
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mydb.insertAccountData(byteArray, name, string[0], string[1], string[2], string[3], string[4], string[5]);
        Cursor res = mydb.getSpecificAccountData(name, string[2]);
        if (res.moveToNext()) {
            id = res.getInt(0);
        }
        if (id != 0) {
            mydb.insertPrivacyData(id);

            if (img == 0) {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_addphoto);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArray = stream.toByteArray();
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mydb.insertAlbumData(id, "Profile Pictures", "Public", byteArray);
            }
            else if (img == 1) {
                int albumId = 0;
                mydb.insertAlbumData(id, "Profile Pictures", "Public", byteArray);
                Cursor resPhoto = mydb.getSpecificAlbumId(id, "Profile Pictures");
                if (resPhoto.moveToNext()) {
                    albumId = resPhoto.getInt(0);
                }
                if (albumId != 0) {
                    mydb.insertPhotoData(id, albumId, byteArray);
                }
            }
        }
    }

    private void loadFragment() {

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, new Signin()).commit();

        ed1.setText("");
        ed2.setText("");
        ed3.setText("");
        ed4.setText("");
        ed5.setText("");
        ed6.setText("");
        ed7.setText("");
        Bitmap bp = BitmapFactory.decodeResource(getResources(), R.drawable.defaultuser);
        imageView.setImageBitmap(bp);
    }
}
