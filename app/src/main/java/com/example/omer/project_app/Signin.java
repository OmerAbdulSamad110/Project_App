package com.example.omer.project_app;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Signin extends Fragment {
    EditText ed1, ed2;
    Button btn1, btn2;
    TextView txt1, txt2, txt3;
    String email = null, password = null;
    int id;
    DatabaseClass mydb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signin, container, false);

        ed1 = (EditText) view.findViewById(R.id.email_ledt);
        ed2 = (EditText) view.findViewById(R.id.pass_ledt);
        btn1 = (Button) view.findViewById(R.id.login_btn);
        btn2 = (Button) view.findViewById(R.id.create_btn);
        txt1 = (TextView) view.findViewById(R.id.forget_txt);
        txt2 = (TextView) view.findViewById(R.id.help_txt);
        txt3 = (TextView) view.findViewById(R.id.exit_txt);
        mydb = new DatabaseClass(getContext());
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ed1.getText().toString().equals("")) {
                    ed1.setError("Enter required field");
                }
                else if (ed2.getText().toString().equals("")) {
                    ed2.setError("Enter required field");
                }
                if (ed1.getText().toString().equals("") && ed2.getText().toString().equals("")) {
                    ed1.setError("Enter required field");
                    ed2.setError("Enter required field");
                }
                else if (!ed1.getText().toString().equals("") && !ed2.getText().toString().equals("")) {
                    accountCheck();
                }
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed1.setText("");
                ed2.setText("");
                LoadFragment(new Signup());
            }
        });

        txt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        txt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        txt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        return view;
    }

    private void accountCheck() {
        Cursor res = mydb.getAllAccountData();
        while (res.moveToNext()) {
            if (ed1.getText().toString().equals(res.getString(7))) {
                id = res.getInt(0);
                email = res.getString(7);
                password = res.getString(8);
                break;
            }
        }
        if (email != null) {
            if (ed2.getText().toString().equals(password)) {
                mydb.log_inOrout(email, id, 1);
                ed1.setText("");
                ed2.setText("");
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.replace(R.id.container, new FrontTabs()).commit();
            }
            else {
                Toast.makeText(getActivity(), "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }

    private void LoadFragment(Fragment fragment) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, fragment).addToBackStack(null).commit();
    }
}
