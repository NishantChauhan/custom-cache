package com.nishant.customcache.interfaces;

import java.util.concurrent.TimeUnit;

public interface Expirable {
    void expire(ExpirableItem item);
    long getExpiry();
    TimeUnit getExpiryTimeUnit();
}
