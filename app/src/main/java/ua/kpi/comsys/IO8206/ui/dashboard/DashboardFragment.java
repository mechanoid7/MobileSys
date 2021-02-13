package ua.kpi.comsys.IO8206.ui.dashboard;

import android.graphics.Color;
import android.media.audiofx.AudioEffect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ua.kpi.comsys.IO8206.R;

public class DashboardFragment extends Fragment {
    LineGraphSeries<DataPoint> series1;


//    private DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sec_tab, container, false);

        int dots = 100;
        double min = -5.0, max = 5.0, currentPointX=min, currentPointY, step;
        dots -= 1;
        DataPoint[] dataPoint = new DataPoint[dots+1];
        step = (max-min)/dots;

        for (int i = 0; i <= dots; i++) {
            currentPointY = currentPointX*currentPointX;
            dataPoint[i] = new DataPoint(currentPointX, currentPointY);
            currentPointX += step;
        }

        GraphView graph = (GraphView) root.findViewById(R.id.graph); // график
        PieChart pieChart = root.findViewById(R.id.pieChart); // круговая диаграмма

        graph.setFocusable(true);
        series1 = new LineGraphSeries<>(dataPoint);
        System.out.println(Arrays.toString(dataPoint));
        graph.addSeries(series1);

        graph.getViewport().setMinX(min-1); // установка границ
        graph.getViewport().setMaxX(max+1);
        graph.getViewport().setMaxY(max*max+5);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);


        pieChart.setUsePercentValues(true);
        Description desc = new Description();
        desc.setText("Variant 5");
        desc.setTextSize(20f);

        List<PieEntry> value = new ArrayList<>();
        value.add(new PieEntry(35f, "green"));
        value.add(new PieEntry(40f, "yellow"));
        value.add(new PieEntry(25f, "red"));

        int[] colors = {getResources().getColor(R.color.green), getResources().getColor(R.color.yellow), getResources().getColor(R.color.red)};

        PieDataSet pieDataSet = new PieDataSet(value, "Chart");
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);

        pieDataSet.setValueTextSize(15f);
        pieDataSet.setColors(colors);
        pieChart.setDescription(desc);

        return root;
    }
}