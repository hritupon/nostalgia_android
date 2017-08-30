package com.hritupon.nostalgia;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hritupon.nostalgia.models.Story;

import java.util.List;

/**
 * Created by hritupon on 31/8/17.
 */



public class DateWiseListItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Story> items;
    static boolean alternateRow=true;
    public DateWiseListItemAdapter(List<Story> items) {
        this.items = items;
        alternateRow=true;
    }


    /** References to the views for each data item **/
    public class DateWiseListItemHolder extends RecyclerView.ViewHolder{
        public TextView titleView;
        public TextView authorView;
        RelativeLayout rootView;
        public DateWiseListItemHolder(View v) {
            super(v);
            titleView = (TextView) v.findViewById(R.id.title);
            authorView = (TextView) v.findViewById(R.id.author);
            rootView=(RelativeLayout)v.findViewById(R.id.date_wise_item_root_view);

        }
    }


    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.date_wise_list_item, parent, false);

        return new DateWiseListItemHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Story story = items.get(position);
        DateWiseListItemHolder viewHolder = (DateWiseListItemHolder) holder;
        viewHolder.titleView.setText(story.getDescription());
        viewHolder.authorView.setText(story.getTimeStamp());
        if(alternateRow){
            viewHolder.rootView.setBackgroundColor(Color.parseColor("#d8e5e5"));
        }else{
            viewHolder.rootView.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        alternateRow=!alternateRow;
    }


}