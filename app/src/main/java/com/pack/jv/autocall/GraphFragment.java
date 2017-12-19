package com.pack.jv.autocall;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

/**
 * Created by João on 21/06/2017.
 */

public class GraphFragment extends Fragment {
    public static final String ARG_TURMA = "ARG_TURMA";
    public static final String ARG_PAGE = "ARG_PAGE";
    private Turma pageTurma;
    private int nPage;


    public static GraphFragment newInstance(Turma turma, int page) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TURMA, turma);
        args.putInt(ARG_PAGE, page);
        GraphFragment fragment = new GraphFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageTurma = (Turma) getArguments().getSerializable(ARG_TURMA);
        nPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        switch (nPage){
            case 0:
                View view = inflater.inflate(R.layout.graph_fragment, container, false);
                GraphView graph = (GraphView) view.findViewById(R.id.graph);
                TextView title = (TextView) view.findViewById(R.id.textTitle);
                title.setText("Gráfico de Análise de Frequência: (Nº de presentes x Data)");
                int tam = pageTurma.getData().length;
                DataPoint[] data = new DataPoint[tam];
                for(int i = 0; i < tam; i++){
                    data[i] = new DataPoint(i + 1,pageTurma.getNPresentes(i));
                }
                BarGraphSeries<DataPoint> series = new BarGraphSeries<>(data);
                series.setColor(getResources().getColor(R.color.colorPrimary));
                series.setAnimated(true);
                graph.addSeries(series);
                series.setSpacing(50);
                graph.getGridLabelRenderer().setHighlightZeroLines( true );
                graph.getGridLabelRenderer().setVerticalLabelsVisible( true );
                graph.getGridLabelRenderer().setGridColor(getResources().getColor(R.color.cinzaClaro));
                graph.getGridLabelRenderer().setVerticalLabelsColor(getResources().getColor(R.color.cinzaEscuro));
                graph.getGridLabelRenderer().setHorizontalLabelsColor(getResources().getColor(R.color.cinzaEscuro));
                int numX = tam+1;
                int numY = pageTurma.getAllMatriculas().length;
                if(numY >= 15)
                    while (numY % 10 != 0) numY++;
                graph.getViewport().setXAxisBoundsManual(true);
                graph.getViewport().setMinX(0);
                graph.getViewport().setMaxX(numX);
                graph.getViewport().setYAxisBoundsManual(true);
                graph.getViewport().setMinY(0);
                graph.getViewport().setMaxY(numY);
                graph.getGridLabelRenderer().setHumanRounding(false);
                graph.getGridLabelRenderer().setNumHorizontalLabels(numX + 1);
                if(numY >=15)
                    graph.getGridLabelRenderer().setNumVerticalLabels(11);
                else
                    graph.getGridLabelRenderer().setNumVerticalLabels(numY + 1);
                /*graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
                    @Override
                    public String formatLabel(double value, boolean isValueX) {
                        if (isValueX) {
                            if(value % 1 == 0 && value != 0 && value <= pageTurma.getData().length){
                                int i = (int) value;
                                String date = pageTurma.getData()[i - 1];
                                return date;
                            }
                            return "";

                        } else {
                            return super.formatLabel(value, isValueX);
                        }
                    }
                });*/

                return view;
            case 1:
                View view1 = inflater.inflate(R.layout.graph_fragment, container, false);
                GraphView graph1 = (GraphView) view1.findViewById(R.id.graph);
                TextView title1 = (TextView) view1.findViewById(R.id.textTitle);
                title1.setText("Relação Frequência - Nota (Nota Média x N° de presenças)");
                int tam1 = pageTurma.getData().length;
                DataPoint[] data1 = new DataPoint[tam1];
                for(int i = 0; i < tam1; i++){
                    data1[i] = new DataPoint(i,pageTurma.getMediaByFreq(i));
                }
                BarGraphSeries<DataPoint> series1 = new BarGraphSeries<>(data1);
                series1.setColor(getResources().getColor(R.color.colorPrimary));
                graph1.addSeries(series1);
                series1.setSpacing(50);
                series1.setAnimated(true);
                graph1.getGridLabelRenderer().setHighlightZeroLines( true );
                graph1.getGridLabelRenderer().setVerticalLabelsVisible( true );
                graph1.getGridLabelRenderer().setGridColor(getResources().getColor(R.color.cinzaClaro));
                graph1.getGridLabelRenderer().setVerticalLabelsColor(getResources().getColor(R.color.cinzaEscuro));
                graph1.getGridLabelRenderer().setHorizontalLabelsColor(getResources().getColor(R.color.cinzaEscuro));
                int nX = tam1+1;
                int nY = 100;
                graph1.getViewport().setXAxisBoundsManual(true);
                graph1.getViewport().setMinX(0);
                graph1.getViewport().setMaxX(nX);
                graph1.getViewport().setYAxisBoundsManual(true);
                graph1.getViewport().setMinY(0);
                graph1.getViewport().setMaxY(nY);
                graph1.getGridLabelRenderer().setHumanRounding(false);
                graph1.getGridLabelRenderer().setNumHorizontalLabels(nX + 1);
                graph1.getGridLabelRenderer().setNumVerticalLabels(11);
                return view1;
            default:
                return null;

        }

    }
}
