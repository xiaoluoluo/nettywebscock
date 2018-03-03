package net.mengkang.service;

import io.netty.channel.Channel;
import net.mengkang.dto.ClientStatus;
import net.mengkang.dto.UserStatus;
import net.mengkang.entity.Client;
import net.mengkang.entity.RoomInfo;
import net.mengkang.manager.RedisMgr;
import org.json.JSONObject;

import java.util.List;


public class RequestService {


    /**
     * 根据客户端的请求生成 Client
     *
     * @param request
     * @return
     */
    public static Client clientRegisterChannel(Channel Channel, String request) {
        JSONObject json = new JSONObject(request);
        //注册协议
        int codeId = (Integer) json.get("code");
        if(codeId == 10100){
            String username= (String) json.get("user");
            String password = (String) json.get("password");
            Integer userStatus = (Integer) json.get("password");
            Client client0 = RedisMgr.getClient(username);
            if (client0 != null){
                // 你已经有用户了 不需要注册
                return null;
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
            return client;
        }

        if(codeId == 10101){
            //这个是登录协议
            String username= (String) json.get("user");
            String password = (String) json.get("password");
            // 到数据库获取客户端 然后匹配 密码
            Client client = RedisMgr.getClient(username);
            if (client == null){
                //你没有注册  请先注册再登录
                return null;
            }
            if(!client.getPassword().equals(password)){
                //密码不对
            }
            client.setStatus(ClientStatus.login.getStatus());
            //修改状态
            RedisMgr.saveClient(client);
            return client;
        }

        if(codeId == 10102){
            // 获取所有的房间信息
            String user= (String) json.get("user");
            Client client = RedisMgr.getClient(user);
            if (client == null){
                //你没有注册  请先注册再登录
                return null;
            }
            if(client.getStatus() != ClientStatus.login.getStatus()){
                //你没有登录 请先登录
            }
            List<RoomInfo> allRoomInfo = RedisMgr.getAllRoomInfo(user);
            // 把所有的房间信息发给前端
        }

        //没有对应的协议
        return null;
    }
}
