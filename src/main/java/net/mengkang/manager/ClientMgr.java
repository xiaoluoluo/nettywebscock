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

    private static Map<Long, Client> allClient = new ConcurrentHashMap<Long, Client>();

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
}
