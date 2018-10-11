package com.example.omer.project_app;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class FriendListSt extends Fragment {

    DatabaseClass mydb;
    SearchView searchView;
    RecyclerView recyclerView;
    TextView textView;
    private List<Account> accounts;
    Context context;
    int userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friendlist_st, container, false);
        mydb = new DatabaseClass(getContext());
        searchView = (SearchView) view.findViewById(R.id.search_frnd);
        recyclerView = (RecyclerView) view.findViewById(R.id.frnd_list);
        textView = (TextView) view.findViewById(R.id.list_txt);
        recyclerView.setHasFixedSize(true);

        searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search friends");
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        if (getArguments() != null) {
            userId = getArguments().getInt("userId");
            accountCheck();
        }
        return view;
    }

    private void accountCheck() {
        int i = 0;
        int activeId = 0;
        byte[] image;
        Bitmap bitmap;
        final List<String> listNames = new ArrayList<String>();
        Cursor res = mydb.getAllAccountData();
        while (res.moveToNext()) {
            if (res.getInt(1) == 1) {
                activeId = res.getInt(0);
                i = 1;
                break;
            }
        }

        if (i == 1) {
            final List<Account> accounts1 = new ArrayList<>();
            Cursor res1 = mydb.friendList(userId);
            while (res1.moveToNext()) {
                if (res1.getInt(1) != activeId) {
                    image = res1.getBlob(2);
                    bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                    Account account = new Account(res1.getInt(1), bitmap, res1.getString(3));
                    accounts1.add(account);
                }
            }

            if (!accounts1.isEmpty()) {
                Cursor privacy = mydb.getSpecificPrivacy(userId);
                Cursor friend = mydb.friendList(userId);
                String priv = null;
                int fr = 0;
                if (privacy.moveToNext()) {
                    priv = privacy.getString(3);
                }
                while (friend.moveToNext()) {
                    if (friend.getInt(1) == userId) {
                        fr = 1;
                        break;
                    }
                }
                SimpleViewAdapter adapter = new SimpleViewAdapter(accounts1);
                textView.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(adapter);
                adapter.setOnItemClickListener(new SimpleViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        int sId = accounts1.get(position).userId;
                        loadFragment(new UserAccount(), sId);
                    }
                });
                if (userId != activeId) {
                    if (fr == 1 && !priv.equals("Only me")) {
                        intializeAdapterAndData(userId);
                    } else if (priv.equals("Public")) {
                        intializeAdapterAndData(userId);
                    } else {
                        recyclerView.setVisibility(View.INVISIBLE);
                        textView.setVisibility(View.VISIBLE);
                        textView.setText("Nothing to show");
                    }
                }
                else if (userId == activeId) {
                    intializeAdapterAndData(userId);
                }
            } else if (accounts1.isEmpty()) {
                if (userId != activeId) {
                    textView.setText("Nothing to show");
                    recyclerView.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.VISIBLE);
                }
                recyclerView.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.VISIBLE);
            }
        }

    }

    private void intializeAdapterAndData(final int id) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                if (filters(query, id).isEmpty()) {
                    recyclerView.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.VISIBLE);
                } else if (!filters(query, id).isEmpty()) {
                    recyclerView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.INVISIBLE);
                    final SimpleViewAdapter adapter = new SimpleViewAdapter(filters(query, id));
                    recyclerView.setAdapter(adapter);
                    adapter.setOnItemClickListener(new SimpleViewAdapter.OnItemClickListener() {
                        //Goto Account by click
                        @Override
                        public void onItemClick(int position) {
                            int sId = filters(query, id).get(position).userId;
                            loadFragment(new UserAccount(), sId);
                        }
                    });
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private List<Account> filters(String query, int active) {
        String name;
        int id;
        Bitmap bitmap;
        byte[] image;
        Account account;
        Cursor res = mydb.friendList(active);
        query = query.toLowerCase();
        final List<Account> filtered = new ArrayList<>();
        while (res.moveToNext()) {
            if (res.getString(3).toLowerCase().startsWith(query) || res.getString(3).toLowerCase().contains(query)) {
                id = res.getInt(0);
                image = res.getBlob(2);
                name = res.getString(3);
                bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                account = new Account(id, bitmap, name);
                filtered.add(account);
            }
        }
        return filtered;
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
