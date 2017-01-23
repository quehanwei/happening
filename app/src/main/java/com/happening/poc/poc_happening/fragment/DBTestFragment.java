package com.happening.poc.poc_happening.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.happening.poc.poc_happening.R;
import com.happening.poc.poc_happening.dataStore.AndroidDatabaseManager;
import com.happening.poc.poc_happening.dataStore.DBHelper;

import java.util.Random;

/**
 * Created by daired on 08/01/17.
 */

public class DBTestFragment  extends Fragment {

    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";

    private static DBTestFragment instance = null;

    private View rootView = null;
    private DBHelper dbHelper;

    public static DBTestFragment getInstance() {
        instance = new DBTestFragment();
        return instance;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dbHelper = DBHelper.getInstance(getContext());

        rootView = inflater.inflate(R.layout.fragment_db_test, container, false);



        rootView.findViewById(R.id.button_show_db).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dbmanager = new Intent(getActivity(),AndroidDatabaseManager.class);
                startActivity(dbmanager);

            }
        });

        return rootView;
    }



    @Override
    public void onResume() {
        super.onResume();
    }


    private static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

}
