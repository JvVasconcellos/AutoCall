package com.pack.jv.autocall;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by João on 21/06/2017.
 */

public class GraphFramentPagerAdapter extends FragmentStatePagerAdapter {
    private Context context;
    private int turmaIndex;
    private ArrayList<Turma> listTurmas = new ArrayList();
    private static int NUM_ITEMS = 2;

    public GraphFramentPagerAdapter(FragmentManager fm, Context context, int turmaIndex) {
        super(fm);
        this.context = context;
        this.turmaIndex = turmaIndex;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public Fragment getItem(int position) {
        leDataBase();
        return GraphFragment.newInstance(listTurmas.get(turmaIndex), position);
    }

    @Override
    public int getItemPosition(Object item) {
        return POSITION_NONE;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return "";
    }

    private void leDataBase(){
        SharedPreferences mPrefs = context.getSharedPreferences("databaseAutoCall", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("dataCall", "");
        if(json != "")
            listTurmas = gson.fromJson(json,new TypeToken<ArrayList<Turma>>(){}.getType());
    }
}
