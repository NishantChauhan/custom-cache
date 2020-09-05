package com.nishant.customcache.services;

import com.nishant.customcache.interfaces.ExpirableItem;
import com.nishant.customcache.interfaces.Expirable;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ExpirationService {
    ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(10);

    public void schedule(Expirable expirable, ExpirableItem item) {
        scheduledService.schedule(()-> expirable.expire(item),expirable.getExpiry(), expirable.getExpiryTimeUnit());
    }
}
