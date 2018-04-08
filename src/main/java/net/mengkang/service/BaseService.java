package net.mengkang.service;

import net.mengkang.manager.RedisMgr;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by luoxiaosong on 2018/3/10.
 */
public class BaseService {

    public static String redisKey = "";

    private static AtomicLong CONCURRENT_INTEGER = new AtomicLong(0);

    public static long getId() {
         Long id = RedisMgr.getId();
         if (id == null){
            id = CONCURRENT_INTEGER.getAndIncrement();
         }
         RedisMgr.setId(id+1);
        return id+1;
    }
}
