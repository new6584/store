package com.evoke.cn.evokecompanion.views;

import android.content.Context;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.evoke.cn.evokecompanion.R;
import com.evoke.cn.evokecompanion.models.AddressResults;
import com.google.android.gms.vision.text.Text;

import java.util.ArrayList;

/**
 * Created by Womble on 3/27/2017.
 */

public class AddressResultView extends LinearLayout {

    private TextView durationElem;
    private TextView distanceElem;
    private ViewGroup directionParent;
    private TextView headElem;
    private View exitButton;
    private ArrayList<DirectionItem> directionItems;

    public AddressResultView(Context context) {
        super(context);

        initialize();
    }

    public AddressResultView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initialize();
    }

    public AddressResultView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initialize();
    }

    private void initialize() {
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        View me = li.inflate(R.layout.address_result, this, true);
        directionItems = new ArrayList<DirectionItem>();
        distanceElem = (TextView) me.findViewById(R.id.search_res_total_distance);
        durationElem = (TextView) me.findViewById(R.id.search_res_total_duration);
        directionParent = (ViewGroup) me.findViewById(R.id.search_res_direction_container);
        headElem = (TextView) me.findViewById(R.id.search_res_address_head) ;
        exitButton = me.findViewById(R.id.search_res_exit);

    }

    public void setHeader(String placeName) {
        if(placeName != null){
            headElem.setText( placeName );
        }
    }
    public View getExitButton(){
        return exitButton;
    }
    public void setRouteInfo(String distance, String duration){

        distanceElem.setText(distance);
        durationElem.setText(duration);
    }

    //leg here = mapbox legstep
    public void addLeg(String summary, String distance, int directionModifier, int givenIndex){
        DirectionItem item = new DirectionItem(getContext());

        item.addSummary(summary);
        item.addDirection(directionModifier);
        item.addDistance(distance);
        item.setIndex(givenIndex);
        directionItems.add(item);
        directionParent.addView(item);
    }
    /*
     * the index of the direction will not be the same index it was added as after the first
     * removal
     */
    public void removeLeg(int index){
        for(int i =0; i < directionItems.size(); i++){
            if( directionItems.get(i).getIndex() == index){
                this.removeView(directionItems.get(i));
                directionItems.remove(i);
            }
        }
    }
    public int getFirstShowingIndex(){
        return ( (DirectionItem) directionParent.getChildAt(0)).getIndex();
    }

}
