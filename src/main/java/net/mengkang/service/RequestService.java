package net.mengkang.service;

import io.netty.channel.Channel;
import net.mengkang.entity.Client;
import org.json.JSONObject;


public class RequestService {

    public static final String roomId = "rid";

    /**
     * 根据客户端的请求生成 Client
     *
     * @param request 例如 {id:1;rid:21;token:'43606811c7305ccc6abb2be116579bfd'}
     * @return
     */
    public static Client clientRegisterChannel(Channel Channel, String request) {
        JSONObject json = new JSONObject(request);
        Client client = new Client();

        if (!json.has(roomId)) {
            return client;
        }
        client.setChannel(Channel);
        client.setRoomId(json.getInt(roomId));
        client.setClientId(Client.CONCURRENT_INTEGER.getAndIncrement());
        return client;
    }
}
