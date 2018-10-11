package com.example.omer.project_app;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> headerTitles;
    private HashMap<String, List<String>> childTitles;

    public ExpandableListAdapter(Context context, List<String> headerTitles, HashMap<String, List<String>> childTitles) {
        this.context = context;
        this.headerTitles = headerTitles;
        this.childTitles = childTitles;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.headerTitles.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.headerTitles.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.elist_header, null);
        }
        TextView listTitletv = (TextView) convertView.findViewById(R.id.list_heading);
        listTitletv.setText(listTitle);
        return convertView;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.childTitles.get(this.headerTitles.get(groupPosition)).get(childPosition);
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.childTitles.get(this.headerTitles.get(groupPosition)).size();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String listSub = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.elist_child, null);
        }
        TextView listSubtv = (TextView) convertView.findViewById(R.id.child_heading);
        listSubtv.setText(listSub);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

class ListData_Setting {

    public HashMap<String, List<String>> geData() {

        LinkedHashMap<String, List<String>> listData = new LinkedHashMap<String, List<String>>();

        List<String> information = new ArrayList<>();
        information.add("Change name");
        information.add("Change email address");
        information.add("Change password");

        List<String> privacy = new ArrayList<>();
        privacy.add("Who can see your posts?");
        privacy.add("Who can like & comment on the posts?");
        privacy.add("Who can see your friends list?");
        privacy.add("Who can see your date of birth?");
        privacy.add("Who can see your address?");
        privacy.add("Who can see your email address?");
        privacy.add("Who can send you friend requests?");
        privacy.add("Who can send you messages?");

        List<String> account = new ArrayList<>();
        account.add("Deleting your account is permanent");

        listData.put("account information Settings", information);
        listData.put("account privacy Settings", privacy);
        listData.put("Delete account", account);

        return listData;
    }
}

class ListData_Description {
    List<String> strings;

    ListData_Description(List<String> strings) {
        this.strings = strings;
    }


    public HashMap<String, List<String>> geData() {
        LinkedHashMap<String, List<String>> listData = new LinkedHashMap<String, List<String>>();
        List<String> Work = new ArrayList<>();
        List<String> Address = new ArrayList<>();
        List<String> education = new ArrayList<>();
        List<String> Dob = new ArrayList<>();
        List<String> Gender = new ArrayList<>();

        if (!strings.get(0).equals("")) Work.add(strings.get(0));
        else if (strings.get(0).equals("")) Work.add("Add work");

        if (!strings.get(1).equals("")) education.add(strings.get(1));
        else if (strings.get(1).equals("")) education.add("Add study");

        if (!strings.get(2).equals("")) Address.add(strings.get(2));
        else if (strings.get(2).equals("")) Address.add("Add current address");

        Address.add(strings.get(3));
        Dob.add(strings.get(4));
        Gender.add(strings.get(5));

        listData.put("Work experience", Work);
        listData.put("Education", education);
        listData.put("Addresses", Address);
        listData.put("Date of birth", Dob);
        listData.put("Gender", Gender);

        return listData;
    }
}