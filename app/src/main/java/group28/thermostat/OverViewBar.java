package group28.thermostat;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class OverViewBar extends View implements View.OnTouchListener {
    private ArrayList<Double> dayIntervals;
    private ArrayList<Double> nightIntervals;
    private int barColor, dayColor, nightColor, textColor;
    private Paint barPaint, dayPaint, nightPaint, textPaint;
    boolean drawLabels = true, specialBar = false, noBar = false;
    int height, width, barWidth;

    public OverViewBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        dayIntervals = new ArrayList<Double>();
        nightIntervals = new ArrayList<Double>();

        barPaint = new Paint();
        dayPaint = new Paint();
        nightPaint = new Paint();
        textPaint = new Paint();

        //get the attributes specified in attrs.xml using the name we included
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.OverViewBar, 0, 0);

        try {
            barColor = a.getInteger(R.styleable.OverViewBar_barColor, 0);
            dayColor = a.getInteger(R.styleable.OverViewBar_dayColor, 0);
            nightColor = a.getInteger(R.styleable.OverViewBar_nightColor, 0);
            textColor = a.getInteger(R.styleable.OverViewBar_textColor, 0);
            drawLabels = a.getBoolean(R.styleable.OverViewBar_showLabels, true);
            specialBar = a.getBoolean(R.styleable.OverViewBar_specialBar, false);
            noBar = a.getBoolean(R.styleable.OverViewBar_noBar, false);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //get half of the width and height as we are working with a circle
        height = this.getMeasuredHeight();
        width = this.getMeasuredWidth();
        barWidth = (int)(width * 0.85);
        final double HUP = 0.0;
        final double HCENTER = 0.3;
        final double HDOWN = 0.6;
        final double HTEXT = 0.9;

        barPaint.setStyle(Style.FILL);
        barPaint.setAntiAlias(true);
        barPaint.setStrokeWidth(8.0f);
        barPaint.setColor(barColor);
        dayPaint.setStyle(Style.FILL);
        dayPaint.setAntiAlias(true);
        dayPaint.setStrokeWidth(4.0f);
        dayPaint.setColor(dayColor);
        nightPaint.setStyle(Style.FILL);
        nightPaint.setAntiAlias(true);
        nightPaint.setStrokeWidth(4.0f);
        nightPaint.setColor(nightColor);
        textPaint.setStyle(Style.FILL);
        textPaint.setAntiAlias(true);
        textPaint.setStrokeWidth(4.0f);
        textPaint.setColor(textColor);
        textPaint.setTextSize(50);
        textPaint.setTextAlign(Paint.Align.CENTER);

        if (!noBar) {
            canvas.drawLine((float) ((width - barWidth) / 2), (float) (HCENTER * height), (float) ((width - barWidth) / 2 + barWidth), (float) (HCENTER * height), barPaint);

            canvas.drawLine((float) ((width - barWidth) / 2) + 2f, (float) (HUP * height), (float) ((width - barWidth) / 2) + 2f, (float) (HDOWN * height), barPaint);
            canvas.drawLine((float)((width - barWidth) / 2 + barWidth) - 2f, (float) (HUP * height), (float)((width - barWidth) / 2 + barWidth) - 2f, (float) (HDOWN * height), barPaint);
        }

        if (specialBar) {
            for (double d = 0.25; d <= 0.75; d = d + 0.25) {
                canvas.drawLine((float) ((width - barWidth) / 2 + (barWidth / (2 * width)) * width + d * barWidth), (float) (HUP * height), (float) ((width - barWidth) / 2 + (barWidth / (2 * width)) * width + d * barWidth), (float) (HDOWN * height), textPaint);
            }
        }

        for (double d : dayIntervals) {
            canvas.drawLine((float) ((width - barWidth) / 2 + d * barWidth), (float) (HUP * height), (float) ((width - barWidth) / 2 + d * barWidth), (float) (HDOWN * height), dayPaint);
        }

        for (double d : nightIntervals) {
            canvas.drawLine((float)((width - barWidth) / 2 + d * barWidth), (float) (HUP * height), (float) ((width - barWidth) / 2 + d * barWidth), (float) (HDOWN * height), nightPaint);
        }

        if (drawLabels) {
            canvas.drawText("00:00", (float) ((width - barWidth) / 2 + (barWidth / (2 * width)) * width + 0 * barWidth), (float) (HTEXT * height), textPaint);
            canvas.drawText("06:00", (float)((width - barWidth) / 2 + (barWidth / (2 * width)) * width + 0.25 * barWidth), (float) (HTEXT * height), textPaint);
            canvas.drawText("12:00", (float)((width - barWidth) / 2 + (barWidth / (2 * width)) * width + 0.5 * barWidth), (float)(HTEXT * height), textPaint);
            canvas.drawText("18:00", (float)((width - barWidth) / 2 + (barWidth / (2 * width)) * width + 0.75 * barWidth), (float) (HTEXT * height), textPaint);
            canvas.drawText("24:00", (float)((width - barWidth) / 2 + (barWidth / (2 * width)) * width + 1 * barWidth), (float)(HTEXT * height), textPaint);
        }
    }

    public void set(ArrayList<Integer> daySwitches, ArrayList<Integer> nightSwitches) {
        dayIntervals.clear();
        nightIntervals.clear();

        for (int i : daySwitches) {
            double j = (double)i;
            dayIntervals.add((((int)(j / 100.0)) * 60 + (j % 100.0)) / 1440.0);
        }

        for (int i : nightSwitches) {
            double j = (double)i;
            nightIntervals.add((((int)(j / 100.0)) * 60 + (j % 100.0)) / 1440.0);
        }

        invalidate();
        postInvalidate();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    public int getOvbWidth() {
        return width;
    }

    public int getOvbHeight() {
        return height;
    }

    public int getOvbBarWidth() {
        return barWidth;
    }
}
