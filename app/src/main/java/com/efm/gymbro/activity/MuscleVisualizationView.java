package com.efm.gymbro.activity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.HashSet;
import java.util.Set;

public class MuscleVisualizationView extends View {
    private Paint inactivePaint;
    private Paint activePaint;
    private Set<String> activeGroups;

    private Path shouldersPath;
    private Path chestPath;
    private Path bicepsPath;
    private Path backPath;
    private Path absPath;
    private Path quadsPath;
    private Path calvesPath;

    public MuscleVisualizationView(Context context) {
        super(context);
        init();
    }

    public MuscleVisualizationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inactivePaint = new Paint();
        inactivePaint.setColor(Color.LTGRAY);
        inactivePaint.setStyle(Paint.Style.FILL);
        inactivePaint.setAntiAlias(true);

        activePaint = new Paint();
        activePaint.setColor(Color.rgb(255, 68, 68)); // Red color for active muscles
        activePaint.setStyle(Paint.Style.FILL);
        activePaint.setAntiAlias(true);

        activeGroups = new HashSet<>();
        initPaths();
    }

    private void initPaths() {
        shouldersPath = new Path();
        chestPath = new Path();
        bicepsPath = new Path();
        backPath = new Path();
        absPath = new Path();
        quadsPath = new Path();
        calvesPath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float centerX = w / 2f;
        float scale = h / 400f;

        // Reset all paths
        shouldersPath.reset();
        shouldersPath.moveTo(centerX - 40 * scale, 80 * scale);
        shouldersPath.quadTo(centerX, 70 * scale, centerX + 40 * scale, 80 * scale);

        chestPath.reset();
        chestPath.moveTo(centerX - 25 * scale, 90 * scale);
        chestPath.quadTo(centerX, 100 * scale, centerX + 25 * scale, 90 * scale);
        chestPath.quadTo(centerX, 120 * scale, centerX - 25 * scale, 90 * scale);

        // Add similar path definitions for other muscle groups
        // ... (implement paths for other muscle groups)
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw each muscle group with appropriate paint
        canvas.drawPath(shouldersPath, activeGroups.contains("shoulders") ? activePaint : inactivePaint);
        canvas.drawPath(chestPath, activeGroups.contains("chest") ? activePaint : inactivePaint);
        // ... draw other muscle groups
    }

    public void setActiveGroups(Set<String> groups) {
        activeGroups.clear();
        activeGroups.addAll(groups);
        invalidate();
    }
}