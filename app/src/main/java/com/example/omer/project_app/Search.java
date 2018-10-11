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
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Search extends Fragment {
    DatabaseClass mydb;
    SearchView searchView;
    RecyclerView recyclerView;
    TextView textView1;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_5_search, container, false);
        searchView = (SearchView) view.findViewById(R.id.search);
        recyclerView = (RecyclerView) view.findViewById(R.id.user_list);
        textView1 = (TextView) view.findViewById(R.id.srch_txt);
        recyclerView.setHasFixedSize(true);
        mydb = new DatabaseClass(getContext());
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search Account");
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        accountCheck();
        return view;
    }

    private void accountCheck() {
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
        if (i == 1)
        {
            initializeAdapterAndData(id);
        }
    }

    private void initializeAdapterAndData(final int id) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                if (filters(query, id).isEmpty()) {
                    recyclerView.setVisibility(View.INVISIBLE);
                    textView1.setVisibility(View.VISIBLE);
                }
                else if (!filters(query, id).isEmpty()) {
                    recyclerView.setVisibility(View.VISIBLE);
                    textView1.setVisibility(View.INVISIBLE);
                    final SearchViewAdapter adapter = new SearchViewAdapter(filters(query, id), getActivity());
                    recyclerView.setAdapter(adapter);
                    adapter.setOnItemClickListener(new SearchViewAdapter.OnItemClickListener() {
                        //Goto Account by click
                        @Override
                        public void onItemClick(int position)
                        {
                            int sId = filters(query, id).get(position).userId;
                            loadFragment(new UserAccount(), sId);
                        }

                        //Button1 method
                        @Override
                        public void onAddClick(int position) {
                            //to get receiver id through row
                            int rid = filters(query, id).get(position).userId;
                            //database to save friend request data
                            mydb.sendRequest(id, rid);
                            Toast.makeText(getActivity(), "Request send to " + filters(query, id).get(position).userName, Toast.LENGTH_SHORT).show();
                            adapter.notifyItemChanged(position);
                        }

                        //Button2 method
                        @Override
                        public void onCancelClick(int position) {
                            //to get receiver id through row
                            int rid = filters(query, id).get(position).userId;
                            //database to remove friend request data
                            mydb.reqCancel(id, rid);
                            adapter.notifyItemChanged(position);
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
        Cursor res = mydb.searchAccounts(active);
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
