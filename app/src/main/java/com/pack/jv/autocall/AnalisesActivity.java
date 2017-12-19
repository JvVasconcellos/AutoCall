package com.pack.jv.autocall;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pixelcan.inkpageindicator.InkPageIndicator;

import java.util.ArrayList;

/**
 * Created by João on 21/06/2017.
 */

public class AnalisesActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private int position;
    private ArrayList<Turma> listTurmas = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analises);

        Bundle b = getIntent().getExtras();
        position = -1;
        if(b != null)
        {
            position = b.getInt("turma");
        }
        leDataBase();
        Turma turma = listTurmas.get(position);


        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) findViewById(R.id.vPagerAnalises);
        PagerAdapter adapter = new GraphFramentPagerAdapter(getSupportFragmentManager(), AnalisesActivity.this, position);
        viewPager.setAdapter(adapter);

        InkPageIndicator inkPageIndicator = (InkPageIndicator) findViewById(R.id.indicator);
        inkPageIndicator.setViewPager(viewPager);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Análises - Turma " + turma.nomeTurma);

    }

    private void leDataBase(){
        SharedPreferences mPrefs = getSharedPreferences("databaseAutoCall", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("dataCall", "");
        if(json != "")
            listTurmas = gson.fromJson(json,new TypeToken<ArrayList<Turma>>(){}.getType());
    }

}
