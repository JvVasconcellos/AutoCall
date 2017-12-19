package com.pack.jv.autocall;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by João on 18/06/2017.
 */

public class ChamadaActivity extends AppCompatActivity {
    private TableLayout table;
    private Turma turma;
    private NfcAdapter nfcAdapter;
    private ArrayList<Turma> listTurmas = new ArrayList<>();
    private String nome,matricula;
    private int position;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chamada);

        Bundle b = getIntent().getExtras();
        position = -1;
        if(b != null)
        {
            position = b.getInt("turma");
        }
        leDataBase();
        turma = listTurmas.get(position);
        turma.addData();

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        table = (TableLayout) findViewById(R.id.tableChamada);
        criaTabela();

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Chamada - Turma " + turma.nomeTurma);

        FloatingActionButton bt_FinalizaChamada = (FloatingActionButton) findViewById(R.id.fabFinaliza);
        FloatingActionButton bt_CancelaChamada = (FloatingActionButton) findViewById(R.id.fabCancel);


        bt_FinalizaChamada.setOnClickListener(buttonClickListener);
        bt_CancelaChamada.setOnClickListener(buttonClickListener);
    }

    @Override
    protected void onResume(){
        super.onResume();

        Intent intent = new Intent(this,ChamadaActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        IntentFilter[] intentFilter = new IntentFilter[]{};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilter, null);
    }

    @Override
    protected void onPause(){
        super.onPause();

        nfcAdapter.disableForegroundDispatch(this);
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.fabFinaliza:
                    finishChamada();
                    break;
                case R.id.fabCancel:
                    cancelaChamada();
                    break;
            }
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String ID = AES.toHex(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID));
        String action = intent.getAction();
        if (action.equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            //Toast.makeText(ChamadaActivity.this,"Cartão não cadastrado!",Toast.LENGTH_LONG).show();
        //} else if (action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {
            try {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                String data = NFCRW.nfcRead(tag, ID);

                if (data == null) {
                    Toast.makeText(ChamadaActivity.this,"Cartão não cadastrado!",Toast.LENGTH_LONG).show();

                } else {
                    int index, aux;
                    index = data.indexOf('%');
                    if (data.substring(0, index).equals("AUTOCALLAPP")) {
                        aux = index + 1;
                        index = data.indexOf('%', aux);
                        nome = data.substring(aux, index);
                        matricula = data.substring(index + 1, data.length());
                        leDataBase();
                        if(turma.hasAluno(matricula)){
                            turma.addPresenca(matricula);
                            Toast.makeText(ChamadaActivity.this,nome + " está presente!",Toast.LENGTH_LONG).show();
                            atualizaView();
                        }
                        else{
                            Toast.makeText(ChamadaActivity.this, nome + " não está cadastrado nessa turma.", Toast.LENGTH_LONG).show();

                        }
                    } else {
                        Toast.makeText(ChamadaActivity.this,"Cartão não cadastrado!",Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                Toast.makeText(ChamadaActivity.this,"Cartão não cadastrado!",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    private void finishChamada(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Finalizar Chamada");
        alert.setMessage("Tem certeza de que deseja finalizar a chamada?");


        alert.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                turma.finalizaChamada();
                listTurmas.set(position,turma);
                escreveDataBase();
                finish();
            }
        });

        alert.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ChamadaActivity.this,"Finalização de chamada cancelada.", Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
        Button bConf = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        bConf.setTextColor(getResources().getColor(R.color.colorPrimary));
        Button bCancel = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        bCancel.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void cancelaChamada(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Finalizar Chamada");
        alert.setMessage("Tem certeza de que deseja cancelar a chamada? Todas as presenças registradas serão perdidas");


        alert.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                finish();
            }
        });

        alert.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ChamadaActivity.this,"Finalização de chamada cancelada.", Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
        Button bConf = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        bConf.setTextColor(getResources().getColor(R.color.colorPrimary));
        Button bCancel = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        bCancel.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void criaTabela() {
        LayoutInflater inflater = LayoutInflater.from(this);
        String[] matriculas = turma.getAllMatriculas();
        TableRow row = (TableRow) inflater.inflate(R.layout.cabecalho_row, null);
        ((TextView) row.findViewById(R.id.nome_column)).setText("NOME");
        ((TextView) row.findViewById(R.id.mat_column)).setText("MATRICULA");
        ((TextView) row.findViewById(R.id.freq_column)).setText("PRES.");
        table.addView(row);
        boolean alterna = true;
        for (int i = 0; i < matriculas.length; i++) {
            String[] aluno = turma.getAluno(matriculas[i]);
            TableRow rowDinamico = (TableRow) inflater.inflate(R.layout.turma_row, null);
            ((TextView) rowDinamico.findViewById(R.id.nome_column)).setText(aluno[0]);
            ((TextView) rowDinamico.findViewById(R.id.mat_column)).setText(aluno[1]);
            if(turma.getData().length + 2 == aluno.length)((TextView) rowDinamico.findViewById(R.id.freq_column)).setText(aluno[turma.getData().length + 1]);
            if(alterna){
                rowDinamico.setBackgroundResource(R.color.cinzaClaro);
                alterna = false;
            }
            else alterna = true;
            table.addView(rowDinamico);
        }
    }

    private void atualizaView(){
        setContentView(R.layout.activity_chamada);

        table = (TableLayout) findViewById(R.id.tableChamada);
        criaTabela();

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Chamada - Turma " + turma.nomeTurma);

        FloatingActionButton bt_FinalizaChamada = (FloatingActionButton) findViewById(R.id.fabFinaliza);
        FloatingActionButton bt_CancelaChamada = (FloatingActionButton) findViewById(R.id.fabCancel);


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
