package com.hritupon.nostalgia.services.impl;

import com.hritupon.nostalgia.R;
import com.hritupon.nostalgia.commands.impl.DinnerCommand;
import com.hritupon.nostalgia.commands.impl.LunchCommand;
import com.hritupon.nostalgia.commands.impl.SleepCommand;
import com.hritupon.nostalgia.commands.impl.StartJourneyCommand;
import com.hritupon.nostalgia.commands.impl.WakeupCommand;
import com.hritupon.nostalgia.models.Story;
import com.hritupon.nostalgia.services.ImageService;

/**
 * Created by hritupon on 28/8/17.
 */

public class ImageServiceImpl implements ImageService{
    private int[] images = {
            R.drawable.default_1,
            R.drawable.default_2,
            R.drawable.default_3,
            R.drawable.default_4
    };

    @Override
    public int getImage(Story story) {
        if(null != story.getImageId() && !story.getImageId().isEmpty()){
            return getImageId(story);
        }else {
            return guessTheImage(story);
        }
    }

    @Override
    public int getImage(Story story, int position) {
        if(null != story.getImageId() && !story.getImageId().isEmpty()){
            return getImageId(story);
        }else {
            return guessTheImage(story);
        }
    }

    private int getImageId(Story story){
        if(story.getImageId().equalsIgnoreCase(WakeupCommand.IMAGE_ID))
            return WakeupCommand.getImageId();
        else if(story.getImageId().equalsIgnoreCase(StartJourneyCommand.IMAGE_ID))
            return StartJourneyCommand.getImageId();
        else if(story.getImageId().equalsIgnoreCase(DinnerCommand.IMAGE_ID))
            return DinnerCommand.getImageId();
        else if(story.getImageId().equalsIgnoreCase(LunchCommand.IMAGE_ID))
            return LunchCommand.getImageId();
        else if(story.getImageId().equalsIgnoreCase(SleepCommand.IMAGE_ID))
            return SleepCommand.getImageId();
        return guessTheImage(story);
    }

    private int guessTheImage(Story story){
        String description = story.getDescription().toLowerCase();
        if(description.contains("movie") || description.contains("watching") ||description.contains("watch"))
            return R.drawable.movie_1;
        if(description.contains("sleep")||description.contains("morning"))
            return R.drawable.sleep_1;
        else{
            return getRandomDefaultImage();
        }

    }

    private int getRandomDefaultImage(){
        int max=5; int min=1;
        int rand=(int)(Math.random()*(max-min)+min)%4;
        return images[rand];
    }

}
