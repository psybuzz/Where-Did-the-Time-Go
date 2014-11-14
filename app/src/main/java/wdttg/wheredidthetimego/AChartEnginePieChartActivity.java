package wdttg.wheredidthetimego;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AChartEnginePieChartActivity extends Activity {

    private static int[] COLORS = new int[] { Color.GREEN, Color.BLUE,Color.MAGENTA, Color.CYAN };

    private static double[] VALUES = new double[] { 10, 11, 12, 13 };

    private static String[] NAME_LIST = new String[] { "A", "B", "C", "D" };

    private CategorySeries mSeries = new CategorySeries("");

    private DefaultRenderer mRenderer = new DefaultRenderer();

    private GraphicalView mChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achart_engine_pie_chart);

        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
        mRenderer.setChartTitleTextSize(20);
        mRenderer.setLabelsTextSize(15);
        mRenderer.setLegendTextSize(15);
        mRenderer.setMargins(new int[] { 20, 30, 15, 0 });
        mRenderer.setZoomButtonsVisible(true);
        mRenderer.setStartAngle(90);

        for (int i = 0; i < VALUES.length; i++) {
            mSeries.add(NAME_LIST[i] + " " + VALUES[i], VALUES[i]);
            SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
            renderer.setColor(COLORS[(mSeries.getItemCount() - 1) % COLORS.length]);
            mRenderer.addSeriesRenderer(renderer);
        }

        if (mChartView != null) {
            mChartView.repaint();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mChartView == null) {
            FrameLayout layout = (FrameLayout) findViewById(R.id.chart);
            mChartView = ChartFactory.getPieChartView(this, mSeries, mRenderer);
            mRenderer.setClickEnabled(true);
            mRenderer.setSelectableBuffer(10);

            mChartView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();

                    if (seriesSelection == null) {
                        Toast.makeText(AChartEnginePieChartActivity.this,"No chart element was clicked",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AChartEnginePieChartActivity.this,"Chart element data point index "+ (seriesSelection.getPointIndex()+1) + " was clicked" + " point value="+ seriesSelection.getValue(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            mChartView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
                    if (seriesSelection == null) {
                        Toast.makeText(AChartEnginePieChartActivity.this,"No chart element was long pressed", Toast.LENGTH_SHORT);
                        return false;
                    } else {
                        Toast.makeText(AChartEnginePieChartActivity.this,"Chart element data point index "+ seriesSelection.getPointIndex()+ " was long pressed",Toast.LENGTH_SHORT);
                        return true;
                    }
                }
            });
            layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        }
        else {
            mChartView.repaint();
        }
    }
}