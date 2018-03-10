package net.mengkang.manager;

import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import net.mengkang.entity.Client;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by luoxiaosong on 2018/2/22.
 */
public class ClientMgr {

    private static Map<Long, Client> allClient = new ConcurrentHashMap<>();

    public static Client getClient(long clientId){
        return allClient.get(clientId);
    }

    public static void addClient(Client client){
        allClient.put(client.getClientId(),client);
    }

    public static void removeClient(Client client){
        Client client1 = allClient.get(client.getClientId());
        if (client1 == null){
            System.err.println("removeClient don't find this client. clientId: "+client.getClientId()+"clientUserName:"+client.getUsername());
        }else {
            allClient.remove(client);
        }
    }



//    public static boolean isHasRoom(int roomId){
//        return allClient.containsKey(roomId);
//    }

//    public static List<Client> createRoom(int roomId){
//        if (isHasRoom(roomId)){
//            return allClient.get(roomId);
//        }
//        List<Client> room = new ArrayList<>();
//        allClient.put(roomId,room);
//        return room;
//    }

//    public static void sendMessToRoomClient(Client client,  String request) {
//
//        if (allClient.containsKey(client.getRoomId())) {
//            List<Client> roomClient= allClient.get(client.getRoomId());
//            for(Client c:roomClient){
//                if(c.getClientId() == client.getClientId()){
//                    //不发给自己
//                    continue;
//                }
//                String msg  = MessMgr.createMessage(0,"",c.getClientId(), request);
//                c.getChannel().writeAndFlush(new TextWebSocketFrame(msg));
//            }
//        }
//        //发送完保存消息
//        String clientMsg  = MessMgr.createMessage(0,"",client.getClientId(), request);
//        RedisMgr.setValue(client.getRoomId()+"_"+client.getClientId(),clientMsg);
//
//    }




}
