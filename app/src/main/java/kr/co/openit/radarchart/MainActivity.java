package kr.co.openit.radarchart;

import android.graphics.Color;
import android.os.Bundle;

import java.util.ArrayList;

import kr.co.openit.radarchart.animation.Easing;
import kr.co.openit.radarchart.charts.RadarChart;
import kr.co.openit.radarchart.components.AxisBase;
import kr.co.openit.radarchart.components.Legend;
import kr.co.openit.radarchart.components.MarkerView;
import kr.co.openit.radarchart.components.XAxis;
import kr.co.openit.radarchart.components.YAxis;
import kr.co.openit.radarchart.data.RadarData;
import kr.co.openit.radarchart.data.RadarDataSet;
import kr.co.openit.radarchart.data.RadarEntry;
import kr.co.openit.radarchart.formatter.IAxisValueFormatter;
import kr.co.openit.radarchart.interfaces.datasets.IRadarDataSet;

public class MainActivity extends BaseActivity {

    private RadarChart mChart;

    private ArrayList<RadarEntry> entries2;

//    private String[] mActivities;
//        private String[] mActivities = new String[] {"칼로리 소모량", "걸음수", "물 섭취량", "칼로리 섭취량", "수면 점수"};
//    private String[] mActivities = new String[] {"칼로리 소모량", "걸음수", "물 섭취량", "칼로리 섭취량"};
    private String[] mActivities = new String[] {"칼로리 소모량", "걸음수", "물 섭취량"};

//    private String[] mActivities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        entries2 = new ArrayList<RadarEntry>();

        mChart = (RadarChart)findViewById(R.id.chart1);

        mChart.getDescription().setEnabled(false);

        mChart.setWebLineWidth(1f);
        mChart.setWebColor(Color.LTGRAY);
        mChart.setWebLineWidthInner(1f);
        mChart.setWebColorInner(Color.LTGRAY);
        mChart.setWebAlpha(100);
        mChart.setRotationEnabled(false);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MarkerView mv = new RadarMarkerView(this, R.layout.radar_markerview);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv); // Set the marker to the chart

        setData();

        mChart.animateXY(1400, 1400, Easing.EasingOption.EaseInOutQuad, Easing.EasingOption.EaseInOutQuad);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTypeface(mTfLight);
        xAxis.setTextSize(9f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);
        xAxis.setTextColor(Color.rgb(159, 155, 152));
        xAxis.setValueTextColor(Color.rgb(61, 55, 50));
        xAxis.setValueTextSize(14f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mActivities[(int)value % mActivities.length];
            }

            @Override
            public RadarEntry getValue(float value, AxisBase axis) {
                return entries2.get((int)value % entries2.size());
            }
        });
        YAxis yAxis = mChart.getYAxis();
        yAxis.setTypeface(mTfLight);
        yAxis.setLabelCount(1, false);
        yAxis.setTextSize(9f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(80f);
        yAxis.setDrawLabels(false);

        Legend l = mChart.getLegend();
        l.setEnabled(false);
        //        l.setForm(Legend.LegendForm.NONE);
        //        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        //        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        //        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //        l.setDrawInside(false);
        //        l.setTypeface(mTfLight);
        //        l.setXEntrySpace(7f);
        //        l.setYEntrySpace(5f);
        //        l.setTextColor(Color.WHITE);

    }

    public void setData() {

        float mult = 50;
        float min = 40;
        int cnt = mActivities.length;


        //        ArrayList<RadarEntry> entries1 = new ArrayList<RadarEntry>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < cnt; i++) {
            //            float val1 = (float) (Math.random() * mult) + min;
            //            entries1.add(new RadarEntry(val1));

            float val2 = (float)(Math.random() * mult) + min;
            //            float val2 = 80;
            entries2.add(new RadarEntry(val2));
        }

        //        RadarDataSet set1 = new RadarDataSet(entries1, "Last Week");
        //        set1.setColor(Color.rgb(103, 110, 129));
        //        set1.setFillColor(Color.rgb(103, 110, 129));
        //        set1.setDrawFilled(true);
        //        set1.setFillAlpha(180);
        //        set1.setLineWidth(2f);
        //        set1.setDrawHighlightCircleEnabled(true);
        //        set1.setDrawHighlightIndicators(false);

        RadarDataSet set2 = new RadarDataSet(entries2, "");
        set2.setColor(Color.rgb(29, 198, 161));
        //        set2.setFillColor(Color.rgb(121, 162, 175));
        set2.setDrawFilled(false);
        set2.setFillAlpha(180);
        set2.setLineWidth(5f);
        set2.setDrawHighlightCircleEnabled(false);
        set2.setDrawHighlightIndicators(false);

        ArrayList<IRadarDataSet> sets = new ArrayList<IRadarDataSet>();
        //        sets.add(set1);
        sets.add(set2);

        RadarData data = new RadarData(sets);
        data.setValueTypeface(mTfLight);
        data.setValueTextSize(8f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.WHITE);

        mChart.setData(data);
        mChart.invalidate();
    }
}
