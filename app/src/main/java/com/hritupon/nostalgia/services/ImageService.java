package com.hritupon.nostalgia.services;

import com.hritupon.nostalgia.models.Story;

/**
 * Created by hritupon on 28/8/17.
 */

public interface ImageService {

    int getImage(Story story);
    int getImage(Story story, int position);
}
