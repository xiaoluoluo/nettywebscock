package net.mengkang.manager;

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import net.mengkang.entity.Client;
import net.mengkang.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by luoxiaosong on 2018/2/22.
 */
public class ClientMgr {
    private static Map<Integer, List<Client>> allClient= new ConcurrentHashMap<>();

    public static boolean isHasRoom(int roomId){
        return allClient.containsKey(roomId);
    }

    public static List<Client> createRoom(int roomId){
        if (isHasRoom(roomId)){
            return allClient.get(roomId);
        }
        List<Client> room = new ArrayList<>();
        allClient.put(roomId,room);
        return room;
    }

    public static void sendMessToRoomClient(Client client, WebSocketFrame frame) {
        String request = ((TextWebSocketFrame) frame).text();
        System.out.println(" 收到 " + client.getChannel() + request);

        //保存消息
        String clientMsg  = MessageService.createMessage(client.getClientId(), request);
        RedisMgr.setValue(client.getRoomId()+"_"+client.getClientId(),clientMsg);

        if (allClient.containsKey(client.getRoomId())) {
            List<Client> roomClient= allClient.get(client.getRoomId());
            for(Client c:roomClient){
                if(c.getClientId() == client.getClientId()){
                    //不发给自己
                    continue;
                }
                String msg  = MessageService.createMessage(c.getClientId(), request);
                c.getChannel().writeAndFlush(new TextWebSocketFrame(msg));
            }
        }

    }

}
