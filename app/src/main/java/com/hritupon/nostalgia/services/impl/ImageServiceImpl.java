package com.hritupon.nostalgia.services.impl;

import com.hritupon.nostalgia.R;
import com.hritupon.nostalgia.commands.impl.DinnerCommand;
import com.hritupon.nostalgia.commands.impl.LunchCommand;
import com.hritupon.nostalgia.commands.impl.SleepCommand;
import com.hritupon.nostalgia.commands.impl.StartJourneyCommand;
import com.hritupon.nostalgia.commands.impl.WakeupCommand;
import com.hritupon.nostalgia.models.Story;
import com.hritupon.nostalgia.services.ImageService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hritupon on 28/8/17.
 */

public class ImageServiceImpl implements ImageService{

    @Override
    public int getImage(Story story) {
        if(null != story.getImagePath() && !story.getImagePath().isEmpty()){
            return getImageId(story);
        }else {
            return guessTheImage(story);
        }
    }

    @Override
    public int getImage(Story story, int position) {
        if(null != story.getImagePath() && !story.getImagePath().isEmpty()){
            return getImageId(story);
        }else {
            return guessTheImage(story);
        }
    }

    @Override
    public int getAmPmImage(long timeStamp) {
        Date date = new Date(timeStamp);
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("hh aaa");
        String amPm = simpleDateformat.format(date);
        int hour = Integer.parseInt(amPm.substring(0,2));
        amPm=amPm.substring(3);
        //night icon from 6 pm till morning 5 am
        if(amPm.equalsIgnoreCase("pm")){
            if(hour>=6 && hour<=10)
                return R.drawable.if_icon_27_moon_stars_315964;
            else if(hour>=11 && hour<12)
                return R.drawable.if_4_1208027;
            else if(hour>=1 && hour<=5)
                return R.drawable.if_sun_simple_367526;
        }else{
            if(hour>=1 && hour<=5)
                return R.drawable.if_4_1208027;
            else if(hour>=6 && hour<=10)
                return R.drawable.if_sun_simple_cloudy_367525;
            else if(hour>=11 && hour<12)
                return R.drawable.if_sun_simple_367526;
        }

        if(hour==12 && amPm.equalsIgnoreCase("am"))
            return R.drawable.if_4_1208027;

        return R.drawable.if_sun_simple_367526;
    }

    private int getImageId(Story story){
        if(story.getImagePath().equalsIgnoreCase(WakeupCommand.IMAGE_ID))
            return WakeupCommand.getImageId();
        else if(story.getImagePath().equalsIgnoreCase(StartJourneyCommand.IMAGE_ID))
            return StartJourneyCommand.getImageId();
        else if(story.getImagePath().equalsIgnoreCase(DinnerCommand.IMAGE_ID))
            return DinnerCommand.getImageId();
        else if(story.getImagePath().equalsIgnoreCase(LunchCommand.IMAGE_ID))
            return LunchCommand.getImageId();
        else if(story.getImagePath().equalsIgnoreCase(SleepCommand.IMAGE_ID))
            return SleepCommand.getImageId();
        return guessTheImage(story);
    }

    private int guessTheImage(Story story){
        String description = story.getDescription().toLowerCase();
        if(description.contains("movie") || description.contains("watching") ||description.contains("watch"))
            return R.drawable.default_1;
        if(description.contains("sleep")||description.contains("morning"))
            return R.drawable.default_1;
        else{
            return getRandomDefaultImage();
        }

    }

    private int getRandomDefaultImage(){
        return R.drawable.default_1;
    }

}
