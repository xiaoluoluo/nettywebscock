package net.mengkang.manager;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luoxiaosong on 2018/2/22.
 */
public class RedisMgr {

    private static JedisPool pool = null;

    public static void startJedisPool() {
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

    public static void setValue(String key,String value){
        Jedis jedis = pool.getResource();
        jedis.rpush(key, value);
        jedis.close();
    }

    public static  List<String> getAllValue(String key){
        Jedis jedis = pool.getResource();
        //0：表头，-1：表尾
        List<String> resultList = jedis.lrange(key, 0, -1);
        jedis.close();
        return  resultList;
    }

    // 结束的时候需要调用
    public static void end(){
        if(null != pool){
            pool.destroy();
            System.out.println("连接池关闭");
        }
    }
}
