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
import com.hritupon.nostalgia.services.ImageService;
import com.hritupon.nostalgia.services.impl.ImageServiceImpl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by hritupon on 27/8/17.
 */

public class StoriesListAdapter extends RecyclerView.Adapter<StoriesListAdapter.ViewHolder>{
    private Activity context;
    private List<Story> storyList;
    private ImageService imageService;


    public StoriesListAdapter(StoriesActivity storiesActivity, List<Story> storyList) {
        this.storyList=storyList;
        this.imageService = new ImageServiceImpl();
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        TextView  textViewDescription;
        TextView  textViewDay;
        TextView textViewWeekDay;
        TextView  textViewTimestamp;
        ImageView amPmImageView;
       // public ImageView itemImage;

        public ViewHolder(View itemView) {
            super(itemView);
           // itemImage = (ImageView)itemView.findViewById(R.id.item_image);
            textViewDescription = (TextView) itemView.findViewById(R.id.textview_description);
            textViewDay = (TextView) itemView.findViewById(R.id.textview_day);
            textViewWeekDay = (TextView) itemView.findViewById(R.id.textview_weekday);
            textViewTimestamp = (TextView) itemView.findViewById(R.id.textview_timestamp);
            amPmImageView = (ImageView)itemView.findViewById(R.id.am_pm);
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

        if(!checkSameDateWithPreviousItems(position)){
            viewHolder.textViewDay.setVisibility(View.VISIBLE);
            viewHolder.textViewWeekDay.setVisibility(View.VISIBLE);
            viewHolder.textViewWeekDay.setText(getFormattedDay(story.getTimeStamp()));
            viewHolder.textViewDay.setText(getFormattedWeekDay(story.getTimeStamp()));
        }
        else{
            viewHolder.textViewDay.setText(" ");
            viewHolder.textViewDay.setVisibility(View.GONE);
            viewHolder.textViewWeekDay.setText(" ");
            viewHolder.textViewWeekDay.setVisibility(View.GONE);
        }
        viewHolder.textViewTimestamp.setText(getFormattedDate(story.getTimeStamp()));
        viewHolder.amPmImageView.setImageResource(imageService.getAmPmImage(story.getTimeStamp()));
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    private String getFormattedDate(long timeStamp){
        Date date = new Date(timeStamp);
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("hh:mm aaa");
        String dateStr = simpleDateformat.format(date);
        if(dateStr.startsWith("0"))
            dateStr=dateStr.substring(1);
        return dateStr;
    }

    private String getFormattedDay(long timeStamp){
        Date date = new Date(timeStamp);
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("dd MMM yyyy");
        String dateStr = simpleDateformat.format(date);
        String dateNumStr="";
        int dateNum = Integer.parseInt(dateStr.substring(0,2));
        String dateSuffix = getDateSuffix(dateNum);
        dateStr=dateStr.substring(0,2)+dateSuffix+dateStr.substring(2);
        if(dateStr.startsWith("0"))
            dateStr=dateStr.substring(1);
        return dateStr;
    }

    private String getDateSuffix(int n){

        switch (n % 10) {
            case 1:  return "st";
            case 2:  return "nd";
            case 3:  return "rd";
            default: return "th";
        }
    }
    public void setFilter(List<Story> newList){
        storyList.clear();
        storyList.addAll(newList);
        notifyDataSetChanged();

    }

    private String getFormattedWeekDay(long timeStamp){
        Calendar now = Calendar.getInstance();
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(timeStamp);

        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) ) {
            return "Today";
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1  ){
            return "Yesterday";
        }

        Date date = new Date(timeStamp);
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
        return simpleDateformat.format(date);
    }

    private String getLastShownDate(long timeStamp){
        Date date = new Date(timeStamp);
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("dd MMM yyyy");
        return simpleDateformat.format(date);
    }
    private boolean checkSameDateWithPreviousItems(int index){
        if(index!=0){
            Story prevStory = storyList.get(index-1);
            Story currentStory= storyList.get(index);
            return getLastShownDate(prevStory.getTimeStamp()).equalsIgnoreCase(getLastShownDate(currentStory.getTimeStamp()));
        }
        return false;
    }
}
