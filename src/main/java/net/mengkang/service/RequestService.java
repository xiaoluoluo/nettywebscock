package net.mengkang.service;

import io.netty.channel.Channel;
import net.mengkang.entity.Client;
import net.mengkang.manager.RedisMgr;
import org.json.JSONObject;


public class RequestService {


    /**
     * 根据客户端的请求生成 Client
     *
     * @param request
     * @return
     */
    public static Client clientRegisterChannel(Channel Channel, String request) {
        JSONObject json = new JSONObject(request);


        //注册
        int codeId = (Integer) json.get("code");
        if(codeId == 10100){
            // 注册协议
            //判断用户名有没有


            // 如果没有  把用户名 密码存起来 并且分配用户id 保存数据库 注册成功
            String username= "";
            String password = "";

            Client client0 = RedisMgr.getClient(username);
            if (client0 != null){
                // 你已经有用户了 不需要注册
                return client0;
            }

            Client client = new Client();
            client.setUsername(username);
            client.setPassword(password);
            client.setClientId(Client.CONCURRENT_INTEGER.getAndIncrement());

            //存数据库
            RedisMgr.setClient(client);

            return client;


        }

        if(codeId == 10101){
            //这个是登录协议 获取用户名和密码  查看数据库有没有注册  如果没有 请注册 如果有 返回这个用户id
            String username = "";
            String passWord = "";
            // 到数据库获取客户端 然后匹配 密码
            Client client = RedisMgr.getClient(username);
            if (client == null){
                //你没有注册  请先注册再登录

                return client;
            }
            return client;

        }
        return null;
    }
}
