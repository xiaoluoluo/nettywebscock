package net.mengkang.manager;

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import net.mengkang.entity.Client;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by luoxiaosong on 2018/2/22.
 */
public class MessMgr {

    // 消息分发器
    public static  void distribution(Client client,WebSocketFrame frame){

        String request = ((TextWebSocketFrame) frame).text();

        JSONObject json = new JSONObject(request);
        System.out.println(" 收到 " + request);
        int codeId = (Integer) json.get("code");
        //20000 消息是 获取自己的所有的发送过的消息
        if(codeId == 20000){
            List<String> allMessage=  RedisMgr.getAllValue(client.getRoomId()+"_"+client.getClientId());
            JSONObject alljson = new JSONObject();
            alljson.put("allMessage",allMessage);
            client.getChannel().writeAndFlush(new TextWebSocketFrame(alljson.toString()));
            return;
        }
        //20001 消息是 把自己的消息发送给别人
        if(codeId == 20001){

            return;
        }

        // 其他消息是广播消息
        ClientMgr.sendMessToRoomClient(client,frame);
    }

}
