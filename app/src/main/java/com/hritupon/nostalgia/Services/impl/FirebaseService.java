package com.hritupon.nostalgia.Services.impl;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hritupon.nostalgia.Services.DatabaseService;
import com.hritupon.nostalgia.models.Story;

/**
 * Created by hritupon on 26/8/17.
 */

public class FirebaseService implements DatabaseService{

    private static final String STORIES = "Stories";
    FirebaseDatabase database;
    DatabaseReference storiesRef;

    public FirebaseService(FirebaseDatabase database){
        this.database = database;
        storiesRef = database.getReference(STORIES);
    }
    public boolean save(Story story){
        String userId = story.getUserId();
        if(null != userId && !userId.isEmpty()){
            String id= storiesRef.child(userId).push().getKey();
            if(null!=id && !id.isEmpty()){
                story.setId(id);
                storiesRef.child(userId).child(id).setValue(story);
                return true;
            }
        }
        return false;
    }
}
