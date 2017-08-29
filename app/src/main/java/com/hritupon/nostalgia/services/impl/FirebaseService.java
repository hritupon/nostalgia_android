package com.hritupon.nostalgia.services.impl;

import com.google.firebase.database.DatabaseReference;
import com.hritupon.nostalgia.models.Story;
import com.hritupon.nostalgia.services.DatabaseService;

/**
 * Created by hritupon on 26/8/17.
 */

public class FirebaseService implements DatabaseService{


    DatabaseReference storiesRef;

    public FirebaseService(DatabaseReference storyRef){
        this.storiesRef = storyRef;
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
