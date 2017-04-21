package com.evoke.cn.evokecompanion.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.View;

public class OdometerSpinner extends View
{
    public static final float IDEAL_ASPECT_RATIO = 1.66f;

    private float mWidth;
    private float mHeight;

    private float mDigitX;

    private float mDigitY;
    private int mCurrentDigit;
    private String mDigitString;
    private Paint mDigitPaint;

    private int mDigitAbove;
    private int mDigitBelow;
    private float mDigitAboveY;
    private float mDigitBelowY;
    private String mDigitAboveString;
    private String mDigitBelowString;

    private float mTouchStartY;
    private float mTouchLastY;

    private OnDigitChangeListener mDigitChangeListener;

    /*
     * Simple constructor used when creating a view from code.
     */
    public OdometerSpinner(Context context)
    {
        super(context);

        initialize();
    }

    /*
     * This is called when a view is being constructed from an XML file,
     * supplying attributes that were specified in the XML file.
     */
    public OdometerSpinner(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initialize();
    }

    /*
     * Perform inflation from XML and apply a class-specific base style.
     * This constructor of View allows subclasses to use their own base
     * style when they are inflating.
     */
    public OdometerSpinner(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        initialize();
    }

    /*
     * Initialize all of our class members and variables
     */
    private void initialize()
    {

        mDigitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDigitPaint.setColor(Color.WHITE);
        mDigitPaint.setTextAlign(Align.CENTER);

        setCurrentDigit(0);
    }

    public int getCurrentDigit()
    {
        return mCurrentDigit;
    }

    public void setCurrentDigit(int digit)
    {
		/*
		 *  Basic range limiting - in a production widget,
		 *  you might want to throw an exception if the number passed
		 *  if less than 0 or greater than 9
		 */
        int newVal = digit;

        if(newVal < 0)
            newVal = 0;
        if(newVal > 9)
            newVal = 9;

        int old = mCurrentDigit;
        mCurrentDigit = newVal;

        if(mCurrentDigit != old && mDigitChangeListener != null)
            mDigitChangeListener.onDigitChange(this, mCurrentDigit);

        mDigitAbove = mCurrentDigit + 1;

        if(mDigitAbove > 9)
            mDigitAbove = 0;

        mDigitBelow = mCurrentDigit - 1;

        if(mDigitBelow < 0)
            mDigitBelow = 9;

        mDigitString = String.valueOf(mCurrentDigit);
        mDigitAboveString = String.valueOf(mDigitAbove);
        mDigitBelowString = String.valueOf(mDigitBelow);

        setDigitYValues();
        invalidate();
    }

    private void setDigitYValues()
    {
        mDigitY = findCenterY(mCurrentDigit);
        mDigitAboveY = findCenterY(mDigitAbove) - mHeight;
        mDigitBelowY = mHeight + findCenterY(mDigitBelow);
    }

    private float findCenterY(int digit)
    {
        String text = String.valueOf(digit);
        Rect bounds = new Rect();
        mDigitPaint.getTextBounds(text, 0, text.length(), bounds);

        int textHeight = Math.abs(bounds.height());

        float result = mHeight - ((mHeight - textHeight) / 2);

        return result;
    }

    public void setOnDigitChangeListener(OnDigitChangeListener listener)
    {
        mDigitChangeListener = listener;
    }

    /*
     * This is where all of the drawing for the spinner is done.
     */
    @Override
    protected void onDraw(Canvas canvas)
    {
        // if our super has to do any drawing, do that first
        super.onDraw(canvas);

        // draw the digit text using our calculated position and Paint
        canvas.drawText(mDigitString, mDigitX, mDigitY, mDigitPaint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // get width and height size and mode
        int wSpec = MeasureSpec.getSize(widthMeasureSpec);

        int hSpec = MeasureSpec.getSize(heightMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = wSpec;
        int height = hSpec;

        // ideal height for the number display
        int idealHeight = (int) (wSpec * IDEAL_ASPECT_RATIO);

        if(idealHeight < hSpec)
        {
            height = idealHeight;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        // set the text paint to draw appropriately-sized text
        mDigitPaint.setTextSize(h);
        mDigitX = mWidth / 2;
        setDigitYValues();
    }


    public interface OnDigitChangeListener {
        abstract void onDigitChange(OdometerSpinner sender, int newDigit);
    }
}
