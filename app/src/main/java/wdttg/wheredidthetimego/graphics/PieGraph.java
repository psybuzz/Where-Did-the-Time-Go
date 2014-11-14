package wdttg.wheredidthetimego.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

/**
 * Created by the1banana on 11/13/2014.
 * Here's how this works...
 * to create a graph view, simply pass it the context to be drawn in
 * and an array of floats
 * for now this array of floats must only contain 3 values:
 * the number of entries for productive, sort of productive, and not productive
 * and makes 3 pie slices total as a result
 */
public class PieGraph extends View {

    private Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private float[] values;
    private int[] COLORS={Color.GREEN,Color.YELLOW,Color.RED,Color.GRAY};
    RectF rectf = new RectF(10, 10, 200, 200);
    int temp=0;

    public PieGraph(Context context, float[] parsedData){
        super(context);
        values = new float[parsedData.length];
        int total = 0;
        for(int i=0;i<parsedData.length;i++)
        {
            total += parsedData[i];
        }
        for(int i=0;i<parsedData.length;i++){
            values[i] = 360*(parsedData[i]/total);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < values.length; i++) {
            if (i == 0) {
                paint.setColor(COLORS[i]);
                canvas.drawArc(rectf, 0, values[i], true, paint);
            }
            else
            {
                temp += (int) values[i - 1];
                paint.setColor(COLORS[i]);
                canvas.drawArc(rectf, temp, values[i], true, paint);
            }
        }
    }
}
