package net.mengkang.entity;

import io.netty.channel.Channel;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by zhoumengkang on 16/7/2.
 */
public class Client {

    public static AtomicLong CONCURRENT_INTEGER = new AtomicLong(0);

    private long clientId;
    private long roomId;
    private int status;
    private int userStatus;
    private Channel channel;
    private String username;
    private String password;

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long id) {
        this.clientId = id;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public void setStatus(int status){
        this.status  = status;
    }

    public int getStatus() {
        return status;
    }

    public int getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(int userStatus) {
        this.userStatus = userStatus;
    }

    public Channel getChannel(){
        return channel;
    }

    public void setChannel(Channel channel){
        this.channel = channel;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void sendMessage(String message){

    }
}
