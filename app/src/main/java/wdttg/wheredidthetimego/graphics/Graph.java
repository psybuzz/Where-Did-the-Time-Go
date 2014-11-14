package wdttg.wheredidthetimego.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.Button;

import wdttg.wheredidthetimego.R;

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
    float max;
    private int[] COLORS={Color.GREEN,Color.YELLOW,Color.RED};
    RectF rectf;
    //needed to draw a scatter plot:
    private float[] raw;
    private String[] time;
    private Button changeView;

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
        rectf = new RectF(w/6, w/6, w*5/6, w*5/6);
        magnitudes = new float[3];
        parsePieBar(data);

        //scatterPlot
        parseScatter(data, timeStamps);
    }

    private void parseScatter(float[] data, String[] timeStamps){
        if(data.length > 24){
            raw = new float[24];
            time = new String[24];
            for(int i = data.length-24; i < data.length; i++){
                raw[i-(data.length-24)] = data[i];
                time[i-(data.length-24)] = timeStamps[i];
            }
        } else {
            raw = data;
            time = timeStamps;
        }
    }

    private void parsePieBar(float[] data){
        total = 0;
        int redTotal = 0;
        int yellowTotal = 0;
        int greenTotal = 0;
        int i = 0;
        if(data.length >= 24) {
            i = data.length - 24;
        }
        for (i = 0; i < data.length; i++) {
            if (data[i] < 30) {
                redTotal++;
            } else if (data[i] < 70) {
                yellowTotal++;
            } else {
                greenTotal++;
            }
            total++;
        }
        magnitudes[0] = greenTotal;
        magnitudes[1] = yellowTotal;
        magnitudes[2] = redTotal;
        max = Math.max(Math.max(greenTotal, yellowTotal), redTotal);
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
            canvas.drawRect(w / 6 + i * w * 2 / 9, w * 5 / 6 - (m / max) * w * 2 / 3, w * 7 / 18 + i * w * 2 / 9, w * 5 / 6, paint);
            paint.setColor(Color.BLUE);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(24);
            canvas.drawText("" + ((int)magnitudes[i]), 5*w/18+i*w*2/9, w*5/6+36, paint);
        }
    }

    private void drawScatter(Canvas canvas){
        int segments = raw.length-1;
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(0);
        canvas.drawLine(w / 6, w / 6 + (w * 2 / 3) * 7 / 10, w * 5 / 6, w / 6 + (w * 2 / 3) * 7 / 10, paint);
        canvas.drawLine(w/6, w/6+(w*2/3)*3/10, w*5/6, w/6+(w*2/3)*3/10, paint);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(5);
        for(int i = 0; i < raw.length-1; i++){
            canvas.drawLine(w/6+(w*2/3)*i/segments, w/6+(w*2/3)*(100-raw[i])/100,
                w/6+(w*2/3)*(i+1)/segments,w/6+(w*2/3)*(100-raw[i+1])/100, paint
            );
        }
        for(int i = 0; i < raw.length; i++){
            if(raw[i] < 30) {
                paint.setColor(Color.RED);
            } else if(raw[i] < 70){
                paint.setColor(Color.YELLOW);
            } else {
                paint.setColor(Color.GREEN);
            }
            canvas.drawCircle(w/6+(w*2/3)*i/segments, w/6+(w*2/3)*(100-raw[i])/100, 10, paint);
        }
        paint.setColor(Color.BLUE);
        paint.setTextSize(24);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(time[0], w/6, w*5/6+36, paint);
        canvas.drawText(time[time.length-1], w*5/6, w*5/6+36, paint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.argb(255, 200, 200, 200));
        canvas.drawRect(w / 12, w / 12, w * 11 / 12, w * 11 / 12, paint);
        paint.setColor(Color.argb(255, 0, 0, 0));
        canvas.drawRect(rectf, paint);
        if(mode == 0){
            drawPie(canvas);
            paint.setColor(Color.BLUE);
            paint.setTextSize(36);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("Pie Chart", w/2, w*1/6-12, paint);
        } else if(mode == 1){
            drawBar(canvas);
            paint.setColor(Color.BLUE);
            paint.setTextSize(36);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("Bar Chart", w/2, w*1/6-12, paint);
        } else if(mode == 2){
            drawScatter(canvas);
            paint.setColor(Color.BLUE);
            paint.setTextSize(36);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("Scatterplot", w/2, w*1/6-12, paint);
        }
    }

    //call when the "change graph type" button is pressed.
    public void changeType(){
        mode = (mode+1)%3;
        this.invalidate();
    }

    //call with updated data to update the graph
    public void update(float[] data, String[] timeStamp){
        parsePieBar(data);
        parseScatter(data, timeStamp);
        this.invalidate();
    }
}
