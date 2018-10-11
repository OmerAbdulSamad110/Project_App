package com.example.omer.project_app;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class userDescription extends Fragment {
    ExpandableListView elist;
    List<String> gItems;
    HashMap<String, List<String>> cItems;
    DatabaseClass mydb;
    ListData_Description description;
    ExpandableListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_description, container, false);
        mydb = new DatabaseClass(getContext());
        elist = (ExpandableListView) view.findViewById(R.id.description_list);
        populateEList();
        return view;
    }

    private void populateEList() {
        List<String> strings = new ArrayList<>();
        int userId, aid = 0;
        Cursor res = mydb.getAllAccountData();
        while (res.moveToNext()) {
            if (res.getInt(1) == 1) {
                aid = res.getInt(0);
                break;
            }
        }
        if (getArguments() != null) {
            userId = getArguments().getInt("userId");
            Cursor des = mydb.getSpecificAccountData(userId);
            if (des.moveToNext()) {
                strings.add(des.getString(12));
                strings.add(des.getString(11));
                strings.add(des.getString(10));
                strings.add(des.getString(7));
                strings.add(des.getString(9));
                strings.add(des.getString(6));
            }
            if (userId == aid) {
                description = new ListData_Description(strings);
                cItems = description.geData();
                gItems = new ArrayList<String>(cItems.keySet());
                adapter = new ExpandableListAdapter(getActivity(), gItems, cItems);

                List<String> desList = new ArrayList<>();
                desList.add(strings.get(0));
                desList.add(strings.get(1));
                desList.add(strings.get(2));
                listClick(userId, desList);
            }
            else if (userId != aid) {
                for (int no=0;no<strings.size();no++)
                {
                    if (strings.get(no).equals(""))
                    {
                        Collections.replaceAll(strings,"","Nothing to show");
                    }
                }
                int fr = 0;
                List<String> privte = new ArrayList<>();
                Cursor friend = mydb.friendList(userId);
                Cursor privacy = mydb.getSpecificPrivacy(userId);
                if (privacy.moveToNext()) {
                    privte.add(privacy.getString(3));
                    privte.add(privacy.getString(4));
                    privte.add(privacy.getString(5));
                }
                while (friend.moveToNext()) {
                    if (res.getInt(1) == aid) {
                        fr = 1;
                        break;
                    }
                }
                if (fr == 0) {
                    if (!privte.get(0).equals("Public")) {
                        Collections.replaceAll(strings,strings.get(5),"Private");
                    }
                    if (!privte.get(1).equals("Public")) {
                        Collections.replaceAll(strings,strings.get(2),"Private");
                    }
                    if (!privte.get(1).equals("Public")) {
                        Collections.replaceAll(strings,strings.get(3),"Private");
                    }
                    description = new ListData_Description(strings);
                    cItems = description.geData();
                    gItems = new ArrayList<String>(cItems.keySet());
                    adapter = new ExpandableListAdapter(getActivity(), gItems, cItems);
                } else if (fr == 1) {
                    if (!privte.get(0).equals("Only me"))
                        Collections.replaceAll(strings,strings.get(5),"Private");
                    if (!privte.get(1).equals("Only me"))
                        Collections.replaceAll(strings,strings.get(2),"Private");
                    if (!privte.get(1).equals("Only me"))
                        Collections.replaceAll(strings,strings.get(3),"Private");
                    description = new ListData_Description(strings);
                    cItems = description.geData();
                    gItems = new ArrayList<String>(cItems.keySet());
                    adapter = new ExpandableListAdapter(getActivity(), gItems, cItems);
                }
            }
            elist.setAdapter(adapter);
            for (int i = 0; i < adapter.getGroupCount(); i++) {
                elist.expandGroup(i);
            }
        }
    }

    private void listClick(final int userId, final List<String> desList) {
        elist.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                loadAction(groupPosition, childPosition, userId, desList);
                return false;
            }
        });
        //To make E list always expanded
        elist.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
    }

    private void loadAction(final int parent_pos, final int child_pos, final int userId, List<String> desList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.setting_dialog, null);

        TextView textView = (TextView) dialogView.findViewById(R.id.setting_text);
        LinearLayout linearLayout1 = (LinearLayout) dialogView.findViewById(R.id.one_box);
        final EditText editText = (EditText) dialogView.findViewById(R.id.setting_edit);
        Button button1 = (Button) dialogView.findViewById(R.id.setting_btn1);
        Button button2 = (Button) dialogView.findViewById(R.id.setting_btn2);

        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        button1.setText("Enter");
        linearLayout1.setVisibility(View.VISIBLE);
        if (parent_pos == 0) {
            textView.setText("Enter your work");
            editText.setText(desList.get(0));
            editText.setHint("");
            dialog.show();
        } else if (parent_pos == 1) {
            textView.setText("Enter your education");
            editText.setText(desList.get(1));
            editText.setHint("");
            dialog.show();
        } else if (parent_pos == 2) {
            if (child_pos == 0) {
                textView.setText("Enter your home address");
                editText.setText(desList.get(2));
                editText.setHint("");
            }
            dialog.show();
        }
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().equals("")) {
                    editText.setError("Enter required fields");
                } else {
                    if (parent_pos == 0) {
                        mydb.updateAccountData(userId, "userWork", editText.getText().toString());
                    } else if (parent_pos == 1) {
                        mydb.updateAccountData(userId, "userStudy", editText.getText().toString());
                    } else if (parent_pos == 2) {
                        mydb.updateAccountData(userId, "userAddress", editText.getText().toString());
                    }
                    dialog.dismiss();
                    populateEList();
                    Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT);
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
}
