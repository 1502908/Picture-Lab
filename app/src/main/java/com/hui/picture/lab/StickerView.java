package com.hui.picture.lab;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewGroup;
import android.widget.ImageView;

public class StickerView extends androidx.appcompat.widget.AppCompatImageView {
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private float[] lastEvent = null;
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    private ScaleGestureDetector scaleDetector;

    public StickerView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setScaleType(ScaleType.MATRIX);
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                bringToFront();
                savedMatrix.set(matrix);
                start.set(event.getRawX(), event.getRawY());
                mode = DRAG;
                lastEvent = null;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    lastEvent = new float[4];
                    lastEvent[0] = event.getRawX(0);
                    lastEvent[1] = event.getRawX(1);
                    lastEvent[2] = event.getRawY(0);
                    lastEvent[3] = event.getRawY(1);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    float dx = event.getRawX() - start.x;
                    float dy = event.getRawY() - start.y;
                    matrix.postTranslate(dx, dy);
                } else if (mode == ZOOM && event.getPointerCount() == 2) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                    if (lastEvent != null) {
                        float rotation = rotation(event);
                        matrix.postRotate(rotation, mid.x, mid.y);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                lastEvent = null;
                break;
        }

        setImageMatrix(matrix);
        invalidate();
        return true;
    }

    private float spacing(MotionEvent event) {
        float x = event.getRawX(0) - event.getRawX(1);
        float y = event.getRawY(0) - event.getRawY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getRawX(0) + event.getRawX(1);
        float y = event.getRawY(0) + event.getRawY(1);
        point.set(x / 2, y / 2);
    }

    private float rotation(MotionEvent event) {
        double delta_x = (event.getRawX(0) - event.getRawX(1));
        double delta_y = (event.getRawY(0) - event.getRawY(1));
        double radians = Math.atan2(delta_y, delta_x);
        
        double delta_x_last = (lastEvent[0] - lastEvent[1]);
        double delta_y_last = (lastEvent[2] - lastEvent[3]);
        double radians_last = Math.atan2(delta_y_last, delta_x_last);
        
        return (float) Math.toDegrees(radians - radians_last);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            matrix.postScale(scaleFactor, scaleFactor, 
                detector.getFocusX(), detector.getFocusY());
            setImageMatrix(matrix);
            return true;
        }
    }

    public void updateMatrix(Matrix newMatrix) {
        matrix.set(newMatrix);
        setImageMatrix(matrix);
    }

    public Matrix getMatrix() {
        return new Matrix(matrix);
    }
} 