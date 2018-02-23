package net.mengkang.entity;

import io.netty.channel.Channel;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhoumengkang on 16/7/2.
 */
public class Client {

    public static AtomicInteger CONCURRENT_INTEGER = new AtomicInteger();

    private long clientId;
    private int roomId;
    private Channel channel;

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long id) {
        this.clientId = id;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public Channel getChannel(){
        return channel;
    }

    public void setChannel(Channel channel){
        this.channel = channel;
    }

    public void sendMessage(String message){

    }
}
