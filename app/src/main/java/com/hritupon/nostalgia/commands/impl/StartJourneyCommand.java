package com.hritupon.nostalgia.commands.impl;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.hritupon.nostalgia.R;
import com.hritupon.nostalgia.commands.Command;
import com.hritupon.nostalgia.models.Story;
import com.hritupon.nostalgia.services.DatabaseService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by hritupon on 28/8/17.
 */

public class StartJourneyCommand implements Command{
    public static final String IMAGE_ID = "journey_start";
    public static Set<String> keyWords = new HashSet<>();
    Context context;
    String description;
    FirebaseAuth mAuth;
    DatabaseService firebaseService;

    static {
        keyWords.add("start journey");
        keyWords.add("started my journey");
        keyWords.add("i have started");
        keyWords.add("i have started my journey");
        keyWords.add("started journey");
        keyWords.add("journey start");

    }

    public StartJourneyCommand(Context context, String description, FirebaseAuth mAuth, DatabaseService firebaseService) {
        this.context = context;
        this.description = description;
        this.mAuth = mAuth;
        this.firebaseService = firebaseService;
    }

    public static boolean isCommand(String text) {
        for(String keys: keyWords){
            if(text.toLowerCase().contains(keys)){
                return true;
            }
        }
        return false;
    }

    public static int getImageId(){
        return R.drawable.cycle;
    }

    public void execute(){
        String timeStamp = System.currentTimeMillis()+"";
        String description = "You started your journey at "+getFormattedDate(timeStamp);
        String userId = mAuth.getCurrentUser().getUid();
        Story story = new Story(description,timeStamp, userId, IMAGE_ID);

        boolean isSaveSuccessful = firebaseService.save(story);
        if(isSaveSuccessful){
            Toast.makeText(context, "Story saved. Add another story.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Unable to save story. Please try again", Toast.LENGTH_SHORT).show();
        }
        //@Todo
        //cassandraService.save(story);
    }
    private String getFormattedDate(String timeStamp){
        Date date = new Date(Long.parseLong(timeStamp));
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("hh:mm:ss aaa EEE dd MMM yyyy");
        return simpleDateformat.format(date);
    }
}
