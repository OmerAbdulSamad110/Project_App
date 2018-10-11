package com.example.omer.project_app;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Setting extends Fragment {
    ExpandableListView elist;
    List<String> gItems;
    HashMap<String, List<String>> cItems;
    ListData_Setting setting;
    DatabaseClass mydb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        mydb = new DatabaseClass(getContext());
        setting = new ListData_Setting();
        elist = (ExpandableListView) view.findViewById(R.id.setting_list);
        cItems = setting.geData();
        gItems = new ArrayList<String>(cItems.keySet());
        ExpandableListAdapter adapter = new ExpandableListAdapter(getActivity(), gItems, cItems);
        elist.setAdapter(adapter);
        listClick();
        return view;
    }

    private void listClick() {
        elist.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                loadAction(groupPosition, childPosition);
                return false;
            }
        });

    }

    private void loadAction(final int parent_pos, final int child_pos) {
        //accountCheck + get data
        int id = 0;
        String fname = null, lname = null, email = null, password = null;
        Cursor privacy = null;
        final Cursor res = mydb.getAllAccountData();
        while (res.moveToNext()) {
            if (res.getInt(1) == 1) {
                id = res.getInt(0);
                fname = res.getString(4);
                lname = res.getString(5);
                email = res.getString(7);
                password = res.getString(8);
                break;
            }
        }
        if (id != 0) {
            privacy = mydb.getSpecificPrivacy(id);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View dailiogView = inflater.inflate(R.layout.setting_dialog, null);

        final TextView textView = (TextView) dailiogView.findViewById(R.id.setting_text);
        final LinearLayout linearLayout1 = (LinearLayout) dailiogView.findViewById(R.id.one_box);
        LinearLayout linearLayout2 = (LinearLayout) dailiogView.findViewById(R.id.two_boxes);
        LinearLayout linearLayout3 = (LinearLayout) dailiogView.findViewById(R.id.spinner_box);
        final LinearLayout linearLayout4 = (LinearLayout) dailiogView.findViewById(R.id.text_box);
        final EditText editText = (EditText) dailiogView.findViewById(R.id.setting_edit);
        final EditText editText1 = (EditText) dailiogView.findViewById(R.id.setting_edit1);
        final EditText editText2 = (EditText) dailiogView.findViewById(R.id.setting_edit2);
        Button button1 = (Button) dailiogView.findViewById(R.id.setting_btn1);
        Button button2 = (Button) dailiogView.findViewById(R.id.setting_btn2);
        final Spinner spinner = (Spinner) dailiogView.findViewById(R.id.pri_spinner);
        RelativeLayout relativeLayout = (RelativeLayout) dailiogView.findViewById(R.id.setting_box);
        ViewGroup.LayoutParams params = relativeLayout.getLayoutParams();

        builder.setView(dailiogView);
        int acc = 0, pri = 0;

        final List<String> listItems1 = new ArrayList<>();
        listItems1.add("Public");
        listItems1.add("Friends");
        listItems1.add("Only me");

        List<String> listItems2 = new ArrayList<>();
        listItems2.add("Every one");
        listItems2.add("No one");
        final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, listItems1);
        final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, listItems2);

        final AlertDialog dialog = builder.create();
        if (parent_pos == 0) {
            linearLayout3.setVisibility(View.INVISIBLE);
            if (child_pos == 0) {
                textView.setText("Enter New Name");
                editText1.setText(fname);
                editText2.setText(lname);
                linearLayout1.setVisibility(View.INVISIBLE);
                linearLayout2.setVisibility(View.VISIBLE);
                acc = 1;
            } else if (child_pos == 1) {
                textView.setText("Enter New Email");
                editText.setText(email);
                linearLayout1.setVisibility(View.VISIBLE);
                linearLayout2.setVisibility(View.INVISIBLE);
                acc = 2;
            } else if (child_pos == 2) {
                textView.setText("Enter New Password");
                editText.setText(password);
                linearLayout1.setVisibility(View.VISIBLE);
                linearLayout2.setVisibility(View.INVISIBLE);
                acc = 3;
            }
            dialog.show();
        } else if (parent_pos == 1) {
            params.height = 400;
            if (child_pos == 0) {
                textView.setText("Choose who can see posts on your account");
                spinner.setAdapter(adapter1);
                if (privacy.moveToNext()) {
                    spinner.setSelection(adapter1.getPosition(privacy.getString(1)));
                }
                pri = 1;
            } else if (child_pos == 1) {
                textView.setText("Choose who can like & comment your posts");
                spinner.setAdapter(adapter1);
                if (privacy.moveToNext()) {
                    spinner.setSelection(adapter1.getPosition(privacy.getString(2)));
                }
                pri = 2;
            } else if (child_pos == 2) {
                textView.setText("Choose who can see your friend list");
                spinner.setAdapter(adapter1);
                if (privacy.moveToNext()) {
                    spinner.setSelection(adapter1.getPosition(privacy.getString(3)));
                }
                pri = 3;
            } else if (child_pos == 3) {
                textView.setText("Choose who can see your date of birth");
                spinner.setAdapter(adapter1);
                if (privacy.moveToNext()) {
                    spinner.setSelection(adapter1.getPosition(privacy.getString(4)));
                }
                pri = 4;
            } else if (child_pos == 4) {
                textView.setText("Choose who can see your address");
                spinner.setAdapter(adapter1);
                if (privacy.moveToNext()) {
                    spinner.setSelection(adapter1.getPosition(privacy.getString(5)));
                }
                pri = 5;
            } else if (child_pos == 5) {
                textView.setText("Choose who can see your email address");
                spinner.setAdapter(adapter1);
                if (privacy.moveToNext()) {
                    spinner.setSelection(adapter1.getPosition(privacy.getString(6)));
                }
                pri = 6;
            } else if (child_pos == 6) {
                textView.setText("Choose who can send you friend requests");
                spinner.setAdapter(adapter2);
                if (privacy.moveToNext()) {
                    spinner.setSelection(adapter2.getPosition(privacy.getString(7)));
                }
                pri = 7;
            } else if (child_pos == 7) {
                textView.setText("Choose who can send you messages");
                spinner.setAdapter(adapter2);
                if (privacy.moveToNext()) {
                    spinner.setSelection(adapter1.getPosition(privacy.getString(8)));
                }
                pri = 8;
            }
            linearLayout1.setVisibility(View.INVISIBLE);
            linearLayout2.setVisibility(View.INVISIBLE);
            linearLayout3.setVisibility(View.VISIBLE);
            dialog.show();
        } else if (parent_pos == 2) {
            textView.setText("Warning");
            button1.setText("Confirm");
            linearLayout3.setVisibility(View.INVISIBLE);
            linearLayout4.setVisibility(View.VISIBLE);
            dialog.show();
        }
        final int finalAcc = acc;
        final int finalId = id;
        final int finalPri = pri;
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int chng = 0;
                if (parent_pos == 0) {
                    if (finalAcc == 1) {
                        if (editText1.getText().toString().equals("")) {
                            editText1.setError("Enter required field");
                        } else if (editText2.getText().toString().equals("")) {
                            editText2.setError("Enter required field");
                        } else if (editText1.getText().toString().equals("") && editText2.getText().toString().equals("")) {
                            editText1.setError("Enter required field");
                            editText2.setError("Enter required field");
                        } else if (!editText1.getText().toString().equals("") && !editText2.getText().toString().equals("")) {
                            String name = editText1.getText().toString() + " " + editText2.getText().toString();
                            mydb.updateAccountData(finalId, "userName", name);
                            mydb.updateAccountData(finalId, "userFName", editText1.getText().toString());
                            mydb.updateAccountData(finalId, "userLName", editText2.getText().toString());
                            Toast.makeText(getActivity(), "Name has been changed", Toast.LENGTH_SHORT).show();
                            chng = 1;
                        }
                    } else if (finalAcc == 2) {
                        int i = 0;
                        if (!editText.getText().toString().equals("")) {
                            Cursor res = mydb.getAllAccountData();
                            while (res.moveToNext()) {
                                if (res.getString(7).equals(editText.getText().toString())) {
                                    i = 1;
                                    break;
                                }
                            }
                            if (i == 1) {
                                editText.setError("This email is already in use");
                            } else if (i == 0) {
                                mydb.updateAccountData(finalId, "userEmail", editText.getText().toString());
                                Toast.makeText(getActivity(), "Email has been changed", Toast.LENGTH_SHORT).show();
                                chng = 1;
                            }
                        } else {
                            editText.setError("Enter required field");
                        }
                    } else if (finalAcc == 3) {
                        int i = 0;
                        if (!editText.getText().toString().equals("")) {
                            if (editText.getText().toString().length() < 6) {
                                editText.setError("Password should have minimum 6 letters");
                            } else if (editText.getText().toString().length() >= 6) {
                                mydb.updateAccountData(finalId, "userPassword", editText.getText().toString());
                                Toast.makeText(getActivity(), "Password has been changed", Toast.LENGTH_SHORT).show();
                                chng = 1;
                            }
                        } else {
                            editText.setError("Enter required field");
                        }
                    }
                    if (chng == 1) {
                        chng = 0;
                        dialog.dismiss();
                    }
                } else if (parent_pos == 1) {
                    if (finalPri == 1) {
                        mydb.updatePrivacy(finalId, "postVisible", adapter1.getItem(spinner.getSelectedItemPosition()));
                    } else if (finalPri == 2) {
                        mydb.updatePrivacy(finalId, "postLC", adapter1.getItem(spinner.getSelectedItemPosition()));

                    } else if (finalPri == 3) {
                        mydb.updatePrivacy(finalId, "friendsVisible", adapter1.getItem(spinner.getSelectedItemPosition()));

                    } else if (finalPri == 4) {
                        mydb.updatePrivacy(finalId, "dobVisible", adapter1.getItem(spinner.getSelectedItemPosition()));

                    } else if (finalPri == 5) {
                        mydb.updatePrivacy(finalId, "addressVisible", adapter1.getItem(spinner.getSelectedItemPosition()));

                    } else if (finalPri == 6) {
                        mydb.updatePrivacy(finalId, "emailVisible", adapter1.getItem(spinner.getSelectedItemPosition()));

                    } else if (finalPri == 7) {
                        mydb.updatePrivacy(finalId, "sendFR", adapter2.getItem(spinner.getSelectedItemPosition()));

                    } else if (finalPri == 8) {
                        mydb.updatePrivacy(finalId, "sendMsg", adapter2.getItem(spinner.getSelectedItemPosition()));
                    }
                    Toast.makeText(getActivity(), "Changes in privacy have been made", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else if (parent_pos == 2) {
                    delete(finalId);
                    dialog.dismiss();
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

    private void delete(final int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        View dailiogView = inflater.inflate(R.layout.setting_dialog, null);
        final TextView textView = (TextView) dailiogView.findViewById(R.id.setting_text);
        final LinearLayout linearLayout1 = (LinearLayout) dailiogView.findViewById(R.id.one_box);
        final EditText editText = (EditText) dailiogView.findViewById(R.id.setting_edit);
        Button button1 = (Button) dailiogView.findViewById(R.id.setting_btn1);
        Button button2 = (Button) dailiogView.findViewById(R.id.setting_btn2);
        builder.setView(dailiogView);
        final AlertDialog dialog = builder.create();

        String pass = null;
        linearLayout1.setVisibility(View.VISIBLE);
        textView.setText("Enter password");
        editText.setText("");
        dialog.show();
        Cursor res1 = mydb.getSpecificAccountData(id);
        if (res1.moveToNext()) {
            pass = res1.getString(8);
        }

        final String finalPass = pass;
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalPass != null) {
                    if (!editText.getText().toString().isEmpty()) {
                        if (editText.getText().toString().equals(finalPass)) {
                            dialog.dismiss();
                            mydb.deleteAll(id);
                            FragmentManager manager = getFragmentManager();
                            FragmentTransaction transaction = manager.beginTransaction();
                            transaction.replace(R.id.container, new Signin()).commit();
                        } else if (!editText.getText().toString().equals(finalPass)) {
                            editText.setError("Invalid Password");
                        }
                    } else {
                        editText.setError("Enter password");
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

}
