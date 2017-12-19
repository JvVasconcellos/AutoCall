package com.pack.jv.autocall;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by João on 16/04/2017.
 */

public class CSVRW {
    public static void deleteLastRow(String filePath) throws IOException{
        CSVReader reader2 = new CSVReader(new FileReader(filePath), ';');
        List<String[]> allElements = reader2.readAll();
        allElements.remove(allElements.size() - 1);
        FileWriter sw = new FileWriter(filePath);
        CSVWriter writer = new CSVWriter(sw, ';');
        writer.writeAll(allElements);
        writer.close();
        reader2.close();

    }

    public static Turma parseCSV(Uri uri, Turma turma, Activity activity) throws Exception{
        CSVReader reader = new CSVReader(new InputStreamReader(new BufferedInputStream(activity.getContentResolver().openInputStream(uri)), "ISO-8859-1"), ';');
        String [] nextLine;
        int cont = 0;
        while ((nextLine = reader.readNext()) != null) {
            // nextLine[] is an array of values from the line
            if(cont==0 && !nextLine[0].equals("Universidade Federal de Juiz de Fora - UFJF")) throw new Exception();
            else if(cont > 4){
                int index = nextLine[0].indexOf(' ');
                String matricula = nextLine[0].substring(0,index);
                if(!turma.hasAluno(matricula)) turma.addAluno(nextLine[1], matricula);
            }
            cont++;
        }
        return turma;
    }

    public static void visualizador(String filePath, Turma turma, Activity activity) throws IOException{
        verifyStoragePermissions(activity);
        OutputStreamWriter sw = new OutputStreamWriter(new FileOutputStream(filePath), "ISO-8859-1");
        List<String[]> allElements = turma.getTurmaPresenca();
        CSVWriter writer = new CSVWriter(sw, ';');
        String[] cabecalho = {"TURMA:",turma.nomeTurma};
        writer.writeNext(cabecalho);
        String[] vazio = {};
        writer.writeNext(vazio);
        writer.writeNext(vazio);
        writer.writeNext(vazio);
        String[] dateLine = new String[turma.getData().length + 2];
        System.arraycopy(turma.getData(),0,dateLine,2,turma.getData().length);
        dateLine[0] = "NOME";
        dateLine[1] = "MATRICULA";
        writer.writeNext(dateLine);
        writer.writeAll(allElements);
        List<String[]> notas = turma.getTurmaNota();
        String[] notaLine = new String[turma.getNProvas() + 2];
        notaLine[0] = "NOME";
        notaLine[1] = "MATRICULA";
        for (int i = 0; i < turma.getNProvas(); i++){
            notaLine[i+2] = "AVALIAÇÃO " + Integer.toString(i+1);
        }
        writer.writeNext(vazio);
        writer.writeNext(vazio);
        writer.writeNext(vazio);
        writer.writeNext(notaLine);
        writer.writeAll(notas);
        writer.close();

    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
