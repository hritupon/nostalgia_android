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

public class LunchCommand implements Command {
    public static final String IMAGE_ID = "lunch";
    public static Set<String> keyWords = new HashSet<>();
    Context context;
    String description;
    FirebaseAuth mAuth;
    DatabaseService firebaseService;

    static {
        keyWords.add("LUNCH TIME");
        keyWords.add("I AM HAVING LUNCH");
        keyWords.add("LUNCH STARTED");
        keyWords.add("STARTING LUNCH");
        keyWords.add("LUNCH");
        keyWords.add("HAVING LUNCH");
        keyWords.add("HAD LUNCH");
        keyWords.add("JUST HAD LUNCH");
        keyWords.add("FINISHED LUNCH");

    }

    public LunchCommand(Context context, String description, FirebaseAuth mAuth, DatabaseService firebaseService) {
        this.context = context;
        this.description = description;
        this.mAuth = mAuth;
        this.firebaseService = firebaseService;
    }

    public static boolean isCommand(String text) {
        for(String keys: keyWords){
            if(text.toUpperCase().contains(keys)){
                return true;
            }
        }
        return false;
    }

    public static int getImageId()
    {
            return R.drawable.default_1;
    }

    public void execute(){
        long timeStamp = System.currentTimeMillis();
        String description = "You had lunch at "+getFormattedDate(timeStamp);
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
    private String getFormattedDate(long timeStamp){
        Date date = new Date(timeStamp);
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("hh:mm:ss aaa EEE dd MMM yyyy");
        return simpleDateformat.format(date);
    }
}
