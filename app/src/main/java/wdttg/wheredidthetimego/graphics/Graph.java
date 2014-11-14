package wdttg.wheredidthetimego.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.Display;
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
public class Graph extends View {

    private Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    float w, h;
    int mode;
    //needed to draw a pie/bar chart:
    private float[] magnitudes;
    float total;
    private int[] COLORS={Color.GREEN,Color.YELLOW,Color.RED};
    RectF rectf;
    //needed to draw a scatter plot:
    private float[] raw;
    private String[] time;

    //width and height are inaccessible from View and require an activity
    //requires: the context
    //startType: 0 - pie, 1 - bar, 2 - scatter
    public Graph(Context context, int startType, float[] data, String[] timeStamps){
        super(context);
        mode = startType;
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        w = (float)display.widthPixels;
        h = (float)display.heightPixels;

        //pieChart and barChart
        rectf = new RectF(w/6, w/6, w*2/3, w*2/3);
        magnitudes = new float[3];
        parsePieBar(data);

        //scatterPlot
        raw = data;
        time = timeStamps;
    }

    private void parsePieBar(float[] parsedData){
        total = 0;
        int redTotal = 0;
        int yellowTotal = 0;
        int greenTotal = 0;
        for(int i=0;i<parsedData.length;i++) {
            if(parsedData[i] < 30) {
                redTotal++;
            } else if(parsedData[i] < 70){
                yellowTotal++;
            } else {
                greenTotal++;
            }
            total++;
        }
        magnitudes[0] = greenTotal;
        magnitudes[1] = yellowTotal;
        magnitudes[2] = redTotal;
    }

    private void drawPie(Canvas canvas){
        int pieTemp = 0;
        for (int i = 0; i < magnitudes.length; i++) {
            if (i == 0) {
                paint.setColor(COLORS[i]);
                canvas.drawArc(rectf, 0, 360*(magnitudes[i]/total), true, paint);
            }
            else
            {
                pieTemp += 360*(magnitudes[i - 1]/total);
                paint.setColor(COLORS[i]);
                canvas.drawArc(rectf, pieTemp, 360*magnitudes[i]/total, true, paint);
            }
        }
    }

    private void drawBar(Canvas canvas){
        for(int i = 0; i < magnitudes.length; i++){
            float m = magnitudes[i];
            paint.setColor(COLORS[i]);
            canvas.drawRect(w/6+i*w*2/9, w*5/6-(m/total)*w*2/3, w*7/18+i*w*2/9, w*5/6, paint);
        }
    }

    private void drawScatter(Canvas canvas){
        int segments = raw.length-1;
        for(int i = 0; i < raw.length-1; i++){
            paint.setColor(Color.BLACK);
            canvas.drawLine(w/6+(w*2/3)*i/segments, w/6+(w*2/3)*(100-raw[i])/100,
                w/6+(w*2/3)*(i+1)/segments,w/6+(w*2/3)*(100-raw[i+1])/100, paint
            );
        }
        paint.setColor(Color.BLUE);
        canvas.drawText(time[0], w/6, w*2/3, paint);
        canvas.drawText(time[time.length-1], w*5/6, w*2/3, paint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mode == 0){
            drawPie(canvas);
        } else if(mode == 1){
            drawBar(canvas);
        } else if(mode == 2){
            drawScatter(canvas);
        }
    }
}
