package com.kritsin.holehint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;


public class HoleView extends View {

    public enum ShapeType {
        NONE, RECTANGLE, CIRCLE;//, MIN_INNER_CIRCLE, MAX_INNER_CIRCLE, OUTER_CIRCLE
    }

    private final int ALPHA = 0xFF;

    private ShapeType shapeType = ShapeType.CIRCLE;

    private int posX, posY;

    private int width, height;

    private int padding;

    public HoleView(Context context) {
        super(context);
    }

    public HoleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HoleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    public void onDraw(Canvas canvas) {
        Rect rect;
        Paint transparentPaint = new Paint();
        transparentPaint.setAlpha(ALPHA);
        transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        switch (shapeType) {
            case RECTANGLE:
                rect = new Rect(0, 0, getShapeWidth(), getShapeHeight());
                canvas.drawRect(rect, transparentPaint);
                break;
            case CIRCLE:
                canvas.drawCircle(getShapeWidth() / 2, getShapeHeight() / 2, getShapeWidth() / 2, transparentPaint);
                break;
            case NONE:
                break;
            default:
                rect = new Rect(0, 0, width, height);
                canvas.drawRect(rect, transparentPaint);
        }
    }

    public void setLinkedView(View linkedView, ShapeType shapeType) {
        int[] coords = new int[2];
        linkedView.getLocationOnScreen(coords);

        setLinkedView(coords[0], coords[1], linkedView.getWidth(), linkedView.getHeight(), shapeType);
    }

    public void setLinkedView(int x, int y, int width, int height, ShapeType shapeType) {
        this.shapeType = shapeType;
        this.width = width;
        this.height = height;
        this.posX = x;
        this.posY = y;

        invalidate();
    }

    public void setPadding(int padding) {
        this.padding = padding;

        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int w = MeasureSpec.getSize(widthMeasureSpec);
//        int h = MeasureSpec.getSize(heightMeasureSpec);

        int w = getShapeWidth();
        int h = getShapeHeight();

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) getLayoutParams();
        layoutParams.leftToLeft = ConstraintSet.PARENT_ID;
        layoutParams.topToTop = ConstraintSet.PARENT_ID;
        layoutParams.topMargin = getLocationY();
        layoutParams.leftMargin = getLocationX();
        setLayoutParams(layoutParams);

//            setX(getLocationX());
//            setY(getLocationY());

        setMeasuredDimension(w, h);
    }

    private int getLocationX() {
        switch (shapeType) {
            case RECTANGLE:
                return posX - padding / 2;
            case CIRCLE:
                return posX - (getShapeWidth() - width) / 2;
            default:
                return posX;
        }
    }

    private int getLocationY() {
        switch (shapeType) {
            case RECTANGLE:
                return posY - padding / 2;
            case CIRCLE:
                return posY - (getShapeHeight() - height) / 2;
            default:
                return posY;
        }
    }

    private int getShapeWidth() {
        switch (shapeType) {
            case RECTANGLE:
                return width + padding;
            case CIRCLE:
                return 2 * (int) Math.sqrt(Math.pow((width + padding) / 2, 2) + Math.pow((height + padding) / 2, 2));
            default:
                return width;
        }
    }

    private int getShapeHeight() {
        switch (shapeType) {
            case RECTANGLE:
                return height + padding;
            case CIRCLE:
                return 2 * (int) Math.sqrt(Math.pow((width + padding) / 2, 2) + Math.pow((height + padding) / 2, 2));
            default:
                return width;
        }
    }

}