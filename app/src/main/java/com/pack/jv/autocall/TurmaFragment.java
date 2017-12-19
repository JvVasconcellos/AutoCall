package com.pack.jv.autocall;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Created by João on 08/06/2017.
 */

public class TurmaFragment extends Fragment {
    public static final String ARG_TURMA = "ARG_TURMA";
    private Turma pageTurma;


    public static TurmaFragment newInstance(Turma turma) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TURMA, turma);
        TurmaFragment fragment = new TurmaFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageTurma = (Turma) getArguments().getSerializable(ARG_TURMA);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.turma_fragment, container, false);
        TableLayout table = (TableLayout) view.findViewById(R.id.table);
        String[] matriculas = pageTurma.getAllMatriculas();
        TableRow row = (TableRow) inflater.inflate(R.layout.cabecalho_row,null);
        ((TextView)row.findViewById(R.id.nome_column)).setText("NOME");
        ((TextView)row.findViewById(R.id.mat_column)).setText("MATRICULA");
        ((TextView)row.findViewById(R.id.freq_column)).setText("FREQ.");
        table.addView(row);
        boolean alterna = true;
        for(int i=0; i<matriculas.length;i++){
            String[] aluno = pageTurma.getAluno(matriculas[i]);
            TableRow rowDinamico = (TableRow) LayoutInflater.from(getActivity()).inflate(R.layout.turma_row,null);
            ((TextView)rowDinamico.findViewById(R.id.nome_column)).setText(aluno[0]);
            ((TextView)rowDinamico.findViewById(R.id.mat_column)).setText(aluno[1]);
            ((TextView)rowDinamico.findViewById(R.id.freq_column)).setText(Integer.toString(pageTurma.getFreqRate(aluno)) + "%");
            if(alterna){
                rowDinamico.setBackgroundResource(R.color.cinzaClaro);
                alterna = false;
            }
            else alterna = true;
            table.addView(rowDinamico);
        }
        return view;
    }
}


