package com.evoke.cn.evokecompanion.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.evoke.cn.evokecompanion.R;

/**
 * Created by Womble on 3/27/2017.
 * Layout For the route options, Eco, S Distance, Fastest Time
 */

public class RouteOption extends LinearLayout {

    TextView name;
    TextView desc;
    int meta_index;

    public RouteOption(Context context)
    {
        super(context);

        initialize();
    }

    public RouteOption(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initialize();
    }

    public RouteOption(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        initialize();
    }

    private void initialize() {
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        li.inflate(R.layout.route_option, this, false);

        name = (TextView) findViewById(R.id.option_name);
        desc = (TextView) findViewById(R.id.option_desc);
    }

    public void setLabels(String aName, String theDesc){
        setName(aName);
        setDesc(theDesc);
    }
    public void setName(String aName){
        name.setText(aName);
    }
    public void setDesc(String theDesc){
        desc.setText(theDesc);
    }

    public void setMeta(int meta) {
        meta_index = meta;
    }
    public int getIndex(){
        return meta_index;
    }
}
