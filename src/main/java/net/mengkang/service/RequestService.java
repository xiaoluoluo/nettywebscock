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
     *
     * @param request
     * @return
     */
    public static Client getClient(String request) {
        JSONObject json = new JSONObject(request);
        String username= (String) json.get("user");
        Client client = RedisMgr.getClient(username);
        return client;
    }
}
