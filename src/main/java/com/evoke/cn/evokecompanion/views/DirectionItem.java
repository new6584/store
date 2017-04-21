package com.evoke.cn.evokecompanion.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.evoke.cn.evokecompanion.R;
import com.evoke.cn.evokecompanion.models.BluetoothConstants;

/**
 * Created by Womble on 4/18/2017.
 */

public class DirectionItem extends LinearLayout implements BluetoothConstants {

    ImageView directionImage;
    TextView directionDistance;
    TextView directionSummary;
    private int index;

    public DirectionItem(Context context)
    {
        super(context);

        initialize();
    }

    /*
     * This is called when a view is being constructed from an XML file,
     * supplying attributes that were specified in the XML file.
     */
    public DirectionItem(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initialize();
    }

    /*
     * Perform inflation from XML and apply a class-specific base style.
     * This constructor of View allows subclasses to use their own base
     * style when they are inflating.
     */
    public DirectionItem(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        initialize();
    }

    /*
     * Initialize all of our class members and variables
     */
    private void initialize()
    {
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        View me = li.inflate(R.layout.direction_item, this, true);
        directionImage = (ImageView) me.findViewById(R.id.direction_item_direction);
        directionDistance = (TextView) me.findViewById(R.id.direction_item_distance);
        directionSummary = (TextView) me.findViewById(R.id.direction_item_summary);
    }

    public void addSummary(String summary){
        directionSummary.setText(summary);
    }
    public void addDirection(int modifier){
        directionImage.setImageResource( this.getDirectionResource(modifier));
    }
    public void addDistance(String distance){
        
    }

    /*
     * parses direction modifier code into a resource number
     */
    public int getDirectionResource(int imageCode){
        switch(imageCode){
            case MAN_UTURN:
                return 0;
            case MAN_RIGHT:
                return 0;
            case MAN_STRAIGHT:
                return 0;
            case MAN_LEFT:
                return 0;
            default:
                return MAN_TERMINATE;
        }
    }

    public void setIndex(int index) {
        this.index = index;
    }
    public int getIndex(){
        return index;
    }
}
