
package kr.co.openit.radarchart.renderer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;

import kr.co.openit.radarchart.animation.ChartAnimator;
import kr.co.openit.radarchart.charts.RadarChart;
import kr.co.openit.radarchart.data.RadarData;
import kr.co.openit.radarchart.data.RadarEntry;
import kr.co.openit.radarchart.highlight.Highlight;
import kr.co.openit.radarchart.interfaces.datasets.IRadarDataSet;
import kr.co.openit.radarchart.utils.ColorTemplate;
import kr.co.openit.radarchart.utils.MPPointF;
import kr.co.openit.radarchart.utils.Utils;
import kr.co.openit.radarchart.utils.ViewPortHandler;

public class RadarChartRenderer extends LineRadarRenderer {

    protected RadarChart mChart;

    /**
     * paint for drawing the web
     */
    protected Paint mWebPaint;

    protected Paint mHighlightCirclePaint;

    float radius = 0f;

    public RadarChartRenderer(RadarChart chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = chart;

        mHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlightPaint.setStyle(Paint.Style.STROKE);
        mHighlightPaint.setStrokeWidth(2f);
        mHighlightPaint.setColor(Color.rgb(255, 187, 115));

        mWebPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWebPaint.setStyle(Paint.Style.STROKE);

        mHighlightCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public Paint getWebPaint() {
        return mWebPaint;
    }

    @Override
    public void initBuffers() {
        // TODO Auto-generated method stub

    }

    @Override
    public void drawData(Canvas c) {

        RadarData radarData = mChart.getData();

        int mostEntries = radarData.getMaxEntryCountSet().getEntryCount();

        for (IRadarDataSet set : radarData.getDataSets()) {

            if (set.isVisible()) {
                drawDataSet(c, set, mostEntries);
            }
        }
    }

    protected Path mDrawDataSetSurfacePathBuffer = new Path();

    /**
     * Draws the RadarDataSet
     *
     * @param c
     * @param dataSet
     * @param mostEntries the entry count of the dataset with the most entries
     */
    protected void drawDataSet(Canvas c, IRadarDataSet dataSet, int mostEntries) {

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        float sliceAngle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();

        float x = 0, y = 0;
        float moveX = 0, moveY = 0;
        MPPointF center = mChart.getCenterOffsets();
        MPPointF pOut = MPPointF.getInstance(0, 0);

        Path surface = mDrawDataSetSurfacePathBuffer;
        surface.reset();

        int[] colors = new int[] {Color.rgb(135, 176, 115),
                                  Color.rgb(18, 189, 163),
                                  Color.rgb(175, 169, 88),
                                  Color.rgb(255, 69, 96),
                                  Color.rgb(212, 169, 42),

        };

        boolean hasMovedToPoint = false;

        mRenderPaint.setColor(Color.rgb(241, 235, 230));
        mRenderPaint.setStyle(Paint.Style.FILL);
        mRenderPaint.setAlpha(mChart.getWebAlpha());
        c.drawCircle(center.x, center.y, radius * 5, mRenderPaint);

        for (int j = 0; j < dataSet.getEntryCount(); j++) {

            mRenderPaint.setColor(colors[j]);
            mRenderPaint.setStrokeWidth(dataSet.getLineWidth());
            mRenderPaint.setStyle(Paint.Style.STROKE);

            RadarEntry e = dataSet.getEntryForIndex(j);
            Utils.getPosition(center,
                              (e.getY() - mChart.getYChartMin()) * factor * phaseY,
                              sliceAngle * j * phaseX + mChart.getRotationAngle(),
                              pOut);
            if (radius < e.getY())
                radius = e.getY();

            if (Float.isNaN(pOut.x))
                continue;

            if (!hasMovedToPoint) {
                surface.moveTo(pOut.x, pOut.y);
                x = pOut.x;
                y = pOut.y;
                hasMovedToPoint = true;
            } else {
                switch (j) {
                    case 1:
                        //                        surface.quadTo(pOut.x - 100, pOut.y + 100, pOut.x, pOut.y);
                        surface.cubicTo(moveX, moveY, moveX, pOut.y, pOut.x, pOut.y);
                        break;
                    case 2:
                        surface.cubicTo(moveX,
                                        moveY,
                                        center.x + ((pOut.x - center.x) / 2),
                                        pOut.y + ((center.y - pOut.y) / 2),
                                        pOut.x,
                                        pOut.y);
                        //                        surface.quadTo(pOut.x - 100, pOut.y - 60, pOut.x, pOut.y);
                        break;
                    case 3:
                        surface.cubicTo(moveX,
                                        moveY,
                                        center.x + ((pOut.x - center.x) / 2),
                                        pOut.y + ((center.y - pOut.y) / 2),
                                        pOut.x,
                                        pOut.y);
                        //                        surface.quadTo(pOut.x, pOut.y - 100, pOut.x, pOut.y);
                        break;
                    case 4:
                        surface.cubicTo(moveX,
                                        moveY,
                                        center.x + ((pOut.x - center.x) / 2),
                                        pOut.y + ((center.y - pOut.y) / 2),
                                        pOut.x,
                                        pOut.y);
                        break;
                }

                //                                surface.quadTo(pOut.x - 20, pOut.y - 20, pOut.x + 20, pOut.y + 20);
                //                surface.lineTo(pOut.x, pOut.y);
            }
            //            surface.quadTo(pOut.x + 30, pOut.y + 30, pOut.x - 30, pOut.y - 30);
            //            surface.lineTo(pOut.x, pOut.y);
            c.drawPath(surface, mRenderPaint);

            surface.reset();
            surface.moveTo(pOut.x, pOut.y);
            moveX = pOut.x;
            moveY = pOut.y;

        }

        if (dataSet.getEntryCount() > mostEntries) {
            // if this is not the largest set, draw a line to the center before closing
            surface.lineTo(center.x, center.y);

        }

        surface.cubicTo(moveX, moveY, center.x + ((x - center.x) / 2), y + ((center.y - y) / 2), x, y);

        mRenderPaint.setColor(colors[0]);
        c.drawPath(surface, mRenderPaint);

        surface.close();

        if (dataSet.isDrawFilledEnabled()) {

            final Drawable drawable = dataSet.getFillDrawable();
            if (drawable != null) {

                drawFilledPath(c, surface, drawable);
            } else {

                drawFilledPath(c, surface, dataSet.getFillColor(), dataSet.getFillAlpha());
            }
        }

        //        if (!dataSet.isDrawFilledEnabled() || dataSet.getFillAlpha() < 255)
        //            c.drawPath(surface, mRenderPaint);

        mRenderPaint.setColor(Color.rgb(185, 179, 174));
        mRenderPaint.setStyle(Paint.Style.FILL);
        c.drawCircle(center.x, center.y, 140f, mRenderPaint);
        mRenderPaint.setColor(Color.WHITE);
        mRenderPaint.setTextSize(Utils.convertDpToPixel(12));
        c.drawText("SHPBAND",
                   center.x - (Utils.convertDpToPixel(mRenderPaint.getTextSize()) / 2),
                   center.y + (Utils.convertDpToPixel(mRenderPaint.getTextSize()) / 3),
                   mRenderPaint);

        MPPointF.recycleInstance(center);
        MPPointF.recycleInstance(pOut);
    }

    @Override
    public void drawValues(Canvas c) {

        float phaseX = mAnimator.getPhaseX();
        float phaseY = mAnimator.getPhaseY();

        float sliceAngle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();

        MPPointF center = mChart.getCenterOffsets();
        MPPointF pOut = MPPointF.getInstance(0, 0);

        float yoffset = Utils.convertDpToPixel(5f);

        for (int i = 0; i < mChart.getData().getDataSetCount(); i++) {

            IRadarDataSet dataSet = mChart.getData().getDataSetByIndex(i);

            if (!shouldDrawValues(dataSet))
                continue;

            // apply the text-styling defined by the DataSet
            applyValueTextStyle(dataSet);

            for (int j = 0; j < dataSet.getEntryCount(); j++) {

                RadarEntry entry = dataSet.getEntryForIndex(j);

                Utils.getPosition(center,
                                  (entry.getY() - mChart.getYChartMin()) * factor * phaseY,
                                  sliceAngle * j * phaseX + mChart.getRotationAngle(),
                                  pOut);

                drawValue(c,
                          dataSet.getValueFormatter(),
                          entry.getY(),
                          entry,
                          i,
                          pOut.x,
                          pOut.y - yoffset,
                          dataSet.getValueTextColor(j));
            }
        }

        MPPointF.recycleInstance(center);
        MPPointF.recycleInstance(pOut);
    }

    @Override
    public void drawExtras(Canvas c) {
        drawWeb(c);
    }

    protected void drawWeb(Canvas c) {

        float sliceangle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();
        float rotationangle = mChart.getRotationAngle();

        MPPointF center = mChart.getCenterOffsets();

        final int xIncrements = 1 + mChart.getSkipWebLineCount();
        int maxEntryCount = mChart.getData().getMaxEntryCountSet().getEntryCount();

        MPPointF p = MPPointF.getInstance(0, 0);

        // draw the web lines that come from the center
        mWebPaint.setStrokeWidth(mChart.getWebLineWidth());
        mWebPaint.setColor(mChart.getWebColor());
        mWebPaint.setAlpha(mChart.getWebAlpha());
        mWebPaint.setStyle(Paint.Style.STROKE);

        for (int i = 0; i < maxEntryCount; i += xIncrements) {

            mWebPaint.setStyle(Paint.Style.STROKE);

            Utils.getPosition(center, mChart.getYRange() * factor, sliceangle * i + rotationangle, p);

            c.drawLine(center.x, center.y, p.x, p.y, mWebPaint);
            mWebPaint.setStyle(Paint.Style.FILL);
            c.drawCircle(p.x, p.y, 10f, mWebPaint);
        }
        MPPointF.recycleInstance(p);

        // draw the inner-web
        //        mWebPaint.setStrokeWidth(mChart.getWebLineWidthInner());
        //        mWebPaint.setColor(mChart.getWebColorInner());
        //        mWebPaint.setAlpha(mChart.getWebAlpha());

        int labelCount = mChart.getYAxis().mEntryCount;

        MPPointF p1out = MPPointF.getInstance(0, 0);
        MPPointF p2out = MPPointF.getInstance(0, 0);
        //        for (int j = 0; j < labelCount; j++) {
        //
        //            for (int i = 0; i < mChart.getData().getEntryCount(); i++) {
        //
        //                float r = (mChart.getYAxis().mEntries[j] - mChart.getYChartMin()) * factor;
        //
        //                Utils.getPosition(center, r, sliceangle * i + rotationangle, p1out);
        //                Utils.getPosition(center, r, sliceangle * (i + 1) + rotationangle, p2out);
        //
        //                c.drawLine(p1out.x, p1out.y, p2out.x, p2out.y, mWebPaint);
        //
        //            }
        //        }

        MPPointF.recycleInstance(p1out);
        MPPointF.recycleInstance(p2out);
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        float sliceangle = mChart.getSliceAngle();

        // calculate the factor that is needed for transforming the value to
        // pixels
        float factor = mChart.getFactor();

        MPPointF center = mChart.getCenterOffsets();
        MPPointF pOut = MPPointF.getInstance(0, 0);

        RadarData radarData = mChart.getData();

        for (Highlight high : indices) {

            IRadarDataSet set = radarData.getDataSetByIndex(high.getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            RadarEntry e = set.getEntryForIndex((int)high.getX());

            if (!isInBoundsX(e, set))
                continue;

            float y = (e.getY() - mChart.getYChartMin());

            Utils.getPosition(center,
                              y * factor * mAnimator.getPhaseY(),
                              sliceangle * high.getX() * mAnimator.getPhaseX() + mChart.getRotationAngle(),
                              pOut);

            high.setDraw(pOut.x, pOut.y);

            // draw the lines
            drawHighlightLines(c, pOut.x, pOut.y, set);

            if (set.isDrawHighlightCircleEnabled()) {

                if (!Float.isNaN(pOut.x) && !Float.isNaN(pOut.y)) {

                    int strokeColor = set.getHighlightCircleStrokeColor();
                    if (strokeColor == ColorTemplate.COLOR_NONE) {
                        strokeColor = set.getColor(0);
                    }

                    if (set.getHighlightCircleStrokeAlpha() < 255) {
                        strokeColor = ColorTemplate.colorWithAlpha(strokeColor, set.getHighlightCircleStrokeAlpha());
                    }

                    drawHighlightCircle(c,
                                        pOut,
                                        set.getHighlightCircleInnerRadius(),
                                        set.getHighlightCircleOuterRadius(),
                                        set.getHighlightCircleFillColor(),
                                        strokeColor,
                                        set.getHighlightCircleStrokeWidth());
                }
            }
        }

        MPPointF.recycleInstance(center);
        MPPointF.recycleInstance(pOut);
    }

    protected Path mDrawHighlightCirclePathBuffer = new Path();

    public void drawHighlightCircle(Canvas c,
                                    MPPointF point,
                                    float innerRadius,
                                    float outerRadius,
                                    int fillColor,
                                    int strokeColor,
                                    float strokeWidth) {
        c.save();

        outerRadius = Utils.convertDpToPixel(outerRadius);
        innerRadius = Utils.convertDpToPixel(innerRadius);

        if (fillColor != ColorTemplate.COLOR_NONE) {
            Path p = mDrawHighlightCirclePathBuffer;
            p.reset();
            p.addCircle(point.x, point.y, outerRadius, Path.Direction.CW);
            if (innerRadius > 0.f) {
                p.addCircle(point.x, point.y, innerRadius, Path.Direction.CCW);
            }
            mHighlightCirclePaint.setColor(fillColor);
            mHighlightCirclePaint.setStyle(Paint.Style.FILL);
            c.drawPath(p, mHighlightCirclePaint);
        }

        if (strokeColor != ColorTemplate.COLOR_NONE) {
            mHighlightCirclePaint.setColor(strokeColor);
            mHighlightCirclePaint.setStyle(Paint.Style.STROKE);
            mHighlightCirclePaint.setStrokeWidth(Utils.convertDpToPixel(strokeWidth));
            c.drawCircle(point.x, point.y, outerRadius, mHighlightCirclePaint);
        }

        c.restore();
    }
}
