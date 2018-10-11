package com.example.omer.project_app;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FriendRequest extends Fragment {

    DatabaseClass mydb;
    TextView txt1;
    RecyclerView recyclerView;
    private List<Account> accounts;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_2_friendreq, container, false);
        txt1 = (TextView) view.findViewById(R.id.req_txt);
        recyclerView = (RecyclerView) view.findViewById(R.id.request_list);
        recyclerView.setHasFixedSize(true);
        mydb = new DatabaseClass(getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        accountCheck();
        return view;
    }

    protected void accountCheck() {
        int i = 0;
        int id = 0;
        final List<String> listNames = new ArrayList<String>();
        Cursor res = mydb.getAllAccountData();
        while (res.moveToNext()) {
            if (res.getInt(1) == 1) {
                i = 1;
                id = res.getInt(0);
                break;
            }
        }
        if (i == 1) {
            intializeAdapterAndData(id);
        }
    }

    private void intializeAdapterAndData(final int id) {
        if (filters(id).isEmpty()) {
            recyclerView.setVisibility(View.INVISIBLE);
            txt1.setVisibility(View.VISIBLE);
        } else if (!filters(id).isEmpty()) {
            recyclerView.setVisibility(View.VISIBLE);
            txt1.setVisibility(View.INVISIBLE);
            final ReqViewAdapter adapter = new ReqViewAdapter(filters(id));
            recyclerView.setAdapter(adapter);
            adapter.setOnItemClickListener(new ReqViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    int fid = filters(id).get(position).userId;
                    loadFragment(new UserAccount(), fid);
                }

                @Override
                public void onCnfrmClick(int position) {
                    int fid = filters(id).get(position).userId;
                    mydb.insertFriendList(id, fid);
                    mydb.insertFriendList(fid, id);
                    mydb.reqCancel(fid, id);
                    refresh();
                    adapter.notifyItemChanged(position);
                    Toast.makeText(getActivity(), "Request accepted", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onDltClick(int position) {
                    int fid = filters(id).get(position).userId;
                    mydb.reqCancel(fid, id);
                    refresh();
                    adapter.notifyItemChanged(position);
                    Toast.makeText(getActivity(), "Request cancelled", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private List<Account> filters(int id) {
        String name;
        Bitmap bitmap;
        byte[] image;
        Account account;
        Cursor res = mydb.reqData(id);
        final List<Account> filtered = new ArrayList<>();
        while (res.moveToNext()) {
            id = res.getInt(1);
            image = res.getBlob(2);
            name = res.getString(3);
            bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            account = new Account(id, bitmap, name);
            filtered.add(account);
        }
        return filtered;
    }

    public void refresh() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(FriendRequest.this).attach(FriendRequest.this).commit();
    }

    private void loadFragment(Fragment fragment, int id) {
        Bundle bundle = new Bundle();
        bundle.putInt("userId", id);
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, fragment).addToBackStack(null).commit();
        fragment.setArguments(bundle);
    }
}
