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

public class SleepCommand implements Command {
    public static final String IMAGE_ID = "sleep";
    public static Set<String> keyWords = new HashSet<>();
    Context context;
    String description;
    FirebaseAuth mAuth;
    DatabaseService firebaseService;

    static {
        keyWords.add("sleep");
        keyWords.add("sleeping");
        keyWords.add("i am sleeping");
        keyWords.add("i am going to sleep");
        keyWords.add("going to sleep");
    }

    public SleepCommand(Context context, String description, FirebaseAuth mAuth, DatabaseService firebaseService) {
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
        int max=10;int min=1;
        int rand=(int)(Math.random()*(max-min)+min);
        if(rand%2==0)return R.drawable.sleep_1;
        else return R.drawable.sleep_2;
    }

    public void execute(){
        String timeStamp = System.currentTimeMillis()+"";
        String description = "You slept at "+getFormattedDate(timeStamp);
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
