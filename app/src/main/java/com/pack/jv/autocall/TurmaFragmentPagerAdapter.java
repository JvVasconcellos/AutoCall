package com.pack.jv.autocall;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by João on 13/06/2017.
 */

public class TurmaFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private Context context;
    private ArrayList<Turma> listTurmas = new ArrayList();

    public TurmaFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        leDataBase();
        return listTurmas.size();
    }

    @Override
    public Fragment getItem(int position) {
        leDataBase();
        return TurmaFragment.newInstance(listTurmas.get(position));
    }

    @Override
    public int getItemPosition(Object item) {
        return POSITION_NONE;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        leDataBase();
        Turma turma = listTurmas.get(position);
        return turma.nomeTurma;
    }

    private void leDataBase(){
        SharedPreferences mPrefs = context.getSharedPreferences("databaseAutoCall", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("dataCall", "");
        if(json != "")
            listTurmas = gson.fromJson(json,new TypeToken<ArrayList<Turma>>(){}.getType());
    }

}
