package com.hritupon.nostalgia.services.impl;

import com.hritupon.nostalgia.services.DatabaseService;
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
