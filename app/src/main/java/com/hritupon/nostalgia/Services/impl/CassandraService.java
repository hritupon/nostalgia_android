package com.hritupon.nostalgia.Services.impl;

import com.hritupon.nostalgia.Services.DatabaseService;
import com.hritupon.nostalgia.models.Story;

/**
 * Created by hritupon on 26/8/17.
 */

public class CassandraService  implements DatabaseService {
    @Override
    public boolean save(Story story) {
        return false;
    }
}
