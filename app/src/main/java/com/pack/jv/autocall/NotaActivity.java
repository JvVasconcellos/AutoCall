package com.pack.jv.autocall;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by João on 20/06/2017.
 */

public class NotaActivity extends AppCompatActivity{
    private int position;
    private Turma turma;
    private TableLayout table;
    private ArrayList<Turma> listTurmas = new ArrayList<>();
    private ArrayList<EditText> listEditText = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nota);

        Bundle b = getIntent().getExtras();
        position = -1;
        if(b != null)
        {
            position = b.getInt("turma");
        }
        leDataBase();
        turma = listTurmas.get(position);
        turma.newProva();

        table = (TableLayout) findViewById(R.id.tableNota);
        criaTabela();

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Notas - Turma " + turma.nomeTurma);

        FloatingActionButton bt_FinalizaNota = (FloatingActionButton) findViewById(R.id.fabFinalizaNota);
        FloatingActionButton bt_CancelaNota = (FloatingActionButton) findViewById(R.id.fabCancelNota);


        bt_FinalizaNota.setOnClickListener(buttonClickListener);
        bt_CancelaNota.setOnClickListener(buttonClickListener);
    }
    private void criaTabela() {
        LayoutInflater inflater = LayoutInflater.from(this);
        String[] matriculas = turma.getAllMatriculas();
        TableRow row = (TableRow) inflater.inflate(R.layout.cabecalho_row, null);
        ((TextView) row.findViewById(R.id.nome_column)).setText("NOME");
        ((TextView) row.findViewById(R.id.mat_column)).setText("MATRICULA");
        ((TextView) row.findViewById(R.id.freq_column)).setText("NOTA");
        table.addView(row);
        boolean alterna = true;
        for (int i = 0; i < matriculas.length; i++) {
            String[] aluno = turma.getAluno(matriculas[i]);
            TableRow rowDinamico = (TableRow) inflater.inflate(R.layout.nota_row, null);
            if(alterna){
                rowDinamico.setBackgroundResource(R.color.cinzaClaro);
                alterna = false;
            }
            else alterna = true;
            ((TextView) rowDinamico.findViewById(R.id.nome_column)).setText(aluno[0]);
            ((TextView) rowDinamico.findViewById(R.id.mat_column)).setText(aluno[1]);
            EditText text = (EditText) rowDinamico.findViewById(R.id.nota_column);
            text.setInputType(InputType.TYPE_CLASS_NUMBER);
            text.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        if(Integer.parseInt(s.toString()) > 100)
                            s.replace(0, s.length(), "100");
                    }
                    catch(NumberFormatException nfe){}

                }
            });
            listEditText.add(text);
            table.addView(rowDinamico);
        }
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.fabFinalizaNota:
                    finishNota();
                    break;
                case R.id.fabCancelNota:
                    cancelaNota();
                    break;
            }
        }
    };

    private void finishNota(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Finalizar Avaliação");
        alert.setMessage("Tem certeza de que deseja finalizar a avaliação?");


        alert.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                for (int i=0;i<listEditText.size();i++){
                    String nota = listEditText.get(i).getText().toString();
                    if(!nota.equals("")){
                        turma.addNota(i,nota);
                    }
                    else turma.addNota(i,"0");
                }
                listTurmas.set(position,turma);
                escreveDataBase();
                finish();
            }
        });

        alert.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(NotaActivity.this,"Finalização de avaliação cancelada.", Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
        Button bConf = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        bConf.setTextColor(getResources().getColor(R.color.colorPrimary));
        Button bCancel = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        bCancel.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void cancelaNota(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Finalizar Avaliação");
        alert.setMessage("Tem certeza de que deseja cancelar a avaliação? Todas as notas inseridas serão perdidas");


        alert.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                finish();
            }
        });

        alert.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(NotaActivity.this,"Finalização de avaliação cancelada.", Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
        Button bConf = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        bConf.setTextColor(getResources().getColor(R.color.colorPrimary));
        Button bCancel = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        bCancel.setTextColor(getResources().getColor(R.color.colorPrimary));
    }


    private void atualizaView(){
        setContentView(R.layout.activity_nota);

        table = (TableLayout) findViewById(R.id.tableNota);
        criaTabela();

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Inserimento nota da turma de " + turma.nomeTurma);

        FloatingActionButton bt_FinalizaChamada = (FloatingActionButton) findViewById(R.id.fabFinalizaNota);
        FloatingActionButton bt_CancelaChamada = (FloatingActionButton) findViewById(R.id.fabCancelNota);


        bt_FinalizaChamada.setOnClickListener(buttonClickListener);
        bt_CancelaChamada.setOnClickListener(buttonClickListener);
    }

    private void leDataBase(){
        SharedPreferences mPrefs = getSharedPreferences("databaseAutoCall", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("dataCall", "");
        if(json != "")
            listTurmas = gson.fromJson(json,new TypeToken<ArrayList<Turma>>(){}.getType());
    }

    private void escreveDataBase(){
        SharedPreferences mPrefs = getSharedPreferences("databaseAutoCall", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(listTurmas);
        prefsEditor.putString("dataCall", json);
        prefsEditor.commit();
    }




}




