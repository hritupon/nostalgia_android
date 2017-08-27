package com.hritupon.nostalgia;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hritupon.nostalgia.models.Story;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by hritupon on 27/8/17.
 */

public class StoriesListAdapter extends RecyclerView.Adapter<StoriesListAdapter.ViewHolder>{
    private Activity context;
    private List<Story> storyList;
    private int[] images = {
            R.drawable.breakfast,
            R.drawable.morning,
            R.drawable.movie,
            R.drawable.tea,
    };

    public StoriesListAdapter(StoriesActivity storiesActivity, List<Story> storyList) {
        this.storyList=storyList;
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        TextView  textViewDescription;
        TextView  textViewTimestamp;
        public ImageView itemImage;

        public ViewHolder(View itemView) {
            super(itemView);
            itemImage = (ImageView)itemView.findViewById(R.id.item_image);
            textViewDescription = (TextView) itemView.findViewById(R.id.textview_description);
            textViewTimestamp = (TextView) itemView.findViewById(R.id.textview_timestamp);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    int position = getAdapterPosition();

                    Snackbar.make(v, "Click detected on item " + position,
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }
            });
        }
    }

    @Override
    public StoriesListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(StoriesListAdapter.ViewHolder viewHolder, int position) {
        Story story = storyList.get(position);
        viewHolder.textViewDescription.setText(story.getDescription());
        viewHolder.textViewTimestamp.setText(getFormattedDate(story.getTimeStamp()));
        int randomImageNumber =(int) (Math.random()+position)%4;
        viewHolder.itemImage.setImageResource(images[randomImageNumber]);
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    private String getFormattedDate(String timeStamp){
        Date date = new Date(Long.parseLong(timeStamp));
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEE dd MMM yyyy hh:mm:ss aaa");
        return simpleDateformat.format(date);
    }

}
