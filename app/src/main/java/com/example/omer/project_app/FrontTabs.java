package com.example.omer.project_app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FrontTabs extends Fragment {
    TabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tabs, container, false);
        tabLayout = (TabLayout) view.findViewById(R.id.front_tabs);
        bindWidgetsWithAnEvent();
        setupTabLayout();

        CheckValue();
        return view;
    }

    private void CheckValue() {

    }

    public static MainActivity getInstance() {
        MainActivity instance = getInstance();
        return instance;
    }

    private void setupTabLayout() {
        Integer pos = null;
        if (getArguments() != null) {
            pos = getArguments().getInt("position");
            setCurrentTabFragment(pos);
            tabLayout.setScrollPosition(pos, 0f, true);
            setArguments(null);
            if (pos == 0) {
                final Integer finalPos = pos;
                tabLayout.postOnAnimation(new Runnable() {
                    @Override
                    public void run() {
                        tabLayout.getTabAt(finalPos).select();
                    }
                });
            }
            else if (pos == 1) {
                final Integer finalPos = pos;
                tabLayout.postOnAnimation(new Runnable() {
                    @Override
                    public void run() {
                        tabLayout.getTabAt(finalPos).select();
                    }
                });
            }

            else if (pos == 2) {
                final Integer finalPos = pos;
                tabLayout.postOnAnimation(new Runnable() {
                    @Override
                    public void run() {
                        tabLayout.getTabAt(finalPos).select();
                    }
                });
            }
        }
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_newsfeed), true);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_friendreq));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_searchtab));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_burger));
    }

    private void bindWidgetsWithAnEvent() {
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos;
                if (getArguments() != null) {
                    pos = getArguments().getInt("position");
                    setCurrentTabFragment(pos);
                } else {
                    setCurrentTabFragment(tab.getPosition());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setCurrentTabFragment(int tabPosition) {
        switch (tabPosition) {
            case 0:
                replaceFragment(new NewsFeed());
                break;
            case 1:
                replaceFragment(new FriendRequest());
                break;
            case 2:
                replaceFragment(new Search());
                break;
            case 3:
                replaceFragment(new Burger());
                break;
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.front_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }
}
