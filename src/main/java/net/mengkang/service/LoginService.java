package net.mengkang.service;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import net.mengkang.dto.ClientStatus;
import net.mengkang.entity.Client;
import net.mengkang.manager.MessMgr;
import net.mengkang.manager.RedisMgr;
import org.json.JSONObject;

/**
 * Created by luoxiaosong on 2018/3/10.
 */
public class LoginService extends BaseService{

    /**注册消息**/
    public static void registMessage(Channel channel, JSONObject json){
        String username= (String) json.get("user");
        String password = (String) json.get("password");
        Integer userStatus = (Integer) json.get("userStatus");
        Client client0 = RedisMgr.getClient(username);
        if (client0 != null){
            String message = MessMgr.createMessage(1,"登录名已经被注册 请换个账号注册",0, "");
            channel.writeAndFlush(new TextWebSocketFrame(message));
            return;
        }
        Client client = new Client();
        client.setUsername(username);
        client.setPassword(password);
        client.setStatus(ClientStatus.regist.getStatus());
        client.setUserStatus(userStatus);
        client.setClientId(Client.CONCURRENT_INTEGER.getAndIncrement());
        client.setRoomId(Client.CONCURRENT_INTEGER.getAndIncrement());
        //存数据库
        RedisMgr.saveClient(client);

        JSONObject data = new JSONObject();
        data.put("code",10100);
        //1表示成功
        data.put("status",1);
        String dataMessage =data.toString();
        String message = MessMgr.createMessage(0,"",0, dataMessage);
        channel.writeAndFlush(new TextWebSocketFrame(message));
    }

    public static void loginMessage(Channel channel, JSONObject json){
        //这个是登录协议
        String username= (String) json.get("user");
        String password = (String) json.get("password");
        // 到数据库获取客户端 然后匹配 密码
        Client client = RedisMgr.getClient(username);
        if (client == null){
            //你没有注册  请先注册再登录
            String message = MessMgr.createMessage(2,"你没有注册请先注册再登录",0, "");
            channel.writeAndFlush(new TextWebSocketFrame(message));
            return ;
        }
        if(!client.getPassword().equals(password)){
            //密码不对
            String message = MessMgr.createMessage(3,"密码不对",0, "");
            channel.writeAndFlush(new TextWebSocketFrame(message));
            return ;
        }
        client.setStatus(ClientStatus.login.getStatus());
        //更新客户端的状态
        RedisMgr.saveClient(client);
        JSONObject data = new JSONObject();
        data.put("code",10101);
        //1表示成功
        data.put("status",1);
        data.put("userStatus",client.getUserStatus());
        String dataMessage =data.toString();
        String message = MessMgr.createMessage(0,"",0, dataMessage);
        channel.writeAndFlush(new TextWebSocketFrame(message));
    }


}
