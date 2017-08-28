package com.hritupon.nostalgia.commands;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.hritupon.nostalgia.commands.impl.DinnerCommand;
import com.hritupon.nostalgia.commands.impl.LunchCommand;
import com.hritupon.nostalgia.commands.impl.SleepCommand;
import com.hritupon.nostalgia.commands.impl.StartJourneyCommand;
import com.hritupon.nostalgia.commands.impl.WakeupCommand;
import com.hritupon.nostalgia.services.DatabaseService;

/**
 * Created by hritupon on 28/8/17.
 */

public class CommandUtil {
    public static Command getCommand(Context context, String description, FirebaseAuth mAuth, DatabaseService firebaseService){
        if(WakeupCommand.isCommand(description))return new WakeupCommand(context, description, mAuth, firebaseService);
        if(SleepCommand.isCommand(description))return new SleepCommand(context, description, mAuth, firebaseService);
        if(LunchCommand.isCommand(description))return new LunchCommand(context, description, mAuth, firebaseService);
        if(DinnerCommand.isCommand(description))return new DinnerCommand(context, description, mAuth, firebaseService);
        if(StartJourneyCommand.isCommand(description))return new StartJourneyCommand(context, description, mAuth, firebaseService);
        return null;
    }
}
