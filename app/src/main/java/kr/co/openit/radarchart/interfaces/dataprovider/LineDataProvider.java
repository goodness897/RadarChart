package kr.co.openit.radarchart.interfaces.dataprovider;


import kr.co.openit.radarchart.components.YAxis;
import kr.co.openit.radarchart.data.LineData;

public interface LineDataProvider extends BarLineScatterCandleBubbleDataProvider {

    LineData getLineData();

    YAxis getAxis(YAxis.AxisDependency dependency);
}
