package com.example.omer.project_app;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    DatabaseClass mydb;
    private static String Tag="TAG_FRAGMENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mydb = new DatabaseClass(this);
        accountCheck();
    }

    private void accountCheck() {
        int i = 0, id = 0;
        String name = null, email = null;
        Cursor res = mydb.getAllAccountData();
        while (res.moveToNext()) {
            if (res.getInt(1) == 1) {
                id=res.getInt(0);
                i = 1;
                break;
            }
        }
        if (i == 0)
        {
            loadFragment(new Signin());
        }
        else if (i == 1)
        {
            loadFragment(new FrontTabs());
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, fragment,Tag).commit();
    }

}
