package com.evoke.cn.evokecompanion.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TableLayout;

import com.evoke.cn.evokecompanion.R;

public class SixDigitOdometer extends TableLayout
{
    private static final int NUM_DIGITS = 6;
    private int mCurrentValue;
    private OdometerSpinner[] mDigitSpinners;
    private OnValueChangeListener mValueChangeListener;
    private static final String defaultUnits = "km";

    public SixDigitOdometer(Context context)
    {
        super(context);

        initialize();
    }

    public SixDigitOdometer(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initialize();
    }


    private void initialize()
    {
        mDigitSpinners = new OdometerSpinner[NUM_DIGITS];

        // Inflate the view from the layout resource.
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li;
        li = (LayoutInflater)getContext().getSystemService(infService);
        li.inflate(R.layout.six_digit_odometer, this, true);

        setUnits(false,true);

        mDigitSpinners[0] = (OdometerSpinner) findViewById(R.id.widget_odometer_spinner_1);
        mDigitSpinners[1] = (OdometerSpinner) findViewById(R.id.widget_odometer_spinner_10);
        mDigitSpinners[2] = (OdometerSpinner) findViewById(R.id.widget_odometer_spinner_100);
        mDigitSpinners[3] = (OdometerSpinner) findViewById(R.id.widget_odometer_spinner_1k);
        mDigitSpinners[4] = (OdometerSpinner) findViewById(R.id.widget_odometer_spinner_10k);
        mDigitSpinners[5] = (OdometerSpinner) findViewById(R.id.widget_odometer_spinner_100k);

        for(OdometerSpinner s : mDigitSpinners)
        {
            s.setOnDigitChangeListener(new OdometerSpinner.OnDigitChangeListener()
            {
                public void onDigitChange(OdometerSpinner s, int newDigit)
                {
                    updateValue();
                }
            });
        }
    }
//    public void setUnits(String units){
//        TextView unitElem = (TextView) findViewById(R.id.units);
//        unitElem.setText(units);
//    }
    public void setUnits(boolean useImperial,  boolean isMeasuringDistance){
        ImageView units = (ImageView) findViewById(R.id.units);
        if(isMeasuringDistance){
            if(useImperial) {
                units.setImageResource(R.drawable.miles);
            }else{
                units.setImageResource(R.drawable.km);
            }
        }else{
            units.setImageResource(R.drawable.kwh);
        }
    }

    private void updateValue()
    {
        int tempValue = 0;
        int factor = 1;

        for(OdometerSpinner s : mDigitSpinners)
        {
            tempValue += (s.getCurrentDigit() * factor);
            factor *= 10;
        }

        int old = mCurrentValue;
        mCurrentValue = tempValue;

        if(old != mCurrentValue && mValueChangeListener != null)
            mValueChangeListener.onValueChange(this, mCurrentValue);
    }

    public void setValue(int value)
    {
        int old = mCurrentValue;
        mCurrentValue = value;
        int tempValue = value;

        for(int i = 5; i > 0; --i)
        {
            int factor = (int)Math.pow(10, i);

            int digitVal = (int) Math.floor(tempValue / factor);
            tempValue -= (digitVal * factor);
            mDigitSpinners[i].setCurrentDigit(digitVal);
        }
        mDigitSpinners[0].setCurrentDigit(tempValue);

        if(old != mCurrentValue && mValueChangeListener != null)
            mValueChangeListener.onValueChange(this, mCurrentValue);
    }

    public int getValue()
    {
        return mCurrentValue;
    }

    public void setOnValueChangeListener(OnValueChangeListener listener)
    {
        mValueChangeListener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    public interface OnValueChangeListener
    {
        abstract void onValueChange(SixDigitOdometer sender, int newValue);
    }
}
