package kr.co.openit.radarchart.interfaces.dataprovider;

import kr.co.openit.radarchart.utils.Transformer;
import kr.co.openit.radarchart.components.YAxis.AxisDependency;
import kr.co.openit.radarchart.data.BarLineScatterCandleBubbleData;


public interface BarLineScatterCandleBubbleDataProvider extends ChartInterface {

    Transformer getTransformer(AxisDependency axis);
    boolean isInverted(AxisDependency axis);
    
    float getLowestVisibleX();
    float getHighestVisibleX();

    BarLineScatterCandleBubbleData getData();
}
