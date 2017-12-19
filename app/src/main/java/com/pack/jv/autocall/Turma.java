package com.pack.jv.autocall;

import android.util.Size;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;


/**
 * Created by João on 02/06/2017.
 */

public class Turma implements Serializable{

    public String nomeTurma;
    private ArrayList<String[]> alunosPresenca = new ArrayList();
    private ArrayList<String[]> alunosNota = new ArrayList();
    private String[] data = new String[0];
    private int nProvas = 0;

    public Turma(String nomeTurma){
        this.nomeTurma = nomeTurma;
    }

    public ArrayList<String[]> getTurmaPresenca(){
        return alunosPresenca;
    }

    public ArrayList<String[]> getTurmaNota(){
        return alunosNota;
    }

    public String[] getData() {return data;}

    public String[] getAllMatriculas(){
        String[] allMatriculas = new String[alunosPresenca.size()];
        for(int i=0; i<alunosPresenca.size(); i++){
            String[] aluno = alunosPresenca.get(i);
            allMatriculas[i] = aluno[1];
        }
        return allMatriculas;
    }

    public int getNProvas(){return  nProvas;}

    public String[] getAluno(String alunoMatricula) {
        alunoMatricula = alunoMatricula.toUpperCase();
        for(int i=alunosPresenca.size()-1; i >= 0; i--){
            String [] aluno = alunosPresenca.get(i);
            if (aluno[1].equals(alunoMatricula)){
                return aluno;
            }
        }
        return null;
    }

    public int getFreqRate(String [] aluno){
        float freqNum = 0;
        for(int i=2; i<aluno.length; i++){
            if(aluno[i].equals("P")) freqNum++;
        }
        return (int) (freqNum *(100./(aluno.length -2.)));
    }

    public int getNFreq(String [] aluno){
        int freqNum = 0;
        for(int i=2; i<aluno.length; i++){
            if(aluno[i].equals("P")) freqNum++;
        }
        return freqNum;
    }

    public int getAlunoMedia(String [] aluno){
        float cont = 0;
        float nota = 0;
        for(int i=2; i<aluno.length; i++){
            nota += Float.parseFloat(aluno[i]);
            cont++;
        }
        if(cont >= 1) return (int) (nota/cont);
        return 0;
    }

    public int getMediaByFreq(int nFreq){
        float cont = 0;
        float nota = 0;
        for (int i = 0; i< alunosNota.size(); i++){
            String[] alunoNota = alunosNota.get(i);
            String[] alunoPres = alunosPresenca.get(i);
            if(getNFreq(alunoPres) == nFreq){
                nota += (float) getAlunoMedia(alunoNota);
                cont++;
            }
        }
        if (cont >= 1)return (int) (nota/cont);
        return 0;
    }


    public int getNPresentes (int pos){
        int cont = 0;
        for(int i = 0; i < alunosPresenca.size(); i++){
            String[] aluno = alunosPresenca.get(i);
            if(aluno[pos+2].equals("P")) cont++;
        }
        return cont;
    }

    public void addAluno(String nome, String matricula){
        String [] aluno = {nome.toUpperCase(), matricula.toUpperCase()};
        alunosPresenca.add(aluno);
        alunosNota.add(aluno);
    }

    public boolean hasAluno(String alunoMatricula){
        alunoMatricula = alunoMatricula.toUpperCase();
        for(int i=alunosPresenca.size()-1; i >= 0; i--){
            String [] aluno = alunosPresenca.get(i);
            if (aluno[1].equals(alunoMatricula)){
                return true;
            }
        }
        return false;
    }


    public void addData(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format1 =  new SimpleDateFormat("dd/MM/yy");
        String date = format1.format(cal.getTime());
        data = Arrays.copyOf(data, data.length+1);
        data[data.length - 1] = date;
    }

    public void addData(int dia){
        data = Arrays.copyOf(data, data.length+1);
        data[data.length - 1] = Integer.toString(dia) +"/06/17";
    }

    public void addPresenca(int pos){
        if (pos<alunosPresenca.size()){
            String[] aluno = alunosPresenca.get(pos);
            aluno = Arrays.copyOf(aluno, data.length+2);
            aluno[data.length + 1] = "P";
            alunosPresenca.set(pos,aluno);
        }
    }





    public void addPresenca(String alunoMatricula){
        alunoMatricula = alunoMatricula.toUpperCase();
        for(int i=alunosPresenca.size()-1; i >= 0; i--){
            String [] aluno = alunosPresenca.get(i);
            if (aluno[1].equals(alunoMatricula)){
                aluno = Arrays.copyOf(aluno, data.length+2);
                aluno[data.length + 1] = "P";
                alunosPresenca.set(i,aluno);
            }
        }
    }

    public void addNota(int pos, String nota){
        String [] aluno = alunosNota.get(pos);
        aluno = Arrays.copyOf(aluno, nProvas+2);
        aluno[nProvas + 1] = nota;
        alunosNota.set(pos,aluno);
    }

    public void newProva(){nProvas++;}


    public void finalizaChamada(){
        for(int i=alunosPresenca.size()-1; i >= 0; i--){
            String[]aluno = alunosPresenca.get(i);
            if (aluno.length != data.length+2){
                aluno = Arrays.copyOf(aluno, data.length+2);
                aluno[data.length + 1] = "-";
                alunosPresenca.set(i,aluno);
            }
        }
    }
}




