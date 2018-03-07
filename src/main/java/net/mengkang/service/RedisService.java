package net.mengkang.service;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisService{

    private JedisPool pool = null;

    public  void startJedisPool() {
        if (pool != null) {
            return;
        }
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        //最大连接数
        jedisPoolConfig.setMaxTotal(100);
        //最大空闲连接
        jedisPoolConfig.setMaxIdle(50);
        pool = new JedisPool(jedisPoolConfig, "127.0.0.1", 6379,1000*5);
    }

}
