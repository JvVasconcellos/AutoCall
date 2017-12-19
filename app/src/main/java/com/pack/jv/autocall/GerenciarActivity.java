package com.pack.jv.autocall;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


/**
 * Created by João on 07/06/2017.
 */

public class GerenciarActivity extends AppCompatActivity {
    private NfcAdapter nfcAdapter;
    private boolean flag;
    private boolean flag2;
    private AlertDialog alert;
    private ViewPager viewPager;
    private TextView semTurmaText;
    private ArrayList<Turma> listTurmas = new ArrayList();
    private String nome, matricula;
    private Turma turma;
    private int turmaIndex;
    private String nomeAluno, matAluno;
    private static final int PICKFILE_RESULT_CODE = 1;

    private String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenciar);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout mDrawer = (DrawerLayout) findViewById(R.id.drawerlayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navView);
        navigationView.setItemIconTintList(null);
        semTurmaText = (TextView) findViewById(R.id.semturma_tv);



        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) findViewById(R.id.vPager);
        PagerAdapter adapter = new TurmaFragmentPagerAdapter(getSupportFragmentManager(), GerenciarActivity.this);
        viewPager.setAdapter(adapter);


        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        ActionBarDrawerToggle mToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.open, R.string.close);
        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();

        leDataBase();
        if(listTurmas.size() == 0) semTurmaText.setVisibility(View.VISIBLE);



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_name);
        getSupportActionBar().setTitle("Gerenciar Turmas");

        FloatingActionButton addFAB =(FloatingActionButton) findViewById(R.id.fab);
        addFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogName();

            }
        });





        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.chamada_item:
                        if(listTurmas.size() > 0){
                            Intent intent = new Intent(GerenciarActivity.this, ChamadaActivity.class);
                            Bundle b = new Bundle();
                            b.putInt("turma", viewPager.getCurrentItem());
                            intent.putExtras(b);
                            startActivity(intent);
                        }
                        else Toast.makeText(GerenciarActivity.this,"Por favor, adicione uma turma.",Toast.LENGTH_LONG);
                        return true;
                    case R.id.addAluno_item:
                        if(listTurmas.size() > 0){
                            addAlunoDialog();
                        }
                        else Toast.makeText(GerenciarActivity.this,"Por favor, adicione uma turma.",Toast.LENGTH_LONG);
                        return true;
                    case R.id.remove_item:
                        if(listTurmas.size() > 0){
                            removeTurma();
                        }
                        else Toast.makeText(GerenciarActivity.this,"Por favor, adicione uma turma.",Toast.LENGTH_LONG);
                        return true;
                    case R.id.sair_item:
                        finish();
                        /*int turmaIndex = viewPager.getCurrentItem();
                        leDataBase();
                        Turma turma = listTurmas.get(turmaIndex);
                        for(int dia=1; dia<15; dia++){
                            Random random = new Random();
                            turma.addData(dia);
                            int nPres = random.nextInt(25-20+1) + 20;
                            int i = 1;
                            while (i <= nPres){
                                int pos = random.nextInt(25+1);
                                turma.addPresenca(pos);
                                i++;
                            }
                            turma.finalizaChamada();
                        }
                        for (int ava= 1; ava <=3; ava++)
                        {
                            Random random = new Random();
                            turma.newProva();
                            for(int aluno = 0; aluno < turma.getAllMatriculas().length; aluno++){
                                turma.addNota(aluno, Integer.toString(random.nextInt(100-40+1) + 40));
                            }

                        }
                        listTurmas.set(turmaIndex,turma);
                        escreveDataBase();
                        viewPager.getAdapter().notifyDataSetChanged();*/
                        return true;
                    case R.id.planilha_item:
                        if(listTurmas.size() > 0){
                            dialogPlanilha();
                        }
                        else Toast.makeText(GerenciarActivity.this,"Por favor, adicione uma turma.",Toast.LENGTH_LONG);
                        return true;
                    case R.id.nota_item:
                        if(listTurmas.size() > 0){
                            Intent intent1 = new Intent(GerenciarActivity.this, NotaActivity.class);
                            Bundle b1 = new Bundle();
                            b1.putInt("turma", viewPager.getCurrentItem());
                            intent1.putExtras(b1);
                            startActivity(intent1);
                        }
                        else Toast.makeText(GerenciarActivity.this,"Por favor, adicione uma turma.",Toast.LENGTH_LONG);
                        return true;
                    case R.id.analises_item:
                        if(listTurmas.size() > 0){
                            Intent intent2 = new Intent(GerenciarActivity.this, AnalisesActivity.class);
                            Bundle b2 = new Bundle();
                            b2.putInt("turma", viewPager.getCurrentItem());
                            intent2.putExtras(b2);
                            startActivity(intent2);
                        }
                        else Toast.makeText(GerenciarActivity.this,"Por favor, adicione uma turma.",Toast.LENGTH_LONG);
                        return true;
                    case R.id.cadastrar_item:
                        dialogCadastraAluno();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }


    @Override
    protected void onResume(){
        super.onResume();

        Intent intent = new Intent(this,GerenciarActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        IntentFilter[] intentFilter = new IntentFilter[]{};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilter, null);

        viewPager.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onPause(){
        super.onPause();

        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();
        if(action.equals(NfcAdapter.ACTION_TAG_DISCOVERED)){
            if(flag) {
                String ID = AES.toHex(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID));
                alert.dismiss();
                try {
                    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                    String data = NFCRW.nfcRead(tag, ID);

                    if (data == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(GerenciarActivity.this);
                        builder.setTitle("Adicionar Aluno");
                        builder.setMessage("Cartão não cadastrado!");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                flag = false;
                            }
                        });
                        alert = builder.create();
                        alert.show();
                        Button bConf = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                        bConf.setTextColor(getResources().getColor(R.color.colorPrimary));
                    } else {
                        int index, aux;
                        index = data.indexOf('%');
                        if (data.substring(0, index).equals("AUTOCALLAPP")) {
                            aux = index + 1;
                            index = data.indexOf('%', aux);
                            nome = data.substring(aux, index);
                            matricula = data.substring(index + 1, data.length());
                            leDataBase();
                            turma = listTurmas.get(viewPager.getCurrentItem());
                            if(turma.hasAluno(matricula)){
                                AlertDialog.Builder builder = new AlertDialog.Builder(GerenciarActivity.this);
                                builder.setTitle("Adicionar Aluno");
                                builder.setMessage("Este aluno já está cadastrado nessa turma.");
                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        flag = false;
                                    }
                                });
                                alert = builder.create();
                                alert.show();
                                Button bConf = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                                bConf.setTextColor(getResources().getColor(R.color.colorPrimary));

                            }
                            else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(GerenciarActivity.this);
                                builder.setTitle("Adicionar Aluno");
                                builder.setMessage("Aluno a ser adicionado à turma " + turma.nomeTurma + ":\nNome: " + nome + "\nMatricula: " + matricula);
                                builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        flag = false;
                                        turma.addAluno(nome, matricula);
                                        listTurmas.set(turmaIndex,turma);
                                        escreveDataBase();
                                        viewPager.getAdapter().notifyDataSetChanged();
                                        alert.dismiss();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(GerenciarActivity.this);
                                        builder.setTitle("Adicionar Aluno");
                                        builder.setMessage("Aluno adicionado com sucesso!");
                                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {

                                            }
                                        });
                                        alert = builder.create();
                                        alert.show();
                                        Button bConf = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                                        bConf.setTextColor(getResources().getColor(R.color.colorPrimary));

                                    }
                                });
                                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        flag = false;
                                        alert.cancel();
                                    }
                                });
                                alert = builder.create();
                                alert.show();
                                Button bConf = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                                bConf.setTextColor(getResources().getColor(R.color.colorPrimary));
                                Button bCancel = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                                bCancel.setTextColor(getResources().getColor(R.color.colorPrimary));

                            }




                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(GerenciarActivity.this);
                            builder.setTitle("Adicionar Aluno");
                            builder.setMessage("Cartão não cadastrado!");
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    flag = false;
                                }
                            });
                            alert = builder.create();
                            alert.show();
                            Button bConf = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                            bConf.setTextColor(getResources().getColor(R.color.colorPrimary));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /*AlertDialog.Builder builder = new AlertDialog.Builder(GerenciarActivity.this);
                builder.setTitle("Adicionar Aluno");
                builder.setMessage("Cartão não cadastrado!");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        flag= false;
                    }
                });
                alert = builder.create();
                alert.show();
                Button bConf = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                bConf.setTextColor(getResources().getColor(R.color.colorPrimary));*/

            }
            else if(flag2){
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                String ID = AES.toHex(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID));
                alert.dismiss();
                try{
                    String msg = "AUTOCALLAPP%" + nomeAluno + "%" + matAluno;
                    boolean worked = NFCRW.nfcWrite(msg,tag,ID);
                    if(worked){
                        flag2 = false;
                        dialogSuccess();
                    }
                    else {
                        dialogInfo("Erro ao gravar. Tente novamente.");
                    }


                }
                catch (Exception e) {
                    dialogInfo("Erro ao gravar. Tente novamente.");
                    e.printStackTrace();
                }
            }
        }


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case PICKFILE_RESULT_CODE:
                if(resultCode == RESULT_OK){
                    try {
                        Uri uri = data.getData();
                        leDataBase();
                        turmaIndex = viewPager.getCurrentItem();
                        turma = listTurmas.get(turmaIndex);
                        turma = CSVRW.parseCSV(uri, turma, GerenciarActivity.this);
                        listTurmas.set(turmaIndex,turma);
                        escreveDataBase();
                        viewPager.getAdapter().notifyDataSetChanged();
                        Toast.makeText(GerenciarActivity.this,"Alunos adicionados com sucesso!",Toast.LENGTH_LONG).show();
                    }
                    catch(IOException e){
                        Toast.makeText(GerenciarActivity.this,"Erro ao adicionar turma!",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    catch (Exception e){
                        Toast.makeText(GerenciarActivity.this, "Planilha não corresponde à FAE.", Toast.LENGTH_LONG).show();
                    }

                    break;
                }
        }
    }

    private void dialogName(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Nome da Turma:");
        alert.setMessage("Digite o nome da turma a ser adicionada.");


        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String nomeTurma = input.getText().toString();
                if(!nomeTurma.equals("")){
                    Turma turma = new Turma(nomeTurma);
                    leDataBase();
                    listTurmas.add(turma);
                    escreveDataBase();
                    semTurmaText.setVisibility(View.INVISIBLE);
                    viewPager.getAdapter().notifyDataSetChanged();
                    Toast.makeText(GerenciarActivity.this, "Turma adicionada com sucesso!",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(GerenciarActivity.this,"Nome da turma vazio.",Toast.LENGTH_LONG).show();
                }


            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(GerenciarActivity.this,"Criação de turma cancelada.",Toast.LENGTH_LONG).show();
                // Canceled.

            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
        Button bConf = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        bConf.setTextColor(getResources().getColor(R.color.colorPrimary));
        Button bCancel = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        bCancel.setTextColor(getResources().getColor(R.color.colorPrimary));

    }

    private void addAlunoDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] items = {"CARTÃO","MANUAL","FAE"};
        builder.setTitle("Adicionar Aluno")
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                flag = true;
                                infoDialog();
                                break;
                            case 1:
                                addAlunoManual();
                                break;
                            case 2:
                                chooseFile();
                                break;
                        }
                    }
                });
        alert = builder.create();
        alert.show();
    }

    private void infoDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(GerenciarActivity.this);
        builder.setTitle("Adicionar Aluno");
        leDataBase();
        turmaIndex = viewPager.getCurrentItem();
        turma = listTurmas.get(turmaIndex);
        builder.setMessage("Aproxime o cartão para adicionar o aluno à turma " + turma.nomeTurma + ".");
        alert = builder.create();
        alert.show();
    }

    private void addAlunoManual(){
        leDataBase();
        turmaIndex = viewPager.getCurrentItem();
        turma = listTurmas.get(turmaIndex);

        AlertDialog.Builder builder = new AlertDialog.Builder(GerenciarActivity.this);
        builder.setTitle("Adicionar Aluno");
        builder.setMessage("Digite o nome e a matricula do aluno que você deseja adicionar na turma " + turma.nomeTurma + ":");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText nomeBox = new EditText(this);
        nomeBox.setHint("Nome");
        layout.addView(nomeBox);

        final EditText matriculaBox = new EditText(this);
        matriculaBox.setHint("Matricula");
        layout.addView(matriculaBox);

        builder.setView(layout);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String nomeAluno = nomeBox.getText().toString();
                String matAluno = matriculaBox.getText().toString();
                if(nomeAluno.equals("") || matAluno.equals("")){
                    Toast.makeText(GerenciarActivity.this, "Campo(s) de nome e/ou matricula vazio(s).", Toast.LENGTH_LONG).show();
                }
                else if(turma.hasAluno(matAluno)){
                    Toast.makeText(GerenciarActivity.this,"Aluno já cadastrado nessa turma!",Toast.LENGTH_LONG).show();
                }
                else{
                    turma.addAluno(nomeAluno,matAluno);
                    listTurmas.set(turmaIndex,turma);
                    escreveDataBase();
                    viewPager.getAdapter().notifyDataSetChanged();
                    Toast.makeText(GerenciarActivity.this,"Aluno adicionado com sucesso!",Toast.LENGTH_LONG).show();
                }


            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(GerenciarActivity.this,"Adição de aluno cancelada.",Toast.LENGTH_LONG).show();
                // Canceled.

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        Button bConf = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        bConf.setTextColor(getResources().getColor(R.color.colorPrimary));
        Button bCancel = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        bCancel.setTextColor(getResources().getColor(R.color.colorPrimary));

    }

    private void chooseFile() {
        Intent intent = new Intent();
        intent.setType("text/csv");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Escolha a planilha da FAE"), PICKFILE_RESULT_CODE);
    }

    private void removeTurma(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        turmaIndex = viewPager.getCurrentItem();
        leDataBase();
        turma = listTurmas.get(turmaIndex);

        alert.setTitle("Remover Turma");
        alert.setMessage("Tem certeza de que deseja remover a turma de " + turma.nomeTurma +  "? Todos os dados armazenados para essa turma serão perdidos.");


        alert.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                listTurmas.remove(turmaIndex);
                escreveDataBase();
                viewPager.getAdapter().notifyDataSetChanged();
                if(listTurmas.size() == 0)semTurmaText.setVisibility(View.VISIBLE);
            }
        });

        alert.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(GerenciarActivity.this,"Remoção de turma cancelada", Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();
        Button bConf = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        bConf.setTextColor(getResources().getColor(R.color.colorPrimary));
        Button bCancel = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        bCancel.setTextColor(getResources().getColor(R.color.colorPrimary));

    }

    private void dialogPlanilha(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        turmaIndex = viewPager.getCurrentItem();
        leDataBase();
        turma = listTurmas.get(turmaIndex);

        alert.setTitle("Gerar Planilha");
        alert.setMessage("Deseja gerar a planilha de frequência da turma de " + turma.nomeTurma +  "?");


        alert.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try{
                    String fileName = "Planilha da Turma de " + turma.nomeTurma +".csv";
                    String filePath = baseDir + File.separator + fileName;
                    CSVRW.visualizador(filePath,turma,GerenciarActivity.this);
                    File csvFile = new File(filePath);
                    Intent intent2 = new Intent();
                    intent2.setAction(android.content.Intent.ACTION_VIEW);
                    intent2.setDataAndType(Uri.fromFile(csvFile), "text/csv");
                    startActivity(intent2);
                }
                catch (Exception e){
                    Toast.makeText(GerenciarActivity.this,"Arquivo não existe.",Toast.LENGTH_LONG).show();
                }
            }
        });

        alert.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(GerenciarActivity.this,"Geraçao de planilha cancelada.", Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();
        Button bConf = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        bConf.setTextColor(getResources().getColor(R.color.colorPrimary));
        Button bCancel = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        bCancel.setTextColor(getResources().getColor(R.color.colorPrimary));

    }

    private void dialogCadastraAluno(){
        AlertDialog.Builder builder = new AlertDialog.Builder(GerenciarActivity.this);
        builder.setTitle("Cadastrar Aluno");
        builder.setMessage("Digite o nome e a matricula do aluno que você deseja cadastrar:");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText nomeBox = new EditText(this);
        nomeBox.setHint("Nome");
        layout.addView(nomeBox);

        final EditText matriculaBox = new EditText(this);
        matriculaBox.setHint("Matricula");
        layout.addView(matriculaBox);

        builder.setView(layout);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                nomeAluno = nomeBox.getText().toString();
                matAluno = matriculaBox.getText().toString();
                if(nomeAluno.equals("") || matAluno.equals("")){
                    Toast.makeText(GerenciarActivity.this, "Campo(s) de nome e/ou matricula vazio(s).", Toast.LENGTH_LONG).show();
                }
                else{
                    flag2 = true;
                    dialogInfo("Aproxime o cartão para cadastrar o aluno.");
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(GerenciarActivity.this,"Cadastro de aluno cancelado.",Toast.LENGTH_LONG).show();
                // Canceled.

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        Button bConf = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        bConf.setTextColor(getResources().getColor(R.color.colorPrimary));
        Button bCancel = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        bCancel.setTextColor(getResources().getColor(R.color.colorPrimary));

    }

    private void dialogInfo(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(GerenciarActivity.this);
        builder.setTitle("Cadastrar Aluno");
        leDataBase();
        builder.setMessage(message);
        alert = builder.create();
        alert.show();
    }

    private void dialogSuccess(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Cadastrar Aluno");
        alert.setMessage("Cadastro efetuado com sucesso!");


        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {


            }
        });

        AlertDialog dialog = alert.create();
        dialog.show();
        Button bConf = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        bConf.setTextColor(getResources().getColor(R.color.colorPrimary));
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
